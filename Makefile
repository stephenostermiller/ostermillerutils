JFLAGS=
JAVAC=javac
JAVA=java
JAVADOC=javadoc

all: 
	$(JAVAC) $(JFLAGS) *.java

clean:
	rm -rf doc/
	rm -f *.class
	rm -f *~
	rm -f ~*
	rm -f *.jar

docs:
	rm -rf doc/
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
	jar cfv utils.jar com/
	rm -rf com/

test:
	$(JAVA) com.Ostermiller.util.TokenizerTests > out.txt
	diff out.txt TokenizerTestResults.txt
	$(JAVA) com.Ostermiller.util.CSVLexer CSVRegressionTest.csv > out.txt
	diff out.txt CSVRegressionTestResults.txt
	rm out.txt

