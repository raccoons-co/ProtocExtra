/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.GenericDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * A set of the protocol buffer message types.
 */
@Immutable
final class ProtocolTypeSet {

    private final ImmutableSet<ServiceDescriptor> rawServices;
    private final ImmutableSet<EnumDescriptor> rawEnumTypes;
    private final ImmutableSet<Descriptor> rawMessageTypes;

    private ProtocolTypeSet(Builder builder) {
        this.rawServices = ImmutableSet.copyOf(builder.rawServices);
        this.rawEnumTypes = ImmutableSet.copyOf(builder.rawEnumTypes);
        this.rawMessageTypes = ImmutableSet.copyOf(builder.rawMessageTypes);
    }

    /**
     * Returns a new builder of the {@code ProtocolTypeSet} instance.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Returns {@code true} if set contains a given type name, otherwise
     * {@code false}.
     */
    public boolean contains(String typeName) {
        checkNotNull(typeName);
        return allRawTypeNames().contains(typeName);
    }

    /**
     * Obtains the values of set.
     * <p>
     * The values are mapped to the java proto names file
     */
    public ImmutableSet<ProtocolType> values(JavaName javaName) {
        var mapper = new ProtocolTypeMapper(javaName);
        return ImmutableSet.<ProtocolType>builder()
                .addAll(services(mapper))
                .addAll(enumTypes(mapper))
                .addAll(messageTypes(mapper))
                .build();
    }

    private ImmutableSet<String> allRawTypeNames() {
        return allRawTypes()
                .stream()
                .map(GenericDescriptor::getName)
                .collect(toImmutableSet());
    }

    private ImmutableSet<GenericDescriptor> allRawTypes() {
        return ImmutableSet.<GenericDescriptor>builder()
                .addAll(rawServices)
                .addAll(rawEnumTypes)
                .addAll(rawMessageTypes)
                .build();
    }

    private ImmutableSet<ProtocolType> services(ProtocolTypeMapper mapper) {
        return rawServices.stream()
                .map(mapper::service)
                .collect(toImmutableSet());
    }

    private ImmutableSet<ProtocolType> enumTypes(ProtocolTypeMapper mapper) {
        return rawEnumTypes.stream()
                .map(mapper::enumType)
                .collect(toImmutableSet());
    }

    private ImmutableSet<ProtocolType> messageTypes(ProtocolTypeMapper mapper) {
        return rawMessageTypes.stream()
                .map(mapper::messageType)
                .collect(toImmutableSet());
    }

    static final class Builder {

        private final Set<ServiceDescriptor> rawServices = new HashSet<>();
        private final Set<EnumDescriptor> rawEnumTypes = new HashSet<>();
        private final Set<Descriptor> rawMessageTypes = new HashSet<>();

        private Builder() {
        }

        public Builder add(ServiceDescriptor service) {
            checkNotNull(service);
            rawServices.add(service);
            return this;
        }

        public Builder add(EnumDescriptor enumType) {
            checkNotNull(enumType);
            rawEnumTypes.add(enumType);
            return this;
        }

        public Builder add(Descriptor messageType) {
            checkNotNull(messageType);
            rawMessageTypes.add(messageType);
            return this;
        }

        public ProtocolTypeSet build() {
            return new ProtocolTypeSet(this);
        }
    }

    @Immutable
    private static final class ProtocolTypeMapper {

        private final JavaName javaName;

        public ProtocolTypeMapper(JavaName javaName) {
            this.javaName = checkNotNull(javaName);
        }

        private ProtocolType service(ServiceDescriptor service) {
            return ProtocolType.newBuilder()
                    .setName(service.getFullName())
                    .setService(service.toProto())
                    .build();
        }

        private ProtocolType enumType(EnumDescriptor enumType) {
            var javaFileName = ProtocolType.JavaFileName.newBuilder()
                    .setName(javaName.enumFileName(enumType))
                    .build();

            return ProtocolType.newBuilder()
                    .setName(enumType.getFullName())
                    .setEnumType(enumType.toProto())
                    .setJavaFileName(javaFileName)
                    .build();
        }

        private ProtocolType messageType(Descriptor messageType) {
            var javaFileName = ProtocolType.JavaFileName.newBuilder()
                    .setName(javaName.messageFileName(messageType))
                    .setOrBuilderName(javaName.orBuilderFileName(messageType))
                    .setOuterClassName(javaName.outerClassFileName())
                    .build();

            return ProtocolType.newBuilder()
                    .setName(messageType.getFullName())
                    .setMessageType(messageType.toProto())
                    .setJavaFileName(javaFileName)
                    .build();
        }
    }
}
