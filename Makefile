all: test

.PHONY: test
test:
	clojure -A:test

lint:
	clj-kondo --lint src test

clean:
	rm -rf out/ target/ .cpcache/ .nrepl-port
