/*
 * Tests StringTokenizer.
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

/**
 * A regression test for com.Ostermiller.util.StringTokenizer.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/StringTokenizer.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */
class TokenizerTests {

	public static void main(String args[]){
		java.util.StringTokenizer oldTok;
		com.Ostermiller.util.StringTokenizer newTok;
		String token;

		//newTok.test();
		System.out.println("  ===============================================");
		token = "this is a test";
		oldTok = new java.util.StringTokenizer(token);
		newTok = new com.Ostermiller.util.StringTokenizer(token);
		compareAndPrint("" + oldTok.countTokens(), "" + newTok.countTokens());
		compareState("Test 1", oldTok, newTok);
		while (oldTok.hasMoreTokens()){
			compareAndPrint(oldTok.nextToken(), newTok.nextToken());
			compareState("Test 1", oldTok, newTok);
		}
		System.out.println("  ===============================================");
		token = "";
		oldTok = new java.util.StringTokenizer(token);
		newTok = new com.Ostermiller.util.StringTokenizer(token);
		compareAndPrint("" + oldTok.countTokens(), "" + newTok.countTokens());
		System.out.println("  ===============================================");
		token = "no delims";
		oldTok = new java.util.StringTokenizer(token, "");
		newTok = new com.Ostermiller.util.StringTokenizer(token, "");
		compareAndPrint("" + oldTok.countTokens(), "" + newTok.countTokens());
		while (oldTok.hasMoreTokens()){
			compareAndPrint("" + oldTok.countTokens(), "" + newTok.countTokens());
			compareAndPrint(oldTok.nextToken(), newTok.nextToken());
		}
		System.out.println("  ===============================================");
		token = "AstringB";
		oldTok = new java.util.StringTokenizer(token, "AB");
		newTok = new com.Ostermiller.util.StringTokenizer(token, "AB");
		compareAndPrint("" + oldTok.countTokens(), "" + newTok.countTokens());
		while (oldTok.hasMoreTokens()){
			compareAndPrint(oldTok.nextToken(), newTok.nextToken());
		}
		System.out.println("  ===============================================");
		token = "AstringB";
		oldTok = new java.util.StringTokenizer(token, "AB", true);
		newTok = new com.Ostermiller.util.StringTokenizer(token, "AB", true);
		compareAndPrint("" + oldTok.countTokens(), "" + newTok.countTokens());
		while (oldTok.hasMoreTokens()){
			compareAndPrint(oldTok.nextToken(), newTok.nextToken());
		}
		System.out.println("  ===============================================");
		token = "someURL?name=value&name=value";
		oldTok = new java.util.StringTokenizer(token);
		newTok = new com.Ostermiller.util.StringTokenizer(token);
		compareAndPrint(oldTok.nextToken("?"), newTok.nextToken("?"));
		compareAndPrint(oldTok.nextToken("=&"), newTok.nextToken("=&"));
		compareAndPrint(oldTok.nextToken("=&"), newTok.nextToken("=&"));
		compareAndPrint(oldTok.nextToken(), newTok.nextToken());
		compareAndPrint(oldTok.nextToken(), newTok.nextToken());
		System.out.println("  ===============================================");
		newTok = new com.Ostermiller.util.StringTokenizer(token);
		compareAndPrint("someURL", newTok.nextToken("?"));
		newTok.skipDelimiters();
		compareAndPrint("name", newTok.nextToken("=&"));
		compareAndPrint("value", newTok.nextToken("=&"));
		compareAndPrint("name", newTok.nextToken());
		compareAndPrint("value", newTok.nextToken());
		System.out.println("  ===============================================");
		token = "  (   aaa	\t	* (b+c1 ))";
		newTok = new com.Ostermiller.util.StringTokenizer(token, " \t\n\r\f", "()+*");
		compareAndPrint("9", "" + newTok.countTokens());
		compareAndPrint("(", newTok.nextToken());
		compareAndPrint("aaa", newTok.nextToken());
		compareAndPrint("*", newTok.nextToken());
		compareAndPrint("(", newTok.nextToken());
		compareAndPrint("b", newTok.nextToken());
		compareAndPrint("+", newTok.nextToken());
		compareAndPrint("c1", newTok.nextToken());
		compareAndPrint(")", newTok.nextToken());
		compareAndPrint(")", newTok.nextToken());
		System.out.println("  ===============================================");
		token = "one,two,,four,five,,,eight,";
		oldTok = new java.util.StringTokenizer(token, ",");
		newTok = new com.Ostermiller.util.StringTokenizer(token, ",");
		compareAndPrint("" + oldTok.countTokens(), "" + newTok.countTokens());
		while (oldTok.hasMoreTokens()){
			compareAndPrint(oldTok.nextToken(), newTok.nextToken());
		}
		System.out.println("  ===============================================");
		token = "one,two,,four,five,,,eight";
		newTok = new com.Ostermiller.util.StringTokenizer(token, ",");
		newTok.setReturnEmptyTokens(true);
		compareAndPrint("8", "" + newTok.countTokens());
		System.out.println("  ===============================================");
		token = ",two,,four,five,,,eight,";
		newTok = new com.Ostermiller.util.StringTokenizer(token, ",");
		newTok.setReturnEmptyTokens(true);
		compareAndPrint("9", "" + newTok.countTokens());
		System.out.println("  ===============================================");
		token = ",";
		newTok = new com.Ostermiller.util.StringTokenizer(token, ",");
		newTok.setReturnEmptyTokens(true);
		compareAndPrint("2", "" + newTok.countTokens());
		System.out.println("  ===============================================");
		token = "";
		newTok = new com.Ostermiller.util.StringTokenizer(token, ",");
		newTok.setReturnEmptyTokens(true);
		compareAndPrint("1", "" + newTok.countTokens());
		System.out.println("  ===============================================");
		token = ",two,,four,five,,,eight,";
		newTok = new com.Ostermiller.util.StringTokenizer(token, ",", true);
		newTok.setReturnEmptyTokens(true);
		compareAndPrint("17", "" + newTok.countTokens());
		System.out.println("  ===============================================");
		token = ",one,,,four,";
		newTok = new com.Ostermiller.util.StringTokenizer(token, ",", null, false);
		compareAndPrint("one", newTok.nextToken());
		newTok.setReturnEmptyTokens(true);
		compareAndPrint("4", "" + newTok.countTokens());
		System.out.println("  ===============================================");
		token = "list=";
		newTok = new com.Ostermiller.util.StringTokenizer(token, "=");
		newTok.setReturnEmptyTokens(true);
		compareAndPrint("list", newTok.nextToken());
		newTok.skipDelimiters();
		compareAndPrint("", newTok.nextToken(","));
		compareAndPrint("0", "" + newTok.countTokens());
		System.out.println("  ===============================================");
		token = "list=,";
		newTok = new com.Ostermiller.util.StringTokenizer(token, "=");
		newTok.setReturnEmptyTokens(true);
		compareAndPrint("list", newTok.nextToken());
		newTok.skipDelimiters();
		compareAndPrint("", newTok.nextToken(","));
		compareAndPrint("1", "" + newTok.countTokens());
		System.out.println("  ===============================================");
		token = "list=,two,";
		newTok = new com.Ostermiller.util.StringTokenizer(token, "=");
		newTok.setReturnEmptyTokens(true);
		compareAndPrint("list", newTok.nextToken());
		newTok.skipDelimiters();
		compareAndPrint("", newTok.nextToken(","));
		compareAndPrint("2", "" + newTok.countTokens());
		System.out.println("  ===============================================");
		token = "this is a test";
		newTok = new com.Ostermiller.util.StringTokenizer(token);
		String[] tokens = newTok.toArray();
		newTok.setText(token);
		for (int i=0; i<tokens.length; i++){
			compareAndPrint(tokens[i], newTok.nextToken());
		}
		System.out.println("  ===============================================");
		token = "token rest of string";
		newTok = new com.Ostermiller.util.StringTokenizer(token);
		compareAndPrint("token", newTok.nextToken());
		newTok.skipDelimiters();
		compareAndPrint("rest of string", newTok.restOfText());
		compareAndPrint("false", "" + newTok.hasMoreTokens());
		System.out.println("  ===============================================");
		token = "testing the peek method";
		newTok = new com.Ostermiller.util.StringTokenizer(token);
		compareAndPrint("4", "" + newTok.countTokens());
		compareAndPrint("testing", newTok.peek());
		compareAndPrint("4", "" + newTok.countTokens());
		compareAndPrint("testing", newTok.nextToken());
		compareAndPrint("3", "" + newTok.countTokens());
		compareAndPrint("the", newTok.peek());
		compareAndPrint("the", newTok.peek());
		compareAndPrint("the", newTok.nextToken());
		compareAndPrint("peek", newTok.peek());
		compareAndPrint("peek", newTok.nextToken());
		compareAndPrint("true", "" + newTok.hasMoreTokens());
		compareAndPrint("method", newTok.peek());
		compareAndPrint("true", "" + newTok.hasMoreTokens());
		compareAndPrint("method", newTok.nextToken());
		compareAndPrint("false", "" + newTok.hasMoreTokens());
		System.out.println("  ===============================================");
	}

