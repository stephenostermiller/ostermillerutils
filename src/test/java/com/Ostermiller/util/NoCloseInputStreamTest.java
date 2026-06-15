/*
 * Unit tests for NoCloseInputStream
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
 * Unit test for NoCloseInputStream.
 *
 * @author Stephen Ostermiller https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.02
 */
public class NoCloseInputStreamTest extends TestCase {

	/**
	 * Mock input stream that tracks whether it has been closed
	 */
	static class MockInputStream extends InputStream {
		private byte[] data;
		private int position = 0;
		private boolean closed = false;

		MockInputStream(byte[] data) {
			this.data = data;
		}

		@Override
		public int read() throws IOException {
			if (closed) {
				throw new IOException("Stream closed");
			}
			if (position >= data.length) {
				return -1;
			}
			return data[position++] & 0xFF;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			if (closed) {
				throw new IOException("Stream closed");
			}
			if (position >= data.length) {
				return -1;
			}
			int remaining = data.length - position;
			int toRead = Math.min(len, remaining);
			System.arraycopy(data, position, b, off, toRead);
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
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] { 1, 2, 3 });
		NoCloseInputStream ncis = new NoCloseInputStream(bais);

		// Read one byte
		int firstByte = ncis.read();
		assertEquals(1, firstByte);

		// Call close - should have no effect
		ncis.close();

		// Should still be able to read
		int secondByte = ncis.read();
		assertEquals(2, secondByte);
	}

	public void testReallyCloseActuallyCloses() throws IOException {
		MockInputStream mis = new MockInputStream(new byte[] { 1, 2, 3 });
		NoCloseInputStream ncis = new NoCloseInputStream(mis);

		// Read one byte
		int firstByte = ncis.read();
		assertEquals(1, firstByte);

		// reallyClose should close the underlying stream
		assertFalse(mis.isClosed());
		ncis.reallyClose();
		assertTrue(mis.isClosed());
	}

	public void testReadSingleByte() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] { 42 });
		NoCloseInputStream ncis = new NoCloseInputStream(bais);

		assertEquals(42, ncis.read());
		assertEquals(-1, ncis.read());
	}

	public void testReadByteArray() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] { 1, 2, 3, 4, 5 });
		NoCloseInputStream ncis = new NoCloseInputStream(bais);

		byte[] buffer = new byte[3];
		int bytesRead = ncis.read(buffer);
		assertEquals(3, bytesRead);
		assertEquals(1, buffer[0]);
		assertEquals(2, buffer[1]);
		assertEquals(3, buffer[2]);
	}

	public void testReadByteArrayWithOffset() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] { 1, 2, 3, 4, 5 });
		NoCloseInputStream ncis = new NoCloseInputStream(bais);

		byte[] buffer = new byte[5];
		int bytesRead = ncis.read(buffer, 1, 3);
		assertEquals(3, bytesRead);
		assertEquals(0, buffer[0]);
		assertEquals(1, buffer[1]);
		assertEquals(2, buffer[2]);
		assertEquals(3, buffer[3]);
		assertEquals(0, buffer[4]);
	}

	public void testSkip() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] { 1, 2, 3, 4, 5 });
		NoCloseInputStream ncis = new NoCloseInputStream(bais);

		long skipped = ncis.skip(2);
		assertEquals(2, skipped);
		assertEquals(3, ncis.read());
	}

	public void testAvailable() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] { 1, 2, 3 });
		NoCloseInputStream ncis = new NoCloseInputStream(bais);

		int available = ncis.available();
		assertEquals(3, available);

		ncis.read();
		available = ncis.available();
		assertEquals(2, available);
	}

	public void testMark() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] { 1, 2, 3, 4, 5 });
		NoCloseInputStream ncis = new NoCloseInputStream(bais);

		ncis.read(); // read 1
		ncis.mark(10);
		ncis.read(); // read 2
		ncis.read(); // read 3
		ncis.reset();
		assertEquals(2, ncis.read()); // should read 2 again
	}

	public void testMarkSupported() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] { 1, 2, 3 });
		NoCloseInputStream ncis = new NoCloseInputStream(bais);

		assertTrue(ncis.markSupported());
	}

	public void testEmptyStream() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] {});
		NoCloseInputStream ncis = new NoCloseInputStream(bais);

		assertEquals(-1, ncis.read());
	}

	public void testMultipleCloseCallsHaveNoEffect() throws IOException {
		MockInputStream mis = new MockInputStream(new byte[] { 1, 2, 3 });
		NoCloseInputStream ncis = new NoCloseInputStream(mis);

		ncis.read();
		ncis.close();
		ncis.close();
		ncis.close();

		// Should still be able to read, underlying stream should not be closed
		assertEquals(2, ncis.read());
		assertFalse(mis.isClosed());
	}

	public void testCloseBeforeReallyClose() throws IOException {
		MockInputStream mis = new MockInputStream(new byte[] { 1, 2, 3 });
		NoCloseInputStream ncis = new NoCloseInputStream(mis);

		ncis.read();
		ncis.close(); // Should have no effect
		assertFalse(mis.isClosed()); // Underlying stream should still be open
		assertEquals(2, ncis.read()); // Should still be able to read

		ncis.reallyClose(); // Now actually close
		assertTrue(mis.isClosed()); // Now underlying stream is closed
	}
}
