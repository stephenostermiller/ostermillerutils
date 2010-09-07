/* DateTimeLexer.java is a generated file.  You probably want to
 * edit DateTimeLexer.lex to make changes.  Use JFlex to generate it.
 * To generate DateTimeLexer.java
 * JFlex may be obtained from
 * <a href="http://jflex.de">the JFlex website</a>.
 * JFlex 1.4 or later is required.
 * Run:<br>
 * jflex  DateTimeLexer.lex<br>
 * You will then have a file called DateTimeLexer.java
 */

/*
 * Copyright (C) 2010 Stephen Ostermiller
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
 * See LICENSE.txt for details.
 */

package com.Ostermiller.util;

import java.io.*;

/**
 * DateTimeLexer is a Java date time lexer.  Created with JFlex.  An example of how it is used:
 *  <CODE>
 *  <PRE>
 *  DateTimeLexer shredder = new DateTimeLexer(System.in);
 *  DateTimeToken t;
 *  while ((t = shredder.getNextToken()) != null){
 *	  System.out.println(t);
 *  }
 *  </PRE>
 *  </CODE>
 *
 * @see DateTimeToken
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */

%%

%class DateTimeLexer
%apiprivate
%function getToken
%type DateTimeToken
%unicode

%{
    /**
     * Return the next token 
     *
     * @return the next token
     * @throws IOException if an error occurs while reading the properties.
     */
    public DateTimeToken getNextToken() throws IOException {
        return getToken();
    }
%}

Integer=([0-9]+)
Word=([a-zA-Z]+)
Punctuation=([ \:\-\/\,]+)
OrdinalSt=([23]?[1][sS][tT])
OrdinalNd=([23]?[2][nN][dD])
OrdinalRd=([2]?[3][rR][dD])
OrdinalTh=(([456789]|([1][023456789])|[2][0456789]|"30")[tT][hH])
Ordinal=(|||)

%%

<YYINITIAL> {Integer} {
	return new DateTimeToken(yytext(), DateTimeToken.DateTimeTokenType.NUMBER);
}

<YYINITIAL> {Word} {
	return new DateTimeToken(yytext(), DateTimeToken.DateTimeTokenType.WORD);
}

<YYINITIAL> {Punctuation} {
	return new DateTimeToken(yytext(), DateTimeToken.DateTimeTokenType.PUNCTUATION);
}

<YYINITIAL> [^] {
	return new DateTimeToken(yytext(), DateTimeToken.DateTimeTokenType.ERROR);
}



