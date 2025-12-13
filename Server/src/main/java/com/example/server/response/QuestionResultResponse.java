package com.example.server.response;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class QuestionResultResponse {
    private UUID questionId;
    private String questionText;
    private String type;           // SINGLE, MULTIPLE, TEXT
    private Double maxScore;       // Maximum points for this question
    private Double studentScore;   // Points earned by student

    // --- Multiple Choice Specifics ---
    private List<OptionResponse> options;       // All available options
    private List<UUID> selectedOptionIds;       // Options selected by student

    // --- Essay/Text Specifics ---
    private String essayAnswer;    // Student's text answer
    private String teacherComment; // Feedback from teacher
}
