package com.bobocode.mappers.users;

import com.bobocode.dto.users.UserRegistrationDto;
import com.bobocode.entities.users.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting user registration DTOs to user entities.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRegistrationMapper {

    /**
     * Converts a user registration DTO to a user entity.
     *
     * @param registrationDto the user registration DTO
     * @return the corresponding user entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", constant = "true")
    User toEntity(UserRegistrationDto registrationDto);
}
