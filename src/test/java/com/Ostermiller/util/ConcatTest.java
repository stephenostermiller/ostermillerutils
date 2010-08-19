/*
 * Contact Streams Regression test.
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
 * Regression test for Concatenation Streams.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/Conact.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.04.00
 */
public class ConcatTest extends TestCase {	
	
	public void testConcatReader(){
		ConcatReader cr = new ConcatReader(
			new Reader[]{
				new StringReader("1"),
				new StringReader("two"),
				new StringReader(""),
				new StringReader("4"),
				new StringReader("five"),
				new StringReader("six"),
				new StringReader("seven"),
			}
		);
		assertTrue(ready(cr));
		read(cr, '1');
		read(cr, 't');
		read(cr, 'w');
		read(cr, 'o');
		assertTrue(ready(cr));
		read(cr, '4');
		read(cr, "fiv");
		skip(cr, 2);
		read(cr, "i");
		read(cr, "xseven");
		read(cr, -1);
		read(cr, -1);
		close(cr);
	}
	
	public void testConcatReaderAdd(){
		final ConcatReader cr = new ConcatReader();
		assertFalse(ready(cr));
		cr.addReader(new StringReader("one"));
		read(cr, 'o');
		cr.addReader(new StringReader("two"));
		read(cr, "netwo");
		new Thread(){
			@Override public void run(){
				try {
					Thread.sleep(200);
				} catch (InterruptedException ix){
					throw new RuntimeException(ix);
				}
				cr.addReader(new StringReader("three"));
			}
		}.start();
		read(cr, "three");
		cr.lastReaderAdded();
		read(cr, -1);
	}
		
	public void testConcatInputStream(){
		ConcatInputStream cis = new ConcatInputStream(
			new InputStream[]{
				new ByteArrayInputStream(new byte[]{'1'}),
				new ByteArrayInputStream(new byte[]{'t','w','o'}),
				new ByteArrayInputStream(new byte[]{}),
				new ByteArrayInputStream(new byte[]{'4'}),
				new ByteArrayInputStream(new byte[]{'f','i','v','e'}),
				new ByteArrayInputStream(new byte[]{'s','i','x'}),
				new ByteArrayInputStream(new byte[]{'s','e','v','e','n'}),
			}
		);
		assertTrue(available(cis) != 0);
		read(cis, '1');
		read(cis, 't');
		read(cis, 'w');
		read(cis, 'o');
		read(cis, '4');
		read(cis, "fivesi");
		assertTrue(available(cis) > 0);
		read(cis, "xseven");
		read(cis, -1);
		read(cis, -1);
	}
	
	public void testConcatInputStreamAdd(){
		final ConcatInputStream cis = new ConcatInputStream();
		assertTrue(available(cis) == 0);
		cis.addInputStream(getInputStream("one"));
		read(cis, 'o');
		cis.addInputStream(getInputStream("two"));
		read(cis, "netwo");
		new Thread(){
			@Override public void run(){
				try {
					Thread.sleep(200);
				} catch (InterruptedException ix){
					throw new RuntimeException(ix);
				}
				cis.addInputStream(getInputStream("three"));
			}
		}.start();
		read(cis, "three");
		cis.lastInputStreamAdded();
		read(cis, -1);
	}
	
	private static InputStream getInputStream(String s){
		try {
			return new ByteArrayInputStream(s.getBytes("ASCII"));
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}

	private static void skip(Reader in, int n){
		try {
			int s = 0;
			while (s<n){
				s+=in.skip(n-s);
			}
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}


	private static void read(Reader in, char expected) {
		try {
			int c = in.read();
			assertEquals(expected, c);
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}
	
	private static void read(Reader in, int expected) {
		try {
			int c = in.read();
			assertEquals(expected, c);
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}

	private static void read(InputStream in, char expected) {
		try {
			int c = in.read();
			assertEquals(expected, c);
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}

	private static void read(InputStream in, int expected) {
		try {
			int c = in.read();
			assertEquals(expected, c);
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}
	
	private static void read(Reader in, String expected) {
		try {
			int totalRead = 0;
			while (totalRead < expected.length()){
				char[] buffer = new char[expected.length()-totalRead];
				int read = in.read(buffer);
				if (read == -1) throw new RuntimeException("Read terminated early");
				if (!expected.substring(totalRead, totalRead+read).equals(new String(buffer,0,read))){
					throw new RuntimeException ("Expected to read " + expected.substring(totalRead, totalRead+read) + " but read " + new String(buffer,0,read));
				}
				totalRead+=read;
			}
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}

	private static void read(InputStream in, String expected) {
		try {
			int totalRead = 0;
			while (totalRead < expected.length()){
				byte[] buffer = new byte[expected.length()-totalRead];
				int read = in.read(buffer);
				if (read == -1) throw new RuntimeException("Read terminated early");
				if (!expected.substring(totalRead, totalRead+read).equals(new String(buffer,0,read,"ASCII"))){
					throw new RuntimeException ("Expected to read " + expected.substring(totalRead, totalRead+read) + " but read " + new String(buffer,0,read,"ASCII"));
				}
				totalRead+=read;
			}
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}
	
	public static boolean ready(Reader in){
		try {
			return in.ready();
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}
	
	public static int available(InputStream in){
		try {
			return in.available();
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}
	
	public static void close(Reader in){
		try {
			in.close();
		} catch (Exception x){
			throw new RuntimeException(x);
		}
	}

}