/*
 * Unit tests for NoCloseReader
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
 * Unit test for NoCloseReader.
 *
 * @author Stephen Ostermiller https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.03
 */
public class NoCloseReaderTest extends TestCase {

	/**
	 * Mock reader that tracks whether it has been closed
	 */
	static class MockReader extends Reader {
		private String data;
		private int position = 0;
		private boolean closed = false;

		MockReader(String data) {
			this.data = data;
		}

		@Override
		public int read() throws IOException {
			if (closed) {
				throw new IOException("Stream closed");
			}
			if (position >= data.length()) {
				return -1;
			}
			return data.charAt(position++);
		}

		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			if (closed) {
				throw new IOException("Stream closed");
			}
			if (position >= data.length()) {
				return -1;
			}
			int remaining = data.length() - position;
			int toRead = Math.min(len, remaining);
			data.getChars(position, position + toRead, cbuf, off);
			position += toRead;
			return toRead;
		}

		@Override
		public void close() throws IOException {
			closed = true;
		}

		boolean isClosed() {
			return closed;
		}
	}

	public void testCloseHasNoEffect() throws IOException {
		MockReader mr = new MockReader("Hello World");
		NoCloseReader ncr = new NoCloseReader(mr);

		char firstChar = (char) ncr.read();
		assertEquals('H', firstChar);

		ncr.close();

		char secondChar = (char) ncr.read();
		assertEquals('e', secondChar);
		assertFalse(mr.isClosed());
	}

	public void testReallyCloseActuallyCloses() throws IOException {
		MockReader mr = new MockReader("Hello World");
		NoCloseReader ncr = new NoCloseReader(mr);

		int firstChar = ncr.read();
		assertEquals('H', firstChar);

		assertFalse(mr.isClosed());
		ncr.reallyClose();
		assertTrue(mr.isClosed());

		try {
			ncr.read();
			fail("Expected IOException after reallyClose");
		} catch (IOException e) {
			// Expected
		}
	}

	public void testReadSingleCharacter() throws IOException {
		MockReader mr = new MockReader("Test");
		NoCloseReader ncr = new NoCloseReader(mr);

		assertEquals('T', ncr.read());
		assertEquals('e', ncr.read());
		assertEquals('s', ncr.read());
		assertEquals('t', ncr.read());
		assertEquals(-1, ncr.read());
	}

	public void testReadCharArray() throws IOException {
		MockReader mr = new MockReader("Hello World");
		NoCloseReader ncr = new NoCloseReader(mr);

		char[] buffer = new char[5];
		int charsRead = ncr.read(buffer);
		assertEquals(5, charsRead);
		assertEquals('H', buffer[0]);
		assertEquals('e', buffer[1]);
		assertEquals('l', buffer[2]);
		assertEquals('l', buffer[3]);
		assertEquals('o', buffer[4]);
	}

	public void testReadCharArrayWithOffset() throws IOException {
		MockReader mr = new MockReader("Hello World");
		NoCloseReader ncr = new NoCloseReader(mr);

		char[] buffer = new char[10];
		int charsRead = ncr.read(buffer, 2, 5);
		assertEquals(5, charsRead);
		assertEquals('H', buffer[2]);
		assertEquals('e', buffer[3]);
		assertEquals('l', buffer[4]);
		assertEquals('l', buffer[5]);
		assertEquals('o', buffer[6]);
	}

	public void testSkip() throws IOException {
		MockReader mr = new MockReader("Hello World");
		NoCloseReader ncr = new NoCloseReader(mr);

		long skipped = ncr.skip(6);
		assertEquals(6, skipped);
		assertEquals('W', ncr.read());
	}

	public void testReady() throws IOException {
		StringReader sr = new StringReader("Test");
		NoCloseReader ncr = new NoCloseReader(sr);

		assertTrue(ncr.ready());
	}

	public void testMark() throws IOException {
		StringReader sr = new StringReader("Hello World");
		NoCloseReader ncr = new NoCloseReader(sr);

		ncr.read();
		ncr.mark(10);
		ncr.read();
		ncr.read();
		ncr.reset();
		assertEquals('e', ncr.read());
	}

	public void testMarkSupported() throws IOException {
		StringReader sr = new StringReader("Test");
		NoCloseReader ncr = new NoCloseReader(sr);

		assertTrue(ncr.markSupported());
	}

	public void testEmptyReader() throws IOException {
		MockReader mr = new MockReader("");
		NoCloseReader ncr = new NoCloseReader(mr);

		assertEquals(-1, ncr.read());
	}

	public void testMultipleCloseCallsHaveNoEffect() throws IOException {
		MockReader mr = new MockReader("Hello");
		NoCloseReader ncr = new NoCloseReader(mr);

		ncr.read();
		ncr.close();
		ncr.close();
		ncr.close();

		assertEquals('e', ncr.read());
		assertFalse(mr.isClosed());
	}

	public void testCloseBeforeReallyClose() throws IOException {
		MockReader mr = new MockReader("Hello");
		NoCloseReader ncr = new NoCloseReader(mr);

		ncr.read();
		ncr.close();
		assertFalse(mr.isClosed());
		assertEquals('e', ncr.read());

		ncr.reallyClose();
		assertTrue(mr.isClosed());

		try {
			ncr.read();
			fail("Expected IOException after reallyClose");
		} catch (IOException e) {
			// Expected
		}
	}

	public void testUnicodeCharacters() throws IOException {
		MockReader mr = new MockReader("Café");
		NoCloseReader ncr = new NoCloseReader(mr);

		assertEquals('C', ncr.read());
		assertEquals('a', ncr.read());
		assertEquals('f', ncr.read());
		assertEquals('é', ncr.read());
		assertEquals(-1, ncr.read());
	}

	public void testLargeRead() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			sb.append("a");
		}

		MockReader mr = new MockReader(sb.toString());
		NoCloseReader ncr = new NoCloseReader(mr);

		char[] buffer = new char[1000];
		int charsRead = ncr.read(buffer);
		assertEquals(1000, charsRead);
	}
}
