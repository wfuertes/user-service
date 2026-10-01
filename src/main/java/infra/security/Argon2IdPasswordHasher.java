package infra.security;

import domain.AppConfig;
import domain.PasswordHash;
import domain.PasswordHasher;
import jakarta.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

public class Argon2IdPasswordHasher implements PasswordHasher {
    private static final String ALGORITHM = "argon2id";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int HASH_LENGTH = 32;

    private final AppConfig appConfig;

    @Inject
    Argon2IdPasswordHasher(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public PasswordHash hash(String password) {
        byte[] salt = generateSalt16Byte();

        var argon2Parameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(appConfig.argon2Iterations())
                .withMemoryAsKB(appConfig.argon2Memory())
                .withParallelism(appConfig.argon2Parallelism())
                .withSalt(salt)
                .build();

        var generator = new Argon2BytesGenerator();
        generator.init(argon2Parameters);
        byte[] hash = new byte[HASH_LENGTH];

        generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), hash, 0, hash.length);

        return new PasswordHash(
                ALGORITHM,
                Argon2Parameters.ARGON2_VERSION_13,
                appConfig.argon2Memory(),
                appConfig.argon2Iterations(),
                appConfig.argon2Parallelism(),
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
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }
}
