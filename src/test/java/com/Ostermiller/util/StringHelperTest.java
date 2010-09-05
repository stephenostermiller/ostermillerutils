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

import java.util.Arrays;
import junit.framework.TestCase;

/**
 * StringHelper regression test.
 */
public class StringHelperTest extends TestCase {

	public void assertEquals(Object[] a, Object[] b) {
		assertTrue(Arrays.equals(a,b));
	}

	public void testSplit1(){
		assertEquals(
			StringHelper.split("1-2-3", "-"),
			new String[]{"1","2","3"}
		);
	}

	public void testSplit2(){
		assertEquals(
			StringHelper.split("-1--2-", "-"),
			new String[]{"","1","","2",""}
		);
	}

	public void testSplit3(){
		assertEquals(
			StringHelper.split("123", ""),
			new String[]{"123"}
		);
	}

	public void testSplit4(){
		assertEquals(
			StringHelper.split("1-2---3----4", "--"),
			new String[]{"1-2","-3","","4"}
		);
	}

	public void testSplit5(){
		assertEquals(
			StringHelper.split("12345678", "--"),
			new String[]{"12345678"}
		);
	}

	public void testPrepad1(){
		assertEquals(
			StringHelper.prepad("a", 8),
			"       a"
		);
	}

	public void testPrepad2(){
		assertEquals(
			StringHelper.prepad("Aaa"+"Aa", 2),
			"Aaa"+"Aa"
		);
	}

	public void testPrepad3(){
		assertEquals(
			StringHelper.prepad("a", 8, '-'),
			"-------a"
		);
	}

	public void testPostpad1(){
		assertEquals(
			StringHelper.postpad("a", 8),
			"a       "
		);
	}

	public void testPostpad2(){
		assertEquals(
			StringHelper.postpad("Aaa"+"Aa", 2),
			"Aaa"+"Aa"
		);
	}

	public void testPostpad3(){
		assertEquals(
			StringHelper.postpad("a", 8, '-'),
			"a-------"
		);
	}

	public void testMidpad1(){
		assertEquals(
			StringHelper.midpad("a", 3),
			" a "
		);
	}

	public void testMidpad2(){
		assertEquals(
			StringHelper.midpad("a", 4),
			" a  "
		);
	}

	public void testMidpad3(){
		assertEquals(
			StringHelper.midpad("a", 5, '-'),
			"--a--"
		);
	}

	public void testMidpad4(){
		assertEquals(
			StringHelper.midpad("Aaa"+"Aa", 2),
			"Aaa"+"Aa"
		);
	}

	public void testReplace1(){
		assertEquals(
			StringHelper.replace("1-2-3", "-", "|"),
			"1|2|3"
		);
	}

	public void testReplace2(){
		assertEquals(
			StringHelper.replace("-1--2-", "-", "|"),
			"|1||2|"
		);
	}

	public void testReplace3(){
		assertEquals(
			StringHelper.replace("123", "", "|"),
			"123"
		);
	}

	public void testReplace4(){
		assertEquals(
			StringHelper.replace("1-2---3----4", "--", "|"),
			"1-2|-3||4"
		);
	}

	public void testReplace5(){
		assertEquals(
			StringHelper.replace("1-2--3---4----", "--", "---"),
			"1-2---3----4------"
		);
	}

	public void testEscapeHTML1(){
		assertEquals(
			StringHelper.escapeHTML("<>&\"'\0\1\2\n\r\f\t"+"hello"),
			"&lt;&gt;&amp;&quot;&#39;\n\r\f\t"+"hello"
		);
	}

	public void testEscapeSQL1(){
		assertEquals(
			StringHelper.escapeSQL("\0\'\"\r\n"+"hello"),
			"\\0\\'\\\"\r\n"+"hello"
		);
	}

	public void testEscapeJavaLiteral1(){
		assertEquals(
			StringHelper.escapeJavaLiteral("\0\'\"\r\n"+"hello"),
			"\0\\'\\\"\\r\\n"+"hello"
		);
	}

	public void testTrim1(){
		assertEquals(
			StringHelper.trim("- -\r\n"+"hello- -\r\n"+"hello- -\r\n", "\r- \n"),
			"hello- -\r\n"+"hello"
		);
	}

	public void testUnescapeHTML1(){
		assertEquals(
			StringHelper.unescapeHTML("&gt;hello"+"&"+"euro"+";"),
			">hello\u20AC"
		);
	}

