/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.ExtensionRegistry;

import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@code ExtensionRegistry} utility class.
 */
@Immutable
final class CustomOptionRegistry {

    private CustomOptionRegistry() {
    }

    /**
     * Obtains a new instance of {@code ExtensionRegistry} with custom options,
     * if any were registered by the consumer.
     */
    public static ExtensionRegistry newRegistry(Consumer<ExtensionRegistry> customizer) {
        checkNotNull(customizer);
        var registry = ExtensionRegistry.newInstance();
        customizer.accept(registry);
        return registry;
    }
}
