all: test

.PHONY: test
test:
	clojure -A:test
