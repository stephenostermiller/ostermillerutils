package com.Ostermiller.util;

import java.io.*;

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
        csvOut.printlnComment("A two line comment\njust to see that it works");
        csvOut.println();
        csvOut.println("Some weird values that could break things");
        for (int i=0; i<256; i++){
            csvOut.print("" + (char)i);
        }

        CSVParser shredder = new CSVParser(new FileInputStream(f));
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
