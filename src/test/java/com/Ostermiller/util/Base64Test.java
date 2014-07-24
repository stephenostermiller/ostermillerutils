/*
 * Copyright (C) 2004-2014 Stephen Ostermiller
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

import junit.framework.TestCase;

/**
 * Test cases for Base64
 */
public class Base64Test extends TestCase {

	public void testEmpty() {
		assertBase64Equal("", new byte[] {});
	}

	public void testH() {
		assertBase64Equal("aA==", new byte[] { 'h' });
	}

	public void testTe() {
		assertBase64Equal("dGU=", new byte[] { 't', 'e' });
	}

	public void testCob() {
		assertBase64Equal("Y29i", new byte[] { 'c', 'o', 'b' });
	}

	public void testRandom() {
		for (int i = 0; i < 1024; i++) {
			byte[] before = randBytes();
			byte[] after = Base64.decodeToBytes(Base64.encodeToString(before));
			assertTrue(byteArraysEqual(before, after));
		}
	}

	public void testIsBase64Blank() {
		assertNotBase64("");
	}

	public void testIsBase64Y29() {
		assertNotBase64("Y29");
	}

	public void testIsBase64Y29i() {
		assertBase64("Y29i");
	}

	public void testIsBase64WithSpaces() {
		assertBase64(" Y 2 9 i ");
	}

	public void testIsBase64WithSymbols() {
		assertNotBase64("Y29&");
	}

	public void testIsBase64dGU() {
		assertNotBase64("dGU");
	}

	public void testIsBase64dGUEquals() {
		assertBase64("dGU=");
	}

	public void testIsBase64dGUEqualsEquals() {
		assertNotBase64("dGU==");
	}

	public void testIsBase64aA() {
		assertNotBase64("aA");
	}

	public void testIsBase64aAEquals() {
		assertNotBase64("aA=");
	}

	public void testIsBase64aAEqualsEquals() {
		assertBase64("aA==");
	}

	public void testIsBase64Unicode() {
		assertNotBase64("\uffff");
	}

	private void assertBase64(String s){
		try {
			assertTrue(Base64.isBase64(s));
			assertTrue(Base64.isBase64(s.toCharArray()));
			assertTrue(Base64.isBase64(new StringReader(s)));
			assertTrue(Base64.isBase64(s.getBytes("UTF-8")));
			assertTrue(Base64.isBase64(new ByteArrayInputStream(s.getBytes("UTF-8"))));
		} catch (IOException iox) {
			throw new RuntimeException(iox);
		}
	}

	private void assertNotBase64(String s){
		try {
			assertFalse(Base64.isBase64(s));
			assertFalse(Base64.isBase64(s.toCharArray()));
			assertFalse(Base64.isBase64(new StringReader(s)));
			assertFalse(Base64.isBase64(s.getBytes("UTF-8")));
			assertFalse(Base64.isBase64(new ByteArrayInputStream(s.getBytes("UTF-8"))));
		} catch (IOException iox) {
			throw new RuntimeException(iox);
		}
	}

	private void assertBase64Equal(String encoded, byte[] decoded) {
		String enc = Base64.encodeToString(decoded);
		assertEquals(encoded, enc);
		byte[] b = Base64.decodeToBytes(encoded);
		assertTrue(byteArraysEqual(b, decoded));
	}

	private static boolean byteArraysEqual(byte[] b1, byte[] b2) {
		if (b1.length != b2.length)
			return false;
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] != b2[i])
				return false;
		}
		return true;
	}

	private static byte[] randBytes() {
		Random rand = new Random(123456789);
		byte[] bytes = new byte[rand.nextInt(128) * 3];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (rand.nextInt() & 0xff);
		}
		return bytes;
	}
}
