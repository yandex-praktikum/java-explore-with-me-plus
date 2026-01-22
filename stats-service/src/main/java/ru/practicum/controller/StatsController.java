package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for managing endpoint hit statistics.
 * <p>
 * Provides endpoints for:
 * <ul>
 *   <li>Recording endpoint hits from various services</li>
 *   <li>Retrieving aggregated view statistics with filtering options</li>
 * </ul>
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * Records an endpoint hit event.
     * <p>
     * Saves information about a request made to a specific URI of a service.
     * The hit data includes the application name, URI, client IP address,
     * and timestamp of the request.
     * </p>
     *
     * @param endpointHitDto the hit data to record (validated)
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("POST /hit: app={}, uri={}, ip={}",
                endpointHitDto.getApp(), endpointHitDto.getUri(), endpointHitDto.getIp());
        statsService.saveHit(endpointHitDto);
    }

    /**
     * Retrieves aggregated view statistics for endpoints.
     * <p>
     * Returns statistics for the specified time range, optionally filtered
     * by URI list. Supports counting unique visitors (by IP) or total hits.
     * Results are sorted by hit count in descending order.
     * </p>
     *
     * @param start  start of the time range (inclusive), format: yyyy-MM-dd HH:mm:ss
     * @param end    end of the time range (inclusive), format: yyyy-MM-dd HH:mm:ss
     * @param uris   optional list of URIs to filter statistics
     * @param unique if true, count only unique IP addresses; otherwise count all hits
     * @return list of view statistics DTOs sorted by hits descending
     * @throws IllegalArgumentException if start date is after end date
     */
    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {

        log.info("GET /stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        return statsService.getStats(start, end, uris, unique);
    }
}