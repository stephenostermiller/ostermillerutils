CLASSPATH=../../..
SOURCPATH=../../..
JFLAGS=-classpath $(CLASSPATH)
JDFLAGS=-classpath $(CLASSPATH) -sourcepath $(SOURCPATH)
JAVAC=javac $(JFLAGS)
JAVA=java $(JFLAGS)
JAVADOC=javadoc $(JDFLAGS)
JLEXFLAGS=-q
JLEX=$(JAVA) JFlex.Main  $(JLEXFLAGS)
CVS=cvs -q

.SUFFIXES:
.SUFFIXES: .lex .java
.SUFFIXES: .java .class
.SUFFIXES: .bte .html

all:
	@$(MAKE) -s --no-print-directory junkclean
	@$(MAKE) -s --no-print-directory spell
	@$(MAKE) -s --no-print-directory neaten
	@$(MAKE) -s --no-print-directory compile
	@$(MAKE) -s --no-print-directory build
	@$(MAKE) -s --no-print-directory javadoc
	@$(MAKE) -s --no-print-directory htmlsource
	@$(MAKE) -s --no-print-directory release

spell: *.bte *.java
	@echo Make: Running spell check.
	@./spell.sh $?
	@touch spell

.PHONY : compile
compile: javafiles classes

neaten: *.java
	@./neaten.sh $?
	@touch neaten
	
LEXFILES=$(wildcard *.lex)
.PHONY: javafiles
javafiles: $(LEXFILES:.lex=.java)
	@# Write a bash script that will compile the files in the todo list
	@echo "#!/bin/bash" > tempCommand	
	@# If the todo list doesn't exist, don't compile anything
	@echo "if [ -e tempChangedLexFileList ]" >> tempCommand
	@echo "then" >> tempCommand
	@# Make sure each file is only on the todo list once.
	@echo "sort tempChangedLexFileList | uniq  > tempChangedLexFileListUniq" >> tempCommand
	@echo "FILES=\`cat tempChangedLexFileListUniq\`" >> tempCommand
	@# Compile the files.
	@echo "echo Make: Compiling: $$ FILES" >> tempCommand
	@echo "$(JLEX) $$ FILES" >> tempCommand
	@echo "for file in $$ FILES" >> tempCommand
	@echo "do" >> tempCommand
	@# Each generated java file needs to be compiled by the java compiler.
	@echo "echo \"$$ {file%.lex}.java\" >> tempChangedJavaFileList" >> tempCommand
	@echo "done" >> tempCommand
	@echo "fi" >> tempCommand
	@# Remove extra spaces in the script that follow the dollar signs.
	@sed "s/\$$ /\$$/" tempCommand > tempCommand.sh
	@# Make the script executable.
	@chmod +x tempCommand.sh
	@# Call the script
	@./tempCommand.sh
	@rm -f tempCommand tempCommand.sh tempChangedLexFileList tempChangedLexFileListUniq *~

.lex.java:
	@#for each changed lex file, add it to the todo list.
	@echo "$<" >> tempChangedLexFileList

.PHONY: javafilesclean
javafilesclean: 
	@echo Make: Removing generated Lexer java files.
	@rm -f `find . -name "*.lex" | sed s/.lex/.java/`

JAVAFILES=$(wildcard *.java)
.PHONY: classes
classes: javafiles $(JAVAFILES:.java=.class)
	@# Write a bash script that will compile the files in the todo list
	@echo "#!/bin/bash" > tempCommand	
	@# If the todo list doesn't exist, don't compile anything
	@echo "if [ -e tempChangedJavaFileList ]" >> tempCommand
	@echo "then" >> tempCommand
	@# Make sure each file is only on the todo list once.
	@echo "sort tempChangedJavaFileList | uniq  > tempChangedJavaFileListUniq" >> tempCommand
	@echo "FILES=\`cat tempChangedJavaFileListUniq\`" >> tempCommand
	@# Compile the files.
	@echo "echo Make: Compiling: $$ FILES" >> tempCommand
	@echo "$(JAVAC) $$ FILES" >> tempCommand
	@echo "fi" >> tempCommand
	@# Remove extra spaces in the script that follow the dollar signs.
	@sed "s/\$$ /\$$/" tempCommand > tempCommand.sh
	@# Make the script executable.
	@chmod +x tempCommand.sh
	@# Call the script
	@./tempCommand.sh
	@rm -f tempCommand tempCommand.sh tempChangedJavaFileList tempChangedJavaFileListUniq

.java.class:
	@#for each changed java file, add it to the todo list.
	@echo "$<" >> tempChangedJavaFileList

.PHONY: classesclean
classesclean: junkclean
	@echo Make: Removing Lexer class files
	@rm -f *.class

.PHONY: junkclean	        
junkclean:
	@echo Make: Removing utilites detritus.
	@rm -rf *~ ~* temp* utils_*.jar out.txt *.bak CSVTest.txt CircularBufferTestResults.txt com/ gnu/ srcbuild/

