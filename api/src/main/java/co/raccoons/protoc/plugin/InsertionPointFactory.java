/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import static java.lang.String.format;

/**
 * Represents extension of the code generator output.
 *
 * @see <a href="https://protobuf.dev/reference/java/java-generated/#plugins">
 * Plugin Insertion Points</a>
 */
@SuppressWarnings("java:S115") // Constant naming convention
public enum InsertionPointFactory {

    /**
     * Extra interface of the Message type class.
     */
    message_implements,

    /**
     * Extra interface of the Builder class.
     */
    builder_implements,

    /**
     * Extra code of the Message type class.
     */
    class_scope,

    /**
     * Extra code of Builder class.
     */
    builder_scope,

    /**
     * Extra code of Enum.
     */
    enum_scope,

    /**
     * Extra interface of the MessageOrBuilder interface.
     */
    interface_extends {
        @Override
        protected String javaFileName(ProtocolType type) {
            return type.getJavaFileName().getOrBuilderName();
        }
    },

    /**
     * Extra code of wrapper class.
     */
    outer_class_scope {
        @Override
        protected String javaFileName(ProtocolType type) {
            return type.getJavaFileName().getOuterClassName();
        }

        @Override
        protected String identifier(ProtocolType type) {
            return format("%s", name());
        }
    };

    /**
     * Obtains a new instance of the {@code InsertionPoint} for the given type.
     */
    public InsertionPoint newInsertionPoint(ProtocolType type) {
        return InsertionPoint.newBuilder()
                .setFileName(javaFileName(type))
                .setIdentifier(identifier(type))
                .build();
    }

    /**
     * Returns Java file name of the given type.
     */
    protected String javaFileName(ProtocolType type) {
        return type.getJavaFileName().getName();
    }

    /**
     * Returns insertion point identifier of the given type.
     */
    protected String identifier(ProtocolType type) {
        return format("%s:%s", name(), type.getName());
    }
}
