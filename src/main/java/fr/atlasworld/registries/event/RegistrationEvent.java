package fr.atlasworld.registries.event;

import com.google.common.base.Preconditions;
import fr.atlasworld.event.api.Event;
import fr.atlasworld.registries.Registry;
import org.jetbrains.annotations.NotNull;

public class RegistrationEvent<T> implements Event {
    private final Registry<T> registry;

    protected RegistrationEvent(@NotNull Registry<T> registry) {
        Preconditions.checkNotNull(registry);

        this.registry = registry;
    }

    // Todo: do all the registering stuff.
}
