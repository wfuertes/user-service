package infra.sql;

import static users.jooq.tables.Users.USERS;

import domain.User;
import domain.UserId;
import domain.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.ZoneOffset;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import users.jooq.tables.records.UsersRecord;

@ApplicationScoped
public class JooqUserRepository implements UserRepository {

    private final DSLContext dsl;

    @Inject
    JooqUserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public void save(User user) {
        UsersRecord record = serialize(user);
        dsl.executeInsert(record);
    }

    @Override
    public List<User> findAll(String email, int limit, int offset) {
        // 1. The inner subquery: It only selects the indexed ID column
        var deferred = dsl.select(USERS.ID)
                .from(USERS)
                .where(StringUtils.isBlank(email) ? DSL.noCondition() : USERS.EMAIL.containsIgnoreCase(email))
                .orderBy(USERS.ID.desc()) // Crucial: Orders the subquery so offset is predictable
                .limit(limit)
                .offset(offset)
                .asTable("deferred");

        // 2. The outer query: Joins the full table ONLY to the 10 fetched IDs
        List<User> users = dsl.select(USERS.ID, USERS.EMAIL, USERS.PASSWORD, USERS.CREATED_AT, USERS.UPDATED_AT)
                .from(USERS)
                .join(deferred)
                .on(USERS.ID.eq(deferred.field(USERS.ID)))
                .orderBy(deferred.field(USERS.ID).desc())
                .fetch()
                .map(record -> deserialize(record.into(UsersRecord.class)));
        return users;
    }

    private static User deserialize(UsersRecord record) {
        return new User(
                new UserId(record.getId()),
                record.getEmail(),
                record.getCreatedAt().toInstant(),
                record.getUpdatedAt().toInstant());
    }

    private static UsersRecord serialize(User user) {
        UsersRecord record = new UsersRecord();
        record.setId(user.id().value());
        record.setEmail(user.email());
        record.setCreatedAt(user.createdAt().atOffset(ZoneOffset.UTC));
        record.setUpdatedAt(user.updatedAt().atOffset(ZoneOffset.UTC));
        return record;
    }
}
