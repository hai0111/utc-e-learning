package com.example.server.request;

import com.example.server.enums.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LessonRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(max = 500, message = "URL must not exceed 500 characters")
    private String url;

    @NotNull(message = "Type is required")
    private LessonType type;

    private Integer orderIndex;
    @NotNull(message = "File is required")
    private MultipartFile file;

}
