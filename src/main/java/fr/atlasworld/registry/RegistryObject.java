package fr.atlasworld.registry;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Holds the instance of a registered object.
 *
 * @param <T> type of values hold in the registry.
 */
@ThreadSafe
public class RegistryObject<T> {
    private final RegistryKey key;
    private volatile T value;

    public RegistryObject(@NotNull RegistryKey key) {
        Preconditions.checkNotNull(key);

        this.key = key;
    }

    /**
     * Update the reference of the object.
     *
     * @param registry registry to update from.
     *
     * @throws IllegalStateException if the registry object already holds the reference.
     * @throws IllegalArgumentException if the registry does not contain a reference to this object.
     */
    public synchronized void updateReference(@NotNull Registry<T> registry) {
        Preconditions.checkNotNull(registry);
        Preconditions.checkArgument(this.value == null, "Reference has already been updated!");

        this.value = registry.retrieveValue(this.key).orElseThrow(() -> new IllegalArgumentException("Registry does not contain any reference to the object."));
    }

    /**
     * Verify if the reference is present.
     *
     * @return true if the reference is present, false otherwise.
     */
    public boolean referencePresent() {
        return this.value != null;
    }

    /**
     * Retrieve the reference of the object.
     *
     * @return reference of the object.
     * @throws NoSuchElementException if this registry object does not contain the reference to the object.
     */
    @NotNull
    public T get() {
        if (this.value == null)
            throw new NoSuchElementException("Registry reference is not present!");

        return this.value;
    }

    /**
     * Retrieve the key of the object.
     *
     * @return key of the object.
     */
    @NotNull
    public RegistryKey key() {
        return this.key;
    }

    /**
     * Retrieve the reference of this object as an optional.
     *
     * @return optional containing the reference of this object.
     */
    public Optional<T> asOptional() {
        return Optional.ofNullable(this.value);
    }
}
