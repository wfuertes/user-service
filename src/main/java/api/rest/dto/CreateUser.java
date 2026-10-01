package api.rest.dto;

import domain.User;
import domain.UserId;
import java.time.Instant;

public record CreateUser(String name, String email, String password) {

    public User toUser(UserId userId, Instant instant) {
        return new User(userId, name, email, instant, instant);
    }
}
