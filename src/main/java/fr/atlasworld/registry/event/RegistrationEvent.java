package fr.atlasworld.registry.event;

import com.google.common.base.Preconditions;
import fr.atlasworld.event.api.Event;
import fr.atlasworld.registry.Register;
import fr.atlasworld.registry.Registry;
import fr.atlasworld.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;

public class RegistrationEvent<T> implements Event {
    private final Registry<T> registry;

    protected RegistrationEvent(@NotNull Registry<T> registry) {
        Preconditions.checkNotNull(registry);

        this.registry = registry;
    }

    /**
     * Register an element directly to the registry.
     * <p>
     * This way is not most recommended, the usage of a {@link Register} is recommended.
     *
     * @param key key to register the value with.
     * @param value value to register.
     */
    public void register(@NotNull RegistryKey key, @NotNull T value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);

        this.registry.register(key, value);
    }

    /**
     * Register a register to the registry.
     *
     * @param register register to register.
     */
    public void register(@NotNull Register<T> register) {
        Preconditions.checkNotNull(register);
        register.register(this.registry);
    }
}
