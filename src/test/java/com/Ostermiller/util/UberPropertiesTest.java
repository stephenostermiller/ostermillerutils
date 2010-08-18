/*
 * Copyright (C) 2003-2007 Stephen Ostermiller
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

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * UberProperties regression test.
 */
class UberPropertiesTests {
	private final static String[] TESTS = new String[]{
		"A"+"one=\n"+"A"+"two= \n"+"A"+"three=\t",
		"B"+"one=1\n B"+"two = two \nB"+"three 3\n"+"B"+"four: 4",
		"C"+"on\\\n"+"e=on\\\n"+"e\n"+"C"+"t"+"w \\\n o=t"+"w \\\n o\nCth\\\n r"+"e"+"e=t"+"h \\\n"+"r"+"e"+"e",
		"Done=one\nDone=two\nDone=three",
		"#Comment\n"+"name value\n!Comment\n"+"name value\n# Comment\\n"+"name value\n #name value\n\t \t!name value",
		"#\n# That was a comment\n\nname:value\n"+"name=value\n"+"name value\n name = value \n	name	=	value	\n  name  =  value  ",
		"# empty properties\n"+"name\n"+"name=\n"+"name:\n	name\n  name    ",
		"# property names of length zero\n:value value\n:value\n=value\n :value\n =value\n:value : has colon\n:value ends with equal =\n:value ends with colon :",
		"name::value starts with colon\n"+"name=:value starts with colon\n"+"name :value starts with colon\nname:value ends with colon:\n"+"name=value ends with colon:\n"+"name value ends with colon:\n"+"name:=value starts with equal\n"+"name==value starts with equal\n"+"name =value starts with equal\nname:value ends with equal=\n"+"name=value ends with equal=\n"+"name value ends with equal=\n"+"name:!value starts with exclamation\n"+"name=!value starts with exclamation\n"+"name !value starts with exclamation\n"+"name:#value starts with pound\n"+"name=#value starts with pound\n"+"name #value starts with pound\n"+"name=value ends with colon :\n"+"name=value ends with equal =",
		"@!#$%^name value!@#$%^&*(){}",
		"\n\n\n\n#comment\n\n \n\t \n ",
		"# escapes\n\\ \\=\\:name=value\\ \\=\\:\n\\u3443\\0233name value\\u3432\\0213",
		"name",
		"name ",
		"name =",
		"",
		"#comment",
		"name= ",
		"name= value",
		"name=value ",
		"name\\\n"+"still"+"name value\n"+"name\\\n  still"+"name value\n"+"name\\\n"+"still"+"name\\\n"+"still"+"name value\n"+"name\\\n\\\n \\\n"+"still"+"name value\n"+"name\\\n#still"+"name value\n"+"name\\\n!still"+"name value",
		"# empty property\n"+"name\\\n\n#comment",
	};

	/**
	 * Main method for tests
	 * @param args command line arguments (ignored)
	 */
	public static void main(String[] args){
		try {
			for (String element: TESTS) {
				byte[] bytes = element.getBytes("ISO-8859-1");
				Properties p = new Properties();
				p.load(new ByteArrayInputStream(bytes));
				UberProperties up = new UberProperties();
				up.load(new ByteArrayInputStream(bytes));
				String results = compare(up, p);
				if (results != null){
					System.err.println(results);
					System.err.println(element);
					System.exit(1);
				}
				CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
				up.save(cbb.getOutputStream());
				cbb.getOutputStream().close();
				UberProperties up2 = new UberProperties();
				up2.load(cbb.getInputStream());
				results = compare(up, up2);
				if (results != null){
					System.err.println(results);
					System.err.println(element);
					System.exit(1);
				}
			}
		} catch (Exception x){
			x.printStackTrace();
			System.exit(1);
		}
	}

	private static String compare(UberProperties uberProps, Properties props){
		String[] upNames = uberProps.propertyNames();
		Enumeration<?> pNamesEnum = props.propertyNames();
		ArrayList<String> pNamesList = new ArrayList<String>();
		while (pNamesEnum.hasMoreElements()){
			Object o = pNamesEnum.nextElement();
			if (o instanceof String){
				pNamesList.add((String)o);
			}
		}
		String[] pNames = pNamesList.toArray(new String[0]);
		if (upNames.length != pNames.length){
			return ("Number of properties do not match: UberProperties: " + upNames.length +  " Normal:" + pNames.length);
		}
		for (String element: pNames) {
			String upValue = uberProps.getProperty(element);
			String pValue = props.getProperty(element);
			if (upValue == null) {
				return "UberProperties does not contain property: '" + element + "'";
			}
			if (!upValue.equals(pValue)){
				return ("Values for '" + element + "' do not match:\n  '" + pValue + "'\n  '" + upValue + "'");
			}
		}
		return null;
	}

	private static String compare(UberProperties up1, UberProperties up2){
		String[] up1Names = up1.propertyNames();
		String[] up2Names = up2.propertyNames();
		if (up1Names.length != up2Names.length){
			return ("Number of properties do not match: UberProperties: " + up1Names.length +  " Normal:" + up2Names.length);
		}
		for (String element: up1Names) {
			String up1Value = up1.getProperty(element);
			String up2Value = up2.getProperty(element);
			if (up2Value == null) {
				return "Second does not contain property: '" + element + "'";
			}
			if (!up1Value.equals(up2Value)){
				return ("Values for '" + element + "' do not match:\n  '" + up1Value + "'\n  '" + up2Value + "'");
			}
		}
		return null;
	}
}
