package users;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jooq.impl.DSL.name;
import static users.jooq.Tables.USER_CREDENTIALS;
import static users.jooq.Tables.USERS;

import api.rest.dto.CreateUser;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import java.util.UUID;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

@QuarkusTest
@Isolated
class UserResourceTransactionTest {
    @Inject
    DSLContext dsl;

    @Test
    void rollsBackUserWhenCredentialInsertFails() {
        String email = "rollback-" + UUID.randomUUID() + "@example.test";
        int credentialCount = dsl.fetchCount(USER_CREDENTIALS);
        var rejectionConstraint = name("test_reject_user_credentials");

        // NOT VALID preserves existing rows but rejects every new credential during this test.
        dsl.execute(
                "ALTER TABLE {0} ADD CONSTRAINT {1} CHECK (false) NOT VALID", USER_CREDENTIALS, rejectionConstraint);

        try {
            given().contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateUser("Peter Pan", email, "secret-password"))
                    .when()
                    .post("/users")
                    .then()
                    .statusCode(409)
                    .body("code", is("INTEGRITY_CONSTRAINT_VIOLATION"));

            assertAll(
                    () -> assertEquals(
                            0,
                            dsl.fetchCount(USERS, USERS.EMAIL.eq(email)),
                            "The user must be rolled back when saving its credential fails"),
                    () -> assertEquals(
                            credentialCount,
                            dsl.fetchCount(USER_CREDENTIALS),
                            "No credential must be persisted after the failure"));

            given().queryParam("email", email)
                    .when()
                    .get("/users")
                    .then()
                    .statusCode(200)
                    .body("size()", is(0));
        } finally {
            dsl.execute("ALTER TABLE {0} DROP CONSTRAINT {1}", USER_CREDENTIALS, rejectionConstraint);
            dsl.deleteFrom(USERS).where(USERS.EMAIL.eq(email)).execute();
        }
    }
}