	private static void compareAndPrint(String one, String two){
		 System.out.print((one.equals(two) ? "  " : "* "));
		 System.out.print(one);
		 for (int i=0; i < 20 - one.length(); i++){
			 System.out.print(" ");
		 }
		 System.out.println("| " + two);
	}

	private static boolean compareState(String testName, java.util.StringTokenizer oldTok, com.Ostermiller.util.StringTokenizer newTok){
		boolean success = true;
		// count the number of tokens left in each first.
		success &= compareState(testName, newTok, oldTok.countTokens());
		if (!success){
			System.exit(1);
		}
		return success;
	}

	private static boolean compareState(String testName, com.Ostermiller.util.StringTokenizer newTok, int tokenCount){
		boolean success = true;
		// count the number of tokens left first.
		int newNumTokens = newTok.countTokens();
		if (tokenCount != newNumTokens){
			System.err.println(testName + ": TokenCount does not match. " + tokenCount + " vs " + newNumTokens);
			success = false;
		}
		boolean hasMoreTokens = (tokenCount > 0);
		boolean newTokHasMoreTokens = newTok.hasMoreTokens();
		if (hasMoreTokens != newTokHasMoreTokens){
			System.err.println(testName + ": hasMoreTokens does not match. " + hasMoreTokens + " vs " + newTokHasMoreTokens);
			success = false;
		}
		if (!success){
			System.exit(1);
		}
		return success;
	}
}
