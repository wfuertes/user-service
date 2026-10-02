package domain;

import java.util.Optional;

public interface UserCredentialRepository {

    void save(UserCredential userCredential);

    Optional<UserCredential> findByEmail(String email);
}
