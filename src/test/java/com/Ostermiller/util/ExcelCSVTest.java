/*
 * Regression tests for ExcelCSV
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

/**
 * Unit test for ExcelCSVParser and ExcelCSVPrinter.
 * More information about this class is available from <a target="_top" href=
 * "https://utils.ostermiller.org/CSV.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 *         https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.04
 */
public class ExcelCSVTest extends TestCase {

	public void testBasicParsing() throws IOException {
		String input = "field1,field2,field3";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		assertEquals("field1", parser.nextValue());
		assertEquals("field2", parser.nextValue());
		assertEquals("field3", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testQuotedFields() throws IOException {
		String input = "\"field1\",\"field2\",\"field3\"";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		assertEquals("field1", parser.nextValue());
		assertEquals("field2", parser.nextValue());
		assertEquals("field3", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testFieldsWithCommas() throws IOException {
		String input = "\"field1,with,commas\",\"field2\",\"field3\"";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		assertEquals("field1,with,commas", parser.nextValue());
		assertEquals("field2", parser.nextValue());
		assertEquals("field3", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testFieldsWithNewlines() throws IOException {
		String input = "\"field1\\nwith\\nnewlines\",\"field2\",\"field3\"";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		String val1 = parser.nextValue();
		assertTrue(val1.contains("field1"));
		assertEquals("field2", parser.nextValue());
		assertEquals("field3", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testEscapedQuotes() throws IOException {
		String input = "\"field\\\"1\",\"field2\",\"field3\"";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		String val1 = parser.nextValue();
		assertTrue(val1.contains("field"));
		assertEquals("field2", parser.nextValue());
		assertEquals("field3", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testEmptyFields() throws IOException {
		String input = ",,";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		assertEquals("", parser.nextValue());
		assertEquals("", parser.nextValue());
		assertEquals("", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testMixedEmptyAndNonEmpty() throws IOException {
		String input = "field1,,field3";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		assertEquals("field1", parser.nextValue());
		assertEquals("", parser.nextValue());
		assertEquals("field3", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testLineNumbers() throws IOException {
		String input = "field1,field2\nfield3,field4\nfield5,field6";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		parser.nextValue(); // field1
		assertEquals(1, parser.lastLineNumber());
		parser.nextValue(); // field2
		assertEquals(1, parser.lastLineNumber());
		parser.nextValue(); // field3
		assertEquals(2, parser.lastLineNumber());
		parser.nextValue(); // field4
		assertEquals(2, parser.lastLineNumber());
		parser.nextValue(); // field5
		assertEquals(3, parser.lastLineNumber());
	}

	public void testComments() throws IOException {
		String input = "#comment\nfield1,field2\n#another comment\nfield3,field4";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		parser.setCommentStart("#");
		assertEquals("field1", parser.nextValue());
		assertEquals("field2", parser.nextValue());
		assertEquals("field3", parser.nextValue());
		assertEquals("field4", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testPrinting() throws IOException {
		StringWriter sw = new StringWriter();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(sw);
		printer.println(new String[]{"field1", "field2", "field3"});
		String output = sw.toString();
		assertTrue(output.contains("field1"));
		assertTrue(output.contains("field2"));
		assertTrue(output.contains("field3"));
	}

	public void testPrintingWithQuotes() throws IOException {
		StringWriter sw = new StringWriter();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(sw);
		printer.println(new String[]{"field1", "field,2", "field3"});
		String output = sw.toString();
		assertTrue(output.contains("field1"));
		assertTrue(output.contains("field,2"));
		assertTrue(output.contains("field3"));
	}

	public void testPrintingMultipleLines() throws IOException {
		StringWriter sw = new StringWriter();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(sw);
		printer.println(new String[]{"a", "b", "c"});
		printer.println(new String[]{"d", "e", "f"});
		printer.println(new String[]{"g", "h", "i"});
		String output = sw.toString();
		assertTrue(output.contains("a"));
		assertTrue(output.contains("d"));
		assertTrue(output.contains("g"));
	}

	public void testPrintingEmptyLine() throws IOException {
		StringWriter sw = new StringWriter();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(sw);
		printer.println();
		String output = sw.toString();
		assertTrue(output.length() > 0);
	}

	public void testPrintingMatrix() throws IOException {
		StringWriter sw = new StringWriter();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(sw);
		String[][] data = {
			{"1", "2", "3"},
			{"4", "5", "6"},
			{"7", "8", "9"}
		};
		printer.println(data);
		String output = sw.toString();
		assertTrue(output.contains("1"));
		assertTrue(output.contains("9"));
	}

	public void testRoundTrip() throws IOException {
		String[] originalData = {"field1", "field2", "field3"};

		// Print
		StringWriter sw = new StringWriter();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(sw);
		printer.println(originalData);
		String printed = sw.toString();

		// Parse
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(printed.getBytes()));
		assertEquals("field1", parser.nextValue());
		assertEquals("field2", parser.nextValue());
		assertEquals("field3", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testRoundTripWithSpecialCharacters() throws IOException {
		String[] originalData = {"field1", "field,2", "field3"};

		// Print
		StringWriter sw = new StringWriter();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(sw);
		printer.println(originalData);
		String printed = sw.toString();

		// Parse
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(printed.getBytes()));
		assertEquals("field1", parser.nextValue());
		assertEquals("field,2", parser.nextValue());
		assertEquals("field3", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testEmptyInput() throws IOException {
		String input = "";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		assertNull(parser.nextValue());
	}

	public void testSingleField() throws IOException {
		String input = "field1";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		assertEquals("field1", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testQuotedFieldAtEnd() throws IOException {
		String input = "field1,\"field2\"";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		assertEquals("field1", parser.nextValue());
		assertEquals("field2", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testWhitespaceInFields() throws IOException {
		String input = "  field1  ,  field2  ,  field3  ";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		String val1 = parser.nextValue();
		assertTrue(val1.length() > 0);
	}

	public void testTabDelimiter() throws IOException {
		String input = "field1\tfield2\tfield3";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()), '\t');
		assertEquals("field1", parser.nextValue());
		assertEquals("field2", parser.nextValue());
		assertEquals("field3", parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testPrintingWithAlwaysQuote() throws IOException {
		StringWriter sw = new StringWriter();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(sw);
		printer.println(new String[]{"simple", "field", "value"});
		String output = sw.toString();
		assertTrue(output.length() > 0);
		assertTrue(output.contains("simple"));
	}

	public void testPrinterLineEnding() throws IOException {
		StringWriter sw = new StringWriter();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(sw);
		printer.println(new String[]{"a", "b"});
		String output = sw.toString();
		assertTrue(output.length() > 0);
	}

	public void testGetLastLineNumber() throws IOException {
		String input = "a,b\nc,d\ne,f";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		parser.nextValue();
		assertEquals(1, parser.getLastLineNumber());
		parser.nextValue();
		assertEquals(1, parser.getLastLineNumber());
		parser.nextValue();
		assertEquals(2, parser.getLastLineNumber());
	}

	public void testMultipleCallsToNextValue() throws IOException {
		String input = "field1";
		ExcelCSVParser parser = new ExcelCSVParser(new ByteArrayInputStream(input.getBytes()));
		assertEquals("field1", parser.nextValue());
		assertNull(parser.nextValue());
		assertNull(parser.nextValue());
	}

	public void testPrintingWithOutputStream() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(baos);
		printer.println(new String[]{"field1", "field2"});
		String output = baos.toString();
		assertTrue(output.contains("field1"));
		assertTrue(output.contains("field2"));
	}

	public void testPrintSingleValue() throws IOException {
		StringWriter sw = new StringWriter();
		ExcelCSVPrinter printer = new ExcelCSVPrinter(sw);
		printer.print("value1");
		printer.println("value2");
		String output = sw.toString();
		assertTrue(output.contains("value1"));
		assertTrue(output.contains("value2"));
	}
}
