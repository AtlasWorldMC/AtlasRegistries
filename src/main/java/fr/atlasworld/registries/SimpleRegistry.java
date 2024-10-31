package fr.atlasworld.registries;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Simple implementation of a registry.
 *
 * @param <T> type of values contained in the registry.
 */
@ThreadSafe
public class SimpleRegistry<T> implements Registry<T> {
    protected final RegistryKey key;
    protected final BiMap<RegistryKey, T> entries;
    protected final AtomicBoolean finalized;
    protected final ReentrantReadWriteLock lock;

    public SimpleRegistry(@NotNull RegistryKey key) {
        this(key, HashBiMap.create());
    }

    protected SimpleRegistry(@NotNull RegistryKey key, @NotNull BiMap<RegistryKey, T> map) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(map);

        this.key = key;
        this.entries = map;
        this.finalized = new AtomicBoolean(false);
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public @NotNull RegistryKey key() {
        return this.key;
    }

    @Override
    public void register(@NotNull RegistryKey key, @NotNull T value) {
        if (this.finalized.get())
            throw new IllegalStateException("Cannot register entries when registry is finalized!");

        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);

        this.lock.writeLock().lock();
        try {
            Preconditions.checkArgument(!this.entries.containsKey(key), "An entry is already registered with this key: %s", key);
            Preconditions.checkArgument(!this.entries.containsValue(value), "This value has already been registered.");

            this.entries.put(key, value);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void finalizeRegistry() {
        if (this.finalized.get())
            throw new IllegalStateException("Registry has already been finalized.");

        this.finalized.set(true);
    }

    @Override
    public final boolean finalized() {
        return this.finalized.get();
    }

    @Override
    public boolean containsKey(@NotNull RegistryKey key) {
        return this.executeRead(() -> this.entries.containsKey(key));
    }

    @Override
    public boolean containsValue(@NotNull T value) {
        return this.executeRead(() -> this.entries.containsValue(value));
    }

    @Override
    public boolean isEmpty() {
        return this.executeRead(this.entries::isEmpty);
    }

    @Override
    public Optional<T> retrieveValue(@NotNull RegistryKey key) {
        return this.executeRead(() -> Optional.ofNullable(this.entries.get(key)));
    }

    @Override
    public Optional<RegistryKey> retrieveKey(@NotNull T value) {
        return this.executeRead(() -> Optional.ofNullable(this.entries.inverse().get(value)));
    }

    @Override
    public @NotNull Set<T> values() {
        return this.executeRead(this.entries::values);
    }

    @Override
    public @NotNull Set<RegistryKey> keys() {
        return this.executeRead(this.entries::keySet);
    }

    @Override
    public Set<Map.Entry<RegistryKey, T>> entries() {
        return this.executeRead(this.entries::entrySet);
    }

    protected final <E> E executeRead(Supplier<E> supplier) {
        this.lock.readLock().lock();
        try {
            return supplier.get();
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
