CLASSPATH=../../..
SOURCPATH=../../..
JFLAGS=-classpath $(CLASSPATH)
JDFLAGS=-classpath $(CLASSPATH) -sourcepath $(SOURCPATH)
JAVAC=javac
JAVA=java
JAVADOC=javadoc $(JDFLAGS)
JLEX=$(JAVA) $(JFLAGS) JFlex.Main
CVS=cvs

all: compile build javadoc

compile: CSVLexer.java \
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

junkclean:
	rm -f *~ ~* utils_*.jar
	rm -rf com/ gnu/

buildclean: junkclean
	rm -f utils.jar
        
javadocclean: junkclean
	rm -rf doc/

clean: buildclean javadocclean
	rm -f *.class
        
allclean: clean
	rm -f CSVLexer.java BrowserCommandLexer.java CGILexer.java ExcelCSVLexer.java

javadoc: javadocclean
	mv -f package.html temp
	mkdir doc
	$(JAVADOC) -link http://java.sun.com/j2se/1.3/docs/api/ -d doc/ com.Ostermiller.util
	mv -f temp package.html

build: buildclean compile
	mkdir -p com/Ostermiller/util
	cp *.* Makefile com/Ostermiller/util/
	mkdir -p gnu/getopt		
	cp ../../../gnu/getopt/*.* gnu/getopt
	jar cfv utils.jar com/ gnu/
	rm -rf com/ gnu/

test: compile
	$(JAVA) com.Ostermiller.util.TokenizerTests > out.txt
	diff out.txt TokenizerTestResults.txt
	$(JAVA) com.Ostermiller.util.CSVLexer CSVRegressionTest.csv > out.txt
	diff out.txt CSVRegressionTestResults.txt
	$(JAVA) com.Ostermiller.util.ExcelCSVLexer ExcelCSVRegressionTest.csv > out.txt
	diff out.txt ExcelCSVRegressionTestResults.txt
	$(JAVA) com.Ostermiller.util.CSVTest > out.txt
	diff out.txt CSVTestResults.txt
	rm out.txt CSVTest.txt
        
update: clean
	$(CVS) update
        
commit: clean
	$(CVS) commit

release: update test build javadoc commit
	mv -f package.html temp
	./release.sh
	mv -f temp package.html

install:
	./install.sh

