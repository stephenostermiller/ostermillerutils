files="Base64.java
BinaryDataException.java
BrowserCommandLexer.java
Browser.java
BufferOverflowException.java
CGILexer.java
CGIParser.java
CircularBufferTests.java
CircularByteBuffer.java"
stuff="CircularCharBuffer.java
CircularObjectBuffer.java
CSVLexer.java
CSVParse.java
CSVParser.java
CSVPrinter.java
CSVPrint.java
CSVTest.java
ExcelCSVLexer.java
ExcelCSVParser.java
ExcelCSVPrinter.java
LineEnds.java
MD5InputStream.java
MD5.java
MD5OutputStream.java
PasswordDialog.java
PasswordVerifier.java
RandPass.java
SignificantFigures.java
StraightStreamReader.java
StringHelper.java
StringTokenizer.java
Tabs.java
TokenizerTests.java"

for file in $files
do
    generated='0'
    if [ ! -e $file ]
    then
        echo "$file does not exists."
    elif [ -e ${file%.java}.lex ]
    then
        generated='1'
    elif [ `egrep -l "[ \t]+$" $file` ]
    then
        echo "Removing trailing white space from $file."
        mv "$file" temp
        sed 's/[ \t]*$//' temp > "$file"
    fi
    if [ $generated -eq '0' ]
    then
        java com.Ostermiller.util.Tabs -gtv $file
    fi    
done

