/*
 * Regression tests for ExecHelper
 * Copyright (C) 2005 Stephen Ostermiller
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
 * Regression test for ExecHelper.  When run, this program
 * should nothing unless an error occurs.
 *
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/ExecHelper.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.06.00
 */
public class ExecHelperTest extends TestCase {

	public void testReadFileWithCat(){
		try {
			File temp = File.createTempFile("ExecHelperTests", "tmp");
			temp.deleteOnExit();
			String s = createLargeString();
			Writer out = new FileWriter(temp);
			out.write(s);
			out.close();
			ExecHelper eh = ExecHelper.exec(new String[]{"cat", temp.toString()});
			assertEquals(s, eh.getOutput());			
		} catch (IOException x){
			throw new RuntimeException(x);
		}
	}
	
	public void testStdinStdoutExitStatus(){
		if (shExists()){
			try {
				ExecHelper eh = ExecHelper.execUsingShell("echo -n stdin && echo -n stderr 1>&2; exit 11");
				assertEquals("stdin", eh.getOutput());
				assertEquals("stderr", eh.getError());
				assertEquals(11, eh.getStatus());
			} catch (IOException x){
				throw new RuntimeException(x);
			}
		}
	}
	
	/**
	 * Test to see if a shell exists
	 */
	private static boolean shExists(){
		File sh = new File("/bin/sh");
		return sh.exists();		
	}
	
	private static final int CREATE_LARGE_STRING_SIZE=1000;
	private static final String LARGE_STRING_CONTENTS="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static String createLargeString(){
		StringBuilder sb = new StringBuilder(CREATE_LARGE_STRING_SIZE*LARGE_STRING_CONTENTS.length());
		for (int i=0; i<CREATE_LARGE_STRING_SIZE; i++){
			sb.append(LARGE_STRING_CONTENTS);
		}
		return sb.toString();

	}
}
