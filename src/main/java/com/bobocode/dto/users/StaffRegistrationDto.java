package com.bobocode.dto.users;

import lombok.Data;

@Data
public class StaffRegistrationDto {
    private String email;
    private String password;
    private String firstname;
    private String lastname;
}