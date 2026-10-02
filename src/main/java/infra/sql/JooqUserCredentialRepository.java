package infra.sql;

import static users.jooq.Tables.USER_CREDENTIALS;
import static users.jooq.Tables.USERS;

import java.util.Optional;

import domain.PasswordHash;
import domain.UserCredential;
import domain.UserCredentialRepository;
import domain.UserId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import users.jooq.tables.records.UserCredentialsRecord;

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

    @Override
    public Optional<UserCredential> findByEmail(String email) {
        return dsl.select(USER_CREDENTIALS.fields())
                .from(USER_CREDENTIALS)
                .join(USERS).on(USER_CREDENTIALS.USER_ID.eq(USERS.ID))
                .where(USERS.EMAIL.eq(email))
                .fetchOptional()
                .map(record -> deserialize(record.into(UserCredentialsRecord.class)));
    }

    private static UserCredential deserialize(UserCredentialsRecord record) {
        return new UserCredential(
                new UserId(record.getUserId()),
                PasswordHash.from(record.getPassword()));
    }
}
