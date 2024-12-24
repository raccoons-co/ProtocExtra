BASE_DIR = ..
GENERATED_OUT_DIR = $(BASE_DIR)/generated
EXECUTABLE_PLUGIN = ./plugin.sh

.DEFAULT_GOAL := proto

.PHONY: proto
proto: clean
proto: $(GENERATED_OUT_DIR)
proto: COMMON_PROTO_PATH=$(BASE_DIR)/lib/src/main/proto
proto: DEV_PROTO_PATH=$(BASE_DIR)/lib/src/dev/proto
proto:
	protoc \
		--java_out=$(GENERATED_OUT_DIR) \
		--plugin=protoc-gen-example=$(EXECUTABLE_PLUGIN) \
		--example_out=$(GENERATED_OUT_DIR) \
		--proto_path=$(COMMON_PROTO_PATH) \
		--proto_path=$(DEV_PROTO_PATH) \
		$(wildcard $(DEV_PROTO_PATH)/*.proto)

# Removes generated directory entries
.PHONY: clean
clean:
	rm -rf $(GENERATED_OUT_DIR)

# Creates directory if not exist
$(GENERATED_OUT_DIR):
	[ -d $@ ] || mkdir -p $@