	public void testContainsAny1(){
		assertTrue(
			StringHelper.containsAny(
				"on"+"two"+"t"+"h"+"r"+"e",
				new String[]{
					"one",
					"two",
					"three"
				}
			)
		);
	}

	public void testContainsAny2(){
		assertTrue(
			StringHelper.containsAny(
				"one"+"t"+"w"+"t"+"h"+"r"+"e",
				new String[]{
					"one",
					"two",
					"three"
				}
			)
		);
	}

	public void testContainsAny3(){
		assertTrue(
			StringHelper.containsAny(
				"on"+"t"+"w"+"three",
				new String[]{
					"one",
					"two",
					"three"
				}
			)
		);
	}

	public void testContainsAny4(){
		assertTrue(
			!StringHelper.containsAny(
				"on"+"t"+"w"+"t"+"h"+"r"+"e",
				new String[]{
					"one",
					"two",
					"three"
				}
			)
		);
	}

	public void testEqualsAny1(){
		assertTrue(
			StringHelper.equalsAny(
				"two",
				new String[]{
					"one",
					"two",
					"three"
				}
			)
		);
	}

	public void testEqualsAny2(){
		assertTrue(
			!StringHelper.equalsAny(
				"one"+"two"+"three",
				new String[]{
					"one",
					"two",
					"three"
				}
			)
		);
	}

	public void testStartsWithAny1(){
		assertTrue(
			StringHelper.startsWithAny(
				"two",
				new String[]{
					"one",
					"two",
					"three"
				}
			)
		);
	}

	public void testStartsWithAny2(){
		assertTrue(
			!StringHelper.startsWithAny(
				"on"+"two"+"three",
				new String[]{
					"one",
					"two",
					"three"
				}
			)
		);
	}

	public void testEndsWithAny1(){
		assertTrue(
			StringHelper.endsWithAny(
				"two",
				new String[]{
					"one",
					"two",
					"three"
				}
			)
		);
	}

	public void testEndsWithAny2(){
		assertTrue(
			!StringHelper.endsWithAny(
				"one"+"two"+"t"+"h"+"r"+"e",
				new String[]{
					"one",
					"two",
					"three"
				}
			)
		);
	}

	public void testContainsAnyIgnoreCase1(){
		assertTrue(
			StringHelper.containsAnyIgnoreCase(
				"on"+"two"+"t"+"h"+"r"+"e",
				new String[]{
					"One",
					"Two",
					"Three"
				}
			)
		);
	}

	public void testContainsAnyIgnoreCase2(){
		assertTrue(
			StringHelper.containsAnyIgnoreCase(
				"one"+"t"+"w"+"t"+"h"+"r"+"e",
				new String[]{
					"One",
					"Two",
					"Three"
				}
			)
		);
	}

	public void testContainsAnyIgnoreCase3(){
		assertTrue(
			StringHelper.containsAnyIgnoreCase(
				"on"+"t"+"w"+"three",
				new String[]{
					"One",
					"Two",
					"Three"
				}
			)
		);
	}

	public void testContainsAnyIgnoreCase4(){
		assertTrue(
			!StringHelper.containsAnyIgnoreCase(
				"on"+"t"+"w"+"t"+"h"+"r"+"e",
				new String[]{
					"One",
					"Two",
					"Three"
				}
			)
		);
	}

	public void testEqualsAnyIgnoreCase1(){
		assertTrue(
			StringHelper.equalsAnyIgnoreCase(
				"Two",
				new String[]{
					"One",
					"Two",
					"Three"
				}
			)
		);
	}

	public void testEqualsAnyIgnoreCase2(){
		assertTrue(
			!StringHelper.equalsAnyIgnoreCase(
				"one"+"two"+"three",
				new String[]{
					"One",
					"Two",
					"Three"
				}
			)
		);
	}

	public void testStartsWithAnyIgnoreCase1(){
		assertTrue(
			StringHelper.startsWithAnyIgnoreCase(
				"Two",
				new String[]{
					"One",
					"Two",
					"Three"
				}
			)
		);
	}

	public void testStartsWithAnyIgnoreCase2(){
		assertTrue(
			!StringHelper.startsWithAnyIgnoreCase(
				"on"+"two"+"three",
				new String[]{
					"One",
					"Two",
					"Three"
				}
			)
		);
	}

