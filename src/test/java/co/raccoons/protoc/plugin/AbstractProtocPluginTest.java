package co.raccoons.protoc.plugin;

import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AbstractProtocPlugin")
class AbstractProtocPluginTest {

    @Test
    @DisplayName("throws exception")
    void throwsOnNullResponse() {
        var exception =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                new AbstractProtocPlugin() {
                                    @Override
                                    protected CodeGeneratorResponse response() {
                                        return null;
                                    }
                                }.integrate()
                );

        assertEquals("CodeGeneratorResponse is null.", exception.getMessage());
    }

    @Test
    @DisplayName("writes to stdout")
    void canWriteToSystemOut() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        new AbstractProtocPlugin() {
            @Override
            protected CodeGeneratorResponse response() {
                return CodeGeneratorResponse.newBuilder()
                        .addFile(file)
                        .build();
            }
        }.integrate();

        assertTrue(outputStream.toString().contains("ExampleEvent.java"));
        assertTrue(outputStream.toString().contains("message_implements:"));
        assertTrue(outputStream.toString().contains("co.raccoons.event.Observable"));
    }

    private final File file =
            File.newBuilder()
                    .setName("ExampleEvent.java")
                    .setInsertionPoint("message_implements:")
                    .setContent("co.raccoons.event.Observable")
                    .build();
}