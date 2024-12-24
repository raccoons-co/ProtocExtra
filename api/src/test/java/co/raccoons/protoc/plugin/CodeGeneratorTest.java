/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import com.google.common.testing.NullPointerTester;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

@DisplayName("Multipart Code Generator")
class CodeGeneratorTest {

    @Test
    @DisplayName("not accepts `null`")
    void throwsExceptionOnNull() {
        var generator = CodeGenerator.newBuilder().build();
        new NullPointerTester().testAllPublicInstanceMethods(generator);
    }

    @Test
    @DisplayName("adds concrete code generator")
    void addsCodeGenerator() {
        var generator = CodeGenerator.newBuilder()
                .add(generatorMock)
                .build();
        assertThat(generator.generators()).hasSize(1);
    }

    private final AbstractCodeGenerator generatorMock =
            new AbstractCodeGenerator() {
                @Override
                protected File generate(ProtocolType type) {
                    return File.getDefaultInstance();
                }
            };
}
