/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.protoc.plugin;

import co.raccoons.example.Nothing;
import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

@DisplayName("ProtocolTypeSet")
class ProtocolTypeSetTest {

    @Test
    @DisplayName("not accepts `null`")
    void throwsExceptionOnNull() {
        var protocolTypeSet = ProtocolTypeSet.newBuilder().build();
        new NullPointerTester().testAllPublicInstanceMethods(protocolTypeSet);
    }

    @Test
    @DisplayName("adds message type")
    void addsMessageType() {
        var protocolTypeSet =
                ProtocolTypeSet.newBuilder()
                        .add(Nothing.getDescriptor())
                        .build();
        assertThat(protocolTypeSet.contains("Nothing")).isTrue();
    }

    @Test
    @DisplayName("adds enum type")
    void addsEnumType() {
        var protocolTypeSet =
                ProtocolTypeSet.newBuilder()
                        .add(Nothing.NothingEnum.getDescriptor())
                        .build();
        assertThat(protocolTypeSet.contains("NothingEnum")).isTrue();
    }
}
