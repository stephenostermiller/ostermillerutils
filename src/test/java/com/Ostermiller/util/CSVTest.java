/*
 * CSV Regression test.
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

import junit.framework.TestCase;
import java.io.*;

/**
 * Regression test for CSV.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/CSV.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */
public class CSVTest extends TestCase {

	public void testStepByStep(){
		try {
			StringWriter sw = new StringWriter();
			CSVPrinter csvOut = new CSVPrinter(sw, '#');
			csvOut.setLineEnding("\r");

			csvOut.printlnComment("Comma Separated Value Test");
			csvOut.println();
			csvOut.printlnComment("Five Cities");
			csvOut.println(new String[] {
				"Boston",
				"San Francisco",
				"New York",
				"Chicago",
				"Houston",
			});
			csvOut.println();
			csvOut.println(""); // an empty value on a line by itself.
			csvOut.println(new String[] {
				"Two\n"+"Tokens",
				"On the\n"+"Same Line"
			});
			csvOut.printlnComment("A two line comment\n just to see that it works");

			CSVParser csvParser = new CSVParser(new StringReader(sw.toString()));
			csvParser.setCommentStart("#;!");
			csvParser.setEscapes("nr"+"tf", "\n\r\t\f");
			compare(csvParser.nextValue(), csvParser.lastLineNumber(), "Boston", 4);
			compare(csvParser.nextValue(), csvParser.lastLineNumber(), "San Francisco", 4);
			compare(csvParser.nextValue(), csvParser.lastLineNumber(), "New York", 4);
			compare(csvParser.nextValue(), csvParser.lastLineNumber(), "Chicago", 4);
			compare(csvParser.nextValue(), csvParser.lastLineNumber(), "Houston", 4);
			compare(csvParser.nextValue(), csvParser.lastLineNumber(), "", 6);
			compare(csvParser.nextValue(), csvParser.lastLineNumber(), "Two\n"+"Tokens", 7);
			compare(csvParser.nextValue(), csvParser.lastLineNumber(), "On the\n"+"Same Line", 7);
			compare(csvParser.nextValue(), csvParser.lastLineNumber(), null, 9);
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}

	public void testNormal(){
		String input = ",\"a\",\",\t'\\\"\"";
		String[][] output = new String[][]{{"", "a", ",\t'\""}};
		CSVParser csvParser = new CSVParser(new StringReader(input));
		compare(output, getAllValues(csvParser));
	}

	public void testTabs(){
		String tabInput = "\t\"a\"\t\",\t'\\\"\"";
		String[][] output = new String[][]{{"", "a", ",\t'\""}};
		CSVParser csvParser = new CSVParser(new StringReader(tabInput));
		csvParser.changeDelimiter('\t');
		compare(output, getAllValues(csvParser));
	}

	public void testApostrophes(){
		String input = ",'a',',\t\\'\"'";
		String[][] output = new String[][]{{"", "a", ",\t'\""}};
		CSVParser csvParser = new CSVParser(new StringReader(input));
		csvParser.changeQuote('\'');
		compare(output, getAllValues(csvParser));
	}

	public void testCommasAndQuotesSwapped(){
		String input = "\",a,\",\\,\t'\\\",";
		String[][] output = new String[][]{{"", "a", ",\t'\""}};
		CSVParser csvParser = new CSVParser(new StringReader(input));
		csvParser.changeDelimiter('\t');
		csvParser.changeQuote(',');
		csvParser.changeDelimiter('"');
		compare(output, getAllValues(csvParser));
	}

	public void testBackslashAtEndOfQuoted(){
		String input = "\"test\\\\\",test";
		String[][] output = new String[][]{{"test\\", "test"}};
		CSVParser csvParser = new CSVParser(new StringReader(input));
		compare(output, getAllValues(csvParser));
	}

	public void testWhitSpaceAroundFields(){
		String input = "field1,field2 ,    field3,field4   ,  field5   ,field6";
		String[][] output = new String[][]{{"field1", "field2", "field3", "field4", "field5", "field6"}};
		CSVParser csvParser = new CSVParser(new StringReader(input));
		compare(output, getAllValues(csvParser));
	}

	public void testEmptyFields(){
		String input = ",field2,, ,field5,";
		String[][] output = new String[][]{{"", "field2", "", "", "field5", ""}};
		CSVParser csvParser = new CSVParser(new StringReader(input));
		compare(output, getAllValues(csvParser));
	}

	public void testVariousLengths(){
		String input = "1,to,tre,four,five5,sixsix";
		String[][] output = new String[][]{{"1", "to", "tre", "four", "five5", "sixsix"}};
		CSVParser csvParser = new CSVParser(new StringReader(input));
		compare(output, getAllValues(csvParser));
	}

	public void testCommentMustStartAtbeginningOfLine(){
		String input = "!comment\n !field1\n;comment\n ;field2\n#comment\n #field3";
		String[][] output = new String[][]{{"!field1"},{";field2"},{"#field3"}};
		CSVParser csvParser = new CSVParser(new StringReader(input));
		csvParser.setCommentStart("#;!");
		compare(output, getAllValues(csvParser));
	}

	private static String[][] getAllValues(CSVParser csvParser){
		try {
			return csvParser.getAllValues();
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}

	private static void compare(String value, int line, String expectedValue, int expectedLine){
		assertEquals(line, expectedLine);
		assertEquals(expectedValue, value);
	}

	private static void compare(String[][] a, String[][] b){
		assertEquals(a.length,b.length);
		for(int i=0; i<a.length; i++){
			assertEquals(a[i].length,b[i].length);
			for (int j=0; j<a[i].length; j++){
				assertEquals(a[i][j],b[i][j]);
			}
		}
	}
}
