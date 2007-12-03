/*
 * Regression tests for ArrayHelper
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

/**
 * Regression test for ArrayHelper.  When run, this program
 * should nothing unless an error occurs.
 *
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/ArrayHelper.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.06.00
 */
class ArrayHelperTests {

	/**
	 * Test via command line
	 * @param args command line arguments (ignored)
	 */
	public static void main(String args[]){
		try {
			String[] arr = (String[])ArrayHelper.cat(new String[]{"one","two","three"}, new String[]{"four","five","six"});
			if (!ArrayHelper.equal(arr, new String[]{"one","two","three","four","five","six"})){
				throw new Exception ("Arrays are not equal");
			}
		} catch (Exception x){
			x.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
