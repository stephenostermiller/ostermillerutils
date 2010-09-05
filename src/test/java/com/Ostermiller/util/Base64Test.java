/*
 * Copyright (C) 2004-2010 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Java+Utilities This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * See LICENSE.txt for details.
 */
package com.Ostermiller.util;

import junit.framework.TestCase;
import java.util.*;

/**
 * Test cases for Base64
 */
public class Base64Test extends TestCase {

	public void testEmpty() {
		encodeAndDecode("", new byte[] {});
	}

	public void testH() {
		encodeAndDecode("aA==", new byte[] { 'h' });
	}

	public void testTe() {
		encodeAndDecode("dGU=", new byte[] { 't', 'e' });
	}

	public void testCob() {
		encodeAndDecode("Y29i", new byte[] { 'c', 'o', 'b' });
	}

	public void testRandom() {
		for (int i = 0; i < 1024; i++) {
			byte[] before = randBytes();
			byte[] after = Base64.decodeToBytes(Base64.encodeToString(before));
			assertTrue(byteArraysEqual(before, after));
		}
	}

	private void encodeAndDecode(String encoded, byte[] decoded) {
		String enc = Base64.encodeToString(decoded);
		assertEquals(encoded, enc);
		byte[] b = Base64.decodeToBytes(encoded);
		assertTrue(byteArraysEqual(b, decoded));
	}

	private static boolean byteArraysEqual(byte[] b1, byte[] b2) {
		if (b1.length != b2.length) return false;
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i]) return false;
		}
		return true;
	}

	private static byte[] randBytes() {
		Random rand = new Random();
		byte[] bytes = new byte[rand.nextInt(128) * 3];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (rand.nextInt() & 0xff);
		}
		return bytes;
	}
}
