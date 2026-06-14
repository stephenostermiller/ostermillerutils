JAVA_HOME ?= $(shell dirname $(shell dirname $(shell readlink -f $(shell which java))))
export JAVA_HOME
VERSION := $(shell cat VERSION.txt)

.PHONY: all
all: .git/hooks/pre-commit target/test target/site

.PHONY: build
build:
	mvn compile

target/classes: $(shell find src/main -type f \( -name '*.java' -o -name '*.jflex' -o -name '*.jflexcsv' \) 2>/dev/null)
	mvn compile && touch target/classes

target/test: target/classes
	mvn surefire:test && touch target/test

target/ostermillerutils-$(VERSION).jar: target/classes
	mvn jar:jar -Drevision=$(VERSION)

target/ostermillerutils-$(VERSION)-sources.jar: $(shell find src/main -type f \( -name '*.java' -o -name '*.jflex' -o -name '*.jflexcsv' \) 2>/dev/null)
	mvn source:jar -Drevision=$(VERSION)

target/ostermillerutils-$(VERSION)-bin.zip: target/ostermillerutils-$(VERSION).jar target/ostermillerutils-$(VERSION)-sources.jar
	mvn assembly:single -Drevision=$(VERSION)

target/javadoc: $(shell find src/main -type f \( -name '*.java' -o -name '*.jflex' -o -name '*.jflexcsv' \) 2>/dev/null)
	mvn javadoc:javadoc@javadoc && touch target/javadoc

target/site: target/javadoc target/ostermillerutils-$(VERSION)-bin.zip $(shell find src/site -type f 2>/dev/null)
	mvn resources:resources@srcSiteToGenerated exec:exec@generate-site-files site:site jar:jar@randpassjar resources:copy-resources@archivesToSite resources:copy-resources@javadocToSite -Drevision=$(VERSION) && touch target/site

.PHONY: verify
verify:
	mvn verify

.PHONY: clean
clean:
	mvn clean
	@echo "Build artifacts cleaned."

.git/hooks/pre-commit:
	command -v pre-commit >/dev/null && pre-commit install || true
