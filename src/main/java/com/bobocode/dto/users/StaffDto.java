package com.bobocode.dto.users;

import lombok.Data;

@Data
public class StaffDto {
    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

}
