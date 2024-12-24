/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import co.raccoons.example.Nothing;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static com.google.common.truth.Truth.assertThat;

@DisplayName("AbstractCodeGenerator")
class AbstractCodeGeneratorTest {

    @Test
    @DisplayName("adds extension")
    void addsExtension() {
        var generator =
                new AbstractCodeGenerator() {
                    @Override
                    protected File generate(ProtocolType type) {
                        return File.getDefaultInstance();
                    }
                };

        generator.register();
        ProtocolType.newBuilder().build().post();
        assertThat(generator.extensions()).hasSize(1);
    }

    @Test
    @DisplayName("adds only extension for messages")
    void acceptsAccordingPrecondition() {
        var generator =
                new AbstractCodeGenerator() {
                    @Override
                    protected Predicate<ProtocolType> precondition() {
                        return AbstractCodeGeneratorTest::hasMessageType;
                    }

                    @Override
                    protected File generate(ProtocolType type) {
                        return File.getDefaultInstance();
                    }
                };

        generator.register();
        ProtocolType.newBuilder().build().post();
        ProtocolType.newBuilder()
                .setMessageType(Nothing.getDescriptor().toProto())
                .build()
                .post();
        assertThat(generator.extensions()).hasSize(1);
    }

    private static boolean hasMessageType(ProtocolType protocolType) {
        return protocolType.hasMessageType();
    }
}
