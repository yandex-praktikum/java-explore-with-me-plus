package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentAdminRequest;
import ru.practicum.dto.CommentDto;

import ru.practicum.service.CommentService;


import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;


    @GetMapping("/pending")
    public List<CommentDto> getPendingComments(
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        return commentService.getPendingComments(from, size);
    }


    @PatchMapping("/{commentId}")
    public CommentDto moderateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentAdminRequest request) {
        return commentService.moderateComment(commentId, request);
    }

    @GetMapping("/users/{userId}")
    public List<CommentDto> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        return commentService.getUserComments(userId, from, size);
    }
}