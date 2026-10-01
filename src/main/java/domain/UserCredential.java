package domain;

public record UserCredential(UserId userId, PasswordHash passwordHash) {}
