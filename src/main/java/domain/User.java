package domain;

import java.time.Instant;

public record User(UserId id, String name, String email, Instant createdAt, Instant updatedAt) {

    public User(UserId id, String name, String email, Instant createdAt) {
        this(id, name, email, createdAt, createdAt);
    }

    public User {
        if (id == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("UpdatedAt cannot be null");
        }
    }
}
