/*
 * Copyright (C) 2010 Stephen Ostermiller
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
 * See LICENSE.txt for details.
 */
package com.Ostermiller.util;

import junit.framework.TestCase;

/**
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.00
 */
public class YearExtensionTest extends TestCase {

	public void test00Before2000(){
		assertLatestBefore2000(2000, 0);
	}

	public void test50Before2000(){
		assertLatestBefore2000(1950, 50);
	}

	public void test49Before2000(){
		assertLatestBefore2000(1949, 49);
	}

	public void test01Before2000(){
		assertLatestBefore2000(1901, 1);
	}

	public void test99Before2000(){
		assertLatestBefore2000(1999, 99);
	}

	public void test10Before1810(){
		assertLatestBefore1810(1810, 10);
	}

	public void test60Before1810(){
		assertLatestBefore1810(1760, 60);
	}

	public void test59Before1810(){
		assertLatestBefore1810(1759, 59);
	}

	public void test11Before1810(){
		assertLatestBefore1810(1711, 11);
	}

	public void test09Before1810(){
		assertLatestBefore1810(1809, 9);
	}

	public void test00Around2000(){
		assertAround2000(2000, 0);
	}

	public void test50Around2000(){
		assertAround2000(1950, 50);
	}

	public void test49Around2000(){
		assertAround2000(2049, 49);
	}

	public void test01Around2000(){
		assertAround2000(2001, 1);
	}

	public void test99Around2000(){
		assertAround2000(1999, 99);
	}

	public void test10Around1810(){
		assertAround1810(1810, 10);
	}

	public void test60Around1810(){
		assertAround1810(1760, 60);
	}

	public void test59Around1810(){
		assertAround1810(1859, 59);
	}

	public void test11Around1810(){
		assertAround1810(1811, 11);
	}

	public void test09Around1810(){
		assertAround1810(1809, 9);
	}

	public void testNone(){
		assertEquals(0, YearExtensionNone.YEAR_EXTEND_NONE.extendYear(0));
	}

	private static final YearExtensionPolicy LATEST_2000 = YearExtensionAround.getLatest(2000);

	public void assertLatestBefore2000(int fullYear, int partYear){
		assertEquals(
			fullYear,
			LATEST_2000.extendYear(partYear)
		);
	}

	private static final YearExtensionPolicy LATEST_1810 = YearExtensionAround.getLatest(1810);

	public void assertLatestBefore1810(int fullYear, int partYear){
		assertEquals(
			fullYear,
			LATEST_1810.extendYear(partYear)
		);
	}

	private static final YearExtensionPolicy NEAREST_2000 = YearExtensionAround.getNearest(2000);


	public void assertAround2000(int fullYear, int partYear){
		assertEquals(
			fullYear,
			NEAREST_2000.extendYear(partYear)
		);
	}

	private static final YearExtensionPolicy NEAREST_1810 = YearExtensionAround.getNearest(1810);

	public void assertAround1810(int fullYear, int partYear){
		assertEquals(
			fullYear,
			NEAREST_1810.extendYear(partYear)
		);
	}
}
