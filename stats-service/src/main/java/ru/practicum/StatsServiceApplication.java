package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Statistics Service application.
 * <p>
 * This microservice is responsible for collecting and aggregating
 * endpoint hit statistics across the Explore With Me platform.
 * It provides REST API endpoints for recording hits and retrieving
 * view statistics with support for unique visitor filtering.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 * @see ru.practicum.controller.StatsController
 */
@SpringBootApplication
public class StatsServiceApplication {

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(StatsServiceApplication.class, args);
    }
}