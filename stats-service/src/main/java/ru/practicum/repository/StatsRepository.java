package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for {@link EndpointHit} entity operations.
 * <p>
 * Provides data access methods for storing endpoint hits and
 * retrieving aggregated statistics. Uses custom JPQL queries
 * for efficient aggregation directly in the database.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    /**
     * Retrieves view statistics with unique visitor count (distinct IPs).
     * <p>
     * Aggregates hits by app and URI, counting only distinct IP addresses.
     * Results are filtered by time range and optionally by URI list.
     * Sorted by hit count in descending order.
     * </p>
     *
     * @param start start of the time range (inclusive)
     * @param end   end of the time range (inclusive)
     * @param uris  optional list of URIs to filter; null includes all URIs
     * @return list of statistics with unique visitor counts
     */
    @Query("SELECT new ru.practicum.dto.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
           "FROM EndpointHit e " +
           "WHERE e.timestamp BETWEEN :start AND :end " +
           "AND (:uris IS NULL OR e.uri IN :uris) " +
           "GROUP BY e.app, e.uri " +
           "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStatsDto> findStatsUnique(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("uris") List<String> uris);

    /**
     * Retrieves view statistics with total hit count (all requests).
     * <p>
     * Aggregates hits by app and URI, counting all IP addresses
     * including duplicates. Results are filtered by time range
     * and optionally by URI list. Sorted by hit count in descending order.
     * </p>
     *
     * @param start start of the time range (inclusive)
     * @param end   end of the time range (inclusive)
     * @param uris  optional list of URIs to filter; null includes all URIs
     * @return list of statistics with total hit counts
     */
    @Query("SELECT new ru.practicum.dto.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
           "FROM EndpointHit e " +
           "WHERE e.timestamp BETWEEN :start AND :end " +
           "AND (:uris IS NULL OR e.uri IN :uris) " +
           "GROUP BY e.app, e.uri " +
           "ORDER BY COUNT(e.ip) DESC")
    List<ViewStatsDto> findStats(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("uris") List<String> uris);
}