package ru.practicum.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getPublishedComments(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        return commentService.getPublishedComments(eventId, from, size);
    }

    // Получение конкретного комментария
    @GetMapping("/{commentId}")
    public CommentDto getComment(
            @PathVariable Long eventId,
            @PathVariable Long commentId) {
        return commentService.getPublishedComment(eventId, commentId);
    }
}

