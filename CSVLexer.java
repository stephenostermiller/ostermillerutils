/* CSVLexer.java is a generated file.  You probably want to
 * edit CSVLexer to make changes.  Use JFlex to generate it.
 * JFlex may be obtained from 
 * <a href="http://www.jflex.de">the JFlex website</a>.
 * Once JFlex is in your classpath run<br>
 * java JFlex.Main CSVLexer<br>
 * You will then have a file called CSVLexer.java
 */
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


public class CSVLexer {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 256;
	private final int YY_EOF = 257;

    /**
     * Prints out tokens and line numbers from a file or System.in.
     * If no arguments are given, System.in will be used for input.
     * If more arguments are given, the first argument will be used as
     * the name of the file to use as input
     *
     * @param args program arguments, of which the first is a filename
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
            CSVLexer shredder = new CSVLexer(in);
            shredder.setCommentStart("#;!");
            shredder.setEscapes("nrtf", "\n\r\t\f");
            String t;
            while ((t = shredder.getNextToken()) != null) {
                System.out.println("" + shredder.getLineNumber() + " " + t);
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    private String escapes = "";
    private String replacements = "";
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
        int length = escapes.length();
        if (replacements.length() < length){
            length = replacements.length();
        }
        this.escapes = escapes.substring(0, length);
        this.replacements = replacements.substring(0, length);
    } 
    private String unescape(String s){
        if (s.indexOf('\\') == -1){
            return s.substring(1, s.length()-1);
        }
        StringBuffer sb = new StringBuffer(s.length());
        for (int i=1; i<s.length()-1; i++){
            char c = s.charAt(i);
            if (c == '\\'){
                char c1 = s.charAt(++i);
                int index;
                if (c1 == '\\' || c1 == '\"'){
                    sb.append(c1);
                } else if ((index = escapes.indexOf(c1)) != -1){
                    sb.append(replacements.charAt(index));
                } else {
                    sb.append(c1);
                }                
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
     * By default there are no comments in CVS files.  Commas and quotes may not be
     * used to indicate comment lines.
     *
     * @param commentDelims list of characters a comment line may start with.
     */
    public void setCommentStart(String commentDelims){
        this.commentDelims = commentDelims;
    }
    /**
     * Get the number of the line from which the last value was retreived.
     */
    public int getLineNumber(){
        return yyline + 1;
    }
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public CSVLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	public CSVLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CSVLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int BEFORE = 1;
	private final int YYINITIAL = 0;
	private final int COMMENT = 3;
	private final int AFTER = 2;
	private final int yy_state_dtrans[] = {
		0,
		7,
		10,
		12
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NOT_ACCEPT,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NOT_ACCEPT,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NOT_ACCEPT,
		/* 24 */ YY_NOT_ACCEPT,
		/* 25 */ YY_NOT_ACCEPT
	};
	private int yy_cmap[] = unpackFromString(1,258,
"2:9,1,5,2,1,7,2:18,1,2,3,2:9,4,2:47,6,2:163,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,26,
"0,1,2,3,1:3,4,5,1,6,1,7,8,9,8,10,11,12,13,2,14,15,3,16,5")[0];

	private int yy_nxt[][] = unpackFromString(17,8,
"1,2,3,13,4,5,3,14,-1:9,20,-1:3,5,-1,14,-1,23,3:2,-1:2,3,-1,1,16,8,13,4,9,8," +
"17,-1,25,8:2,-1:2,8,-1,1,18,21:2,11,5,21,14,1,19,22:3,5,22,14,-1,13:2,6,13:" +
"2,24,13,-1:5,5,-1:3,16,-1:11,9,-1:3,18,21:2,-1,5,21,14,-1,19,22:3,5,22,14,-" +
"1,21:3,-1:2,21,-1:2,22:4,-1,22,-1:2,13:2,15,13:2,24,13");

	public String  getNextToken ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {
				return null;
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{
    yybegin(BEFORE);
}
					case -3:
						break;
					case 3:
						{
    String text = yytext();    
    if (commentDelims.indexOf(text.charAt(0)) == -1){
        yybegin(AFTER);
        return(text);
    } else {
        yybegin(COMMENT);
    }
}
					case -4:
						break;
					case 4:
						{
    yybegin(BEFORE);   
	return("");
}
					case -5:
						break;
					case 5:
						{
    yybegin(YYINITIAL);
}
					case -6:
						break;
					case 6:
						{
    yybegin(AFTER);  
	return(unescape(yytext()));    
}
					case -7:
						break;
					case 7:
						{
}
					case -8:
						break;
					case 8:
						{
    yybegin(AFTER);  
    return(yytext());
}
					case -9:
						break;
					case 9:
						{ 
    yybegin(YYINITIAL);
    return("");
}
					case -10:
						break;
					case 10:
						{
}
					case -11:
						break;
					case 11:
						{
	yybegin(BEFORE);
}
					case -12:
						break;
					case 12:
						{
}
					case -13:
						break;
					case 14:
						{
    yybegin(YYINITIAL);
}
					case -14:
						break;
					case 15:
						{
    yybegin(AFTER);  
	return(unescape(yytext()));    
}
					case -15:
						break;
					case 16:
						{
}
					case -16:
						break;
					case 17:
						{ 
    yybegin(YYINITIAL);
    return("");
}
					case -17:
						break;
					case 18:
						{
}
					case -18:
						break;
					case 19:
						{
}
					case -19:
						break;
					case 21:
						{
}
					case -20:
						break;
					case 22:
						{
}
					case -21:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
