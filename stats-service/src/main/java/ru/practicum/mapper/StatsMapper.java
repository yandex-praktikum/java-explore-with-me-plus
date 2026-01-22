package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

/**
 * MapStruct mapper for converting between EndpointHit entities and DTOs.
 * <p>
 * Provides bidirectional mapping with automatic field matching.
 * The ID field is ignored when converting to entity to allow
 * database-generated identifiers.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface StatsMapper {

    /**
     * Converts an EndpointHitDto to an EndpointHit entity.
     * <p>
     * The ID field is ignored to allow JPA to generate it.
     * </p>
     *
     * @param dto the DTO to convert
     * @return the corresponding entity
     */
    @Mapping(target = "id", ignore = true)
    EndpointHit toEntity(EndpointHitDto dto);

    /**
     * Converts an EndpointHit entity to an EndpointHitDto.
     *
     * @param entity the entity to convert
     * @return the corresponding DTO
     */
    EndpointHitDto toDto(EndpointHit entity);
}