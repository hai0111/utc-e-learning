package com.example.server.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateResponse {
    private String message;
    private int status;
    private int successCount;
    private int failureCount;
    private List<BatchUpdateError> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchUpdateError {
        private String lessonId;
        private String error;
    }
}
