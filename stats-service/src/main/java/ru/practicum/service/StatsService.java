package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for statistics business logic.
 * <p>
 * Handles the core operations for recording endpoint hits and
 * retrieving aggregated statistics. Uses transactional boundaries
 * to ensure data consistency.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    /**
     * Persists an endpoint hit record to the database.
     * <p>
     * Converts the incoming DTO to an entity and saves it.
     * This operation is transactional to ensure atomicity.
     * </p>
     *
     * @param endpointHitDto the hit data to persist
     */
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsMapper.toEntity(endpointHitDto);
        statsRepository.save(endpointHit);
    }

    /**
     * Retrieves aggregated view statistics for the specified criteria.
     * <p>
     * Queries the database for hit statistics within the given time range.
     * Supports filtering by specific URIs and counting either unique
     * visitors (distinct IPs) or total hits.
     * </p>
     *
     * @param start  start of the time range (inclusive)
     * @param end    end of the time range (inclusive)
     * @param uris   optional list of URIs to filter; null means all URIs
     * @param unique if true, count distinct IP addresses only
     * @return list of statistics DTOs sorted by hit count descending
     */
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, Boolean unique) {
        if (Boolean.TRUE.equals(unique)) {
            return statsRepository.findStatsUnique(start, end, uris);
        } else {
            return statsRepository.findStats(start, end, uris);
        }
    }
}