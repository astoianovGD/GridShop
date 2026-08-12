package com.bobocode.dto.users;

import com.bobocode.enums.Gender;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private int age;
    private Gender gender;
}