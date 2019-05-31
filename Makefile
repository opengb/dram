all: test

.PHONY: test
test:
	clojure -A:test

# starts nrepl and a jvm + node prepl
repl:
	@clj \
		-J-Dclojure.server.jvm="{:port 5555 :accept clojure.core.server/io-prepl}" \
		-J-Dclojure.server.node="{:port 5556 :accept cljs.server.node/prepl}" \
		-R:test
		-A:nrepl
