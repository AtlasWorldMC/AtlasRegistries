package fr.atlasworld.registry;

import com.google.common.base.Preconditions;
import fr.atlasworld.registry.event.RegistrationEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Register, handles the registering of all provided entries.
 *
 * @param <T> type of values to be registered.
 */
public final class Register<T> {
    private final String namespace;

    private final Map<RegistryKey, Supplier<T>> registeredValues;
    private final List<RegistryObject<T>> objects;

    private boolean registered = false;

    public Register(@NotNull String namespace) {
        Preconditions.checkArgument(RegistryKey.isValidNamespace(namespace), "Invalid namespace, must be [a-z0-9._-]: %s", namespace);

        this.namespace = namespace;

        this.registeredValues = new HashMap<>();
        this.objects = new ArrayList<>();
    }

    /**
     * Registers an element to the register.
     *
     * @param name name of the element, must also be a valid key for a {@link RegistryKey}.
     * @param constructor supplier that will supply the object when the register is called for registering.
     *
     * @return registry object for the registered object,
     *         reference will only be provided when the register is called for registering.
     *
     * @throws IllegalArgumentException if the name is not a valid key for a {@link RegistryKey}.
     * @throws IllegalStateException if an element with the provided name has already been registered before.
     */
    public RegistryObject<T> register(@NotNull String name, @NotNull Supplier<T> constructor) {
        Preconditions.checkArgument(!this.registered, "Entries cannot be added after the register has been registered!");

        Preconditions.checkArgument(RegistryKey.isValidKey(name), "Invalid name, must be [a-z0-9/._-]: %s", name);
        Preconditions.checkNotNull(constructor);

        RegistryKey key = new RegistryKey(this.namespace, name);
        if (this.registeredValues.containsKey(key))
            throw new IllegalStateException("Element with this name has already been registered.");

        RegistryObject<T> holder = new RegistryObject<>(key);

        this.registeredValues.put(key, constructor);
        this.objects.add(holder);

        return holder;
    }

    /**
     * Register the elements of this register to the provided registry.
     *
     * @param registry registry to register the elements to.
     *
     * @throws IllegalStateException if the register has already been registered.
     */
    public void register(@NotNull Registry<T> registry) {
        Preconditions.checkNotNull(registry);
        Preconditions.checkArgument(!this.registered, "Register ahs already seen the registry!");

        this.registered = true;
        this.registeredValues.forEach((key, sup) -> {
            T value = sup.get();
            registry.register(key, value);
        });

        this.objects.forEach(holder -> holder.updateReference(registry));
    }

    /**
     * Register the elements of this register to the provided registry.
     *
     * @param event event containing the registry.
     *
     * @throws IllegalStateException if the register has already been registered.
     */
    public void register(@NotNull RegistrationEvent<T> event) {
        Preconditions.checkNotNull(event);
        event.register(this);
    }
}
