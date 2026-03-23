package servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.sql.SQLException;

import dao.TimetableDao;
import model.ScheduleGenerationResult;
import util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TimetableServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final TimetableDao timetableDao = new TimetableDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireLogin(request, response)) {
            return;
        }

        String action = request.getPathInfo();

        try {
            if ("/generate".equals(action)) {
                LocalDate startDate = parseDate(request.getParameter("startDate"));
                int slotsPerDay = parseInt(request.getParameter("slotsPerDay"), -1);
                int durationMinutes = parseDurationMinutes(request.getParameter("durationHours"));
                String settingsQuery = buildSettingsQuery(startDate, slotsPerDay, request.getParameter("durationHours"));

                if (startDate == null || slotsPerDay < 1 || durationMinutes < 30) {
                    response.sendRedirect(request.getContextPath()
                            + "/admin/timetable/index.jsp?error=invalid_input" + settingsQuery);
                    return;
                }

                ScheduleGenerationResult result =
                        timetableDao.generateSchedule(startDate, slotsPerDay, durationMinutes);

                if (result.isSuccess()) {
                    response.sendRedirect(request.getContextPath()
                            + "/admin/timetable/index.jsp?success=generated&subjects="
                            + result.getScheduledSubjects() + "&rows=" + result.getCreatedRows() + settingsQuery);
                } else {
                    String encodedMessage = URLEncoder.encode(result.getMessage(), StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath()
                            + "/admin/timetable/index.jsp?error=rules&message=" + encodedMessage + settingsQuery);
                }
                return;
            }

            if ("/clear".equals(action)) {
                timetableDao.clearSchedule();
                response.sendRedirect(request.getContextPath() + "/admin/timetable/index.jsp?success=cleared");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/admin/timetable/index.jsp?error=invalid_action");
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/admin/timetable/index.jsp?error=db");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    private int parseDurationMinutes(String hoursText) {
        try {
            double hours = Double.parseDouble(hoursText);
            return (int) Math.round(hours * 60);
        } catch (Exception e) {
            return -1;
        }
    }

    private String buildSettingsQuery(LocalDate startDate, int slotsPerDay, String durationHours) {
        String safeDuration = durationHours == null ? "" : durationHours.trim();
        return "&startDate=" + (startDate == null ? "" : startDate)
                + "&slotsPerDay=" + (slotsPerDay < 1 ? "" : slotsPerDay)
                + "&durationHours=" + URLEncoder.encode(safeDuration, StandardCharsets.UTF_8);
    }
}
