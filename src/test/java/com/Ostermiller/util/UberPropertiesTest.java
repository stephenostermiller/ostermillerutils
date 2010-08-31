/*
 * Copyright (C) 2003-2010 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Java+Utilities This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * See COPYING.TXT for details.
 */
package com.Ostermiller.util;

import java.io.IOException;
import junit.framework.TestCase;
import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * UberProperties regression test.
 */
public class UberPropertiesTest extends TestCase {

	public void test1() {
		check("A" + "one=\n" + "A" + "two= \n" + "A" + "three=\t");
	}

	public void test2() {
		check("B" + "one=1\n B" + "two = two \nB" + "three 3\n" + "B" + "four: 4");
	}

	public void test3() {
		check("C" + "on\\\n" + "e=on\\\n" + "e\n" + "C" + "t" + "w \\\n o=t" + "w \\\n o\nCth\\\n r" + "e" + "e=t" + "h \\\n" + "r" + "e" + "e");
	}

	public void test4() {
		check("Done=one\nDone=two\nDone=three");
	}

	public void test5() {
		check("#Comment\n" + "name value\n!Comment\n" + "name value\n# Comment\\n" + "name value\n #name value\n\t \t!name value");
	}

	public void test6() {
		check("#\n# That was a comment\n\nname:value\n" + "name=value\n" + "name value\n name = value \n	name	=	value	\n  name  =  value  ");
	}

	public void test7() {
		check("# empty properties\n" + "name\n" + "name=\n" + "name:\n	name\n  name    ");
	}

	public void test8() {
		check("# property names of length zero\n:value value\n:value\n=value\n :value\n =value\n:value : has colon\n:value ends with equal =\n:value ends with colon :");
	}

	public void test9() {
		check("name::value starts with colon\n" + "name=:value starts with colon\n" + "name :value starts with colon\nname:value ends with colon:\n" + "name=value ends with colon:\n" + "name value ends with colon:\n" + "name:=value starts with equal\n" + "name==value starts with equal\n" + "name =value starts with equal\nname:value ends with equal=\n" + "name=value ends with equal=\n" + "name value ends with equal=\n" + "name:!value starts with exclamation\n" + "name=!value starts with exclamation\n" + "name !value starts with exclamation\n" + "name:#value starts with pound\n" + "name=#value starts with pound\n" + "name #value starts with pound\n" + "name=value ends with colon :\n" + "name=value ends with equal =");
	}

	public void test10() {
		check("@!#$%^name value!@#$%^&*(){}");
	}

	public void test11() {
		check("\n\n\n\n#comment\n\n \n\t \n ");
	}

	public void test12() {
		check("# escapes\n\\ \\=\\:name=value\\ \\=\\:\n\\u3443\\0233name value\\u3432\\0213");
	}

	public void test13() {
		check("name");
	}

	public void test14() {
		check("name ");
	}

	public void test15() {
		check("name =");
	}

	public void test16() {
		check("");
	}

	public void test17() {
		check("#comment");
	}

	public void test18() {
		check("name= ");
	}

	public void test19() {
		check("name= value");
	}

	public void test20() {
		check("name=value ");
	}

	public void test21() {
		check("name\\\n" + "still" + "name value\n" + "name\\\n  still" + "name value\n" + "name\\\n" + "still" + "name\\\n" + "still" + "name value\n" + "name\\\n\\\n \\\n" + "still" + "name value\n" + "name\\\n#still" + "name value\n" + "name\\\n!still" + "name value");
	}

	public void test22(){
		check("# empty property\n" + "name\\\n\n#comment");
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
