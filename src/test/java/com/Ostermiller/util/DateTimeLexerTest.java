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

import java.io.IOException;
import com.Ostermiller.util.DateTimeToken.DateTimeTokenType;
import java.io.StringReader;
import junit.framework.TestCase;

/**
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.00
 */
public class DateTimeLexerTest extends TestCase {

	public void testInt(){
		assertEquals(DateTimeTokenType.NUMBER, getFirstToken("1").getType());
	}

	public void testAscii(){
		assertEquals(DateTimeTokenType.WORD, getFirstToken("abc").getType());
	}

	public void testLatin1(){
		DateTimeToken t = getFirstToken("M\u00e4r");
		assertEquals(DateTimeTokenType.WORD, t.getType());
		assertEquals("M\u00e4r", t.getText());
	}


	private DateTimeToken getFirstToken(String s){
		try {
			return new DateTimeLexer(new StringReader(s)).getNextToken();
		} catch (IOException iox){
			throw new RuntimeException(iox);
		}
	}
}