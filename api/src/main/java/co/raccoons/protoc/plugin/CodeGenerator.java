/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import co.raccoons.common.eventbus.Subscribable;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * A multipart Java code generator.
 */
public final class CodeGenerator {

    private final ImmutableSet<AbstractCodeGenerator> generators;

    private CodeGenerator(Builder builder) {
        this.generators = ImmutableSet.copyOf(builder.generators);
        register();
    }

    /**
     * Obtains a new builder of the {@code CodeGenerator}.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Processes the given compiler request and generates the Protobuf compiler
     * response files.
     */
    public Collection<File> process(CodeGeneratorRequest request) {
        checkNotNull(request);
        submitEvents(request);
        return extensions();
    }

    @VisibleForTesting
    ImmutableSet<AbstractCodeGenerator> generators() {
        return generators;
    }

    private Collection<File> extensions() {
        return generators.stream()
                .map(AbstractCodeGenerator::extensions)
                .flatMap(Collection::stream)
                .collect(toImmutableSet());
    }

    private void register() {
        generators.forEach(Subscribable::register);
    }

    private static void submitEvents(CodeGeneratorRequest request) {
        var fileDescriptorSet = FileDescriptorSet.of(request.getProtoFileList());
        request.getFileToGenerateList()
                .stream()
                .map(fileDescriptorSet::fileByName)
                .map(ProtocolFile::of)
                .forEach(ProtocolFile::submitEvents);
    }

    /**
     * A builder of the {@code CodeGenerator} instance.
     */
    public static final class Builder {

        private final Set<AbstractCodeGenerator> generators = new HashSet<>();

        private Builder() {
        }

        /**
         * Adds code generator.
         */
        public Builder add(AbstractCodeGenerator generator) {
            checkNotNull(generator);
            generators.add(generator);
            return this;
        }

        /**
         * Returns a new instance of {@code CodeGenerator}.
         */
        public CodeGenerator build() {
            return new CodeGenerator(this);
        }
    }
}
