package domain;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "user-service")
public interface AppConfig {

    SecurityConfig security();

    interface SecurityConfig {
        Argon2Config argon2();
    }

    interface Argon2Config {
        @WithDefault("65536")
        int memory();

        @WithDefault("3")
        int iterations();

        @WithDefault("4")
        int parallelism();
    }
}
