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

    public static PasswordHash from(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }

        String[] parts = passwordHash.split("\\$");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid password hash format");
        }

        String alg = parts[1];
        String[] params = parts[2].split(",");
        int v = Integer.parseInt(params[0].substring(2));
        int m = Integer.parseInt(params[1].substring(2));
        int t = Integer.parseInt(params[2].substring(2));
        int p = Integer.parseInt(params[3].substring(2));
        String salt = parts[3];
        String hash = parts[4];

        return new PasswordHash(alg, v, m, t, p, salt, hash);
    }
}
