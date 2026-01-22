package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for recording endpoint hit information.
 * <p>
 * Used for incoming requests to the POST /hit endpoint.
 * Contains all necessary information about a single endpoint access event.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {

    /**
     * Unique identifier of the hit record (read-only, assigned by database).
     */
    private Long id;

    /**
     * Identifier of the application that recorded the hit.
     * Example: "ewm-main-service"
     */
    @NotBlank(message = "App cannot be blank")
    private String app;

    /**
     * URI path that was accessed.
     * Example: "/events/1"
     */
    @NotBlank(message = "URI cannot be blank")
    private String uri;

    /**
     * IP address of the client who made the request.
     * Example: "192.168.1.1"
     */
    @NotBlank(message = "IP cannot be blank")
    private String ip;

    /**
     * Timestamp when the request was made.
     * Format: "yyyy-MM-dd HH:mm:ss"
     */
    @NotNull(message = "Timestamp cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}