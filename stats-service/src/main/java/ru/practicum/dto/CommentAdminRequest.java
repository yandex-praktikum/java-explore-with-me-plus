package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.CommentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentAdminRequest {
    private CommentStatus status;
    private String moderatorMessage;
}