package com.bobocode.dto.users;

import com.bobocode.enums.Gender;
import lombok.Data;

@Data
public class UserDto {
    private long id;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private int age;
    private Gender gender;
}
