package ru.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity representing an endpoint hit record in the database.
 * <p>
 * Each record captures a single access event to an endpoint,
 * including the source application, accessed URI, client IP,
 * and the timestamp of the request.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@Entity
@Table(name = "hits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHit {

    /**
     * Unique identifier of the hit record (auto-generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifier of the application that recorded this hit.
     */
    @Column(name = "app", nullable = false)
    private String app;

    /**
     * URI path that was accessed.
     */
    @Column(name = "uri", nullable = false)
    private String uri;

    /**
     * IP address of the client who made the request.
     */
    @Column(name = "ip", nullable = false)
    private String ip;

    /**
     * Timestamp when the request occurred.
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}