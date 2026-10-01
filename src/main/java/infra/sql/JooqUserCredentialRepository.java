package infra.sql;

import domain.UserCredential;
import domain.UserCredentialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;

@ApplicationScoped
public class JooqUserCredentialRepository implements UserCredentialRepository {

    private final DSLContext dsl;

    @Inject
    JooqUserCredentialRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public void save(UserCredential userCredential) {}
}
