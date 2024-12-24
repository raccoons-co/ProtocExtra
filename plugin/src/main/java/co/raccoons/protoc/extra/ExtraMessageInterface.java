/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.extra;

import co.raccoons.protoc.OptionsProto;
import co.raccoons.protoc.plugin.AbstractCodeGenerator;
import co.raccoons.protoc.plugin.InsertionPointFactory;
import co.raccoons.protoc.plugin.ProtocolType;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;

import java.util.function.Predicate;

import static java.lang.String.format;

/**
 * An extra message interface code generator.
 * <p>
 * The interface type is defined by custom option `extra`.
 * <p>
 * Example:
 * <pre>
 * message MyEvent {
 *   option (extra).message_implements = "co.raccoons.eventbus.Observable";
 *   string name = 1;
 * }
 * </pre>
 */
final class ExtraMessageInterface extends AbstractCodeGenerator {

    @Override
    protected Predicate<ProtocolType> precondition() {
        return super.precondition()
                .and(ExtraMessageInterface::hasMessageType)
                .and(ExtraMessageInterface::hasExtraOption);
    }

    @Override
    protected File generate(ProtocolType type) {
        var insertionPoint =
                InsertionPointFactory.message_implements.newInsertionPoint(type);
        var content = messageImplementsContent(type);
        return File.newBuilder()
                .setName(insertionPoint.getFileName())
                .setInsertionPoint(insertionPoint.getIdentifier())
                .setContent(content)
                .build();
    }

    private static String messageImplementsContent(ProtocolType type) {
        var className = type.getMessageType()
                .getOptions()
                .getExtension(OptionsProto.extra)
                .getMessageImplements();
        checkClass(className);
        return format("%s,", className);
    }

    private static void checkClass(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Option `(extra)` content should be a class name.", e);
        }
    }

    private static boolean hasMessageType(ProtocolType type) {
        return type.hasMessageType();
    }

    private static boolean hasExtraOption(ProtocolType type) {
        return type.getMessageType()
                .getOptions()
                .hasExtension(OptionsProto.extra);
    }
}
