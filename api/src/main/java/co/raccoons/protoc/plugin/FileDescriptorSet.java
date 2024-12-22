/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A set of Proto files with their dependencies.
 */
@Immutable
final class FileDescriptorSet {

    private final ImmutableMap<String, FileDescriptor> files;

    private FileDescriptorSet(ImmutableMap<String, FileDescriptor> files) {
        this.files = checkNotNull(files);
    }

    /**
     * Returns a new instance of {@code ProtobufFileSet} for the given proto
     * file list.
     */
    public static FileDescriptorSet of(Iterable<FileDescriptorProto> protos) {
        checkNotNull(protos);
        var files = files(protos);
        return new FileDescriptorSet(files);
    }

    /**
     * Obtains proto file descriptor for the given file name.
     */
    public FileDescriptor fileByName(String name) {
        checkNotNull(name);
        return files.get(name);
    }

    private static ImmutableMap<String, FileDescriptor> files(Iterable<FileDescriptorProto> protos) {
        Map<String, FileDescriptor> files = new HashMap<>();
        for (var fileDescriptorProto : protos) {
            var dependencies =
                    fileDescriptorProto.getDependencyList()
                            .stream()
                            .map(files::get)
                            .toArray(FileDescriptor[]::new);

            try {
                var fileDescriptor =
                        FileDescriptor.buildFrom(fileDescriptorProto, dependencies);
                files.put(fileDescriptorProto.getName(), fileDescriptor);
            } catch (DescriptorValidationException e) {
                throw new IllegalStateException("DescriptorProto is not valid.", e);
            }
        }
        return ImmutableMap.copyOf(files);
    }
}
