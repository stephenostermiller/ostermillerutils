/*
 * Regression tests for EnumerationIterator
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
 * Unit test for EnumerationIterator.
 * More information about this class is available from <a target="_top" href=
 * "https://ostermiller.org/utils/Iterator_Enumeration.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 *         https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.04
 */
public class EnumerationIteratorTest extends TestCase {

	public void testEmptyEnumeration() {
		Enumeration<String> enumeration = new IteratorEnumeration<String>(new ArrayList<String>().iterator());
		Iterator<String> iterator = new EnumerationIterator<String>(enumeration);
		assertFalse(iterator.hasNext());
	}

	public void testSingleElement() {
		Vector<String> vector = new Vector<String>();
		vector.add("one");
		Iterator<String> iterator = new EnumerationIterator<String>(vector.elements());

		assertTrue(iterator.hasNext());
		assertEquals("one", iterator.next());
		assertFalse(iterator.hasNext());
	}

	public void testMultipleElements() {
		Vector<String> vector = new Vector<String>();
		vector.add("one");
		vector.add("two");
		vector.add("three");
		Iterator<String> iterator = new EnumerationIterator<String>(vector.elements());

		assertTrue(iterator.hasNext());
		assertEquals("one", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("two", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("three", iterator.next());
		assertFalse(iterator.hasNext());
	}

	public void testNextThrowsNoSuchElementException() {
		Enumeration<String> enumeration = new IteratorEnumeration<String>(new ArrayList<String>().iterator());
		Iterator<String> iterator = new EnumerationIterator<String>(enumeration);

		try {
			iterator.next();
			fail("Expected NoSuchElementException");
		} catch (NoSuchElementException e) {
			// Expected
		}
	}

	public void testIterationOrder() {
		Vector<Integer> vector = new Vector<Integer>();
		vector.add(1);
		vector.add(2);
		vector.add(3);
		vector.add(4);
		vector.add(5);
		Iterator<Integer> iterator = new EnumerationIterator<Integer>(vector.elements());

		int expected = 1;
		while (iterator.hasNext()) {
			assertEquals((Integer) expected, iterator.next());
			expected++;
		}
		assertEquals(6, expected);
	}

	public void testWithNullElements() {
		Vector<String> vector = new Vector<String>();
		vector.add(null);
		vector.add("value");
		vector.add(null);
		Iterator<String> iterator = new EnumerationIterator<String>(vector.elements());

		assertTrue(iterator.hasNext());
		assertNull(iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("value", iterator.next());
		assertTrue(iterator.hasNext());
		assertNull(iterator.next());
		assertFalse(iterator.hasNext());
	}

	public void testConsecutiveHasNextCalls() {
		Vector<String> vector = new Vector<String>();
		vector.add("one");
		Iterator<String> iterator = new EnumerationIterator<String>(vector.elements());

		// Calling hasNext multiple times without next should be consistent
		assertTrue(iterator.hasNext());
		assertTrue(iterator.hasNext());
		assertTrue(iterator.hasNext());

		assertEquals("one", iterator.next());

		assertFalse(iterator.hasNext());
		assertFalse(iterator.hasNext());
	}

	public void testLargeEnumeration() {
		Vector<Integer> vector = new Vector<Integer>();
		for (int i = 0; i < 1000; i++) {
			vector.add(i);
		}
		Iterator<Integer> iterator = new EnumerationIterator<Integer>(vector.elements());

		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		assertEquals(1000, count);
	}

	public void testRemoveThrowsUnsupportedOperationException() {
		Vector<String> vector = new Vector<String>();
		vector.add("one");
		Iterator<String> iterator = new EnumerationIterator<String>(vector.elements());

		iterator.next();
		try {
			iterator.remove();
			fail("Expected UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
	}

	public void testRemoveThrowsUnsupportedOperationExceptionWhenEmpty() {
		Enumeration<String> enumeration = new IteratorEnumeration<String>(new ArrayList<String>().iterator());
		Iterator<String> iterator = new EnumerationIterator<String>(enumeration);

		try {
			iterator.remove();
			fail("Expected UnsupportedOperationException");
		} catch (UnsupportedOperationException e) {
			// Expected
		}
	}

	public void testRoundTripConversion() {
		// Test converting iterator -> enumeration -> iterator
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");

		Iterator<String> original = list.iterator();
		Enumeration<String> enumeration = new IteratorEnumeration<String>(original);
		Iterator<String> roundTrip = new EnumerationIterator<String>(enumeration);

		assertTrue(roundTrip.hasNext());
		assertEquals("a", roundTrip.next());
		assertTrue(roundTrip.hasNext());
		assertEquals("b", roundTrip.next());
		assertTrue(roundTrip.hasNext());
		assertEquals("c", roundTrip.next());
		assertFalse(roundTrip.hasNext());
	}

	public void testWithCustomEnumeration() {
		// Test with a custom enumeration implementation
		Enumeration<Integer> customEnum = new Enumeration<Integer>() {
			private int current = 0;
			private final int max = 3;

			public boolean hasMoreElements() {
				return current < max;
			}

			public Integer nextElement() {
				if (current >= max) {
					throw new NoSuchElementException();
				}
				return current++;
			}
		};

		Iterator<Integer> iterator = new EnumerationIterator<Integer>(customEnum);

		assertEquals((Integer) 0, iterator.next());
		assertEquals((Integer) 1, iterator.next());
		assertEquals((Integer) 2, iterator.next());
		assertFalse(iterator.hasNext());
	}
}
