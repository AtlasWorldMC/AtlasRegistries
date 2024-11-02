package fr.atlasworld.registry;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Registry Key, those are unique key composed of a {@code namespace} and a {@code key}.
 * <p>
 * A full registry key cannot exceed 32767 characters,
 * this is imposed by the minecraft protocol when sending those key to clients.
 */
public final class RegistryKey {
    private final String namespace;
    private final String key;

    public RegistryKey(@NotNull String namespace, @NotNull String key) {
        Preconditions.checkNotNull(namespace, "Namespace may not be null!");
        Preconditions.checkNotNull(key, "Key must not be null!");

        Preconditions.checkArgument(isValidNamespace(namespace), "Invalid namespace, must be [a-z0-9._-]: %s", namespace);
        Preconditions.checkArgument(isValidKey(key), "Invalid key, must be [a-z0-9/._-]: %s", namespace);

        this.namespace = namespace;
        this.key = key;

        String fullKey = this.toString();
        Preconditions.checkArgument(fullKey.length() <= Short.MAX_VALUE, "Namespace must be less than 32768 characters.");
    }

    /**
     * Retrieve the namespace of the key.
     *
     * @return namespace of the key.
     */
    @NotNull
    public String namespace() {
        return this.namespace;
    }

    /**
     * Retrieve the key of the key.
     *
     * @return key of the key.
     */
    @NotNull
    public String key() {
        return this.key;
    }

    @Override
    public int hashCode() {
        int result = this.namespace.hashCode();
        result = (31 * result) + this.key.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RegistryKey other))
            return false;

        return this.key.equals(other.key) && this.namespace.equals(other.namespace);
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.key;
    }

    /**
     * Parse a RegistryKey from a string.
     *
     * @param str string to parse the registry key from.
     *
     * @return optional containing the registry key is the str could be parsed, empty optional if otherwise.
     * @see #fromString(String, String) 
     */
    public static Optional<RegistryKey> fromString(@NotNull String str) {
        return fromString(str, null);
    }

    /**
     * Parse a RegistryKey from a string.
     *
     * @param str string to parse the registry key from.
     * @param defaultNamespace default namespace in-case that the str does not contain a namespace,
     *                         in that case this namespace will be applied.
     *
     * @return optional containing the registry key is the str could be parsed, empty optional if otherwise.
     */
    @NotNull
    public static Optional<RegistryKey> fromString(@NotNull String str, @Nullable String defaultNamespace) {
        Preconditions.checkNotNull(str);
        if (str.isEmpty() || str.length() > Short.MAX_VALUE)
            return Optional.empty();

        String[] parts = str.split(":", 3);
        if (parts.length > 2)
            return Optional.empty();

        String namespace = parts.length == 2 ? parts[0] : defaultNamespace;
        if (!isValidNamespace(namespace))
            return Optional.empty();

        String key = parts.length == 2 ? parts[1] : parts[0];
        if (!isValidKey(key))
            return Optional.empty();

        return Optional.of(new RegistryKey(namespace, key));
    }

    /**
     * Checks if the character is valid for a namespace.
     *
     * @param c character to check.
     * @return true if the namespace char is valid, false otherwise.
     */
    private static boolean isValidNamespaceChar(char c) {
        return switch (c) {
            case '.', '_', '-' -> true;
            default -> (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
        };
    }

    /**
     * Checks if the character is valid for a key.
     *
     * @param c character to check.
     *
     * @return true if the key char is valid, false otherwise.
     */
    private static boolean isValidKeyChar(char c) {
        return c == '/' || isValidNamespaceChar(c);
    }

    /**
     * Checks whether a namespace is valid.
     *
     * @param namespace namespace to check.
     *
     * @return true if the namespace is valid, false otherwise.
     */
    public static boolean isValidNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty())
            return false;

        for (int i = 0; i < namespace.length(); i++) {
            if (!isValidNamespaceChar(namespace.charAt(i)))
                return false;
        }

        return true;
    }

    /**
     * Checks whether a key is valid.
     *
     * @param key key to check.
     *
     * @return true if the key is valid, false otherwise.
     */
    public static boolean isValidKey(String key) {
        if (key == null || key.isEmpty())
            return false;

        for (int i = 0; i < key.length(); i++) {
            if (!isValidKeyChar(key.charAt(i)))
                return false;
        }

        return true;
    }
}
