JFLAGS=
JAVAC=javac
JAVA=java
JAVADOC=javadoc
JLEX=$(JAVA) $(JFLAGS) JFlex.Main

all: CSVLexer.java \
	BrowserCommandLexer.java \
		CGILexer.java
	$(JAVAC) $(JFLAGS) *.java

CSVLexer.java: CSVLexer.lex
	$(JLEX) CSVLexer.lex

CGILexer.java: CGILexer.lex
	$(JLEX) CGILexer.lex

BrowserCommandLexer.java: BrowserCommandLexer.lex
	$(JLEX) BrowserCommandLexer.lex

clean:
	rm -rf docs/ gnu/ com/
	rm -f *.class
	rm -f *~
	rm -f ~*
	rm -f *.jar
	rm -f CSVLexer.java BrowserCommandLexer.java CGILexer.java

docs:
	rm -rf docs/
	mkdir docs
	$(JAVADOC) -d docs/ com.Ostermiller.util

build:
	rm -f utils.jar
	rm -f *~
	rm -f ~*
	mkdir com
	mkdir com/Ostermiller
	mkdir com/Ostermiller/util
	cp *.* Makefile com/Ostermiller/util/
	mkdir gnu
	mkdir gnu/getopt		
	cp ../../../gnu/getopt/*.* gnu/getopt
	jar cfv utils.jar com/ gnu/
	rm -rf com/ gnu/

test:
	$(JAVA) com.Ostermiller.util.TokenizerTests > out.txt
	diff out.txt TokenizerTestResults.txt
	$(JAVA) com.Ostermiller.util.CSVLexer CSVRegressionTest.csv > out.txt
	diff out.txt CSVRegressionTestResults.txt
	rm out.txt

