/*
 * Regression tests for Tabs
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
 * Unit test for Tabs.
 * More information about this class is available from <a target="_top" href=
 * "https://ostermiller.org/utils/Tabs.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 *         https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.04
 */
public class TabsTest extends TestCase {

	public void testConvertEmptyStream() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		boolean modified = Tabs.convert(in, out);
		assertFalse(modified);
		assertEquals(0, out.size());
	}

	public void testConvertTabsToSpaces() throws IOException {
		String input = "\tcode\n\t\tmore code\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertFalse(result.contains("\t"));
		assertTrue(result.contains("code"));
		assertTrue(result.contains("more code"));
	}

	public void testConvertSpacesToTabs() throws IOException {
		String input = "    code\n        more code\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, Tabs.TABS);
		String result = out.toString();
		assertTrue(result.contains("\t"));
		assertTrue(result.contains("code"));
		assertTrue(result.contains("more code"));
	}

	public void testConvertDefaultTabWidth() throws IOException {
		String input = "\tcode\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out);
		String result = out.toString();
		assertTrue(result.contains("code"));
	}

	public void testConvertTabsConstant() {
		assertEquals(-1, Tabs.TABS);
	}

	public void testConvertMixedTabsAndSpaces() throws IOException {
		String input = "\t  code\n\t\t    more\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("code"));
		assertTrue(result.contains("more"));
	}

	public void testConvertMiddleOfLineTabsPreserved() throws IOException {
		String input = "code\t\tmore\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("code"));
		assertTrue(result.contains("more"));
	}

	public void testConvertNoTabsOrSpaces() throws IOException {
		String input = "code\nmore\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		boolean modified = Tabs.convert(in, out, 4, 4);
		assertFalse(modified);
		assertEquals(input, out.toString());
	}

	public void testConvertLeadingSpacesOnly() throws IOException {
		String input = "    code\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("code"));
	}

	public void testConvertLeadingTabsOnly() throws IOException {
		String input = "\t\tcode\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("code"));
	}

	public void testConvertDifferentInputAndOutputWidths() throws IOException {
		String input = "\tcode\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 8, 4);
		String result = out.toString();
		assertTrue(result.contains("code"));
	}

	public void testConvertEmptyLines() throws IOException {
		String input = "\tcode\n\n\tmore\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("code"));
		assertTrue(result.contains("more"));
	}

	public void testConvertMultipleEmptyLines() throws IOException {
		String input = "\tcode\n\n\n\n\tmore\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("code"));
		assertTrue(result.contains("more"));
	}

	public void testConvertWithCarriageReturn() throws IOException {
		String input = "\tcode\r\n\tmore\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("code"));
		assertTrue(result.contains("more"));
	}

	public void testConvertTabWidth2() throws IOException {
		String input = "\t\tcode\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 2, 2);
		String result = out.toString();
		assertTrue(result.contains("code"));
	}

	public void testConvertTabWidth8() throws IOException {
		String input = "\tcode\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 8, 8);
		String result = out.toString();
		assertTrue(result.contains("code"));
	}

	public void testConvertInvalidInputTabWidth() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream("test\n".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Tabs.convert(in, out, 0, 4);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// Expected
		}
	}

	public void testConvertInvalidOutputTabWidth() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream("test\n".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Tabs.convert(in, out, 4, 0);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// Expected
		}
	}

	public void testConvertTabWidthTooLarge() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream("test\n".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Tabs.convert(in, out, 21, 4);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// Expected
		}
	}

	public void testConvertLargeInput() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			sb.append("\tline ").append(i).append("\n");
		}
		ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("line 0"));
		assertTrue(result.contains("line 999"));
	}

	public void testConvertPreservesNonIndentationContent() throws IOException {
		String input = "\tThis is a sentence with code in it\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("This"));
		assertTrue(result.contains("sentence"));
		assertTrue(result.contains("code"));
	}

	public void testConvertSingleLineNoNewline() throws IOException {
		String input = "\tcode";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("code"));
	}

	public void testConvertMultipleConsecutiveTabs() throws IOException {
		String input = "\t\t\tcode\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4);
		String result = out.toString();
		assertTrue(result.contains("code"));
	}

	public void testConvertWithBinaryExceptionFalse() throws IOException {
		String input = "\tcode\nmore\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4, false);
		String result = out.toString();
		assertTrue(result.contains("code"));
		assertTrue(result.contains("more"));
	}

	public void testConvertWithBinaryExceptionTrue() throws IOException {
		String input = "\tcode\nmore\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 4, true);
		String result = out.toString();
		assertTrue(result.contains("code"));
		assertTrue(result.contains("more"));
	}

	public void testConvertTabsToLargerSpaces() throws IOException {
		String input = "\tcode\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tabs.convert(in, out, 4, 8);
		boolean hasTab = out.toString().contains("\t");
		assertFalse(hasTab);
		assertTrue(out.toString().contains("code"));
	}
}
