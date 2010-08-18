/*
 * Copyright (C) 2005 Stephen Ostermiller
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

import java.util.*;

/**
 * StringHelper regression test.
 */
class StringHelperTests {

	private static void equalOrDie(String testName, Object[] a, Object[] b) throws Exception {
		if (!Arrays.equals(a,b)){
			throw new Exception(testName + " failed, arrays are not equal");
		}
	}

	private static void equalOrDie(String testName, String a, String b) throws Exception {
		if (!a.equals(b)){
			throw new Exception(testName + " failed, arrays are not equal");
		}
	}
	private static void trueOrDie(String testName, boolean b) throws Exception {
		if (!b){
			throw new Exception(testName + " failed, condition not met");
		}
	}

	/**
	 * Main method for tests
	 * @param args command line arguments (ignored)
	 */
	public static void main(String[] args){
		try {
			equalOrDie(
				"Split Test 1",
				StringHelper.split("1-2-3", "-"),
				new String[]{"1","2","3"}
			);
			equalOrDie(
				"Split Test 2",
				StringHelper.split("-1--2-", "-"),
				new String[]{"","1","","2",""}
			);
			equalOrDie(
				"Split Test 3",
				StringHelper.split("123", ""),
				new String[]{"123"}
			);
			equalOrDie(
				"Split Test 4",
				StringHelper.split("1-2---3----4", "--"),
				new String[]{"1-2","-3","","4"}
			);
			equalOrDie(
				"Split Test 5",
				StringHelper.split("12345678", "--"),
				new String[]{"12345678"}
			);
			equalOrDie(
				"Prepad Test 1",
				StringHelper.prepad("a", 8),
				"       a"
			);
			equalOrDie(
				"Prepad Test 2",
				StringHelper.prepad("AaaAa", 2),
				"AaaAa"
			);
			equalOrDie(
				"Prepad Test 3",
				StringHelper.prepad("a", 8, '-'),
				"-------a"
			);
			equalOrDie(
				"Postpad Test 1",
				StringHelper.postpad("a", 8),
				"a       "
			);
			equalOrDie(
				"Postpad Test 2",
				StringHelper.postpad("AaaAa", 2),
				"AaaAa"
			);
			equalOrDie(
				"Postpad Test 3",
				StringHelper.postpad("a", 8, '-'),
				"a-------"
			);
			equalOrDie(
				"Midpad Test 1",
				StringHelper.midpad("a", 3),
				" a "
			);
			equalOrDie(
				"Midpad Test 2",
				StringHelper.midpad("a", 4),
				" a  "
			);
			equalOrDie(
				"Midpad Test 3",
				StringHelper.midpad("a", 5, '-'),
				"--a--"
			);
			equalOrDie(
				"midpad Test 4",
				StringHelper.midpad("AaaAa", 2),
				"AaaAa"
			);
			equalOrDie(
				"replace Test 1",
				StringHelper.replace("1-2-3", "-", "|"),
				"1|2|3"
			);
			equalOrDie(
				"replace Test 2",
				StringHelper.replace("-1--2-", "-", "|"),
				"|1||2|"
			);
			equalOrDie(
				"replace Test 3",
				StringHelper.replace("123", "", "|"),
				"123"
			);
			equalOrDie(
				"replace Test 4",
				StringHelper.replace("1-2---3----4", "--", "|"),
				"1-2|-3||4"
			);
			equalOrDie(
				"replace Test 5",
				StringHelper.replace("1-2--3---4----", "--", "---"),
				"1-2---3----4------"
			);
			equalOrDie(
				"escapeHTML Test 1",
				StringHelper.escapeHTML("<>&\"'\0\1\2\n\r\f\t"+"hello"),
				"&lt;&gt;&amp;&quot;&#39;\n\r\f\t"+"hello"
			);
			equalOrDie(
				"escapeSQL Test 1",
				StringHelper.escapeSQL("\0\'\"\r\n"+"hello"),
				"\\0\\'\\\"\r\n"+"hello"
			);
			equalOrDie(
				"escapeJavaLiteral Test 1",
				StringHelper.escapeJavaLiteral("\0\'\"\r\n"+"hello"),
				"\0\\'\\\"\\r\\n"+"hello"
			);
			equalOrDie(
				"trim Test 1",
				StringHelper.trim("- -\r\n"+"hello- -\r\n"+"hello- -\r\n", "\r- \n"),
				"hello- -\r\n"+"hello"
			);
			equalOrDie(
				"unescapeHTML Test 1",
				StringHelper.unescapeHTML("&gt;hello"+"&"+"euro"+";"),
				">hello\u20AC"
			);
			trueOrDie(
				"containsAny Test 1",
				StringHelper.containsAny(
					"on"+"two"+"t"+"h"+"r"+"e",
					new String[]{
						"one",
						"two",
						"three"
					}
				)
			);
			trueOrDie(
				"containsAny Test 2",
				StringHelper.containsAny(
					"one"+"t"+"w"+"t"+"h"+"r"+"e",
					new String[]{
						"one",
						"two",
						"three"
					}
				)
			);
			trueOrDie(
				"containsAny Test 3",
				StringHelper.containsAny(
					"on"+"t"+"w"+"three",
					new String[]{
						"one",
						"two",
						"three"
					}
				)
			);
			trueOrDie(
				"containsAny Test 4",
				!StringHelper.containsAny(
					"on"+"t"+"w"+"t"+"h"+"r"+"e",
					new String[]{
						"one",
						"two",
						"three"
					}
				)
			);
			trueOrDie(
				"equalsAny Test 1",
				StringHelper.equalsAny(
					"two",
					new String[]{
						"one",
						"two",
						"three"
					}
				)
			);
			trueOrDie(
				"equalsAny Test 2",
				!StringHelper.equalsAny(
					"one"+"two"+"three",
					new String[]{
						"one",
						"two",
						"three"
					}
				)
			);
			trueOrDie(
				"startsWithAny Test 1",
				StringHelper.startsWithAny(
					"two",
					new String[]{
						"one",
						"two",
						"three"
					}
				)
			);
			trueOrDie(
				"startsWithAny Test 2",
				!StringHelper.startsWithAny(
					"on"+"two"+"three",
					new String[]{
						"one",
						"two",
						"three"
					}
				)
			);
			trueOrDie(
				"endsWithAny Test 1",
				StringHelper.endsWithAny(
					"two",
					new String[]{
						"one",
						"two",
						"three"
					}
				)
			);
			trueOrDie(
				"endsWithAny Test 2",
				!StringHelper.endsWithAny(
					"one"+"two"+"t"+"h"+"r"+"e",
					new String[]{
						"one",
						"two",
						"three"
					}
				)
			);
			trueOrDie(
				"containsAnyIgnoreCase Test 1",
				StringHelper.containsAnyIgnoreCase(
					"on"+"two"+"t"+"h"+"r"+"e",
					new String[]{
						"One",
						"Two",
						"Three"
					}
				)
			);
			trueOrDie(
				"containsAnyIgnoreCase Test 2",
				StringHelper.containsAnyIgnoreCase(
					"one"+"t"+"w"+"t"+"h"+"r"+"e",
					new String[]{
						"One",
						"Two",
						"Three"
					}
				)
			);
			trueOrDie(
				"containsAnyIgnoreCase Test 3",
				StringHelper.containsAnyIgnoreCase(
					"on"+"t"+"w"+"three",
					new String[]{
						"One",
						"Two",
						"Three"
					}
				)
			);
			trueOrDie(
				"containsAnyIgnoreCase Test 4",
				!StringHelper.containsAnyIgnoreCase(
					"on"+"t"+"w"+"t"+"h"+"r"+"e",
					new String[]{
						"One",
						"Two",
						"Three"
					}
				)
			);
			trueOrDie(
				"equalsAnyIgnoreCase Test 1",
				StringHelper.equalsAnyIgnoreCase(
					"Two",
					new String[]{
						"One",
						"Two",
						"Three"
					}
				)
			);
			trueOrDie(
				"equalsAnyIgnoreCase Test 2",
				!StringHelper.equalsAnyIgnoreCase(
					"one"+"two"+"three",
					new String[]{
						"One",
						"Two",
						"Three"
					}
				)
			);
			trueOrDie(
				"startsWithAnyIgnoreCase Test 1",
				StringHelper.startsWithAnyIgnoreCase(
					"Two",
					new String[]{
						"One",
						"Two",
						"Three"
					}
				)
			);
			trueOrDie(
				"startsWithAnyIgnoreCase Test 2",
				!StringHelper.startsWithAnyIgnoreCase(
					"on"+"two"+"three",
					new String[]{
						"One",
						"Two",
						"Three"
					}
				)
			);
			trueOrDie(
				"endsWithAnyIgnoreCase Test 1",
				StringHelper.endsWithAnyIgnoreCase(
					"Two",
					new String[]{
						"One",
						"Two",
						"Three"
					}
				)
			);
			trueOrDie(
				"endsWithAnyIgnoreCase Test 2",
				!StringHelper.endsWithAnyIgnoreCase(
					"one"+"two"+"t"+"h"+"r"+"e",
					new String[]{
						"One",
						"Two",
						"Three"
					}
				)
			);
			equalOrDie(
				"splitIncludeDelimiters Test 1",
				StringHelper.splitIncludeDelimiters("1-2-3", "-"),
				new String[]{"1","-","2","-","3"}
			);
			equalOrDie(
				"splitIncludeDelimiters Test 2",
				StringHelper.splitIncludeDelimiters("-1--2-", "-"),
				new String[]{"","-","1","-","","-","2","-",""}
			);
			equalOrDie(
				"splitIncludeDelimiters Test 3",
				StringHelper.splitIncludeDelimiters("123", ""),
				new String[]{"123"}
			);
			equalOrDie(
				"splitIncludeDelimiters Test 4",
				StringHelper.splitIncludeDelimiters("1-2--3---4----5", "--"),
				new String[]{"1-2","--","3","--","-4","--","","--","5"}
			);
			equalOrDie(
				"splitIncludeDelimiters Test 5",
				StringHelper.splitIncludeDelimiters("12345678", "--"),
				new String[]{"12345678"}
			);
			equalOrDie(
				"join Test 1",
				StringHelper.join(
					new String[]{
						null
					}
				),
				""
			);
			equalOrDie(
				"join Test 2",
				StringHelper.join(
					new String[]{
						""
					}
				),
				""
			);
			equalOrDie(
				"join Test 3",
				StringHelper.join(
					new String[]{
						"",
						""
					}
				),
				""
			);
			equalOrDie(
				"join Test 4",
				StringHelper.join(
					new String[]{
						"one"
					}
				),
				"one"
			);
			equalOrDie(
				"join Test 5",
				StringHelper.join(
					new String[]{
						"one",
						"two",
					}
				),
				"one"+"two"
			);
			equalOrDie(
				"join Test 6",
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

			equalOrDie(
				"join Test 7",
				StringHelper.join(
					new String[]{
						null
					},
					"|"
				),
				""
			);
			equalOrDie(
				"join Test 8",
				StringHelper.join(
					new String[]{
						""
					},
					"|"
				),
				""
			);
			equalOrDie(
				"join Test 9",
				StringHelper.join(
					new String[]{
						"",
						""
					},
					"|"
				),
				"|"
			);
			equalOrDie(
				"join Test 10",
				StringHelper.join(
					new String[]{
						"one"
					},
					"|"
				),
				"one"
			);
			equalOrDie(
				"join Test 11",
				StringHelper.join(
					new String[]{
						"one",
						"two",
					},
					"|"
				),
				"one|two"
			);
			equalOrDie(
				"join Test 12",
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

		} catch (Exception x){
			x.printStackTrace(System.err);
			System.exit(1);
		}
		System.exit(0);
	}
}
