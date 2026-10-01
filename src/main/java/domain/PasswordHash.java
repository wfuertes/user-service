package domain;

public record PasswordHash(String alg, int v, int m, int t, int p, String salt, String hash) {

    public PasswordHash {
        if (alg == null || alg.isBlank()) {
            throw new IllegalArgumentException("Algorithm (alg) cannot be null or blank");
        }

        if (!alg.equals("argon2id")) {
            throw new IllegalArgumentException("Unsupported algorithm: " + alg);
        }

        if (v <= 0) {
            throw new IllegalArgumentException("Version (v) must be greater than 0");
        }
        if (m <= 0) {
            throw new IllegalArgumentException("Memory cost (m) must be greater than 0");
        }
        if (t <= 0) {
            throw new IllegalArgumentException("Time cost/iterations (t) must be greater than 0");
        }
        if (p <= 0) {
            throw new IllegalArgumentException("Parallelism degree (p) must be greater than 0");
        }
        if (salt == null || salt.isBlank()) {
            throw new IllegalArgumentException("Salt cannot be null or blank");
        }
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Hash cannot be null or blank");
        }
    }

    @Override
    public String toString() {
        return "$%s$v=%d$m=%d,t=%d,p=%d$%s$%s".formatted(alg, v, m, t, p, salt, hash);
    }
}