	public void testEndsWithAnyIgnoreCase1(){
		assertTrue(
			StringHelper.endsWithAnyIgnoreCase(
				"Two",
				new String[]{
					"One",
					"Two",
					"Three"
				}
			)
		);
	}

	public void testEndsWithAnyIgnoreCase2(){
		assertTrue(
			!StringHelper.endsWithAnyIgnoreCase(
				"one"+"two"+"t"+"h"+"r"+"e",
				new String[]{
					"One",
					"Two",
					"Three"
				}
			)
		);
	}

	public void testSplitIncludeDelimiters1(){
		assertEquals(
			StringHelper.splitIncludeDelimiters("1-2-3", "-"),
			new String[]{"1","-","2","-","3"}
		);
	}

	public void testSplitIncludeDelimiters2(){
		assertEquals(
			StringHelper.splitIncludeDelimiters("-1--2-", "-"),
			new String[]{"","-","1","-","","-","2","-",""}
		);
	}

	public void testSplitIncludeDelimiters3(){
		assertEquals(
			StringHelper.splitIncludeDelimiters("123", ""),
			new String[]{"123"}
		);
	}

	public void testSplitIncludeDelimiters4(){
		assertEquals(
			StringHelper.splitIncludeDelimiters("1-2--3---4----5", "--"),
			new String[]{"1-2","--","3","--","-4","--","","--","5"}
		);
	}

	public void testSplitIncludeDelimiters5(){
		assertEquals(
			StringHelper.splitIncludeDelimiters("12345678", "--"),
			new String[]{"12345678"}
		);
	}

	public void testJoin1(){
		assertEquals(
			StringHelper.join(
				new String[]{
					null
				}
			),
			""
		);
	}

	public void testJoin2(){
		assertEquals(
			StringHelper.join(
				new String[]{
					""
				}
			),
			""
		);
	}

	public void testJoin3(){
		assertEquals(
			StringHelper.join(
				new String[]{
					"",
					""
				}
			),
			""
		);
	}

	public void testJoin4(){
		assertEquals(
			StringHelper.join(
				new String[]{
					"one"
				}
			),
			"one"
		);
	}

	public void testJoin5(){
		assertEquals(
			StringHelper.join(
				new String[]{
					"one",
					"two",
				}
			),
			"one"+"two"
		);
	}

	public void testJoin6(){
		assertEquals(
			StringHelper.join(
				new String[]{
					"one",
					null,
					"two",
					"",
					"three"
				}
			),
			"one"+"two"+"three"
		);

	}

	public void testJoin7(){
		assertEquals(
			StringHelper.join(
				new String[]{
					null
				},
				"|"
			),
			""
		);
	}

	public void testJoin8(){
		assertEquals(
			StringHelper.join(
				new String[]{
					""
				},
				"|"
			),
			""
		);
	}

	public void testJoin9(){
		assertEquals(
			StringHelper.join(
				new String[]{
					"",
					""
				},
				"|"
			),
			"|"
		);
	}

	public void testJoin10(){
		assertEquals(
			StringHelper.join(
				new String[]{
					"one"
				},
				"|"
			),
			"one"
		);
	}

	public void testJoin11(){
		assertEquals(
			StringHelper.join(
				new String[]{
					"one",
					"two",
				},
				"|"
			),
			"one|two"
		);
	}

	public void testJoin12(){
		assertEquals(
			StringHelper.join(
				new String[]{
					"one",
					null,
					"two",
					"",
					"three"
				},
				"|"
			),
			"one||two||three"
		);
	}

	public void testParseBooleanTrue(){
		assertEquals(Boolean.TRUE, StringHelper.parseBoolean("true"));
		assertEquals(Boolean.TRUE, StringHelper.parseBoolean("t"));
		assertEquals(Boolean.TRUE, StringHelper.parseBoolean("yes"));
		assertEquals(Boolean.TRUE, StringHelper.parseBoolean("y"));
		assertEquals(Boolean.TRUE, StringHelper.parseBoolean("1"));
	}

	public void testParseBooleanFalse(){
		assertEquals(Boolean.FALSE, StringHelper.parseBoolean("false"));
		assertEquals(Boolean.FALSE, StringHelper.parseBoolean("f"));
		assertEquals(Boolean.FALSE, StringHelper.parseBoolean("no"));
		assertEquals(Boolean.FALSE, StringHelper.parseBoolean("n"));
		assertEquals(Boolean.FALSE, StringHelper.parseBoolean("0"));
	}

