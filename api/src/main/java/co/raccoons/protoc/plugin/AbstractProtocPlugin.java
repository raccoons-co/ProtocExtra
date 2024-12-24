/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static co.raccoons.protoc.plugin.CustomOptionRegistry.newRegistry;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides a skeletal implementation of the Protobuf compiler plugin
 * interface to minimize the effort required to implement this interface.
 * <p>
 * To implement a concrete protoc plugin, the programmer needs to extend this
 * class and provide implementation for the {@code response()} method.
 */
@Immutable
public abstract class AbstractProtocPlugin {
    /**
     * Sole constructor. (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected AbstractProtocPlugin() {
    }

    /**
     * Integrates generated code to Protobuf compiler.
     */
    public final void integrate() {
        write(this::pluginOutput);
    }

    /**
     * Registers custom options.
     * <p>
     * Designed to be overridden if plugin should to process custom options of
     * Protocol Buffers.
     */
    protected void registerCustomOptions(ExtensionRegistry registry) {
        // Intentionally empty.
    }

    /**
     * Obtains an encoded {@code CodeGeneratorRequest} that was written to
     * the plugin's stdin.
     */
    protected final CodeGeneratorRequest request() {
        return read(this::pluginInput);
    }

    /**
     * Returns generated {@code CodeGeneratorResponse} that will be written to
     * the plugin's stdout.
     * <p>
     * This method is intended to implement a concrete Protobuf compiler plugin.
     */
    protected abstract CodeGeneratorResponse response();

    /**
     * Reads an encoded {@code CodeGeneratorRequest} that written to the plugin's
     * stdin.
     */
    private CodeGeneratorRequest read(Supplier<CodeGeneratorRequest> stdin) {
        return stdin.get();
    }

    /**
     * Writes an encoded {@code CodeGeneratorResponse} to stdout.
     */
    private void write(Consumer<CodeGeneratorResponse> stdout) {
        stdout.accept(response());
    }

    private CodeGeneratorRequest pluginInput() {
        try {
            var registry = newRegistry(this::registerCustomOptions);
            return CodeGeneratorRequest.parseFrom(System.in, registry);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read code generator request.", e);
        }
    }

    @SuppressWarnings("java:S106") // Standard outputs used directly
    private void pluginOutput(CodeGeneratorResponse response) {
        try {
            checkNotNull(response, "CodeGeneratorResponse is null.");
            response.writeTo(System.out);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write code generator response.", e);
        }
    }
}
