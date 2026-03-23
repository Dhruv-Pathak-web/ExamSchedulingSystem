package model;

public class ScheduleGenerationResult {

    private final boolean success;
    private final int scheduledSubjects;
    private final int createdRows;
    private final String message;

    public ScheduleGenerationResult(boolean success, int scheduledSubjects, int createdRows, String message) {
        this.success = success;
        this.scheduledSubjects = scheduledSubjects;
        this.createdRows = createdRows;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getScheduledSubjects() {
        return scheduledSubjects;
    }

    public int getCreatedRows() {
        return createdRows;
    }

    public String getMessage() {
        return message;
    }
}
