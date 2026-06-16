/*
 * Regression tests for IteratorEnumeration
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

import junit.framework.TestCase;
import java.util.*;

/**
 * Unit test for IteratorEnumeration.
 * More information about this class is available from <a target="_top" href=
 * "https://utils.ostermiller.org/Iterator_Enumeration.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 *         https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.04
 */
public class IteratorEnumerationTest extends TestCase {

	public void testEmptyIterator() {
		List<String> list = new ArrayList<String>();
		Enumeration<String> enumeration = new IteratorEnumeration<String>(list.iterator());
		assertFalse(enumeration.hasMoreElements());
	}

	public void testSingleElement() {
		List<String> list = new ArrayList<String>();
		list.add("one");
		Enumeration<String> enumeration = new IteratorEnumeration<String>(list.iterator());

		assertTrue(enumeration.hasMoreElements());
		assertEquals("one", enumeration.nextElement());
		assertFalse(enumeration.hasMoreElements());
	}

	public void testMultipleElements() {
		List<String> list = new ArrayList<String>();
		list.add("one");
		list.add("two");
		list.add("three");
		Enumeration<String> enumeration = new IteratorEnumeration<String>(list.iterator());

		assertTrue(enumeration.hasMoreElements());
		assertEquals("one", enumeration.nextElement());
		assertTrue(enumeration.hasMoreElements());
		assertEquals("two", enumeration.nextElement());
		assertTrue(enumeration.hasMoreElements());
		assertEquals("three", enumeration.nextElement());
		assertFalse(enumeration.hasMoreElements());
	}

	public void testNextElementThrowsNoSuchElementException() {
		List<String> list = new ArrayList<String>();
		Enumeration<String> enumeration = new IteratorEnumeration<String>(list.iterator());

		try {
			enumeration.nextElement();
			fail("Expected NoSuchElementException");
		} catch (NoSuchElementException e) {
			// Expected
		}
	}

	public void testIterationOrder() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		Enumeration<Integer> enumeration = new IteratorEnumeration<Integer>(list.iterator());

		int expected = 1;
		while (enumeration.hasMoreElements()) {
			assertEquals((Integer) expected, enumeration.nextElement());
			expected++;
		}
		assertEquals(6, expected);
	}

	public void testWithNullElements() {
		List<String> list = new ArrayList<String>();
		list.add(null);
		list.add("value");
		list.add(null);
		Enumeration<String> enumeration = new IteratorEnumeration<String>(list.iterator());

		assertTrue(enumeration.hasMoreElements());
		assertNull(enumeration.nextElement());
		assertTrue(enumeration.hasMoreElements());
		assertEquals("value", enumeration.nextElement());
		assertTrue(enumeration.hasMoreElements());
		assertNull(enumeration.nextElement());
		assertFalse(enumeration.hasMoreElements());
	}

	public void testWithSetIterator() {
		Set<String> set = new HashSet<String>();
		set.add("a");
		set.add("b");
		set.add("c");
		Enumeration<String> enumeration = new IteratorEnumeration<String>(set.iterator());

		int count = 0;
		while (enumeration.hasMoreElements()) {
			assertNotNull(enumeration.nextElement());
			count++;
		}
		assertEquals(3, count);
	}

	public void testConsecutiveHasMoreElementsCalls() {
		List<String> list = new ArrayList<String>();
		list.add("one");
		Enumeration<String> enumeration = new IteratorEnumeration<String>(list.iterator());

		// Calling hasMoreElements multiple times without nextElement should be consistent
		assertTrue(enumeration.hasMoreElements());
		assertTrue(enumeration.hasMoreElements());
		assertTrue(enumeration.hasMoreElements());

		assertEquals("one", enumeration.nextElement());

		assertFalse(enumeration.hasMoreElements());
		assertFalse(enumeration.hasMoreElements());
	}

	public void testLargeIterator() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 1000; i++) {
			list.add(i);
		}
		Enumeration<Integer> enumeration = new IteratorEnumeration<Integer>(list.iterator());

		int count = 0;
		while (enumeration.hasMoreElements()) {
			enumeration.nextElement();
			count++;
		}
		assertEquals(1000, count);
	}
}
