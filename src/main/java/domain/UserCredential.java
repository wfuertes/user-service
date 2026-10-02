package domain;

public record UserCredential(UserId userId, PasswordHash passwordHash) {

    public UserCredential {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        if (passwordHash == null) {
            throw new IllegalArgumentException("PasswordHash cannot be null");
        }
    }

    public boolean verifyPassword(String password, PasswordHasher hasher) {
        return hasher.verify(passwordHash, password);
    }
}
