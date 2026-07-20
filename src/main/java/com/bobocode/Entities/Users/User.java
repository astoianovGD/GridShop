package com.bobocode.Entities.Users;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Enums.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends AbstractUser {
    private int age;
    private Gender gender;
    private Bucket bucket = new Bucket();
    private List<Bucket> purchaseHistory = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("[User] ID: %d | Name: %s %s | Email: %s | Age: %d | Gender: %s",
                getId(),
                getFirstName(),
                getLastName(),
                getEmail(),
                age,
                gender);
    }
}
