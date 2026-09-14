package com.bobocode.mappers.users;

import com.bobocode.dto.users.StaffDto;
import com.bobocode.entities.users.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between staff DTOs and user entities.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StaffMapper {

    /**
     * Converts a staff DTO to a user entity.
     *
     * @param staffDto the staff DTO
     * @return the corresponding user entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "age", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "role", ignore = true)
    User toEntity(StaffDto staffDto);

    /**
     * Converts a user entity to a staff DTO.
     *
     * @param user the user entity
     * @return the corresponding staff DTO
     */
    @Mapping(target = "firstname", source = "firstname")
    @Mapping(target = "lastname", source = "lastname")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", ignore = true)
    StaffDto toDto(User user);
}
