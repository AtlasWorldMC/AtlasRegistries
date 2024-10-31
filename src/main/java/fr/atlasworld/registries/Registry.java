package fr.atlasworld.registries;

import fr.atlasworld.registries.event.RegistrationEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The Registry holds all references to registered entries.
 * <p>
 * When initialized, the registry is not finalized,
 * and you are still able to register entries to it.
 * <br>
 * It's recommended that after your {@link RegistrationEvent} is called,
 * you finalize the registry using {@link #finalizeRegistry()}.
 * <br>
 * In multithreaded environments,
 * finalizing the registry allows multiple threads to read concurrently without added latency of writing to the registry.
 *
 * @param <T> type of values contained in the registry.
 */
public interface Registry<T> {

    /**
     * Retrieve the unique key of this registry.
     *
     * @return unique key of the registry.
     */
    @NotNull
    RegistryKey key();

    /**
     * Register a new value to this registry.
     *
     * @param key unique key of the value.
     * @param value the value.
     *
     * @throws IllegalArgumentException if a value was already registered with the specified key,
     *                                  or that the same instance of the value is already registered.
     * @throws IllegalStateException if the registry has been finalized.
     */
    void register(@NotNull RegistryKey key, @NotNull T value);

    /**
     * Close the registry, this will make the registry immutable.
     * <br>
     * You won't be able to register entries after this call,
     * and this will unlock threads that are waiting to read the registry.
     *
     * @throws IllegalStateException if the registry has already been finalized.
     */
    void finalizeRegistry();

    /**
     * Checks whether this registry finalized. (aka immutable)
     *
     * @return true if this registry has been finalized, false otherwise.
     */
    boolean finalized();

    /**
     * Checks whether this registry contains the specified key.
     *
     * @param key key to check for.
     *
     * @return true if this registry contains the key, false otherwise.
     */
    boolean containsKey(@NotNull RegistryKey key);

    /**
     * Checks whether this registry contains the specified value.
     *
     * @param value value to check for.
     *
     * @return true if this registry contains the key, false otherwise.
     */
    boolean containsValue(@NotNull T value);

    /**
     * Checks whether this registry is empty.
     *
     * @return true if this registry is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Retrieve the value from this registry.
     *
     * @param key key of the value.
     *
     * @return optional containing the value, empty optional if no value with the key is present.
     */
    Optional<T> retrieveValue(@NotNull RegistryKey key);

    /**
     * Retrieve the key from this registry.
     *
     * @param value value attached to the key to look for.
     *
     * @return optional containing the key, empty optional if no key could be found with the value.
     */
    Optional<RegistryKey> retrieveKey(@NotNull T value);

    /**
     * Retrieve all values in this registry.
     *
     * @return an <b>immutable</b> set of all values in this registry.
     */
    @NotNull
    Set<T> values();

    /**
     * Retrieve all keys in this registry.
     *
     * @return an <b>immutable</b> set of all keys in this registry.
     */
    @NotNull
    Set<RegistryKey> keys();

    /**
     * Retrieve all entries of this registry.
     *
     * @return an <b>immutable</b> set of all entries in this registry.
     */
    Set<Map.Entry<RegistryKey, T>> entries();
}
