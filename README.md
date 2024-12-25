[![GitHub Actions](https://github.com/raccoons-co/ProtocExtra/actions/workflows/gradle-build-multiproject.yml/badge.svg)](https://github.com/raccoons-co/ProtocExtra/actions)
[![codecov](https://codecov.io/gh/raccoons-co/ProtocExtra/graph/badge.svg?token=y9xaNeJ4Lz)](https://codecov.io/gh/raccoons-co/ProtocExtra)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=raccoons-co_ProtocExtra&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=raccoons-co_ProtocExtra)
[![GitHub Actions](https://github.com/raccoons-co/ProtocExtra/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/raccoons-co/ProtocExtra/actions)

# Developing Java Protobuf Compiler Plugin

Let's simplify creation of Protocol Buffers Compiler Plugin:

1. Implement `AbstractProtocPlugin`.
2. Implement `AbstractCodeGenerator`.
3. Apply Plugin Executable with *Gradle* or *protoc* cli.

### Abstract Protoc Plugin
___

The `AbstractProtocPlugin` class has skeletal implementation to handle
plugins' standard input and output streams.

To introduce a concrete plugin the programmer must extend `AbstractProtocPlugin`
class and implement the method `response()` which returns an instance of 
`CodeGeneratorResponse`. This response will be implicitly written to plugins'
stdout on calling `integrate()` method.

The protected method `request()` returns the instance of the 
`CodeGeneratorRequest` which has been read from plugins' standard input.
The request should be used for the further processing by your code generator.

### Adding Custom Options
___

``` Protobuf
message Event {
  option (extra).message_implements = "co.raccoons.common.eventbus.Observable";
  string name = 1;
}
```

To define and use own options for Protocol Buffers the programmer must override 
method `registerCustomOptions(...)` and  implement corresponding extension 
protocol types as well [4, 5].

``` Java
public static void main(String[] args) {
    new AbstractProtocPlugin() {
        @Override
        protected void registerCustomOptions(ExtensionRegistry registry) {
            OptionsProto.registerAllExtensions(registry);
        }

        @Override
        protected CodeGeneratorResponse response() {
            var generator = CodeGenerator.newBuilder()
                    .add(new ExtraMessageInterface())
                    .build();
            var request = request();
            var files = generator.process(request);
            return CodeGeneratorResponse.newBuilder()
                    .addAllFile(files)
                    .build();
        }
    }.integrate();
}
```

### Multipart Code Generator
___

The multipart Java `CodeGenerator` processes the given compiler request and 
generates the Protobuf compiler response files using a concrete generators that 
was added to it. These concrete generators are implementations of 
the `AbstractCodeGenerator`.

### Abstract Code Generator
___

There is another abstraction `AbstractCodeGenerator` that can be used as a part 
of a further plugin development.

The skeletal implementation of `AbstractCodeGenerator` handles processing of
generating Java code extensions for any protocol buffer message types.

To introduce a concrete generator that extends the output produced by another 
code generator, the programmer must extend `AbstractCodeGenerator`class and 
implement the method `generate(...)` which returns an instance of
`CodeGeneratorResponse.File`.

To limit the scope of protocol buffer message types to which concrete generator will
be applied, the programmer should to override `precondition()` method to return
required predicates.

### Insertion Point Factory
___

While composing the Java code extension it is important to know where to insert
your extra code. For this purpose the programmer can use `InsertionPointFactory`
class which creates the insertion point details for the specified scope of a given 
protocol buffer message type: *file name* and *identifier*.

``` Java
final class ExtraMessageInterface extends AbstractCodeGenerator {

    @Override
    protected Predicate<ProtocolType> precondition() {
        return super.precondition()
                .and(ExtraMessageInterface::hasMessageType)
                .and(ExtraMessageInterface::hasExtraOption);
    }

    @Override
    protected File generate(ProtocolType type) {
        var insertionPoint =
                InsertionPointFactory.message_implements.newInsertionPoint(type);
        var content = messageImplementsContent(type);
        return File.newBuilder()
                .setName(insertionPoint.getFileName())
                .setInsertionPoint(insertionPoint.getIdentifier())
                .setContent(content)
                .build();
    }
    ...
}
```

### Applying Plugin Executable

---

The plugin should be named `protoc-gen-$NAME`, and will then be used when the
flag `--${NAME}_out` is passed to protoc.

The code generator that generates 
the initial file and the one which inserts into it must both run as part of 
a single invocation of protoc. Code generators are executed in the order in 
which they appear on the command line.

The examples of usage are given below:
~~~ Kotlin
// gradle.build.kts
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.5"
    }
    plugins {
        id("extra") {
            path = "util/plugin.sh"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("extra") {
                    outputSubDir = "java"
                }
            }
        }
    }
}
~~~
~~~ Makefile
# Makefile
proto: clean $(GENERATED_OUT_DIR)
proto: PROTO_PATH = ./src/main/proto
proto:
    protoc \
        --java_out=$(GENERATED_OUT_DIR) \
        --plugin=protoc-gen-extra=./util/plugin.sh \
        --extra_out=$(GENERATED_OUT_DIR) \
        --proto_path=$(PROTO_PATH) \
        $(wildcard $(PROTO_PATH)/*.proto)
~~~

### References

---

1. [plugin.proto](https://github.com/protocolbuffers/protobuf/blob/main/src/google/protobuf/compiler/plugin.proto)
2. Protobuf compiler [api-docs](https://protobuf.dev/reference/java/api-docs/com/google/protobuf/compiler/package-summary.html)
3. [descriptor.proto](https://github.com/protocolbuffers/protobuf/blob/main/src/google/protobuf/descriptor.proto)
4. Extending Protobuf: [custom options](https://giorgio.azzinna.ro/2017/07/extending-protobuf-custom-options/)
5. Programming-guides (proto3) [custom options](https://protobuf.dev/programming-guides/proto3/#customoptions)