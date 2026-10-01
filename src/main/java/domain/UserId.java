package domain;

import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Objects;
import java.util.UUID;

@RegisterForReflection
public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "UserId cannot be null");
    }

    public static UserId generate() {
        return new UserId(UuidCreator.getTimeOrderedEpoch());
    }

    public static UserId fromString(String uuid) {
        return new UserId(UUID.fromString(uuid));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
