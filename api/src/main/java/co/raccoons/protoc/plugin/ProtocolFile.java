/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import co.raccoons.common.eventbus.Observable;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
final class ProtocolFile implements JavaName {

    private final FileDescriptor file;
    private final ProtocolTypeSet types;

    private ProtocolFile(FileDescriptor file, ProtocolTypeSet types) {
        this.file = checkNotNull(file);
        this.types = checkNotNull(types);
    }

    /**
     * Returns a new instance of the {@code ProtocolFile}.
     */
    public static ProtocolFile of(FileDescriptor file) {
        var types = new ProtoParser().walk(file);
        return new ProtocolFile(file, types);
    }

    @Override
    public FileDescriptor file() {
        return file;
    }

    @Override
    public ProtocolTypeSet types() {
        return types;
    }

    /**
     * Posts an event for each protocol buffer message type in this file for
     * the further processing by code generators.
     */
    public void submitEvents() {
        types.values(this).forEach(Observable::post);
    }

    private static final class ProtoParser {

        private final ProtocolTypeSet.Builder builder = ProtocolTypeSet.newBuilder();

        private ProtocolTypeSet walk(FileDescriptor protoFile) {
            checkNotNull(protoFile);
            walkServices(protoFile.getServices());
            walkEnumTypes(protoFile.getEnumTypes());
            walkMessageTypes(protoFile.getMessageTypes());
            return builder.build();
        }

        private void walkServices(List<ServiceDescriptor> serviceList) {
            for (var service : serviceList) {
                add(service);
            }
        }

        private void walkEnumTypes(List<EnumDescriptor> enumTypeList) {
            for (var enumType : enumTypeList) {
                add(enumType);
            }
        }

        private void walkMessageTypes(List<Descriptor> messageTypeList) {
            for (var messageType : messageTypeList) {
                add(messageType);
                flatten(messageType);
            }
        }

        private void flatten(Descriptor messageType) {
            walkEnumTypes(messageType.getEnumTypes());
            walkMessageTypes(messageType.getNestedTypes());
        }

        @SuppressWarnings("CheckReturnValue")
        private void add(ServiceDescriptor service) {
            builder.add(service);
        }

        @SuppressWarnings("CheckReturnValue")
        private void add(EnumDescriptor enumType) {
            builder.add(enumType);
        }

        @SuppressWarnings("CheckReturnValue")
        private void add(Descriptor messageType) {
            builder.add(messageType);
        }
    }
}
