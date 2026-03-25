package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Room;
import model.ScheduleGenerationResult;
import model.ScheduleEntry;
import model.Subject;
import util.DBConnection;
import util.DatabaseInitializer;

public class TimetableDao {

    private static final LocalTime DAY_START_TIME = LocalTime.of(9, 0);
    private static final DateTimeFormatter SLOT_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final int MAX_COURSES_PER_SHIFT = 3;

    private final SubjectDao subjectDao = new SubjectDao();
    private final RoomDao roomDao = new RoomDao();
    private final StudentDao studentDao = new StudentDao();

    public List<ScheduleEntry> findAll() throws SQLException {
        DatabaseInitializer.ensureInitialized();

        List<ScheduleEntry> entries = new ArrayList<>();
        String sql = """
                SELECT es.id, s.subject_code, s.subject_name, s.department, s.semester,
                       r.room_code, r.capacity, es.exam_date, es.exam_slot, es.slot_number,
                       es.allocated_students, es.expected_students
                FROM exam_schedule es
                JOIN subjects s ON s.id = es.subject_id
                JOIN rooms r ON r.id = es.room_id
                ORDER BY es.exam_date, COALESCE(es.slot_number, 0), s.department, s.semester, s.subject_code, r.room_code
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ScheduleEntry entry = new ScheduleEntry();
                entry.setId(rs.getInt("id"));
                entry.setSubjectCode(rs.getString("subject_code"));
                entry.setSubjectName(rs.getString("subject_name"));
                entry.setDepartment(rs.getString("department"));
                entry.setSemester(rs.getInt("semester"));
                entry.setRoomCode(rs.getString("room_code"));
                entry.setRoomCapacity(rs.getInt("capacity"));
                entry.setExamDate(rs.getDate("exam_date").toString());
                entry.setExamSlot(rs.getString("exam_slot"));
                entry.setSlotNumber(rs.getInt("slot_number"));
                entry.setAllocatedStudents(rs.getInt("allocated_students"));
                entry.setExpectedStudents(rs.getInt("expected_students"));
                entries.add(entry);
            }
        }

        return entries;
    }

    public int clearSchedule() throws SQLException {
        DatabaseInitializer.ensureInitialized();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM exam_schedule")) {
            return ps.executeUpdate();
        }
    }

    public ScheduleGenerationResult generateSchedule(LocalDate startDate, int slotsPerDay, int examDurationMinutes)
            throws SQLException {
        DatabaseInitializer.ensureInitialized();

        if (startDate == null) {
            return new ScheduleGenerationResult(false, 0, 0, "Start date is required.");
        }
        if (slotsPerDay < 1 || slotsPerDay > 8) {
            return new ScheduleGenerationResult(false, 0, 0, "Slots per day must be between 1 and 8.");
        }
        if (examDurationMinutes < 30 || examDurationMinutes > 480) {
            return new ScheduleGenerationResult(false, 0, 0, "Exam duration must be between 30 and 480 minutes.");
        }

        List<Subject> subjects = subjectDao.findAll();
        List<Room> rooms = roomDao.findAll();

        if (subjects.isEmpty()) {
            return new ScheduleGenerationResult(false, 0, 0, "Add subjects before generating timetable.");
        }
        if (rooms.isEmpty()) {
            return new ScheduleGenerationResult(false, 0, 0, "Add rooms before generating timetable.");
        }

        List<Room> usableRooms = new ArrayList<>();
        int totalCapacity = 0;
        for (Room room : rooms) {
            if (room.getCapacity() > 0) {
                usableRooms.add(room);
                totalCapacity += room.getCapacity();
            }
        }
        if (usableRooms.isEmpty()) {
            return new ScheduleGenerationResult(false, 0, 0, "All configured rooms have zero capacity.");
        }

        subjects.sort(Comparator
                .comparingInt(Subject::getSemester)
                .thenComparing(Subject::getDepartment, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Subject::getSubjectCode, String.CASE_INSENSITIVE_ORDER));

        Map<String, Set<LocalDate>> classDailyUsage = new HashMap<>();
        Map<String, Set<String>> classUsageBySession = new HashMap<>();
        Map<String, Set<Integer>> roomUsageBySession = new HashMap<>();
        List<PlannedAllocation> plannedRows = new ArrayList<>();
        int roomPointer = 0;

        for (Subject subject : subjects) {
            int expectedStudents = studentDao.countByDepartmentAndSemester(
                    subject.getDepartment(), subject.getSemester());
            int requiredSeats = Math.max(expectedStudents, 1);

            if (requiredSeats > totalCapacity) {
                return new ScheduleGenerationResult(
                        false,
                        plannedRows.isEmpty() ? 0 : countScheduledSubjects(plannedRows),
                        plannedRows.size(),
                        "Insufficient total room capacity for " + subject.getSubjectCode()
                                + ". Required: " + requiredSeats + ", available: " + totalCapacity + ".");
            }

            AssignmentPlan assignment = allocateSubject(
                    subject,
                    requiredSeats,
                    startDate,
                    slotsPerDay,
                    examDurationMinutes,
                    usableRooms,
                    roomPointer,
                    classDailyUsage,
                    classUsageBySession,
                    roomUsageBySession);

            if (assignment == null) {
                return new ScheduleGenerationResult(
                        false,
                        countScheduledSubjects(plannedRows),
                        plannedRows.size(),
                        "Unable to place " + subject.getSubjectCode()
                                + " with current constraints. Increase slots/day or room capacity.");
            }

            plannedRows.addAll(assignment.rows());
            roomPointer = assignment.nextRoomPointer();
        }

        clearSchedule();
        int insertedRows = insertScheduleRows(plannedRows);

        return new ScheduleGenerationResult(true, subjects.size(), insertedRows, "Schedule generated.");
    }

    private AssignmentPlan allocateSubject(Subject subject,
                                           int requiredSeats,
                                           LocalDate startDate,
                                           int slotsPerDay,
                                           int examDurationMinutes,
                                           List<Room> rooms,
                                           int roomPointer,
                                           Map<String, Set<LocalDate>> classDailyUsage,
                                           Map<String, Set<String>> classUsageBySession,
                                           Map<String, Set<Integer>> roomUsageBySession) {

        String classKey = buildClassKey(subject.getDepartment(), subject.getSemester());

        for (int dayOffset = 0; dayOffset < 3650; dayOffset++) {
            LocalDate date = startDate.plusDays(dayOffset);
            Set<LocalDate> occupiedDays = classDailyUsage.computeIfAbsent(classKey, key -> new HashSet<>());
            if (occupiedDays.contains(date)) {
                continue;
            }

            for (int slot = 1; slot <= slotsPerDay; slot++) {
                String sessionKey = date + "#" + slot;
                Set<String> classesInSession =
                        classUsageBySession.computeIfAbsent(sessionKey, key -> new HashSet<>());

                if (!classesInSession.contains(classKey) && classesInSession.size() >= MAX_COURSES_PER_SHIFT) {
                    continue;
                }

                Set<Integer> usedRooms = roomUsageBySession.computeIfAbsent(sessionKey, key -> new HashSet<>());

                AssignmentPlan plan = allocateRoomsForSession(
                        subject,
                        requiredSeats,
                        date,
                        slot,
                        examDurationMinutes,
                        rooms,
                        roomPointer,
                        usedRooms);

                if (plan != null) {
                    classesInSession.add(classKey);
                    occupiedDays.add(date);
                    return plan;
                }
            }
        }

        return null;
    }

    private AssignmentPlan allocateRoomsForSession(Subject subject,
                                                   int requiredSeats,
                                                   LocalDate date,
                                                   int slot,
                                                   int examDurationMinutes,
                                                   List<Room> rooms,
                                                   int roomPointer,
                                                   Set<Integer> usedRooms) {

        List<Room> rotatedRooms = rotateRooms(rooms, roomPointer);
        List<PlannedAllocation> rows = new ArrayList<>();
        int remaining = requiredSeats;
        int nextPointer = roomPointer;

        for (int i = 0; i < rotatedRooms.size(); i++) {
            Room room = rotatedRooms.get(i);
            if (usedRooms.contains(room.getId())) {
                continue;
            }

            int roomCapacity = room.getCapacity();
            if (roomCapacity < 1) {
                continue;
            }

            int allocated = Math.min(remaining, roomCapacity);
            rows.add(new PlannedAllocation(
                    subject.getId(),
                    room.getId(),
                    date,
                    slot,
                    buildSlotLabel(slot, examDurationMinutes),
                    allocated,
                    requiredSeats));

            usedRooms.add(room.getId());
            remaining -= allocated;
            nextPointer = (roomPointer + i + 1) % rooms.size();

            if (remaining <= 0) {
                return new AssignmentPlan(rows, nextPointer);
            }
        }

        for (PlannedAllocation row : rows) {
            usedRooms.remove(row.roomId());
        }
        return null;
    }

    private int insertScheduleRows(List<PlannedAllocation> plannedRows) throws SQLException {
        String insertSql = """
                INSERT INTO exam_schedule(subject_id, room_id, exam_date, exam_slot, slot_number, allocated_students, expected_students)
                VALUES(?, ?, ?, ?, ?, ?, ?)
                """;

        int inserted = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            for (PlannedAllocation row : plannedRows) {
                ps.setInt(1, row.subjectId());
                ps.setInt(2, row.roomId());
                ps.setDate(3, Date.valueOf(row.examDate()));
                ps.setString(4, row.examSlot());
                ps.setInt(5, row.slotNumber());
                ps.setInt(6, row.allocatedStudents());
                ps.setInt(7, row.expectedStudents());
                inserted += ps.executeUpdate();
            }
        }
        return inserted;
    }

    private String buildSlotLabel(int slotNumber, int examDurationMinutes) {
        LocalTime start = DAY_START_TIME.plusMinutes((long) (slotNumber - 1) * examDurationMinutes);
        LocalTime end = start.plusMinutes(examDurationMinutes);
        return SLOT_TIME_FORMAT.format(start) + " - " + SLOT_TIME_FORMAT.format(end);
    }

    private String buildClassKey(String department, int semester) {
        String dept = department == null ? "" : department.trim().toUpperCase();
        return dept + "::SEM::" + semester;
    }

    private List<Room> rotateRooms(List<Room> rooms, int pointer) {
        List<Room> ordered = new ArrayList<>(rooms.size());
        for (int i = 0; i < rooms.size(); i++) {
            ordered.add(rooms.get((pointer + i) % rooms.size()));
        }
        return ordered;
    }

    private int countScheduledSubjects(List<PlannedAllocation> rows) {
        Set<Integer> subjectIds = new HashSet<>();
        for (PlannedAllocation row : rows) {
            subjectIds.add(row.subjectId());
        }
        return subjectIds.size();
    }

    private record PlannedAllocation(
            int subjectId,
            int roomId,
            LocalDate examDate,
            int slotNumber,
            String examSlot,
            int allocatedStudents,
            int expectedStudents) {
    }

    private record AssignmentPlan(List<PlannedAllocation> rows, int nextRoomPointer) {
    }
}
