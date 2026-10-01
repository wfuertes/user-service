package infra.sql;

import domain.UserCredential;
import domain.UserCredentialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import users.jooq.tables.records.UserCredentialsRecord;

import static users.jooq.Tables.USER_CREDENTIALS;

@ApplicationScoped
public class JooqUserCredentialRepository implements UserCredentialRepository {

    private final DSLContext dsl;

    @Inject
    JooqUserCredentialRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public void save(UserCredential credential) {
        dsl.insertInto(USER_CREDENTIALS).set(serialize(credential)).execute();
    }

    private static UserCredentialsRecord serialize(UserCredential credential) {
        var record = new UserCredentialsRecord();
        record.setUserId(credential.userId().value());
        record.setPassword(credential.passwordHash().toString());
        return record;
    }
}
