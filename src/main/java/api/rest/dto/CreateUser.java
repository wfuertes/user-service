package api.rest.dto;

import domain.User;
import domain.UserId;
import java.time.Instant;

public record CreateUser(String email, String password) {

    public User toUser(UserId userId, Instant instant) {
        return new User(userId, email, instant, instant);
    }
}
