/*
 * Copyright (C) 2005-2007 Stephen Ostermiller
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
 * Regression test for StraightStreamReader.  When run, this program
 * should nothing unless an error occurs.
 *
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/StraightStreamReader.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.06.02
 */
public class StraightStreamReaderTest extends TestCase {

	private byte[] getallBytesArray(){
		byte[] bytes = new byte[0x100];
		for (int i=0x00; i<0x100; i++){
			bytes[i] = (byte)i;
		}
		return bytes;
	}

	public void testReadAllBytesAsChars(){
		try {
			// read it back using the read single character method
			StraightStreamReader in = new StraightStreamReader(new ByteArrayInputStream(getallBytesArray()));
			for (int i=0x00; i<0x100; i++){
				int read = in.read();
				assertEquals(i, read);
			}
			in.close();
		} catch (IOException x){
			throw new RuntimeException(x);
		}
	}

	public void testReadAllBytesSingleCharBufferRead(){
		try {
			char[] cbuf = new char[0x1000];
			// read as much of it back as possible with one simple buffer read.
			StraightStreamReader in = new StraightStreamReader(new ByteArrayInputStream(getallBytesArray()));
			int totRead = in.read(cbuf);
			assertEquals(0x100, totRead);
			for (int i=0x00; i<totRead; i++){
				assertEquals(i, cbuf[i]);
			}
			in.close();
		} catch (IOException x){
			throw new RuntimeException(x);
		}
	}

	public void testReadAllBytesMultipleCharBufferReads(){
		try {
			char[] cbuf = new char[0x1000];
			// read it back using buffer read method.
			StraightStreamReader in = new StraightStreamReader(new ByteArrayInputStream(getallBytesArray()));
			int totRead = 0;
			int read;
			while (totRead <= 0x100 && (read = in.read(cbuf, totRead, 0x100 - totRead)) > 0){
				totRead += read;
			}
			assertEquals(0x100, totRead);
			for (int i=0x00; i<totRead; i++){
				assertEquals(i, cbuf[i]);
			}
			in.close();
		} catch (IOException x){
			throw new RuntimeException(x);
		}
	}

	public void testReadAllBytesOffsetBuffer(){
		try {
			char[] cbuf = new char[0x1000];
			// read it back using an offset buffer read method.
			StraightStreamReader in = new StraightStreamReader(new ByteArrayInputStream(getallBytesArray()));
			int totRead = 0;
			int read;
			while (totRead <= 0x100 && (read = in.read(cbuf, totRead+0x123, 0x100 - totRead)) > 0){
				totRead += read;
			}
			assertEquals(0x100, totRead);
			for (int i=0x00; i<totRead; i++){
				assertEquals(i,cbuf[i+0x123]);
			}
			in.close();
		} catch (IOException x){
			throw new RuntimeException(x);
		}
	}

	public void testReadPartialBytesOffsetBuffer(){
		try {
			char[] cbuf = new char[0x1000];
			// read it back using a partial offset buffer read method.
			StraightStreamReader in = new StraightStreamReader(new ByteArrayInputStream(getallBytesArray()));
			int totRead = 0;
			int read;
			while (totRead <= 0x100 && (read = in.read(cbuf, totRead+0x123, 7)) > 0){
				totRead += read;
			}
			assertEquals(0x100, totRead);
			for (int i=0x00; i<totRead; i++){
				assertEquals(i,cbuf[i+0x123]);
			}
			in.close();
		} catch (IOException x){
			throw new RuntimeException(x);
		}
	}
}
