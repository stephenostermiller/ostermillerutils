/*
 * Significant Figures regression test.
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

import java.util.*;
import java.io.*;

class SignificantFiguresTests {

	private static class TestCase {
		private String original;
		private int sigfigs;
		private String desiredResult;

		public TestCase(String original, int sigfigs, String desiredResult){
			this.original = original;
			this.sigfigs = sigfigs;
			this.desiredResult = desiredResult;
		}
		private void test() throws Exception {
			String result = SignificantFigures.format(original, sigfigs);
			if (!desiredResult.equals(result)) throw new Exception("Got " + result + " but expected " + desiredResult + " for " + original + " with " + sigfigs + " significant digits.");
		}
	}
	private static final TestCase[] testCases = new TestCase[]{
		new TestCase("1234", 3, "1230"),
		new TestCase("200", 2, "2.0E2"),
		new TestCase("6.15", 2, "6.2"),
		new TestCase("6.25", 2, "6.2"),
		new TestCase("60.91", 3, "60.9"),
		new TestCase("5097.808073851760832954355668151943215272", 3, "5.10E3"),
		new TestCase("3343", 1, "3000"),
		new TestCase("200.123", 3, "200."),
		new TestCase("234.123", 3, "234"),
		new TestCase("199.87", 3, "200."),
		new TestCase("6.153", 2, "6.2"),
		new TestCase("6.253", 2, "6.3"),
		new TestCase(".0033234324", 2, "0.0033"),
		new TestCase("0.0033234324", 2, "0.0033"),
		new TestCase(".00033234324", 2, "3.3E-4"),
	};

	public static void main(String[] args){
		try {
			for (int i=0; i<testCases.length; i++){
				testCases[i].test();
			}
		} catch (Exception x){
			x.printStackTrace(System.err);
			System.exit(1);
		}
		System.exit(0);
	}
}
