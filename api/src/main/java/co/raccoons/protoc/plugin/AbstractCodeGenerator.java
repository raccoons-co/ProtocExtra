/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import co.raccoons.common.eventbus.Subscribable;
import com.google.common.eventbus.Subscribe;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class has skeletal implementation to handle extra java code generation
 * for any Protocol Buffer message types.
 * <p>
 * To introduce a concrete code generator the programmer must extend this class
 * and provide implementation for the method {@code generate(...)}.
 * <p>
 * By overriding the {@code precondition()} method, the programmer can filter
 * any protocol buffer message type that must be processed by the concrete
 * generator.
 */
public abstract class AbstractCodeGenerator implements Subscribable {

    private final Set<File> extensions = new HashSet<>();

    /**
     * Handles extra java code generation for any protocol buffer message types.
     * <p>
     * This method subscribes for the events posted by parser of protocol
     * messages.
     */
    @Subscribe
    public final void handle(ProtocolType type) {
        checkNotNull(type);
        if (precondition().test(type)) {
            var file = generate(type);
            extensions.add(file);
        }
    }

    /**
     * Returns extensions that adds extra java code.
     */
    public final Collection<File> extensions() {
        return extensions;
    }

    /**
     * This method is designed to be overridden.
     * <p>
     * Returning predicate, the programmer can filter any protocol buffer
     * message type that must be processed by the concrete generator.
     * <p>
     * If multiple predicates should be composed, the method must call
     * {@code super.precondition()} first.
     * <p>
     * Example:
     * <pre>
     * protected Predicate&lt;ProtocolType&gt; precondition() {
     *     return super.precondition()
     *         .and(ExtraMessageInterface::hasMessageType)
     *         .and(ExtraMessageInterface::hasExtraOption);
     * }
     * </pre>
     */
    protected Predicate<ProtocolType> precondition() {
        return AbstractCodeGenerator::alwaysTrue;
    }

    /**
     * Generates code generator response file that extends the output produced
     * by another code generator for any protocol buffer message type.
     */
    protected abstract File generate(ProtocolType type);

    @SuppressWarnings("unused")
    private static boolean alwaysTrue(ProtocolType ignored) {
        return true;
    }
}
