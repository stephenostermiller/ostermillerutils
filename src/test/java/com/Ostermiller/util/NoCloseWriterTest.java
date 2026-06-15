/*
 * Unit tests for NoCloseWriter
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

import java.io.*;

import junit.framework.TestCase;

/**
 * Unit test for NoCloseWriter.
 *
 * @author Stephen Ostermiller https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.02
 */
public class NoCloseWriterTest extends TestCase {

	/**
	 * Mock writer that tracks whether it has been closed
	 */
	static class MockWriter extends Writer {
		private StringBuilder buffer = new StringBuilder();
		private boolean closed = false;

		@Override
		public void write(int c) throws IOException {
			if (closed) {
				throw new IOException("Stream closed");
			}
			buffer.append((char) c);
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			if (closed) {
				throw new IOException("Stream closed");
			}
			buffer.append(cbuf, off, len);
		}

		@Override
		public void flush() throws IOException {
			if (closed) {
				throw new IOException("Stream closed");
			}
		}

		@Override
		public void close() throws IOException {
			closed = true;
		}

		boolean isClosed() {
			return closed;
		}

		String getContent() {
			return buffer.toString();
		}
	}

	public void testCloseHasNoEffect() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		ncw.write('H');
		ncw.write('e');
		ncw.close();
		ncw.write('y');

		String result = mw.getContent();
		assertEquals("Hey", result);
		assertFalse(mw.isClosed());
	}

	public void testReallyCloseActuallyCloses() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		ncw.write('H');
		ncw.write('i');

		assertFalse(mw.isClosed());
		ncw.reallyClose();
		assertTrue(mw.isClosed());

		try {
			ncw.write('X');
			fail("Expected IOException after reallyClose");
		} catch (IOException e) {
			// Expected
		}
	}

	public void testWriteSingleCharacter() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		ncw.write('A');
		ncw.write('B');
		ncw.write('C');

		String result = mw.getContent();
		assertEquals("ABC", result);
	}

	public void testWriteCharArray() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		char[] data = new char[] { 'H', 'e', 'l', 'l', 'o' };
		ncw.write(data);

		String result = mw.getContent();
		assertEquals("Hello", result);
	}

	public void testWriteCharArrayWithOffset() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		char[] data = new char[] { 'H', 'e', 'l', 'l', 'o' };
		ncw.write(data, 1, 3);

		String result = mw.getContent();
		assertEquals("ell", result);
	}

	public void testWriteString() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		ncw.write("Hello");
		ncw.write(" ");
		ncw.write("World");

		String result = mw.getContent();
		assertEquals("Hello World", result);
	}

	public void testWriteStringWithOffset() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		ncw.write("Hello World", 0, 5);

		String result = mw.getContent();
		assertEquals("Hello", result);
	}

	public void testFlush() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		ncw.write('A');
		ncw.write('B');
		ncw.flush();

		String result = mw.getContent();
		assertEquals("AB", result);
	}

	public void testMultipleWrites() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		for (int i = 0; i < 10; i++) {
			ncw.write("Line " + i + "\n");
		}

		String result = mw.getContent();
		assertTrue(result.contains("Line 0"));
		assertTrue(result.contains("Line 9"));
	}

	public void testMultipleCloseCallsHaveNoEffect() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		ncw.write('A');
		ncw.close();
		ncw.close();
		ncw.close();
		ncw.write('B');

		String result = mw.getContent();
		assertEquals("AB", result);
		assertFalse(mw.isClosed());
	}

	public void testCloseBeforeReallyClose() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		ncw.write('A');
		ncw.close();
		assertFalse(mw.isClosed());
		ncw.write('B');

		String result = mw.getContent();
		assertEquals("AB", result);

		ncw.reallyClose();
		assertTrue(mw.isClosed());

		try {
			ncw.write('C');
			fail("Expected IOException after reallyClose");
		} catch (IOException e) {
			// Expected
		}
	}

	public void testEmptyWrite() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		char[] data = new char[] { 'A', 'B', 'C' };
		ncw.write(data, 0, 0);

		String result = mw.getContent();
		assertEquals("", result);
	}

	public void testFlushAfterClose() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		ncw.write('A');
		ncw.close();
		ncw.flush();

		String result = mw.getContent();
		assertEquals("A", result);
		assertFalse(mw.isClosed());
	}

	public void testUnicodeCharacters() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		ncw.write("Café");
		ncw.write(" ");
		ncw.write("Naïve");

		String result = mw.getContent();
		assertEquals("Café Naïve", result);
	}

	public void testWriteStringSubstring() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		String text = "The quick brown fox";
		ncw.write(text, 4, 5);

		String result = mw.getContent();
		assertEquals("quick", result);
	}

	public void testLargeWrite() throws IOException {
		MockWriter mw = new MockWriter();
		NoCloseWriter ncw = new NoCloseWriter(mw);

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			sb.append("X");
		}

		ncw.write(sb.toString());

		String result = mw.getContent();
		assertEquals(1000, result.length());
	}
}
