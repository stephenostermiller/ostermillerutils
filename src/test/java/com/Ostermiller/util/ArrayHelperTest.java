/*
 * Regression tests for ArrayHelper Copyright (C) 2002-2011 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Java+Utilities This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * See LICENSE.txt for details.
 */
package com.Ostermiller.util;

import junit.framework.TestCase;

/**
 * Unit test for ArrayHelper. More information about this class is available
 * from <a target="_top" href=
 * "http://ostermiller.org/utils/ArrayHelper.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 *         http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.06.00
 */
public class ArrayHelperTest extends TestCase {

	public void testEqualEmptyStringArrays() {
		assertTrue(ArrayHelper.equal(new String[0], new String[0]));
	}

	public void testEqualNulls() {
		assertTrue(ArrayHelper.equal(null, null));
	}

	public void testEqualSingleString() {
		assertTrue(ArrayHelper.equal(new String[] { "one" }, new String[] { "one" }));
	}

	public void testEqualSingleStringNotReferenceSame() {
		assertTrue(ArrayHelper.equal(new String[] { "one" }, new String[] { new String("one") }));
	}

	public void testEqualDifferentString() {
		assertFalse(ArrayHelper.equal(new String[] { "one" }, new String[] { "two" }));
	}

	public void testEqualDifferentTypes() {
		assertTrue(ArrayHelper.equal(new String[0], new Integer[0]));
	}

	public void testEqualFirstNull() {
		assertFalse(ArrayHelper.equal(null, new Integer[0]));
	}

	public void testEqualSecondNull() {
		assertFalse(ArrayHelper.equal(new Integer[0], null));
	}

	public void testEqualDifferentLengths() {
		assertFalse(ArrayHelper.equal(new String[] { "one" }, new String[] { "one", "two" }));
	}

	public void testEqualDifferentLengthsButAllNull() {
		assertFalse(ArrayHelper.equal(new String[1], new String[5]));
	}

	public void testEqualFirstContainsNull() {
		assertFalse(ArrayHelper.equal(new String[] { null }, new String[] { "one" }));
	}

	public void testEqualSecondContainsNull() {
		assertFalse(ArrayHelper.equal(new String[] { "one" }, new String[] { null }));
	}

	public void testEqualBothContainNull() {
		assertTrue(ArrayHelper.equal(new String[] { null }, new String[] { null }));
	}

	public void testCat() {
		String[] first = new String[] { "one", "two", "three" };
		String[] second = new String[] { "four", "five", "six" };
		String[] cat = (String[]) ArrayHelper.cat(first, second);
		String[] result = new String[] { "one", "two", "three", "four", "five", "six" };
		assertTrue(ArrayHelper.equal(cat, result));
	}
}
