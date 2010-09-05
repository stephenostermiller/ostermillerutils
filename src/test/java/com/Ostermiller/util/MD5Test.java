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

import java.io.UnsupportedEncodingException;
import junit.framework.TestCase;

/**
 * MD5 regression test.
 */
public class MD5Test extends TestCase {

	private void runTest(String output, byte[] input){
		assertEquals(output,  MD5.getHashString(input));
	}

	private static byte[] getBytes(String s){
		try {
			return s.getBytes("ASCII");
		} catch (UnsupportedEncodingException uex){
			throw new RuntimeException(uex);
		}
	}

	public void testEmpty(){
		runTest("d41d8cd98f00b204e9800998ecf8427e", getBytes(""));
	}

	public void testA(){
		runTest("0cc175b9c0f1b6a831c399e269772661", getBytes("a"));
	}

	public void testAbc(){
		runTest("900150983cd24fb0d6963f7d28e17f72", getBytes("abc"));
	}

	public void testMessageDigest(){
		runTest("f96b697d7cb7938d525a2f31aaf161d0", getBytes("message digest"));
	}

	public void testAlphabet(){
		runTest("c3fcd3d76192e4007dfb496cca67e13b", getBytes("abcdefghijklmnopqrstuvwxyz"));
	}

	public void testUpperAlphabetLowerAlphabetNumbers(){
		runTest("d174ab98d277d9f5a5611c2c9f419d9f", getBytes("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
	}

	public void testLonger(){
		runTest("57edf4a22be3c955ac49da2e2107b67a", getBytes("12345678901234567890123456789012345678901234567890123456789012345678901234567890"));
	}
}
