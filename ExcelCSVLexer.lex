/* ExcelCSVLexer.java is a generated file.	You probably want to
 * edit ExcelCSVLexer.lex to make changes.	Use JFlex to generate it.
 * JFlex may be obtained from 
 * <a href="http://jflex.de">the JFlex website</a>.
 * Once JFlex is in your classpath run<br>
 * java JFlex.Main ExcelCSVLexer.lex<br>
 * You will then have a file called ExcelCSVLexer.java
 */
 
/*
 * Read files in comma separated value format.
 * Copyright (C) 2001-2003 Stephen Ostermiller 
 * http://ostermiller.org/contact.pl?regarding=Java+Utilities
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * See COPYING.TXT for details.
 */

package com.Ostermiller.util;
import java.io.*;

/**
 * Read files in comma separated value format as outputted by the Microsoft
 * Excel Spreadsheet program.
 * More information about this class is available from <a href=
 * "http://ostermiller.org/utils/ExcelCSV.html">ostermiller.org</a>.
 * <P>
 * Excel CSV is a file format used as a portable representation of a database.
 * Each line is one entry or record and the fields in a record are separated by commas.
 * <P>
 * If field includes a comma or a new line, the whole field must be surrounded with double quotes.
 * When the field is in quotes, any quote literals must be escaped by two quotes ("").
 * Text that comes after quotes that have been closed but come before the next comma will be ignored.
 * <P>
 * Empty fields are returned as as String of length zero: "".  The following line has three empty
 * fields and three non-empty fields in it.  There is an empty field on each end, and one in the
 * middle.	One token is returned as a space.<br>
 * <pre>,second,, ,fifth,</pre>
 * <P>
 * Blank lines are always ignored.	Other lines will be ignored if they start with a
 * comment character as set by the setCommentStart() method.
 * <P>
 * An example of how CVSLexer might be used:
 * <pre>
 * ExcelCSVLexer shredder = new ExcelCSVLexer(System.in);
 * String t;
 * while ((t = shredder.getNextToken()) != null) {
 *	   System.out.println("" + shredder.getLineNumber() + " " + t);
 * }
 * </pre>
 * <P>
 * The CSV that Excel outputs differs from the genrally accepted standard CSV standard in several respects:
 * <ul><li>Leading and trailing whitespace is significant.</li>
 * <li>A backslash is not a special character and is not used to escape anything.</li>
 * <li>Quotes inside quoted strings are escaped with a double quote rather than a backslash.</li>
 * <li>Excel may convert data before putting it in CSV format:<ul>
 * <li>Tabs are converted to a single space.</li>
 * <li>New lines in the data are always represented as the unix new line. ("\n")</li>
 * <li>Numbers that are greater than 12 digits may be represented in trunkated
 * scientific notation form.</li></ul>
 * This parser does not attempt to fix these excel conversions, but users should be aware
 * of them.</li></ul>
 */

