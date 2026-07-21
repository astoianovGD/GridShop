package com.bobocode.Entities.Users;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Enums.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the system.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public final class User extends AbstractUser {

    /**
     * The age of the user.
     */
    private int age;

    /**
     * The gender of the user.
     */
    private Gender gender;

    /**
     * The user's current shopping bucket.
     */
    private Bucket bucket = new Bucket();

    /**
     * The history of buckets purchased by the user.
     */
    private List<Bucket> purchaseHistory = new ArrayList<>();

    /**
     * Returns a formatted string representation of the user.
     *
     * @return a string containing user details
     */
    @Override
    public String toString() {

        return String.format(
                "[User] ID: %d | Name: %s %s | Email: %s "
                        + "| Age: %d | Gender: %s",
                getId(),
                getFirstName(),
                getLastName(),
                getEmail(),
                age,
                gender
        );
    }
}
