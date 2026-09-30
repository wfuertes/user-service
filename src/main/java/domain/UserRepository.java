package domain;

import java.util.List;

public interface UserRepository {

    void save(User user);

    List<User> findAll(String email, int limit, int offset);
}
