/*
 * Unit tests for NoCloseOutputStream
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
 * Unit test for NoCloseOutputStream.
 *
 * @author Stephen Ostermiller https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.03
 */
public class NoCloseOutputStreamTest extends TestCase {

	/**
	 * Mock output stream that tracks whether it has been closed
	 */
	static class MockOutputStream extends OutputStream {
		private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		private boolean closed = false;

		@Override
		public void write(int b) throws IOException {
			if (closed) {
				throw new IOException("Stream closed");
			}
			buffer.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			if (closed) {
				throw new IOException("Stream closed");
			}
			buffer.write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			if (closed) {
				throw new IOException("Stream closed");
			}
			buffer.flush();
		}

		@Override
		public void close() throws IOException {
			closed = true;
			buffer.close();
		}

		boolean isClosed() {
			return closed;
		}

		byte[] toByteArray() {
			return buffer.toByteArray();
		}
	}

	public void testCloseHasNoEffect() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		ncos.write(1);
		ncos.write(2);
		ncos.close();
		ncos.write(3);

		byte[] result = mos.toByteArray();
		assertEquals(3, result.length);
		assertEquals(1, result[0]);
		assertEquals(2, result[1]);
		assertEquals(3, result[2]);
		assertFalse(mos.isClosed());
	}

	public void testReallyCloseActuallyCloses() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		ncos.write(1);
		ncos.write(2);

		assertFalse(mos.isClosed());
		ncos.reallyClose();
		assertTrue(mos.isClosed());

		try {
			ncos.write(3);
			fail("Expected IOException after reallyClose");
		} catch (IOException e) {
			// Expected
		}
	}

	public void testWriteSingleByte() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		ncos.write(42);

		byte[] result = mos.toByteArray();
		assertEquals(1, result.length);
		assertEquals(42, result[0]);
	}

	public void testWriteByteArray() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		byte[] data = new byte[] { 1, 2, 3, 4, 5 };
		ncos.write(data);

		byte[] result = mos.toByteArray();
		assertEquals(5, result.length);
		for (int i = 0; i < 5; i++) {
			assertEquals(data[i], result[i]);
		}
	}

	public void testWriteByteArrayWithOffset() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		byte[] data = new byte[] { 1, 2, 3, 4, 5 };
		ncos.write(data, 1, 3);

		byte[] result = mos.toByteArray();
		assertEquals(3, result.length);
		assertEquals(2, result[0]);
		assertEquals(3, result[1]);
		assertEquals(4, result[2]);
	}

	public void testFlush() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		ncos.write(1);
		ncos.write(2);
		ncos.flush();

		byte[] result = mos.toByteArray();
		assertEquals(2, result.length);
		assertEquals(1, result[0]);
		assertEquals(2, result[1]);
	}

	public void testMultipleWrites() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		for (int i = 0; i < 256; i++) {
			ncos.write(i % 256);
		}

		byte[] result = mos.toByteArray();
		assertEquals(256, result.length);
	}

	public void testMultipleCloseCallsHaveNoEffect() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		ncos.write(1);
		ncos.close();
		ncos.close();
		ncos.close();
		ncos.write(2);

		byte[] result = mos.toByteArray();
		assertEquals(2, result.length);
		assertFalse(mos.isClosed());
	}

	public void testCloseBeforeReallyClose() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		ncos.write(1);
		ncos.close();
		assertFalse(mos.isClosed());
		ncos.write(2);

		byte[] result = mos.toByteArray();
		assertEquals(2, result.length);

		ncos.reallyClose();
		assertTrue(mos.isClosed());

		try {
			ncos.write(3);
			fail("Expected IOException after reallyClose");
		} catch (IOException e) {
			// Expected
		}
	}

	public void testEmptyWrite() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		byte[] data = new byte[] { 1, 2, 3 };
		ncos.write(data, 0, 0);

		byte[] result = mos.toByteArray();
		assertEquals(0, result.length);
	}

	public void testFlushAfterClose() throws IOException {
		MockOutputStream mos = new MockOutputStream();
		NoCloseOutputStream ncos = new NoCloseOutputStream(mos);

		ncos.write(1);
		ncos.close();
		ncos.flush();

		byte[] result = mos.toByteArray();
		assertEquals(1, result.length);
		assertFalse(mos.isClosed());
	}
}
