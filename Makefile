JAVA_HOME ?= $(shell dirname $(shell dirname $(shell readlink -f $(shell which java))))
export JAVA_HOME

.PHONY: all
all: .git/hooks/pre-commit compile test package doc site

.PHONY: build
build:
	mvn compile

.PHONY: compile
compile:
	mvn compile

.PHONY: test
test: build
	mvn test

.PHONY: package
package: build
	mvn package

.PHONY: doc
doc javadoc:
	mvn javadoc:jar javadoc:javadoc

.PHONY: javadoc
javadoc: doc

.PHONY: site
site:
	mvn site

.PHONY: verify
verify:
	mvn verify

.PHONY: source-jar
source-jar:
	mvn source:jar

.PHONY: clean
clean:
	mvn clean
	@echo "Build artifacts cleaned."

.git/hooks/pre-commit:
	pre-commit install