%%
%class ExcelCSVLexer
%function getNextToken
%type String
%{
	/**
	 * Prints out tokens and line numbers from a file or System.in.
	 * If no arguments are given, System.in will be used for input.
	 * If more arguments are given, the first argument will be used as
	 * the name of the file to use as input
	 *
	 * @param args program arguments, of which the first is a filename
	 */
	public static void main(String[] args) {
		InputStream in;
		try {
			if (args.length > 0){
				File f = new File(args[0]);
				if (f.exists()){
					if (f.canRead()){
						in = new FileInputStream(f);
					} else {
						throw new IOException("Could not open " + args[0]);
					}
				} else {
					throw new IOException("Could not find " + args[0]);
				}
			} else {
				in = System.in;
			}
			ExcelCSVLexer shredder = new ExcelCSVLexer(in);
			String t;
			while ((t = shredder.getNextToken()) != null) {
				System.out.println("" + shredder.getLineNumber() + " " + t);
			}
		} catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	private char delimiter = ',';
	/**
	 * Change this Lexer so that it uses a new delimeter.
	 * <p>
	 * The inital character is a comma, the delimiter cannot be changed
	 * to a quote or other character that has special meaning in CSV.
	 * 
	 * @param newDelim delimiter to which to switch.
	 * @throws BadDelimeterException if the character cannot be used as a delimiter.
	 */
	public void changeDelimiter(char newDelim) throws BadDelimeterException {
		if (newDelim == delimiter) return; // no need to do anything.
		// 'a' and 'b' should always be safe delimiters unless already the delimiter.
		if (yycmap[newDelim] != yycmap[(delimiter == 'a')?'b':'a']){
			throw new BadDelimeterException(newDelim + " is not a safe delimiter.");
		}
		char temp = yycmap[newDelim];
		yycmap[newDelim] = yycmap[delimiter];
		yycmap[delimiter] = temp;
		newDelim = newDelim;
	}

	private String unescape(String s){       
		if (s.indexOf('\"', 1) == s.length()-1){
			return s.substring(1, s.length()-1);
		}
		StringBuffer sb = new StringBuffer(s.length());
		for (int i=1; i<s.length()-1; i++){
			char c = s.charAt(i);
			char c1 = s.charAt(i+1);
			if (c == '\"' && c1 == '\"'){
				i++;
				sb.append("\"");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	} 
		
	private String commentDelims = ""; 
	
	/**
	 * Set the characters that indicate a comment at the beginning of the line.
	 * For example if the string "#;!" were passed in, all of the following lines
	 * would be comments:<br>
	 * <pre> # Comment
	 * ; Another Comment
	 * ! Yet another comment</pre>
	 * By default there are no comments in Excel CVS files.  Commas and quotes may not be
	 * used to indicate comment lines.
	 *
	 * @param commentDelims list of characters a comment line may start with.
	 */
	public void setCommentStart(String commentDelims){
		this.commentDelims = commentDelims;
	}
	
    private boolean addLine = true;
    private int lines = 0;
    
	/**
	 * Get the line number that the last token came from.
     * <p>
     * New line breaks that occur in the middle of a token are not
     * counted in the line number count.
     * <p> 
     * If no tokens have been returned, the line number is undefined. 
	 *
	 * @return line number of the last token.
	 */
	public int getLineNumber(){
		return lines;
	}
%}
%unicode

%state BEFORE
%state AFTER
%state COMMENT

CR=([\r])
LF=([\n])
EOL=({CR}|{LF}|{CR}{LF})

/* To change the default delimeter, change the comma in the next four lines */
Separator=([\,])
NotCommaEOLQuote=([^\r\n\,\"])
NotCommaEOL=([^\,\r\n])
IgnoreAfter=(([^\r\n\,])*)

FalseLiteral=([\"]([^\"]|[\"][\"])*)
StringLiteral=({FalseLiteral}[\"])
Value=({NotCommaEOLQuote}(({NotCommaEOL}*))?)

%%

<YYINITIAL> {Value} {
    if (addLine) {
        lines++;
        addLine = false;
    }
	String text = yytext();
	if (commentDelims.indexOf(text.charAt(0)) == -1){
		yybegin(AFTER);
		return(text);
	} else {
		yybegin(COMMENT);
	}
}
<YYINITIAL> {Separator} {
    if (addLine) {
        lines++;
        addLine = false;
    }
	yybegin(BEFORE);   
	return("");
}
<YYINITIAL> {StringLiteral} {
    if (addLine) {
        lines++;
        addLine = false;
    }
	yybegin(AFTER);  
	return(unescape(yytext()));    
}
<YYINITIAL> {FalseLiteral} {
    if (addLine) {
        lines++;
        addLine = false;
    }
	yybegin(YYINITIAL);  
	return(yytext());    
}
<BEFORE> {Separator} {
	yybegin(BEFORE);   
	return("");
}
<BEFORE> {StringLiteral} {
	yybegin(AFTER);  
	return(unescape(yytext()));    
}
<BEFORE> {FalseLiteral} {
	yybegin(YYINITIAL);  
	return(yytext());    
}
<BEFORE> {Value} {
	yybegin(AFTER);  
	return(yytext());
}
<BEFORE> ({EOL}) {
	yybegin(YYINITIAL);
    addLine = true;
	return("");
}
<BEFORE> <<EOF>> {
	yybegin(YYINITIAL);
    addLine = true;
	return("");
}
<AFTER> {Separator} {
	yybegin(BEFORE);
}
<AFTER, COMMENT, YYINITIAL> ({EOL}) {
    addLine = true;
	yybegin(YYINITIAL);
}
<AFTER> {IgnoreAfter} {
}
<COMMENT> (([^\r\n])*) {
}
