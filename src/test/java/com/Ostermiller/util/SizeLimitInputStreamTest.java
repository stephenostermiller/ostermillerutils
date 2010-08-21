/*
 * Copyright (C) 2004-2007 Stephen Ostermiller
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

import junit.framework.TestCase;
import java.io.*;

/**
 * Regression test for SizeLimitInputStreams.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/SizeLimitInputStream.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.04.00
 */
public class SizeLimitInputStreamTest extends TestCase {
	
	public void testSizeThreeOfFour(){
		try {
			SizeLimitInputStream slis;

			slis = new SizeLimitInputStream(
				new ByteArrayInputStream(
					new byte[] {
						1,2,3,4
					}
				),
				3
			);
			assertEquals(1, slis.read());
			assertEquals(2, slis.read());
			assertEquals(3, slis.read());
			assertEquals(-1, slis.read());
		} catch (IOException x){
			throw new RuntimeException(x);
		}
	}
		
	public void testSizeSixOfNine(){
		try {
			SizeLimitInputStream slis = new SizeLimitInputStream(
				new ByteArrayInputStream(
					new byte[] {
						1,2,3,4,5,6,7,8,9
					}
				),
				6
			);
			assertEquals(1, slis.read());
			assertEquals(4, slis.read(new byte[4]));
			assertEquals(1, slis.read(new byte[4]));
			assertEquals(-1, slis.read());
		} catch (IOException x){
			throw new RuntimeException(x);
		}
	}
		
	public void testManySmallStreamsFromSameBaseStream(){
		try {
			InputStream in = new ByteArrayInputStream(
				("one"+"two"+"three"+"four"+"five"+"six"+"seven").getBytes("ASCII")
			);
			assertEquals("one", readFully(new SizeLimitInputStream(in,3)));
			assertEquals("", readFully(new SizeLimitInputStream(in,0)));
			assertEquals("two", readFully(new SizeLimitInputStream(in,3)));
			assertEquals("three", readFully(new SizeLimitInputStream(in,5)));
			assertEquals("four", readFully(new SizeLimitInputStream(in,4)));
			assertEquals("five", readFully(new SizeLimitInputStream(in,4)));
			assertEquals("six", readFully(new SizeLimitInputStream(in,3)));
			assertEquals("s", readFully(new SizeLimitInputStream(in,1)));
			assertEquals("even", readFully(new SizeLimitInputStream(in,4)));
		} catch (IOException x){
			throw new RuntimeException(x);
		}
	}

	private static String readFully(InputStream in) throws IOException {
		StringBuffer sb = new StringBuffer();
		int read;
		while ((read = in.read()) != -1){
			sb.append((char)read);
		}
		return sb.toString();
	}
}
