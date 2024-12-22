package co.raccoons.protoc.plugin;

import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("checkstyle:multiplestringliterals")
@DisplayName("AbstractProtocPlugin")
class AbstractProtocPluginTest {

    @Test
    @DisplayName("throws exception")
    void throwsOnNullResponse() {
        var exception =
                assertThrows(NullPointerException.class,
                        () ->
                                new AbstractProtocPlugin() {
                                    @Override
                                    protected CodeGeneratorResponse response() {
                                        return null;
                                    }
                                }.integrate()
                );

        assertThat(exception).hasMessageThat()
                .contains("CodeGeneratorResponse is null.");
    }

    @Test
    @DisplayName("writes to stdout")
    void writesToSystemOut() {
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

        assertThat(outputStream.toString().contains("ExampleMessage.java")).isTrue();
        assertThat(outputStream.toString().contains("message_implements:")).isTrue();
        assertThat(outputStream.toString().contains("co.raccoons.event.Observable,")).isTrue();
    }

    @Test
    @DisplayName("tries to read stdin")
    void readsFromSystemIn() {
        System.setIn(new ByteArrayInputStream("test".getBytes()));
        var exception =
                assertThrows(IllegalStateException.class,
                        () ->
                                new AbstractProtocPlugin() {
                                    @Override
                                    protected CodeGeneratorResponse response() {
                                        var unused = request();
                                        return null;
                                    }
                                }.integrate()
                );
        assertThat(exception).hasMessageThat()
                .isEqualTo("Unable to read code generator request.");
    }

    private final File file =
            File.newBuilder()
                    .setName("ExampleMessage.java")
                    .setInsertionPoint("message_implements:")
                    .setContent("co.raccoons.event.Observable,")
                    .build();
}
