/*
 * CSV Regression test.
 * Copyright (C) 2001,2002 Stephen Ostermiller
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

import java.io.*;

/**
 * Regression test for CSV.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/CSVLexer.html">ostermiller.org</a>.
 */
class CSVTest {
	public static void main (String[] args) throws IOException{
		File f = new File("CSVTest.txt");
		FileOutputStream out = new FileOutputStream(f);
		CSVPrinter csvOut = new CSVPrinter(out, '#');

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
			"Two\nTokens",
			"On the\nSame Line"
		});
		csvOut.printlnComment("A two line comment\njust to see that it works");

		CSVParser shredder = new CSVParser(new StraightStreamReader(new FileInputStream(f)));
		shredder.setCommentStart("#;!");
		shredder.setEscapes("nrtf", "\n\r\t\f");
		String t;
		while ((t = shredder.nextValue()) != null) {
			if (t.length() == 1){
				System.out.println("" + shredder.lastLineNumber() + " " + (int)(t.charAt(0)));
			} else {
				System.out.println("" + shredder.lastLineNumber() + " " + t);
			}
		}

		try {
			String normalInput = ",\"a\",\",\t'\\\"\"";
			String[][] normalOutput = new String[][]{{"", "a", ",\t'\""}};
			shredder = new CSVParser(new StringReader(normalInput));
			compare("normal", normalOutput, shredder.getAllValues());

			String tabInput = "\t\"a\"\t\",\t'\\\"\"";
			shredder = new CSVParser(new StringReader(tabInput));
			shredder.changeDelimiter('\t');
			compare("tabs", normalOutput, shredder.getAllValues());

			String aposInput = ",'a',',\t\\'\"'";
			shredder = new CSVParser(new StringReader(aposInput));
			shredder.changeQuote('\'');
			compare("apostrophes", normalOutput, shredder.getAllValues());

			String swappedInput = "\",a,\",\\,\t'\\\",";
			shredder = new CSVParser(new StringReader(swappedInput));
			shredder.changeDelimiter('\t');
			shredder.changeQuote(',');
			shredder.changeDelimiter('"');
			compare("commas and quotes swapped", normalOutput, shredder.getAllValues());
		} catch (Exception x){
			System.err.println(x.getMessage());
			System.exit(1);
		}

	}

	private static void compare(String testName, String[][] a, String[][] b){
		if (a.length != b.length) {
			System.err.println(testName + ": unexpected number of lines");
			System.exit(1);
		}
		for(int i=0; i<a.length; i++){
			if (a[i].length != b[i].length) {
				System.err.println(testName + ": unexpected number of values in line: " + b[i].length);
				System.exit(1);
			}
			for (int j=0; j<a[i].length; j++){
				if (!a[i][j].equals(b[i][j])) {
					System.err.println(testName + ": values do not match.");
					System.exit(1);
				}
			}
		}

	}
}
