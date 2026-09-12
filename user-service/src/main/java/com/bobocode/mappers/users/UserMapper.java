package com.bobocode.mappers.users;

import com.bobocode.dto.users.UserDto;
import com.bobocode.entities.users.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between user DTOs and user entities.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Converts a user DTO to a user entity.
     *
     * @param dto the user DTO
     * @return the corresponding user entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "role", ignore = true)
    User toEntity(UserDto dto);

    /**
     * Converts a user entity to a user DTO.
     *
     * @param user the user entity
     * @return the corresponding user DTO
     */
    @Mapping(target = "password", ignore = true)
    UserDto toDto(User user);
}
