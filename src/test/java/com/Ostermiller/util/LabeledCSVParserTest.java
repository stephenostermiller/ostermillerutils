/*
 * Tests reading files in comma separated value format with a fist line of labels.
 *
 * Copyright (C) 2004 Campbell, Allen T. <allenc28@yahoo.com>
 *
 * Copyright (C) 2004 Stephen Ostermiller
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

import junit.framework.TestCase;
import java.io.*;

/**
 * Tests reading files in comma separated value format with a fist line of labels.
 *
 * @author Campbell, Allen T. <allenc28@yahoo.com>
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.03.00
 */
public class LabeledCSVParserTest extends TestCase {

	public void testEmptyInputFile() throws IOException {
		LabeledCSVParser parse = new LabeledCSVParser(
			new CSVParser(
				new StringReader(
					""
				)
			)
		);
		assertNull(parse.getLine());
		assertNull(parse.getLabels());
		assertEquals(-1, parse.getLastLineNumber());
	}

	public void testInputHasOnlyLabels() throws IOException {
		LabeledCSVParser parse = new LabeledCSVParser(
			new CSVParser(
				new StringReader(
					"FIELD01,FIELD02,FIELD03"
				)
			)
		);
		assertNull(parse.getLine());
		String[] labels = parse.getLabels();
		assertEquals(3, labels.length);
		assertEquals("FIELD01", labels[0]);
		assertEquals("FIELD02", labels[1]);
		assertEquals("FIELD03", labels[2]);
		assertEquals(-1, parse.getLastLineNumber());
	}

	public void testLabelsAndData() throws IOException {
		LabeledCSVParser parse = new LabeledCSVParser(
			new CSVParser(
				new StringReader(
					"FIELD01,FIELD02,FIELD03\n" +
					"9.23,\"FOO\",\"BAR\"\n" +
					"10.5,\"BAZ\",\"FOO2\"\n"
				)
			)
		);
		String[] labels = parse.getLabels();
		assertEquals(3, labels.length);
		assertEquals("FIELD01", labels[0]);
		assertEquals("FIELD02", labels[1]);
		assertEquals("FIELD03", labels[2]);
		assertEquals(0, parse.getLabelIdx("FIELD01"));
		assertEquals(1, parse.getLabelIdx("FIELD02"));
		assertEquals(2, parse.getLabelIdx("FIELD03"));
		assertEquals(-1, parse.getLabelIdx("FIELD04"));
		assertNull(parse.getValueByLabel("FIELD01"));
		String[] line = parse.getLine();
		assertNotNull(line);
		assertEquals(1, parse.getLastLineNumber());
		assertEquals(3, line.length);
		assertEquals("9.23", line[0]);
		assertEquals("FOO", line[1]);
		assertEquals("BAR", line[2]);
		assertEquals("9.23",parse.getValueByLabel("FIELD01"));
		String value = parse.nextValue();
		assertEquals("10.5", value);
		IllegalStateException illegalStateException = null;
		try {
			parse.getValueByLabel("FIELD01");
		} catch (IllegalStateException iex){
			illegalStateException = iex;
		}
		assertNotNull(illegalStateException);
		assertEquals(2, parse.getLastLineNumber());
		line = parse.getLine();
		assertNotNull(line);
		assertEquals(2, parse.getLastLineNumber());
		assertEquals(2, line.length);
		assertEquals("BAZ", line[0]);
		assertEquals("FOO2", line[1]);
		illegalStateException = null;
		try {
			parse.getValueByLabel("FIELD01");
		} catch (IllegalStateException iex){
			illegalStateException = iex;
		}
		assertNotNull(illegalStateException);
		assertNull(parse.getLine());
	}
}
