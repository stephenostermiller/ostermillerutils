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
class StraightStreamReaderTests {

	/**
	 * Test via command line
	 * @param args command line arguments (ignored)
	 */
	public static void main(String args[]){
		try {
			StraightStreamReader in;
			char[] cbuf = new char[0x1000];
			int read;
			int totRead;

			// write a buffer with all possible values of bytes
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			for (int i=0x00; i<0x100; i++){
				buffer.write(i);
			}
			buffer.close();

			// read it back using the read single character method
			in = new StraightStreamReader(new ByteArrayInputStream(buffer.toByteArray()));
			for (int i=0x00; i<0x100; i++){
				read = in.read();
				if (read != i){
					throw new Exception("Error: " + i + " read as " + read);
				}
			}
			in.close();

			// read as much of it back as possible with one simple buffer read.
			in = new StraightStreamReader(new ByteArrayInputStream(buffer.toByteArray()));
			totRead = in.read(cbuf);
			if (totRead != 0x100){
				throw new Exception("Simple buffered read did not read the full amount: 0x" + Integer.toHexString(totRead));
			}
			for (int i=0x00; i<totRead; i++){
				 if (cbuf[i] != i){
					 throw new Exception("Error: 0x" + i + " read as 0x" + cbuf[i]);
				}
			}
			in.close();

			// read it back using buffer read method.
			in = new StraightStreamReader(new ByteArrayInputStream(buffer.toByteArray()));
			totRead = 0;
			while (totRead <= 0x100 && (read = in.read(cbuf, totRead, 0x100 - totRead)) > 0){
				totRead += read;
			}
			if (totRead != 0x100){
				throw new Exception("Not enough read. Bytes read: " + Integer.toHexString(totRead));
			}
			for (int i=0x00; i<totRead; i++){
				 if (cbuf[i] != i){
					 throw new Exception("Error: 0x" + i + " read as 0x" + cbuf[i]);
				}
			}
			in.close();

			// read it back using an offset buffer read method.
			in = new StraightStreamReader(new ByteArrayInputStream(buffer.toByteArray()));
			totRead = 0;
			while (totRead <= 0x100 && (read = in.read(cbuf, totRead+0x123, 0x100 - totRead)) > 0){
				totRead += read;
			}
			if (totRead != 0x100){
				throw new Exception("Not enough read. Bytes read: " + Integer.toHexString(totRead));
			}
			for (int i=0x00; i<totRead; i++){
				 if (cbuf[i+0x123] != i){
					 throw new Exception("Error: 0x" + i + " read as 0x" + cbuf[i+0x123]);
				}
			}
			in.close();

			// read it back using a partial offset buffer read method.
			in = new StraightStreamReader(new ByteArrayInputStream(buffer.toByteArray()));
			totRead = 0;
			while (totRead <= 0x100 && (read = in.read(cbuf, totRead+0x123, 7)) > 0){
				totRead += read;
			}
			if (totRead != 0x100){
				throw new Exception("Not enough read. Bytes read: " + Integer.toHexString(totRead));
			}
			for (int i=0x00; i<totRead; i++){
				 if (cbuf[i+0x123] != i){
					 throw new Exception("Error: 0x" + i + " read as 0x" + cbuf[i+0x123]);
				}
			}
			in.close();

		} catch (Exception x){
			System.err.println(x.getMessage());
			System.exit(1);
		}
		System.exit(0);
	}
}
