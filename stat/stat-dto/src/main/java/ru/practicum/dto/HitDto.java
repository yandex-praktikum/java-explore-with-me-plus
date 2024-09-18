package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
