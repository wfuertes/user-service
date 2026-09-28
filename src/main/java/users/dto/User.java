package users.dto;

import java.time.Instant;

public record User(
                String id,
                String email,
                Instant createdAt,
                Instant updatedAt) {

}
