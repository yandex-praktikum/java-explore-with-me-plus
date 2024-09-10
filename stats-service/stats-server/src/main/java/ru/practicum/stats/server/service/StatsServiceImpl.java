package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.model.HitDto;
import ru.practicum.stats.dto.model.StatsDto;
import ru.practicum.stats.server.model.App;
import ru.practicum.stats.server.model.Hit;
import ru.practicum.stats.server.reporitory.AppRepository;
import ru.practicum.stats.server.reporitory.StatsRepository;
import ru.practicum.stats.server.util.mapper.HitMapper;
import ru.practicum.stats.server.util.validator.DateValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.stats.server.util.validator.DateValidator.validateDateNotInFuture;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final AppRepository appRepository;

    @Override
    @Transactional
    public HitDto hit(final HitDto hitDto) {
        validateDateNotInFuture(hitDto.getTimestamp());
        String appName = hitDto.getApp();
        Optional<App> appOptional = appRepository.findAppByName(appName);
        App app = appOptional.orElseGet(() -> appRepository.save(App.builder().name(hitDto.getApp()).build()));
        Hit hit = HitMapper.toHit(hitDto, app);
        Hit savedHit = statsRepository.save(hit);
        return HitMapper.toHitDto(savedHit, app.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsDto> getStats(final LocalDateTime start, final LocalDateTime end,
                                   final List<String> uris, final Boolean unique) {
        DateValidator.validateStartAndEndDate(start, end);
        List<StatsDto> statsDtos;
        if (uris == null) {
            if (unique) {
                statsDtos = statsRepository.findAllStatsInPeriodOfTimeByUniqueIp(start, end);
            } else {
                statsDtos = statsRepository.findAllStatsInPeriodOfTime(start, end);
            }
        } else {
            if (unique) {
                statsDtos = statsRepository.findAllStatsInPeriodOfTimeByUniqueIpAndByUris(start, end, uris);
            } else {
                statsDtos = statsRepository.findAllStatsInPeriodOfTimeByUris(start, end, uris);
            }
        }
        return statsDtos;
    }
}
