package domain;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "user-service")
public interface AppConfig {

    @WithName("security.argon2.memory")
    @WithDefault("65536")
    int argon2Memory();

    @WithName("security.argon2.iterations")
    @WithDefault("3")
    int argon2Iterations();

    @WithName("security.argon2.parallelism")
    @WithDefault("4")
    int argon2Parallelism();
}
