/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin.core;

import co.raccoons.example.Another;
import co.raccoons.example.MultipleFalseProto;
import co.raccoons.example.MultipleTrue;
import co.raccoons.example.Nothing;
import co.raccoons.example.TopLevelEnum;
import co.raccoons.example.UserInfo;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import raccoons.example.NoJavaPackage;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ProtocolFile")
class ProtocolFileTest {

    @ParameterizedTest
    @DisplayName("converts file name to Pascal case")
    @ValueSource(strings = {
            "nothing_else.proto",
            "nothing_else_.proto",
            "_nothing_else.proto",
            "_nothing_else_.proto"
    })
    void convertsSnakeToPascalCase(String sample) {
        assertEquals("NothingElse", JavaName.SimpleName.fromProtoName(sample));
    }

    @Test
    @DisplayName("converts file name to Pascal case")
    void convertsSnakeToPascalCase() {
        var sample = "evenTs__aS.proto";
        assertEquals("EvenTsAS", JavaName.SimpleName.fromProtoName(sample));
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("generates derived `outerClass` file name")
    void outerClassFileName(String expected, Descriptor messageType) {
        var protocolFile = ProtocolFile.of(messageType.getFile());
        assertEquals(expected, protocolFile.outerClassFileName());
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("generates `orBuilder` file name")
    void orBuilderFileName(String expected, Descriptor messageType) {
        var protocolFile = ProtocolFile.of(messageType.getFile());
        assertEquals(expected, protocolFile.orBuilderFileName(messageType));
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("generates `message` file name")
    void messageFileName(String expected, Descriptor messageType) {
        var protocolFile = ProtocolFile.of(messageType.getFile());
        assertEquals(expected, protocolFile.messageFileName(messageType));
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("generates `enum` file name")
    void enumFileName(String expected, EnumDescriptor enumType) {
        var protocolFile = ProtocolFile.of(enumType.getFile());
        assertEquals(expected, protocolFile.enumFileName(enumType));
    }

    private static Stream<Arguments> outerClassFileName() {
        return Stream.of(
                Arguments.of("co/raccoons/example/NothingOuterClass.java", Nothing.getDescriptor()),
                Arguments.of("co/raccoons/example/UserInfoSample.java", UserInfo.getDescriptor()),
                Arguments.of("co/raccoons/example/MultipleTrueOuterClass.java", MultipleTrue.getDescriptor()),
                Arguments.of("co/raccoons/example/MultipleFalseProto.java", MultipleFalseProto.MultipleFalse.getDescriptor()),
                Arguments.of("raccoons/example/NoJavaPackageProto.java", NoJavaPackage.getDescriptor())
        );
    }

    private static Stream<Arguments> orBuilderFileName() {
        return Stream.of(
                Arguments.of("co/raccoons/example/UserInfoOrBuilder.java", UserInfo.getDescriptor()),
                Arguments.of("co/raccoons/example/NothingOrBuilder.java", Nothing.getDescriptor()),
                Arguments.of("co/raccoons/example/Nothing.java", Nothing.Else.getDescriptor()),
                Arguments.of("co/raccoons/example/Nothing.java", Nothing.Else.Matters.getDescriptor()),
                Arguments.of("co/raccoons/example/MultipleTrueOrBuilder.java", MultipleTrue.getDescriptor()),
                Arguments.of("co/raccoons/example/MultipleFalseProto.java", MultipleFalseProto.MultipleFalse.getDescriptor()),
                Arguments.of("raccoons/example/NoJavaPackageOrBuilder.java", NoJavaPackage.getDescriptor())
        );
    }

    private static Stream<Arguments> messageFileName() {
        return Stream.of(
                Arguments.of("co/raccoons/example/Nothing.java", Nothing.getDescriptor()),
                Arguments.of("co/raccoons/example/Nothing.java", Nothing.Else.getDescriptor()),
                Arguments.of("co/raccoons/example/Nothing.java", Nothing.Else.Matters.getDescriptor()),
                Arguments.of("co/raccoons/example/Another.java", Another.getDescriptor()),
                Arguments.of("co/raccoons/example/MultipleTrue.java", MultipleTrue.getDescriptor()),
                Arguments.of("co/raccoons/example/MultipleFalseProto.java", MultipleFalseProto.MultipleFalse.getDescriptor()),
                Arguments.of("raccoons/example/NoJavaPackage.java", NoJavaPackage.getDescriptor())
        );
    }

    private static Stream<Arguments> enumFileName() {
        return Stream.of(
                Arguments.of("co/raccoons/example/TopLevelEnum.java", TopLevelEnum.getDescriptor()),
                Arguments.of("co/raccoons/example/Nothing.java", Nothing.NothingEnum.getDescriptor()),
                Arguments.of("co/raccoons/example/Nothing.java", Nothing.Else.ElseEnum.getDescriptor()),
                Arguments.of("co/raccoons/example/Nothing.java", Nothing.Else.Matters.MattersEnum.getDescriptor())
        );
    }

}