.PHONY: buildclean	        
buildclean: junkclean
	@echo Make: Removing generated jar files.
	@rm -f utils.jar
        
.PHONY: javadocclean	        
javadocclean: junkclean
	@echo Make: Removing generated documentation.
	@rm -rf doc/ javadoc

.PHONY: htmlsourceclean	        
htmlsourceclean: junkclean
	@echo Make: Removing generated html source.
	@rm -rf src/ htmlsource
        
.PHONY: testclean	        
testclean: junkclean
	@echo Make: Removing compiled tests.
	@rm -f CircularBufferTests*.class CSVTest.class TokenizerTests.class

.PHONY: clean	        
clean: buildclean javadocclean htmlsourceclean
	@echo Make: Removing generated class files.
	@rm -f *.class

.PHONY: allclean        
allclean: clean
	@echo Make: Removing all files not in CVS.
	@rm -rf CSVLexer.java BrowserCommandLexer.java CGILexer.java ExcelCSVLexer.java neaten spell release

javadoc: *.java
	@echo Make: Generating javadoc
	@rm -rf doc
	@mv -f package.html temp
	@mkdir doc
	@$(JAVADOC) -quiet -link http://java.sun.com/j2se/1.3/docs/api/ -d doc/ com.Ostermiller.util > /dev/null
	@mv -f temp package.html
	@touch javadoc

.PHONY: build
build: utils.jar

utils.jar: *.java *.html *.class *.sh *.lex *.properties *.txt *.TXT *.csv *.bte *.dict Makefile ../../../gnu/getopt/*.*
	@echo Make: Building jar file.
	@mkdir -p com/Ostermiller/util
	@cp *.java *.html *.class *.sh *.lex *.properties *.txt *.TXT *.csv *.bte *.dict Makefile Makefile com/Ostermiller/util/
	@rm -f `find com/Ostermiller/util -name "*.lex" | sed s/.lex/.java/`
	@rm -f com/Ostermiller/util/CircularBufferTests*.class com/Ostermiller/util/CSVTest.class com/Ostermiller/util/TokenizerTests.class
	@mkdir -p gnu/getopt		
	@cp ../../../gnu/getopt/*.* gnu/getopt
	@jar cfv utils.jar com/ gnu/ > /dev/null
	@rm -rf com/ gnu/

.PHONY: test
test: 
	$(JAVA) com.Ostermiller.util.TokenizerTests > out.txt
	@diff out.txt TokenizerTestResults.txt
	$(JAVA) com.Ostermiller.util.CSVLexer CSVRegressionTest.csv > out.txt
	@diff out.txt CSVRegressionTestResults.txt
	$(JAVA) com.Ostermiller.util.ExcelCSVLexer ExcelCSVRegressionTest.csv > out.txt
	@diff out.txt ExcelCSVRegressionTestResults.txt
	$(JAVA) com.Ostermiller.util.CSVTest > out.txt
	@diff out.txt CSVTestResults.txt
	$(JAVA) com.Ostermiller.util.CircularBufferTests > out.txt
	@diff out.txt CircularBufferTestResults.txt
	@rm out.txt CSVTest.txt CircularBufferTestResults.txt
        
.PHONY: update
update: 
	@$(CVS) update -RPd .
        
.PHONY: commit
commit: 
	@$(CVS) commit

release: *.html src/* utils.jar .htaccess install.sh doc/
	@./release.sh $?
	@touch release

.PHONY: install
install:
	@./install.sh

htmlsource: *.java *.properties *.lex
	@echo Make: Generating colored html source: $?
	@rm -rf srcbuild/
	@mkdir srcbuild
	@cp $? src.bte srcbuild
	@rm -f `find srcbuild -name "*.lex" | sed s/.lex/.java/`
	@touch srcbuild/tempdummy.java srcbuild/tempdummy.lex srcbuild/tempdummy.properties
	@echo "cd srcbuild" > srcbuild/temp.sh
	@echo "$(JAVA)/.. com.Ostermiller.util.Tabs -s 4 *.java" >> srcbuild/temp.sh
	@echo "$(JAVA)/.. com.Ostermiller.Syntax.ToHTML -t src.bte -i whitespace *.lex *.java *.properties" >> srcbuild/temp.sh
	@echo "rm -rf tempdummy*" >> srcbuild/temp.sh	
	@chmod +x srcbuild/temp.sh
	@srcbuild/temp.sh
	@mkdir -p src/
	@mv srcbuild/*.*.html srcbuild/*.css src/
	@rm -rf srcbuild
	@cp source.sh cleansource.sh src/
	@echo "cd src" > src/temp.sh
	@echo "./cleansource.sh" >> src/temp.sh
	@echo "./source.sh" >> src/temp.sh
	@chmod +x src/temp.sh
	@src/temp.sh
	@rm -f src/*.sh bte
	@touch htmlsource
