CLASSPATH=../../..
SOURCEPATH=../../..
JFLAGS=-classpath $(CLASSPATH)
JDFLAGS=-classpath $(CLASSPATH) -sourcepath $(SOURCEPATH)
JAVAC=javac $(JFLAGS) 
OPTIMIZE=-g:none
JAVA=java $(JFLAGS)
JAVADOC=javadoc $(JDFLAGS)
CVS=cvs -q
BTE=bte
ANT=ant
JLEX=jflex

.SUFFIXES:
.SUFFIXES: .lex .java
.SUFFIXES: .java .class

all:
	@$(MAKE) -s --no-print-directory junkclean
	@$(MAKE) -s --no-print-directory spell
	@$(MAKE) -s --no-print-directory neaten
	@$(MAKE) -s --no-print-directory build
	@$(MAKE) -s --no-print-directory htaccess

spell: *.bte *.java
	@echo Make: Running spell check.$?
	@./spell.sh $?
	@touch spell

.PHONY : compile
compile:
	ant compile

.PHONY : build
build:
	ant dist

neaten: *.java
	@./neaten.sh $?
	@touch neaten
	
.PHONY: junkclean		
junkclean:
	ant junkclean

.PHONY: clean		
clean:
	ant clean

doc: src/doc

javadoc: src/doc

src/doc: *.java
	@echo Make: Generating javadoc
	@rm -rf src/doc
	@mkdir -p src/doc
	@$(JAVADOC) \
		-bottom '<p>Copyright (c) 2001-2007 by <a href="http://ostermiller.org/contact.pl?regarding=Java+Utilities">Stephen Ostermiller</a></p>' \
		-header "<h1><a target=\"_top\" href="http://ostermiller.org/utils/">com.Ostermiller.util</a> Java Utilities</h1><script type=\"text/javascript\">var google_ad_client = \"pub-2385172974335864\";var google_ad_width = 728;var google_ad_height = 90;var google_ad_format = \"728x90_as\";var google_ad_type = \"text\";var google_ad_channel =\"\";var google_color_border = \"A8DDA0\";var google_color_bg = \"EBFFED\";var google_color_link = \"0000CC\";var google_color_url = \"008000\";var google_color_text = \"6F6F6F\";var google_page_url = document.location;</script><script type=\"text/javascript\" src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\"></script>" \
		-link http://java.sun.com/j2se/1.5.0/docs/api/ -d src/doc/ \
		com.Ostermiller.util > /dev/null
	@touch javadoc
	
.PHONY: test
test: 
	$(JAVA) com.Ostermiller.util.TokenizerTests
	$(JAVA) com.Ostermiller.util.CSVLexer CSVRegressionTest.csv > out.txt
	@diff out.txt CSVRegressionTestResults.txt
	$(JAVA) com.Ostermiller.util.ExcelCSVLexer ExcelCSVRegressionTest.csv > out.txt
	@diff out.txt ExcelCSVRegressionTestResults.txt
	$(JAVA) com.Ostermiller.util.CSVTests
	$(JAVA) com.Ostermiller.util.CircularBufferTests
	$(JAVA) com.Ostermiller.util.UberPropertiesTests
	$(JAVA) com.Ostermiller.util.LabeledCSVParserTests
	$(JAVA) com.Ostermiller.util.Base64Tests
	$(JAVA) com.Ostermiller.util.MD5Tests
	$(JAVA) com.Ostermiller.util.SizeLimitInputStreamTests
	$(JAVA) com.Ostermiller.util.SignificantFiguresTests
	$(JAVA) com.Ostermiller.util.StraightStreamReaderTests
	$(JAVA) com.Ostermiller.util.ConcatTests
	$(JAVA) com.Ostermiller.util.ParallelizerTests
	$(JAVA) com.Ostermiller.util.StringHelperTests
	$(JAVA) com.Ostermiller.util.ExecHelperTests
	$(JAVA) com.Ostermiller.util.ArrayHelperTests
	$(JAVA) com.Ostermiller.util.CmdLnTests
	@rm -f out.txt

.PHONY: update
update: 
	ant update
	
release: src/* utils.jar randpass.jar .htaccess install.sh
	@./release.sh $?
	@touch release

.PHONY: install
install:
	@./install.sh

htmlsource: *.java *.properties *.lex source.sh *.bte
	ant syntax
	cd src && ../source.sh
	cp *.bte *.css install.sh *.jar src/
	bte src/
	
htaccess: src/.htaccess

src/.htaccess: *.java *.properties *.lex src/doc htmlsource
	./genhtaccess.sh && cp .htaccess src/

	
	
	