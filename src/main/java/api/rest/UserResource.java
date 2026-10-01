package api.rest;

import api.rest.dto.CreateUser;
import domain.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

@Path("/users")
public class UserResource {
    private final PasswordHasher passwordHasher;
    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;

    @Inject
    UserResource(PasswordHasher passwordHasher, UserRepository userRepository, UserCredentialRepository credentialRepository) {
        this.passwordHasher = passwordHasher;
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
    }

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(CreateUser createUser) {
        User user = createUser.toUser(UserId.generate(), Instant.now());
        userRepository.save(user);

        PasswordHash passwordHash = passwordHasher.hash(createUser.password());
        credentialRepository.save(new UserCredential(user.id(), passwordHash));

        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(
            @QueryParam("email") String email,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset) {

        List<User> users = userRepository.findAll(email, limit, offset);

        return Response.ok(users).build();
    }
}
