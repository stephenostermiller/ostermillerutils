/*
 * Regression tests for LineEnds
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
 * Unit test for LineEnds.
 * More information about this class is available from <a target="_top" href=
 * "https://utils.ostermiller.org/LineEnds.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 *         https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.04
 */
public class LineEndsTest extends TestCase {

	public void testConvertEmptyStream() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		boolean modified = LineEnds.convert(in, out);
		assertFalse(modified);
		assertEquals(0, out.size());
	}

	public void testConvertUnixLineEndings() throws IOException {
		String input = "line1\r\nline2\r\nline3\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		boolean modified = LineEnds.convert(in, out, LineEnds.STYLE_N);
		assertTrue(modified);
		String result = out.toString();
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
		assertTrue(result.contains("line3"));
	}

	public void testConvertDosLineEndings() throws IOException {
		String input = "line1\r\nline2\r\nline3\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_RN);
		String result = out.toString();
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
		assertTrue(result.contains("line3"));
	}

	public void testConvertMacLineEndings() throws IOException {
		String input = "line1\rline2\rline3\r";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_N);
		String result = out.toString();
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
		assertTrue(result.contains("line3"));
	}

	public void testConvertMixedLineEndings() throws IOException {
		String input = "line1\nline2\r\nline3\rline4";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		boolean modified = LineEnds.convert(in, out, LineEnds.STYLE_N);
		assertTrue(modified);
		String result = out.toString();
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
		assertTrue(result.contains("line3"));
		assertTrue(result.contains("line4"));
	}

	public void testConvertSingleLineNoEnding() throws IOException {
		String input = "single line";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		boolean modified = LineEnds.convert(in, out, LineEnds.STYLE_N);
		assertFalse(modified);
		assertEquals(input, out.toString());
	}

	public void testConvertTrailingCarriageReturn() throws IOException {
		String input = "line1\rline2\r";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_N);
		String result = out.toString();
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
	}

	public void testConvertCarriageReturnNewlineCarriageReturn() throws IOException {
		String input = "line1\r\nline2";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_RN);
		String result = out.toString();
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
	}

	public void testConvertWithBinaryExceptionFalse() throws IOException {
		// Include a valid tab character
		String input = "line1\twith\ttabs\nline2";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_N, false);
		String result = out.toString();
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
	}

	public void testConvertEmptyLines() throws IOException {
		String input = "line1\n\n\nline2\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_N);
		String result = out.toString();
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
	}

	public void testConvertPreservesContent() throws IOException {
		String input = "The quick brown fox\njumps over the lazy dog\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_N);
		String result = out.toString();
		assertTrue(result.contains("quick"));
		assertTrue(result.contains("fox"));
		assertTrue(result.contains("jumps"));
		assertTrue(result.contains("dog"));
	}

	public void testConvertUnixToWindows() throws IOException {
		String input = "unix\nlines\nhere\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_RN);
		String result = out.toString();
		assertTrue(result.contains("unix"));
		assertTrue(result.contains("lines"));
		assertTrue(result.contains("here"));
	}

	public void testConvertUnixToMac() throws IOException {
		String input = "unix\nlines\nhere\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_R);
		String result = out.toString();
		assertTrue(result.contains("unix"));
		assertTrue(result.contains("lines"));
		assertTrue(result.contains("here"));
	}

	public void testConvertStyleConstants() {
		assertEquals(0, LineEnds.STYLE_SYSTEM);
		assertEquals(1, LineEnds.STYLE_DOS);
		assertEquals(1, LineEnds.STYLE_WINDOWS);
		assertEquals(1, LineEnds.STYLE_RN);
		assertEquals(2, LineEnds.STYLE_UNIX);
		assertEquals(2, LineEnds.STYLE_N);
		assertEquals(2, LineEnds.STYLE_JAVA);
		assertEquals(3, LineEnds.STYLE_MAC);
		assertEquals(3, LineEnds.STYLE_R);
	}

	public void testConvertInvalidStyle() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream("test\n".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			LineEnds.convert(in, out, 999);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// Expected
		}
	}

	public void testConvertLargeInput() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10000; i++) {
			sb.append("line ").append(i).append("\n");
		}
		ByteArrayInputStream in = new ByteArrayInputStream(sb.toString().getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_RN);
		String result = out.toString();
		assertTrue(result.contains("line 0"));
		assertTrue(result.contains("line 9999"));
	}

	public void testConvertNoModificationNeeded() throws IOException {
		String input = "line1\nline2\nline3\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// Convert to Unix style (already Unix)
		boolean modified = LineEnds.convert(in, out, LineEnds.STYLE_N);
		assertFalse(modified);
		assertEquals(input, out.toString());
	}

	public void testConvertDefaultStyle() throws IOException {
		String input = "line1\nline2\n";
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out);
		String result = out.toString();
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
	}

	public void testConvertWithBinaryExceptionTrue() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream("line1\nline2\n".getBytes());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		LineEnds.convert(in, out, LineEnds.STYLE_N, true);
		String result = out.toString();
		assertTrue(result.contains("line1"));
		assertTrue(result.contains("line2"));
	}
}
