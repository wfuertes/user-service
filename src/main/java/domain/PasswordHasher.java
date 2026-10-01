package domain;

public interface PasswordHasher {

    PasswordHash hash(String password);

    boolean verify(PasswordHash hash, String password);
}
