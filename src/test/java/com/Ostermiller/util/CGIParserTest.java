/*
 * Regression tests for CGIParser
 * Copyright (C) 2026 Stephen Ostermiller
 * https://ostermiller.org/contact.pl?regarding=Java+Utilities
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

import junit.framework.TestCase;
import java.io.*;
import java.util.*;

/**
 * Unit test for CGIParser.
 * More information about this class is available from <a target="_top" href=
 * "https://ostermiller.org/utils/CGIParser.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 *         https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.04
 */
public class CGIParserTest extends TestCase {

	public void testParseEmptyString() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("", "UTF-8");
		assertNull(parser.getParameter("anything"));
		assertEquals(0, parser.getParameterNameList().length);
	}

	public void testParseNullString() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser((String)null, "UTF-8");
		assertNull(parser.getParameter("anything"));
		assertEquals(0, parser.getParameterNameList().length);
	}

	public void testParseSingleParameter() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=value", "UTF-8");
		assertEquals("value", parser.getParameter("name"));
		assertNotNull(parser.getParameterValues("name"));
		assertEquals(1, parser.getParameterValues("name").length);
		assertEquals("value", parser.getParameterValues("name")[0]);
	}

	public void testParseMultipleParameters() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name1=value1&name2=value2&name3=value3", "UTF-8");
		assertEquals("value1", parser.getParameter("name1"));
		assertEquals("value2", parser.getParameter("name2"));
		assertEquals("value3", parser.getParameter("name3"));
	}

	public void testParseParameterWithoutValue() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name", "UTF-8");
		assertEquals("", parser.getParameter("name"));
	}

	public void testParseMultipleValuesForSameName() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("color=red&color=blue&color=green", "UTF-8");
		String[] colors = parser.getParameterValues("color");
		assertEquals(3, colors.length);
		assertEquals("red", colors[0]);
		assertEquals("blue", colors[1]);
		assertEquals("green", colors[2]);
	}

	public void testParseURLEncodedValues() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=John+Doe&city=New+York", "UTF-8");
		assertEquals("John Doe", parser.getParameter("name"));
		assertEquals("New York", parser.getParameter("city"));
	}

	public void testParseURLEncodedPercent() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=John%20Doe", "UTF-8");
		assertEquals("John Doe", parser.getParameter("name"));
	}

	public void testParseSpecialCharacters() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("email=test%40example.com", "UTF-8");
		assertEquals("test@example.com", parser.getParameter("email"));
	}

	public void testGetParameterNames() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name1=value1&name2=value2&name3=value3", "UTF-8");
		String[] names = parser.getParameterNameList();
		assertEquals(3, names.length);
		assertTrue(Arrays.asList(names).contains("name1"));
		assertTrue(Arrays.asList(names).contains("name2"));
		assertTrue(Arrays.asList(names).contains("name3"));
	}

	public void testGetParameterNamesEnumeration() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name1=value1&name2=value2", "UTF-8");
		Enumeration<String> names = parser.getParameterNames();
		int count = 0;
		while (names.hasMoreElements()) {
			names.nextElement();
			count++;
		}
		assertEquals(2, count);
	}

	public void testGetNonexistentParameter() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=value", "UTF-8");
		assertNull(parser.getParameter("nonexistent"));
	}

	public void testSetParameter() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=oldvalue", "UTF-8");
		parser.setParameter("name", "newvalue");
		assertEquals("newvalue", parser.getParameter("name"));
	}

	public void testSetParameterOverwritesMultipleValues() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("color=red&color=blue", "UTF-8");
		parser.setParameter("color", "green");
		String[] colors = parser.getParameterValues("color");
		assertEquals(1, colors.length);
		assertEquals("green", colors[0]);
	}

	public void testSetParameterWithArray() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("color=red", "UTF-8");
		parser.setParameter("color", new String[]{"blue", "green", "yellow"});
		String[] colors = parser.getParameterValues("color");
		assertEquals(3, colors.length);
		assertEquals("blue", colors[0]);
		assertEquals("green", colors[1]);
		assertEquals("yellow", colors[2]);
	}

	public void testSetParameterNull() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=value", "UTF-8");
		parser.setParameter("name", (String)null);
		assertNull(parser.getParameter("name"));
	}

	public void testSetParameterEmptyArray() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=value", "UTF-8");
		parser.setParameter("name", new String[]{});
		assertNull(parser.getParameter("name"));
	}

	public void testAddParameter() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("color=red", "UTF-8");
		parser.addParameter("color", "blue");
		String[] colors = parser.getParameterValues("color");
		assertEquals(2, colors.length);
		assertEquals("red", colors[0]);
		assertEquals("blue", colors[1]);
	}

	public void testAddParameterWithArray() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("color=red", "UTF-8");
		parser.addParameter("color", new String[]{"blue", "green"});
		String[] colors = parser.getParameterValues("color");
		assertEquals(3, colors.length);
		assertEquals("red", colors[0]);
		assertEquals("blue", colors[1]);
		assertEquals("green", colors[2]);
	}

	public void testAddParameterNull() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("color=red", "UTF-8");
		parser.addParameter("color", (String)null);
		String[] colors = parser.getParameterValues("color");
		assertEquals(1, colors.length);
		assertEquals("red", colors[0]);
	}

	public void testGetParameters() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name1=value1&name2=value2", "UTF-8");
		NameValuePair[] pairs = parser.getParameters();
		assertEquals(2, pairs.length);
	}

	public void testGetParametersWithMultipleValues() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("color=red&color=blue", "UTF-8");
		NameValuePair[] pairs = parser.getParameters();
		assertEquals(2, pairs.length);
		assertEquals("color", pairs[0].getName());
		assertEquals("red", pairs[0].getValue());
		assertEquals("color", pairs[1].getName());
		assertEquals("blue", pairs[1].getValue());
	}

	public void testToString() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=value", "UTF-8");
		String encoded = parser.toString("UTF-8");
		assertTrue(encoded.contains("name"));
		assertTrue(encoded.contains("value"));
	}

	public void testToStringWithSpecialCharacters() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("", "UTF-8");
		parser.setParameter("name", "John Doe");
		String encoded = parser.toString("UTF-8");
		assertTrue(encoded.length() > 0);
		assertTrue(encoded.contains("name"));
	}

	public void testParseFromInputStream() throws IOException {
		String input = "name=value";
		ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
		CGIParser parser = new CGIParser(bais, "UTF-8");
		assertEquals("value", parser.getParameter("name"));
	}

	public void testParseFromReader() throws IOException {
		String input = "name=value";
		StringReader sr = new StringReader(input);
		CGIParser parser = new CGIParser(sr, "UTF-8");
		assertEquals("value", parser.getParameter("name"));
	}

	public void testParseFromNullInputStream() throws IOException {
		CGIParser parser = new CGIParser((InputStream)null, "UTF-8");
		assertNull(parser.getParameter("anything"));
	}

	public void testParseFromNullReader() throws IOException {
		CGIParser parser = new CGIParser((Reader)null, "UTF-8");
		assertNull(parser.getParameter("anything"));
	}

	public void testParseComplexQueryString() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("user=john&pass=secret123&email=john%40test.com&role=admin&role=user", "UTF-8");
		assertEquals("john", parser.getParameter("user"));
		assertEquals("secret123", parser.getParameter("pass"));
		assertEquals("john@test.com", parser.getParameter("email"));
		String[] roles = parser.getParameterValues("role");
		assertEquals(2, roles.length);
	}

	public void testGetFirstValueWhenMultipleExist() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("item=first&item=second&item=third", "UTF-8");
		String value = parser.getParameter("item");
		assertEquals("first", value);
	}

	public void testParameterNameCaseSensitive() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("Name=value1&name=value2", "UTF-8");
		assertEquals("value1", parser.getParameter("Name"));
		assertEquals("value2", parser.getParameter("name"));
	}

	public void testEmptyParameter() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=&other=value", "UTF-8");
		assertEquals("", parser.getParameter("name"));
		assertEquals("value", parser.getParameter("other"));
	}

	public void testParameterWithoutEquals() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("flag&name=value", "UTF-8");
		assertEquals("", parser.getParameter("flag"));
		assertEquals("value", parser.getParameter("name"));
	}

	public void testUtf8Encoding() throws UnsupportedEncodingException {
		String encoded = "name=%C3%A9"; // é in UTF-8
		CGIParser parser = new CGIParser(encoded, "UTF-8");
		String value = parser.getParameter("name");
		assertNotNull(value);
	}

	public void testAmpersandInValue() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("text=a%26b", "UTF-8");
		assertEquals("a&b", parser.getParameter("text"));
	}

	public void testEqualsSignInValue() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("equation=2%3D2", "UTF-8");
		assertEquals("2=2", parser.getParameter("equation"));
	}

	public void testSetParameterNullArray() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=value", "UTF-8");
		parser.setParameter("name", (String[])null);
		assertNull(parser.getParameter("name"));
	}

	public void testAddParameterNullArray() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("name=value", "UTF-8");
		parser.addParameter("name", (String[])null);
		String[] values = parser.getParameterValues("name");
		assertEquals(1, values.length);
		assertEquals("value", values[0]);
	}

	public void testSetParameterThrowsNullPointerForNullValue() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("", "UTF-8");
		try {
			parser.setParameter("name", new String[]{null});
			fail("Expected NullPointerException");
		} catch (NullPointerException e) {
			// Expected
		}
	}

	public void testAddParameterThrowsNullPointerForNullValue() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("", "UTF-8");
		try {
			parser.addParameter("name", new String[]{null});
			fail("Expected NullPointerException");
		} catch (NullPointerException e) {
			// Expected
		}
	}

	public void testNewParameter() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("existing=value", "UTF-8");
		parser.setParameter("newparam", "newvalue");
		assertEquals("newvalue", parser.getParameter("newparam"));
		assertEquals("value", parser.getParameter("existing"));
	}

	public void testRoundTripEncoding() throws UnsupportedEncodingException {
		CGIParser parser1 = new CGIParser("", "UTF-8");
		parser1.setParameter("name", "John Doe");
		parser1.setParameter("email", "john@example.com");

		String encoded = parser1.toString("UTF-8");
		CGIParser parser2 = new CGIParser(encoded, "UTF-8");
		assertEquals("John Doe", parser2.getParameter("name"));
		assertEquals("john@example.com", parser2.getParameter("email"));
	}

	public void testEmptyParameterName() throws UnsupportedEncodingException {
		CGIParser parser = new CGIParser("=value", "UTF-8");
		assertEquals("value", parser.getParameter(""));
	}
}
