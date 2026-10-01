package api.rest;

import api.rest.dto.CreateUser;
import domain.*;
import jakarta.inject.Inject;
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
    private final PasswordHasher hasher;
    private final UserRepository repository;
    private final UserCredentialRepository credentialRepository;

    @Inject
    UserResource(PasswordHasher hasher, UserRepository repository, UserCredentialRepository credentialRepository
    ) {
        this.hasher = hasher;
        this.repository = repository;
        this.credentialRepository = credentialRepository;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(CreateUser createUser) {
        User user = createUser.toUser(UserId.generate(), Instant.now());
        repository.save(user);

        PasswordHash passwordHash = hasher.hash(createUser.password());
        credentialRepository.save(new UserCredential(user.id(), passwordHash));

        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(
            @QueryParam("email") String email,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset) {

        List<User> users = repository.findAll(email, limit, offset);

        return Response.ok(users).build();
    }
}
