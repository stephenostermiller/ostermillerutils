/*
 * CSV Regression test.
 * Copyright (C) 2001,2002 Stephen Ostermiller <utils@Ostermiller.com>
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
 * More information about this class is available from <a href=
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
    }
}
