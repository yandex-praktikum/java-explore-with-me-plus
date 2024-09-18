package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;

public class HitDto {
    private Long id;

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private String ip;

    private String timestamp;
}
