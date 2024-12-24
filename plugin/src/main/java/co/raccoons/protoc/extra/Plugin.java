/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.extra;

import co.raccoons.protoc.OptionsProto;
import co.raccoons.protoc.plugin.AbstractProtocPlugin;
import co.raccoons.protoc.plugin.CodeGenerator;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;

/**
 * The Protobuf Compiler plugin.
 */
public final class Plugin {

    private Plugin() {
    }

    /**
     * The program entry point.
     */
    public static void main(String[] args) {
        new AbstractProtocPlugin() {

            @Override
            protected void registerCustomOptions(ExtensionRegistry registry) {
                OptionsProto.registerAllExtensions(registry);
            }

            @Override
            protected CodeGeneratorResponse response() {
                var generator = CodeGenerator.newBuilder()
                        .add(new ExtraMessageInterface())
                        .build();
                var request = request();
                var files = generator.process(request);
                return CodeGeneratorResponse.newBuilder()
                        .addAllFile(files)
                        .build();
            }
        }.integrate();
    }
}
