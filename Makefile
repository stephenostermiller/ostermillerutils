JFLAGS=
JAVAC=javac
JAVA=java
JAVADOC=javadoc
JLEX=$(JAVA) $(JFLAGS) JFlex.Main

all: CSVLexer.java \
	BrowserCommandLexer.java \
	CGILexer.java \
	ExcelCSVLexer.java
	$(JAVAC) $(JFLAGS) *.java

CSVLexer.java: CSVLexer.lex
	$(JLEX) CSVLexer.lex

CGILexer.java: CGILexer.lex
	$(JLEX) CGILexer.lex

BrowserCommandLexer.java: BrowserCommandLexer.lex
	$(JLEX) BrowserCommandLexer.lex
		
ExcelCSVLexer.java: ExcelCSVLexer.lex
	$(JLEX) ExcelCSVLexer.lex

clean:
	rm -rf docs/ gnu/ com/
	rm -f *.class
	rm -f *~
	rm -f ~*
	rm -f *.jar
	rm -f CSVLexer.java BrowserCommandLexer.java CGILexer.java ExcelCSVLexer.java
	rm -f CSVTest.txt

docs:
	rm -rf doc/ docs/
	mkdir doc
	$(JAVADOC) -d doc/ com.Ostermiller.util

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
	$(JAVA) com.Ostermiller.util.ExcelCSVLexer ExcelCSVRegressionTest.csv > out.txt
	diff out.txt ExcelCSVRegressionTestResults.txt
	rm out.txt

