/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import co.raccoons.protoc.OptionsProto;
import com.google.common.testing.NullPointerTester;
import com.google.protobuf.ExtensionRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

@DisplayName("CustomOptionRegistry")
class CustomOptionRegistryTest {

    @Test
    @DisplayName("not accepts `null`")
    void throwsNullPointerException() {
        new NullPointerTester().testAllPublicStaticMethods(CustomOptionRegistry.class);
    }

    @Test
    @DisplayName("is instance of ExtensionRegistry")
    void returnsCorrectType() {
        var registry = CustomOptionRegistry.newRegistry(this::register);
        assertThat(registry).isInstanceOf(ExtensionRegistry.class);
    }

    @Test
    @DisplayName("has registered custom option")
    void hasRegisteredCustomOption() {
        var registry = CustomOptionRegistry.newRegistry(this::register);
        var customOption = registry.findImmutableExtensionByName("extra");
        assertThat(customOption.descriptor.getName()).isEqualTo("extra");
    }

    private void register(ExtensionRegistry registry) {
        OptionsProto.registerAllExtensions(registry);
    }
}
