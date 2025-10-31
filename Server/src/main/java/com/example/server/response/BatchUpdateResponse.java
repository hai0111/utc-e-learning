package com.example.server.response;

import java.util.List;

public class BatchUpdateResponse {
    private String message;
    private int status;
    private int successCount;
    private int failureCount;
    private List<BatchUpdateError> errors;

    // Constructors
    public BatchUpdateResponse() {}

    public BatchUpdateResponse(String message, int status, int successCount, int failureCount, List<BatchUpdateError> errors) {
        this.message = message;
        this.status = status;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.errors = errors;
    }

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }

    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }

    public List<BatchUpdateError> getErrors() { return errors; }
    public void setErrors(List<BatchUpdateError> errors) { this.errors = errors; }

    public static class BatchUpdateError {
        private String lessonId;
        private String error;

        public BatchUpdateError() {}

        public BatchUpdateError(String lessonId, String error) {
            this.lessonId = lessonId;
            this.error = error;
        }

        // Getters and Setters
        public String getLessonId() { return lessonId; }
        public void setLessonId(String lessonId) { this.lessonId = lessonId; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
