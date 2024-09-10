package ru.practicum.stats.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.dto.model.HitDto;
import ru.practicum.stats.dto.model.StatsDto;
import ru.practicum.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.dto.model.Constant.DATE_TIME_PATTERN;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto hit(@Valid @RequestBody final HitDto hitDto) {
        log.info("POST request received to /hit endpoint");
        return statsService.hit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) final LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_TIME_PATTERN) final LocalDateTime end,
            @RequestParam(required = false) final List<String> uris,
            @RequestParam(required = false, defaultValue = "false") final Boolean unique) {
        log.info("GET request received to /stats endpoint with params: start:{}; end:{}; uris:{}; unique:{}",
                start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }
}
