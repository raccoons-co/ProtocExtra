/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package co.raccoons.protoc.plugin;

import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

/**
 * Name that are generated by the Java protoc plugin.
 *
 * @see <a href="https://protobuf.dev/reference/java/java-proto-names/">
 * java-proto-names</a>
 */
@Immutable
interface JavaName {

    String JAVA_FILE_NAME_PATTERN = "%s/%s.java";

    /**
     * Returns file descriptor that describes .proto file, including everything
     * defined within.
     */
    FileDescriptor file();

    /**
     * Returns types of the .proto file.
     */
    ProtocolTypeSet types();

    /**
     * Obtains `enum` file name, relative to the output directory.
     */
    default String enumFileName(EnumDescriptor enumType) {
        return format(JAVA_FILE_NAME_PATTERN, directory(), enumName(enumType));
    }

    /**
     * Obtains `message` file name, relative to the output directory.
     */
    default String messageFileName(Descriptor messageType) {
        return format(JAVA_FILE_NAME_PATTERN, directory(), messageName(messageType));
    }

    /**
     * Obtains `messageOrBuilder` file name, relative to the output directory.
     */
    default String orBuilderFileName(Descriptor messageType) {
        return format(JAVA_FILE_NAME_PATTERN, directory(), orBuilderName(messageType));
    }

    /**
     * Obtains `outerClass` file name, relative to the output directory.
     */
    default String outerClassFileName() {
        return format(JAVA_FILE_NAME_PATTERN, directory(), outerClassName());
    }

    private String enumName(EnumDescriptor enumType) {
        return isJavaMultipleFiles()
                ? enumMultiple(enumType)
                : outerClassName();
    }

    private static String enumMultiple(EnumDescriptor enumType) {
        return isTopLevelEnum(enumType)
                ? enumType.getName()
                : topLevelMessageName(enumType.getContainingType());
    }

    private String messageName(Descriptor messageType) {
        return isJavaMultipleFiles()
                ? messageMultiple(messageType)
                : outerClassName();
    }

    private static String messageMultiple(Descriptor messageType) {
        return isTopLevelMessage(messageType)
                ? messageType.getName()
                : topLevelMessageName(messageType.getContainingType());
    }

    private String orBuilderName(Descriptor messageType) {
        return isJavaMultipleFiles()
                ? orBuilderMultiple(messageType)
                : outerClassName();
    }

    private static String orBuilderMultiple(Descriptor messageType) {
        return isTopLevelMessage(messageType)
                ? messageType.getName() + "OrBuilder"
                : topLevelMessageName(messageType.getContainingType());
    }

    private String outerClassName() {
        var outerClass = file().getOptions().getJavaOuterClassname();
        return isNullOrEmpty(outerClass)
                ? derivedOuterClass()
                : outerClass;
    }

    private String derivedOuterClass() {
        var protoFileName = file().getName();
        var derivedOuterClass = SimpleName.fromProtoName(protoFileName);
        return types().contains(derivedOuterClass)
                ? derivedOuterClass + "OuterClass"
                : derivedOuterClass;
    }

    private String directory() {
        return javaPackage().replace(".", "/");
    }

    private String javaPackage() {
        var javaPackage = file().getOptions().getJavaPackage();
        return isNullOrEmpty(javaPackage)
                ? protoPackage()
                : javaPackage;
    }

    private String protoPackage() {
        var protoPackage = file().getPackage();
        checkArgument(!isNullOrEmpty(protoPackage), "Package name is undefined");
        return protoPackage;
    }

    private boolean isJavaMultipleFiles() {
        return file().getOptions().getJavaMultipleFiles();
    }

    private static String topLevelMessageName(Descriptor messageType) {
        return isTopLevelMessage(messageType)
                ? messageType.getName()
                : topLevelMessageName(messageType.getContainingType());
    }

    private static boolean isTopLevelEnum(EnumDescriptor enumType) {
        return enumType.getContainingType() == null;
    }

    private static boolean isTopLevelMessage(Descriptor messageType) {
        return messageType.getContainingType() == null;
    }

    @Immutable
    final class SimpleName {

        private static final String PROTO_REGEX = "\\.proto$";
        private static final String EMPTY_PATTERN = "";

        private SimpleName() {
        }

        @VisibleForTesting
        public static String fromProtoName(String filename) {
            checkArgument(!isNullOrEmpty(filename));
            var simpleName = filename.replaceAll(PROTO_REGEX, EMPTY_PATTERN);
            return snakeToPascalCase(simpleName);
        }

        private static String snakeToPascalCase(String snake) {
            return Arrays.stream(snake.split("_"))
                    .filter(Predicate.not(String::isEmpty))
                    .map(SimpleName::capitalize)
                    .collect(Collectors.joining());
        }

        private static String capitalize(String value) {
            return value.substring(0, 1).toUpperCase(Locale.getDefault())
                    + value.substring(1);
        }
    }
}
