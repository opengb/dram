all: test

.PHONY: test
test: node_modules
	clojure -A:test

lint:
	clj-kondo --lint src test

clean:
	rm -rf out/ target/ .cpcache/ .cljs_node_repl .nrepl-port node_modules package-lock.json

node_modules:
	npm install ws # for kaocha-cljs
	touch node_modules
