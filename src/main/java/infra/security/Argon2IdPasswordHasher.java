package infra.security;

import domain.AppConfig;
import domain.PasswordHash;
import domain.PasswordHasher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

@ApplicationScoped
public class Argon2IdPasswordHasher implements PasswordHasher {
    private static final String ALGORITHM = "argon2id";
    private static final int HASH_LENGTH = 32;

    private final AppConfig.Argon2Config argon2Config;
    private final SecureRandom secureRandom;

    @Inject
    Argon2IdPasswordHasher(AppConfig appConfig) {
        this.argon2Config = appConfig.security().argon2();
        this.secureRandom = new SecureRandom();
    }

    @Override
    public PasswordHash hash(String password) {
        byte[] salt = generateSalt16Byte();

        var argon2Parameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(argon2Config.iterations())
                .withMemoryAsKB(argon2Config.memory())
                .withParallelism(argon2Config.parallelism())
                .withSalt(salt)
                .build();

        var generator = new Argon2BytesGenerator();
        generator.init(argon2Parameters);
        byte[] hash = new byte[HASH_LENGTH];

        generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), hash, 0, hash.length);

        return new PasswordHash(
                ALGORITHM,
                Argon2Parameters.ARGON2_VERSION_13,
                argon2Config.memory(),
                argon2Config.iterations(),
                argon2Config.parallelism(),
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(hash));
    }

    @Override
    public boolean verify(PasswordHash hash, String password) {
        var salt = Base64.getDecoder().decode(hash.salt());
        byte[] expectedHash = Base64.getDecoder().decode(hash.hash());

        var argon2Parameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(hash.v())
                .withIterations(hash.t())
                .withMemoryAsKB(hash.m())
                .withParallelism(hash.p())
                .withSalt(salt)
                .build();

        var verifier = new Argon2BytesGenerator();
        verifier.init(argon2Parameters);
        byte[] testHash = new byte[expectedHash.length];

        verifier.generateBytes(password.getBytes(StandardCharsets.UTF_8), testHash, 0, testHash.length);

        return MessageDigest.isEqual(testHash, expectedHash);
    }

    private byte[] generateSalt16Byte() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }
}
