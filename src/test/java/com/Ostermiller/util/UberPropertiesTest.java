/*
 * Copyright (C) 2003-2011 Stephen Ostermiller
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

import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
import junit.framework.TestCase;
import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * UberProperties regression test.
 */
public class UberPropertiesTest extends TestCase {

	public void testWhiteSpaceAtValueEnd() {
		check(
			"one=\n"+
			"two= \n"+
			"three=\t"
		);
	}

	public void testWhiteSpaceAroundNamesAndValues() {
		check(
			"one=1\n"+
			" two = two \n"+
			"three 3\n"+
			"four: 4"
		);
	}

	public void testBackslashToNextLine() {
		check(
			"on\\\n"+
			"e=on\\\n"+
			"e\n"+
			"tw \\\n"+
			" o=t" + "w \\\n"+
			" o\n"+"Cth\\\n"+
			" ree=t" + "h \\\n"+
			"ree"
		);
	}

	public void testMultiplePropertiesWithSameName() {
		check(
			"Done=one\n"+
			"Done=two\n"+
			"Done=three"
		);
	}

	public void testComments() {
		check(
			"#Comment\n"+
			"name value\n"+
			"!Comment\n"+
			"name value\n"+
			"# Comment\\n"+
			"name value\n"+
			" #name value\n"+
			"\t \t!name value"
		);
	}

	public void testNameDelimiterAndSpacing() {
		check(
			"#\n"+
			"# That was a comment\n"+
			"\n"+
			"one:value\n"+
			"two=value\n"+
			"three value\n"+
			" four = value \n"+
			"\tfive\t=\tvalue\t\n"+
			"  six  =  value  "
		);
	}

	public void testNamesEmptyValues() {
		check(
			"one\n"+
			"two=\n"+
			"three:\n"+
			"	four\n"+
			"  five    "
		);
	}

	public void testNamesZeroLength() {
		check(
			":value value\n"+
			":value\n"+
			"=value\n"+
			" :value\n"+
			" =value\n"+
			":value : has colon\n"+
			":value ends with equal =\n"+
			":value ends with colon :"
		);
	}

	public void testNameValueDelimiters() {
		check(
			"one::value starts with colon\n"+
			"two=:value starts with colon\n"+
			"three :value starts with colon\n"+
			"four:value ends with colon:\n"+
			"five=value ends with colon:\n"+
			"six value ends with colon:\n"+
			"seven:=value starts with equal\n"+
			"eight==value starts with equal\n"+
			"nine =value starts with equal\n"+
			"ten:value ends with equal=\n"+
			"eleven=value ends with equal=\n"+
			"twelve value ends with equal=\n"+
			"thirteen:!value starts with exclamation\n"+
			"fourteen=!value starts with exclamation\n"+
			"fifteen !value starts with exclamation\n"+
			"sixteen:#value starts with pound\n"+
			"seventeen=#value starts with pound\n"+
			"eighteen #value starts with pound\n"+
			"nineteen=value ends with colon :\n"+
			"twenty=value ends with equal ="
		);
	}

	public void testNonDelimiterPuncuation() {
		check("@!#$%^name value!@#$%^&*(){}");
	}

	public void testEmptyLines() {
		check(
			"\n"+
			"\n"+
			"\n"+
			"\n"+
			"#comment\n"+
			"\n"+
			" \n"+
			"\t \n"+
			" "
		);
	}

	public void testEscapes() {
		check(
			"# escapes\n"+
			"\\ \\=\\:name=value\\ \\=\\:\n"+
			"\\u3443\\0233name value\\u3432\\0213"
		);
	}

	public void testSingleNameOnly() {
		check("name");
	}

	public void testSingleNameSpace() {
		check("name ");
	}

	public void testSingleNameEquals() {
		check("name =");
	}

	public void testLengthZero() {
		check("");
	}

	public void testOnlyComment() {
		check("#comment");
	}

	public void testNameEqualsSpace() {
		check("name= ");
	}

