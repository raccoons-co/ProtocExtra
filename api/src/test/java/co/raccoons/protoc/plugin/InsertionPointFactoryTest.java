/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

@DisplayName("InsertionPoint")
class InsertionPointFactoryTest {

    @ParameterizedTest
    @MethodSource("testCases")
    @DisplayName("has correct identifier")
    void hasCorrectFormat(String expected, InsertionPointFactory insertionPointFactory) {
        var type = protobufType();
        var insertionPoint = insertionPointFactory.newInsertionPoint(type);
        assertThat(insertionPoint.getIdentifier()).isEqualTo(expected);
    }

    private ProtocolType protobufType() {
        return ProtocolType.newBuilder()
                .setName("raccoons.protoc.Nothing")
                .build();
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of("message_implements:raccoons.protoc.Nothing", InsertionPointFactory.message_implements),
                Arguments.of("builder_implements:raccoons.protoc.Nothing", InsertionPointFactory.builder_implements),
                Arguments.of("interface_extends:raccoons.protoc.Nothing", InsertionPointFactory.interface_extends),
                Arguments.of("class_scope:raccoons.protoc.Nothing", InsertionPointFactory.class_scope),
                Arguments.of("builder_scope:raccoons.protoc.Nothing", InsertionPointFactory.builder_scope),
                Arguments.of("enum_scope:raccoons.protoc.Nothing", InsertionPointFactory.enum_scope),
                Arguments.of("outer_class_scope", InsertionPointFactory.outer_class_scope)
        );
    }
}
