/*
 * Copyright (C) 2001-2007 Stephen Ostermiller
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

/**
 * A regression test for com.Ostermiller.util.StringTokenizer.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/StringTokenizer.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */
public class StringTokenizerTest extends TestCase {

	public void testBasic(){
		String input = "this is a test";
		java.util.StringTokenizer oldTok = new java.util.StringTokenizer(input);
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input);
		assertEquals(oldTok.countTokens(), newTok.countTokens());
		compareState("Test 1", oldTok, newTok);
		while (oldTok.hasMoreTokens()){
			assertEquals(oldTok.nextToken(), newTok.nextToken());
			compareState("Test 1", oldTok, newTok);
		}
	}

	public void testEmptyInput(){
		String input = "";
		java.util.StringTokenizer oldTok = new java.util.StringTokenizer(input);
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input);
		assertEquals(oldTok.countTokens(), newTok.countTokens());
	}

	public void testNoDelims(){
		String input = "no delimiters";
		java.util.StringTokenizer oldTok = new java.util.StringTokenizer(input, "");
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, "");
		assertEquals(oldTok.countTokens(), newTok.countTokens());
		while (oldTok.hasMoreTokens()){
			assertEquals(oldTok.countTokens(), newTok.countTokens());
			assertEquals(oldTok.nextToken(), newTok.nextToken());
		}
	}

	public void testAbTokens(){
		String input = "AstringB";
		java.util.StringTokenizer oldTok = new java.util.StringTokenizer(input, "AB");
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, "AB");
		assertEquals(oldTok.countTokens(), newTok.countTokens());
		while (oldTok.hasMoreTokens()){
			assertEquals(oldTok.nextToken(), newTok.nextToken());
		}
	}

	public void testAbTokensReturned(){
		String input = "AstringB";
		java.util.StringTokenizer oldTok = new java.util.StringTokenizer(input, "AB", true);
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, "AB", true);
		assertEquals(oldTok.countTokens(), newTok.countTokens());
		while (oldTok.hasMoreTokens()){
			assertEquals(oldTok.nextToken(), newTok.nextToken());
		}
	}

	public void testCgiParse(){
		String input = "someURL?name=value&name=value";
		java.util.StringTokenizer oldTok = new java.util.StringTokenizer(input);
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input);
		assertEquals(oldTok.nextToken("?"), newTok.nextToken("?"));
		assertEquals(oldTok.nextToken("=&"), newTok.nextToken("=&"));
		assertEquals(oldTok.nextToken("=&"), newTok.nextToken("=&"));
		assertEquals(oldTok.nextToken(), newTok.nextToken());
		assertEquals(oldTok.nextToken(), newTok.nextToken());
	}

	public void testCgiParseChangeTokens(){
		String input = "someURL?name=value&name=value";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input);
		assertEquals("someURL", newTok.nextToken("?"));
		newTok.skipDelimiters();
		assertEquals("name", newTok.nextToken("=&"));
		assertEquals("value", newTok.nextToken("=&"));
		assertEquals("name", newTok.nextToken());
		assertEquals("value", newTok.nextToken());
	}

	public void testTokenAndNonTokenDelims(){
		String input = "  (   aaa	\t	* (b+c1 ))";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, " \t\n\r\f", "()+*");
		assertEquals(9, newTok.countTokens());
		assertEquals("(", newTok.nextToken());
		assertEquals("aaa", newTok.nextToken());
		assertEquals("*", newTok.nextToken());
		assertEquals("(", newTok.nextToken());
		assertEquals("b", newTok.nextToken());
		assertEquals("+", newTok.nextToken());
		assertEquals("c1", newTok.nextToken());
		assertEquals(")", newTok.nextToken());
		assertEquals(")", newTok.nextToken());
	}

	public void testCommaDelim(){
		String input = "one,two,,four,five,,,eight,";
		java.util.StringTokenizer  oldTok = new java.util.StringTokenizer(input, ",");
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, ",");
		assertEquals(oldTok.countTokens(), newTok.countTokens());
		while (oldTok.hasMoreTokens()){
			assertEquals(oldTok.nextToken(), newTok.nextToken());
		}
	}

	public void testReturnEmptyTokens(){
		String input = "one,two,,four,five,,,eight";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, ",");
		newTok.setReturnEmptyTokens(true);
		assertEquals(8, newTok.countTokens());
	}

	public void testReturnEmptyTokensBeginAndEnd(){
		String input = ",two,,four,five,,,eight,";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, ",");
		newTok.setReturnEmptyTokens(true);
		assertEquals(9, newTok.countTokens());
	}

	public void testReturnEmptyTokensDelimInput(){
		String input = ",";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, ",");
		newTok.setReturnEmptyTokens(true);
		assertEquals(2, newTok.countTokens());
	}

	public void testReturnEmptyTokensEmptyInput(){
		String input = "";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, ",");
		newTok.setReturnEmptyTokens(true);
		assertEquals(1, newTok.countTokens());
	}

	public void testReturnEmptyTokensDelimitersAsTokens(){
		String input = ",two,,four,five,,,eight,";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, ",", true);
		newTok.setReturnEmptyTokens(true);
		assertEquals(17, newTok.countTokens());
	}

	public void testReturnEmptyTokensSetHalfwayThrough(){
		String input = ",one,,,four,";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, ",", null, false);
		assertEquals("one", newTok.nextToken());
		newTok.setReturnEmptyTokens(true);
		assertEquals(4, newTok.countTokens());
	}

	public void testReturnEmptyTokensNoneLeftOver(){
		String input = "list=";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, "=");
		newTok.setReturnEmptyTokens(true);
		assertEquals("list", newTok.nextToken());
		newTok.skipDelimiters();
		assertEquals("", newTok.nextToken(","));
		assertEquals(0, newTok.countTokens());
	}

	public void testReturnEmptyTokensOneLeftOver(){
		String input = "list=,";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, "=");
		newTok.setReturnEmptyTokens(true);
		assertEquals("list", newTok.nextToken());
		newTok.skipDelimiters();
		assertEquals("", newTok.nextToken(","));
		assertEquals(1, newTok.countTokens());
	}

	public void testReturnEmptyTokensTwoLeftOver(){
		String input = "list=,two,";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input, "=");
		newTok.setReturnEmptyTokens(true);
		assertEquals("list", newTok.nextToken());
		newTok.skipDelimiters();
		assertEquals("", newTok.nextToken(","));
		assertEquals(2, newTok.countTokens());
	}

	public void testToArray(){
		String input = "this is a test";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input);
		String[] tokens = newTok.toArray();
		newTok.setText(input);
		for (int i=0; i<tokens.length; i++){
			assertEquals(tokens[i], newTok.nextToken());
		}
	}

	public void testRestOfString(){
		String input = "token rest of string";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input);
		assertEquals("token", newTok.nextToken());
		newTok.skipDelimiters();
		assertEquals("rest of string", newTok.restOfText());
		assertFalse(newTok.hasMoreTokens());
	}

	public void testPeek(){
		String input = "testing the peek method";
		com.Ostermiller.util.StringTokenizer newTok = new com.Ostermiller.util.StringTokenizer(input);
		assertEquals(4, newTok.countTokens());
		assertEquals("testing", newTok.peek());
		assertEquals(4, newTok.countTokens());
		assertEquals("testing", newTok.nextToken());
		assertEquals(3, newTok.countTokens());
		assertEquals("the", newTok.peek());
		assertEquals("the", newTok.peek());
		assertEquals("the", newTok.nextToken());
		assertEquals("peek", newTok.peek());
		assertEquals("peek", newTok.nextToken());
		assertTrue(newTok.hasMoreTokens());
		assertEquals("method", newTok.peek());
		assertTrue(newTok.hasMoreTokens());
		assertEquals("method", newTok.nextToken());
		assertFalse(newTok.hasMoreTokens());
	}

	private static void compareState(String testName, java.util.StringTokenizer oldTok, com.Ostermiller.util.StringTokenizer newTok) {
		compareState(testName, newTok, oldTok.countTokens());
	}

	private static void compareState(String testName, com.Ostermiller.util.StringTokenizer newTok, int tokenCount) {
		assertEquals(tokenCount, newTok.countTokens());
		assertEquals(tokenCount > 0, newTok.hasMoreTokens());
	}
}
