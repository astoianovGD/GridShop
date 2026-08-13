package com.bobocode.mappers.users;

import com.bobocode.dto.users.StaffRegistrationDto;
import com.bobocode.entities.users.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting staff registration DTOs to user entities.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StaffRegistrationMapper {

    /**
     * Converts a staff registration DTO to a user entity.
     *
     * @param registrationDto the staff registration DTO
     * @return the corresponding user entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "active", constant = "true")
    User toEntity(StaffRegistrationDto registrationDto);
}
