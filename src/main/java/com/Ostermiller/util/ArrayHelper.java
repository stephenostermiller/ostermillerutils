/*
 * Convenience methods for working with Java arrays.
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

import java.io.IOException;
import java.lang.reflect.Array;

/**
 * Convenience methods for working with Java arrays.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/ArrayHelper.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.06.00
 */
public final class ArrayHelper {

	/**
	 * Concatenates the given arrays into a single long array.
	 * The returned array will be of the common super type of the
	 * two given arrays.
	 * <p>
	 * For example it can be called like this:<br>
	 * <pre>
	 * String[] arr = (String[])ArrayHelper.cat(new String[]{"one","two"}, new String[]{"three","four"});
	 * </pre>
	 *
	 * @param arr1 first array
	 * @param arr2 second array
	 * @return an array whose length is the sum of the given array's lengths and contains all the elements in the given arrays.
	 * @since ostermillerutils 1.06.00
	 */
	public static Object[] cat(Object[] arr1, Object[] arr2){
		// Use reflection to find the super class of both arrays
		Class<?> commonSuperClass = Object.class;
		boolean foundcommonSuperClass=false;
		for (Class<?> c1 = arr1.getClass().getComponentType(); !foundcommonSuperClass && !c1.equals(Object.class); c1 = c1.getSuperclass()){
			for (Class<?> c2 = arr2.getClass().getComponentType(); !foundcommonSuperClass && !c2.equals(Object.class); c2 = c2.getSuperclass()){
				if (c2.equals(c1)){
					foundcommonSuperClass = true;
					commonSuperClass = c1;
				}
			}
		}
		// Create a new array of the correct type
		Object[] result = (Object[])Array.newInstance(commonSuperClass, arr1.length + arr2.length);
		// Copy the two arrays into the large array
		System.arraycopy(arr1, 0, result, 0, arr1.length);
		System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
		return result;
	}

	/**
	 * Prints out a comma separated list of all the objects
	 * in the array on a single line in CSV format.
	 *
	 * @param arr Array to print in comma separated value format
	 * @since ostermillerutils 1.06.00
	 */
	public static void print(Object[] arr){
		try {
			CSVPrinter csvp = new CSVPrinter(System.out);
			for (Object element: arr) {
				csvp.write(element.toString());
			}
			csvp.writeln();
			csvp.flush();
		} catch (IOException iox){
			iox.printStackTrace(System.err);
		}
	}

	/**
	 * Tests two arrays to see if the arrays are equal.
	 * Two arrays will be equal only if they are the same length
	 * and contain objects that are equal in the same order.
	 *
	 * @param arr1 first array
	 * @param arr2 second array
	 * @return true iff two arguments are equal
	 * @since ostermillerutils 1.06.00
	 */
	public static boolean equal(Object[] arr1, Object[] arr2){
		if (arr1 == null && arr2 == null) return true;
		if (arr1 == null || arr2 == null) return false;
		if (arr1.length != arr2.length) return false;
		for (int i=0; i<arr1.length; i++){
			if (!equalObjects(arr1[i], arr2[i])) return false;
		}
		return true;
	}

	/**
	 * Tests if the two objects are equal
	 *
	 * @param o1 first object
	 * @param o2 second object
	 * @since ostermillerutils 1.06.00
	 */
	private static boolean equalObjects(Object o1, Object o2){
		if (o1 == null && o2 == null) return true;
		if (o1 == null || o2 == null) return false;
		return o1.equals(o2);
	}
}
