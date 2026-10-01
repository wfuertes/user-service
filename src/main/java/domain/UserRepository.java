package domain;

import java.util.List;

public interface UserRepository {

    void save(User user);

    void saveWithPasswordHash(User user, PasswordHash passwordHash);

    List<User> findAll(String email, int limit, int offset);
}