	public void testNameEqualsSpaceValue() {
		check("name= value");
	}

	public void testSimpleNameValue() {
		check("name=value ");
	}

	public void testLongNames() {
		check(
			"name\\\n"+
			"still" + "name value\n"+
			"name\\\n"+
			"  still" + "name value\n"+
			"name\\\n"+
			"still" + "name\\\n"+
			"still" + "name value\n"+
			"name\\\n"+
			"\\\n"+
			" \\\n"+
			"still" + "name value\n"+
			"name\\\n"+
			"#still" + "name value\n"+
			"name\\\n"+
			"!still" + "name value"
		);
	}

	public void testEmptyProp(){
		check("# empty property\n"+
		"name\\\n"+
		"\n"+
		"#comment");
	}

	public void testBooleanProperty(){
		UberProperties p = new UberProperties();
		p.setProperty("a", "TRUE");
		assertEquals(Boolean.TRUE, p.getBooleanProperty("a"));
		assertNull(p.getBooleanProperty("b"));
		assertTrue(p.getBooleanProperty("a", false));
		assertFalse(p.getBooleanProperty("b", false));
	}

	public void testIntegerProperty(){
		UberProperties p = new UberProperties();
		p.setProperty("a", "3");
		assertEquals(new Integer(3), p.getIntegerProperty("a"));
		assertNull(p.getBooleanProperty("b"));
		assertEquals(3, p.getIntProperty("a", 0));
		assertEquals(-3, p.getIntProperty("b", -3));
	}

	private static void checkLoadAndSaveUtf8(String s1) {
		try {
			UberProperties p1 = new UberProperties();
			p1.load(new StringReader(s1));
			StringWriter sw = new StringWriter();
			p1.save(sw);
			UberProperties p2 = new UberProperties();
			p2.load(new StringReader(sw.toString()));
			compare(p1,p2);
		} catch (IOException iox){
			throw new RuntimeException(iox);
		}

	}

	private static void check(String properties) {
		try {
			byte[] bytes = properties.getBytes("ISO-8859-1");
			Properties p = new Properties();
			p.load(new ByteArrayInputStream(bytes));
			UberProperties up = new UberProperties();
			up.load(new ByteArrayInputStream(bytes));
			compare(up, p);
			CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
			up.save(cbb.getOutputStream());
			cbb.getOutputStream().close();
			UberProperties up2 = new UberProperties();
			up2.load(cbb.getInputStream());
			compare(up, up2);
		} catch (IOException x) {
			throw new RuntimeException(x);
		}
		checkLoadAndSaveUtf8(properties);
	}

	private static void compare(UberProperties uberProps, Properties props) {
		String[] upNames = uberProps.propertyNames();
		Enumeration<?> pNamesEnum = props.propertyNames();
		ArrayList<String> pNamesList = new ArrayList<String>();
		while (pNamesEnum.hasMoreElements()) {
			Object o = pNamesEnum.nextElement();
			if (o instanceof String) {
				pNamesList.add((String) o);
			}
		}
		String[] pNames = pNamesList.toArray(new String[0]);
		assertEquals("Number of properties do not match", upNames.length, pNames.length);

		for (String element : pNames) {
			String upValue = uberProps.getProperty(element);
			String pValue = props.getProperty(element);
			assertNotNull("UberProperties does not contain property: '" + element + "'", upValue);
			assertEquals("Values for '" + element + "' do not match", upValue, pValue);
		}
	}

	private static String compare(UberProperties up1, UberProperties up2) {
		String[] up1Names = up1.propertyNames();
		String[] up2Names = up2.propertyNames();
		assertEquals("Number of properties do not match", up1Names.length, up2Names.length);
		for (String element : up1Names) {
			String up1Value = up1.getProperty(element);
			String up2Value = up2.getProperty(element);
			assertNotNull("Second does not contain property: '" + element + "'", up2Value);
			assertEquals("Values for '" + element + "' do not match", up1Value, up2Value);
		}
		return null;
	}
}
