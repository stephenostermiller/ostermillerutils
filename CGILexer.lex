/* CGILexer.java is a generated file.  You probably want to
 * edit CGILexer.lex to make changes.  Use JFlex to generate it.
 * JFlex may be obtained from 
 * <a href="http://jflex.de">the JFlex website</a>.
 * Once JFlex is in your classpath run<br>
 * java --skel cgi.jflex.skel JFlex.Main CGILexer.lex<br>
 * You will then have a file called CGILexer.java
 */
 
/*
 * Parse CGI query data.
 * Copyright (C) 2001 Stephen Ostermiller 
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
 * Parses query string data from a CGI request into name value pairs.
 * <p>
 * This class has a <a href="http://ostermiller.org/utils/CGIParser.html">website</a>
 * where more information and examples are
 * available.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */
%%
%class CGILexer
%function nextToken
%type String
%{
    /**
     * Prints out tokens and line numbers from a file or System.in.
     * If no arguments are given, System.in will be used for input.
     * If more arguments are given, the first argument will be used as
     * the name of the file to use as input
     *
     * @param args program arguments, of which the first is a filename
	 *
	 * @since ostermillerutils 1.00.00
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
            CGILexer shredder = new CGILexer(in);
            String t;
            while ((t = shredder.nextToken()) != null) {
                System.out.println(t);
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    private StringBuffer token = new StringBuffer();
%}

%unicode

%state VALUE

Escape=("%"[0-9A-Fa-f][0-9A-Fa-f])
AnyChar=([A]|[^A])

%%

<YYINITIAL> ([\=]) {
    String s = token.toString();
    token.setLength(0);
    yybegin(VALUE);
    return (s);
}

<VALUE> ([\&]) {
    String s = token.toString();
    token.setLength(0);
    yybegin(YYINITIAL);
    return (s);
}

<YYINITIAL, VALUE> {Escape} {
    token.append((char)(Integer.parseInt(yytext().substring(1),16)));
}

<YYINITIAL, VALUE> ([\+]) {
    token.append(" ");
}

<YYINITIAL, VALUE> {AnyChar} {
    token.append(yytext());
}

<YYINITIAL> <<EOF>> {
    if (token.length() > 0) {
        String s = token.toString();
        token.setLength(0);
        yybegin(VALUE);        
        return (s);
    } else {
        return null;
    }
}

<VALUE> <<EOF>> {
    String s = token.toString();
    token.setLength(0);
    yybegin(YYINITIAL);
    return (s);
}

