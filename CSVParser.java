/*
 * Read files in comma separated value format.
 * Copyright (C) 2001 Stephen Ostermiller <utils@Ostermiller.com>
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
import java.util.*;

/**
 * Read files in comma separated value format.
 *
 * CSV is a file format used as a portable representation of a database.
 * Each line is one entry or record and the fields in a record are separated by commas.
 * Commas may be preceded or followed by arbitrary space and/or tab characters which are
 * ignored.
 * <P>
 * If field includes a comma or a new line, the whole field must be surrounded with double quotes.
 * When the field is in quotes, any quote literals must be escaped by \" Backslash
 * literals must be escaped by \\.  Otherwise a backslash an the character following it
 * will be treated as the following character, ie."\n" is equivelent to "n".  Other escape
 * sequences may be set using the setEscapes() method.  Text that comes after quotes that have
 * been closed but come before the next comma will be ignored.
 * <P>
 * Empty fields are returned as as String of length zero: "".  The following line has four empty
 * fields and two non-empty fields in it.  There is an empty field on each end, and two in the
 * middle.<br>
 * <pre>,second,, ,fifth,</pre>
 * <P>
 * Blank lines are always ignored.  Other lines will be ignored if they start with a
 * comment character as set by the setCommentStart() method.
 * <P>
 * An example of how CVSLexer might be used:
 * <pre>
 * CSVLexer shredder = new CSVLexer(System.in);
 * shredder.setCommentStart("#;!");
 * shredder.setEscapes("nrtf", "\n\r\t\f");
 * String t;
 * while ((t = shredder.getNextToken()) != null) {
 *     System.out.println("" + shredder.getLineNumber() + " " + t);
 * }
 * </pre>
 *
 */
public class CSVParser {

    /**
     * Does all the dirty work.
     * Calls for new tokens are routed through
     * this object.
     */
    private CSVLexer lexer;

    /**
     * Token cache.  Used for when we request a token
     * from the lexer but can't return it because its
     * on the next line.
     */
    private String tokenCache;

    /**
     * Line cache.  The line number that goes along with
     * the tokenCache.  Not valid if the tokenCache is
     * null.
     */
    private int lineCache;

    /**
     * The line number the last token came from, or -1 if
     * no tokens have been returned.
     */
    private int lastLine = -1;

    /**
     * Create a parser to parse comma separated values from
     * an InputStream.
     *
     * @param in stream that contains comma separated values.
     */
    public CSVParser(java.io.InputStream in) {
        lexer = new CSVLexer(in);
    }

	/**
     * Create a parser to parse comma separated values from
     * a Reader.
     *
     * @param in reader that contains comma separated values.
     */
    public CSVParser(java.io.Reader in) {
        lexer = new CSVLexer(in);
    }

    /**
     * get the next value.
     *
     * @return the next value or null if there are no more values.
     */
    public String nextValue() throws IOException {
        if (tokenCache == null){
            tokenCache = lexer.getNextToken();
            lineCache = lexer.getLineNumber();
        }
        lastLine = lineCache;
        String result = tokenCache;
        tokenCache = null;
        return result;
	}

    /**
     * Get the line number that the last token came from.
     *
	 * @return line number or -1 if no tokens have been returned yet.
     */
    public int lastLineNumber(){
        return lastLine;
    }

    /**
     * Get all the values from a line.
     *
     * @return all the values from the line or null if there are no more values.
     */
    public String[] getLine() throws IOException{
        int lineNumber = -1;
        Vector v = new Vector();
        if (tokenCache != null){
            v.add(tokenCache);
            lineNumber = lineCache;
        }
        while ((tokenCache = lexer.getNextToken()) != null
			&& (lineNumber == -1 || lexer.getLineNumber() == lineNumber)) {
            v.add(tokenCache);
            lineNumber = lexer.getLineNumber();
        }
        if (v.size() == 0){
            return null;
        }
        lastLine = lineNumber;
        lineCache = lexer.getLineNumber();
        String[] result = new String[v.size()];
        return ((String[])v.toArray(result));
    }

    /**
     * Specify escape sequences and their replacements.
     * Escape sequences set here are in addition to \\ and \".
     * \\ and \" are always valid escape sequences.  This method
     * allows standard escape sequenced to be used.  For example
     * "\n" can be set to be a newline rather than an 'n'.
     * A common way to call this method might be:<br>
     * <code>setEscapes("nrtf", "\n\r\t\f");</code><br>
     * which would set the escape sequences to be the Java escape
     * sequences.  Characters that follow a \ that are not escape
     * sequences will still be interpreted as that character.<br>
     * The two arguemnts to this method must be the same length.  If
     * they are not, the longer of the two will be truncated.
     *
     * @param escapes a list of characters that will represent escape sequences.
     * @param replacements the list of repacement characters for those escape sequences.
     */
    public void setEscapes(String escapes, String replacements){
        lexer.setEscapes(escapes, replacements);
    }

    /**
     * Set the characters that indicate a comment at the beginning of the line.
     * For example if the string "#;!" were passed in, all of the following lines
     * would be comments:<br>
     * <pre> # Comment
     * ; Another Comment
     * ! Yet another comment</pre>
     * By default there are no comments in CVS files.  Commas and quotes may not be
     * used to indicate comment lines.
     *
     * @param commentDelims list of characters a comment line may start with.
     */
    public void setCommentStart(String commentDelims){
        lexer.setCommentStart(commentDelims);
    }

    /**
     * Get the number of the line from which the last value was retreived.
     *
     * @return line number or -1 if no tokens have been returned.
     */
    public int getLastLineNumber(){
        return lastLine;
    }

    /**
     * Parse the given file for comma separatad values and print the results
     * to System.out.
     *
     * @param args First argument is the file name.  System.in used if no filename given.
     */
    private static void main(String[] args) {
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
            CSVParser p  = new CSVParser(in);
            p.setCommentStart("#;!");
            p.setEscapes("nrtf", "\n\r\t\f");
            String[] t;
            while ((t = p.getLine()) != null) {
                for (int i=0; i<t.length; i++){
                    System.out.print('"' + t[i] + '"');
                    if (i<t.length-1){
                        System.out.print(", ");
                    }
				}
                System.out.println();
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
