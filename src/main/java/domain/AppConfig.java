package domain;

import io.smallrye.config.ConfigMapping;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigMapping(prefix = "user-service")
public interface AppConfig {

    @ConfigProperty(name = "security.argon2.memory", defaultValue = "65536")
    int argon2Memory();

    @ConfigProperty(name = "security.argon2.iterations", defaultValue = "3")
    int argon2Iterations();

    @ConfigProperty(name = "security.argon2.parallelism", defaultValue = "4")
    int argon2Parallelism();
}
