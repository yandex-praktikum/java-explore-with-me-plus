package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for view statistics response.
 * <p>
 * Represents aggregated statistics for a specific app/URI combination.
 * Returned by the GET /stats endpoint with hit counts.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {

    /**
     * Identifier of the application.
     * Example: "ewm-main-service"
     */
    private String app;

    /**
     * URI path for which statistics are aggregated.
     * Example: "/events/1"
     */
    private String uri;

    /**
     * Number of hits (views) for this app/URI combination.
     * Can represent unique visitors or total hits depending on query params.
     */
    private Long hits;
}