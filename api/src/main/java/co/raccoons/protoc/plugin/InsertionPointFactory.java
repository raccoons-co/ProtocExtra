/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license http://www.apache.org/licenses/LICENSE-2.0
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
    message_implements(InsertionScope.MESSAGE),

    /**
     * Extra interface of the Builder class.
     */
    builder_implements(InsertionScope.MESSAGE),

    /**
     * Extra code of the Message type class.
     */
    class_scope(InsertionScope.MESSAGE),

    /**
     * Extra code of Builder class.
     */
    builder_scope(InsertionScope.MESSAGE),

    /**
     * Extra code of Enum.
     */
    enum_scope(InsertionScope.MESSAGE),

    /**
     * Extra interface of the MessageOrBuilder interface.
     */
    interface_extends(InsertionScope.MESSAGE_OR_BUILDER),

    /**
     * Extra code of wrapper class.
     */
    outer_class_scope(InsertionScope.OUTER_CLASS);

    private final InsertionScope insertionScope;

    InsertionPointFactory(InsertionScope insertionScope) {
        this.insertionScope = insertionScope;
    }

    /**
     * Obtains a new instance of the {@code InsertionPoint} for the given type.
     */
    public InsertionPoint newInsertionPoint(ProtocolType type) {
        return InsertionPoint.newBuilder()
                .setFileName(insertionScope.javaFileName(type))
                .setIdentifier(insertionScope.identifier(this, type))
                .build();
    }

    private enum InsertionScope {
        MESSAGE {
            @Override
            String javaFileName(ProtocolType type) {
                return type.getJavaFileName().getName();
            }
        },

        MESSAGE_OR_BUILDER {
            @Override
            String javaFileName(ProtocolType type) {
                return type.getJavaFileName().getOrBuilderName();
            }
        },

        OUTER_CLASS {
            @Override
            String javaFileName(ProtocolType type) {
                return type.getJavaFileName().getOuterClassName();
            }

            @Override
            String identifier(InsertionPointFactory insertionPointFactory, ProtocolType ignored) {
                return format("%s", insertionPointFactory.name());
            }
        };

        /**
         * Returns Java file name of the given type.
         */
        abstract String javaFileName(ProtocolType type);

        /**
         * Returns insertion point identifier of the given type.
         */
        String identifier(InsertionPointFactory insertionPointFactory, ProtocolType type) {
            return format("%s:%s", insertionPointFactory.name(), type.getName());
        }
    }
}