	public void testParseInteger(){
		assertEquals(new Integer(0), StringHelper.parseInteger("0"));
		assertEquals(new Integer(123456), StringHelper.parseInteger("123456"));
		assertEquals(new Integer(-654321), StringHelper.parseInteger("-654321"));
	}

	public void testParseIntegerStartsWithZero(){
		assertEquals(new Integer(700), StringHelper.parseInteger("0700"));
	}

	public void testParseIntegerBinary(){
		assertEquals(new Integer(7), StringHelper.parseInteger("0b111"));
		assertEquals(new Integer(5), StringHelper.parseInteger("0B101"));
	}

	public void testParseIntegerOctal(){
		assertEquals(new Integer(9), StringHelper.parseInteger("0c11"));
		assertEquals(new Integer(16), StringHelper.parseInteger("0C20"));
	}

	public void testParseIntegerHex(){
		assertEquals(new Integer(0x0), StringHelper.parseInteger("0x0"));
		assertEquals(new Integer(0xbeef), StringHelper.parseInteger("0xBeef"));
		assertEquals(new Integer(0xfeed), StringHelper.parseInteger("0XfeeD"));
		assertEquals(new Integer(0xface), StringHelper.parseInteger(" 0xFaCe "));
	}

	public void testParseIntHex(){
		assertEquals(0x0, StringHelper.parseInt("0x0", -1));
		assertEquals(0xbeef, StringHelper.parseInt("0xBeef", -1));
		assertEquals(0xfeed, StringHelper.parseInt("0XfeeD", -1));
		assertEquals(0xface, StringHelper.parseInt(" 0xFaCe ", -1));
	}

	public void testParseIntegerRadix(){
		assertEquals(new Integer(0), StringHelper.parseInteger("0", 16));
		assertEquals(new Integer(0xbeef), StringHelper.parseInteger("Beef", 16));
	}

	public void testParseIntegerWhiteSpace(){
		assertEquals(new Integer(713838), StringHelper.parseInteger(" 713838 "));
		assertEquals(new Integer(713838), StringHelper.parseInteger("\t713838\t"));
		assertEquals(new Integer(713838), StringHelper.parseInteger("\n713838\n"));
		assertEquals(new Integer(713838), StringHelper.parseInteger("\n \t 713838 \n \t"));
	}

	public void testParseIntegerNull(){
		assertNull(StringHelper.parseInteger(null));
		assertNull(StringHelper.parseInteger(""));
		assertNull(StringHelper.parseInteger("foo"));
		assertNull(StringHelper.parseInteger("9999999999999999999999"));
		assertNull(StringHelper.parseInteger("-9999999999999999999999"));
		assertNull(StringHelper.parseInteger("- 1"));
	}

	public void testParseBooleanNull(){
		assertNull(StringHelper.parseBoolean(""));
		assertNull(StringHelper.parseBoolean(null));
		assertNull(StringHelper.parseBoolean("X"));
		assertNull(StringHelper.parseBoolean("true false"));
		assertNull(StringHelper.parseBoolean("true t 1"));
	}

	public void testParseBooleanCaseInsensitive(){
		assertEquals(Boolean.TRUE, StringHelper.parseBoolean("TRUE"));
		assertEquals(Boolean.FALSE, StringHelper.parseBoolean("False"));
		assertEquals(Boolean.FALSE, StringHelper.parseBoolean("nO"));
	}

	public void testParseBooleanInsignificantWhiteSpace(){
		assertEquals(Boolean.TRUE, StringHelper.parseBoolean(" true "));
		assertEquals(Boolean.TRUE, StringHelper.parseBoolean("yes\t"));
		assertEquals(Boolean.TRUE, StringHelper.parseBoolean("Y\n"));
		assertEquals(Boolean.TRUE, StringHelper.parseBoolean("\t\n OK \t \n"));
	}

	public void testParseBooleanTrueDefault(){
		assertEquals(true, StringHelper.parseBoolean("true", true));
		assertEquals(true, StringHelper.parseBoolean("true", false));
		assertEquals(true, StringHelper.parseBoolean(null, true));
		assertEquals(true, StringHelper.parseBoolean("", true));
	}

	public void testParseBooleanFalseDefault(){
		assertEquals(false, StringHelper.parseBoolean("false", true));
		assertEquals(false, StringHelper.parseBoolean("false", false));
		assertEquals(false, StringHelper.parseBoolean(null, false));
		assertEquals(false, StringHelper.parseBoolean("", false));
	}
}
