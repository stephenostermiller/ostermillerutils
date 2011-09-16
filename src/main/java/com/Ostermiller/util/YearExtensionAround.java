/*
 * Copyright (C) 2010-2011 Stephen Ostermiller
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

import java.util.Calendar;

/**
 * Extend years like '99 to 1999 based on dates around the current date.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.00
 */
public class YearExtensionAround implements YearExtensionPolicy {


	private static final int NEAREST_ALLOWED_YEARS_IN_PAST = 50;

	/**
	 * Extend a two digit year to the nearest year that ends in
	 * those two digits.
	 * <p>
	 * When it is the year 2000:
	 * <ul>
	 * <li> 50 to 1950
	 * <li> 99 to 1999
	 * <li> 00 to 2000
	 * <li> 01 to 2001
	 * </ul>
	 *
	 * @since ostermillerutils 1.08.00
	 */
	public static final YearExtensionAround NEAREST = new YearExtensionAround(
		NEAREST_ALLOWED_YEARS_IN_PAST
	);

	/**
	 * Extend a two digit year to the nearest year that ends in
	 * those two digits.
	 * <p>
	 * When it is the year 2000:
	 * <ul>
	 * <li> 50 to 1950
	 * <li> 99 to 1999
	 * <li> 00 to 2000
	 * <li> 01 to 2001
	 * </ul>
	 *
	 * @param currentYear the current year
	 * @since ostermillerutils 1.08.00
	 */
	public static final YearExtensionAround getNearest(int currentYear){
		return new YearExtensionAround(NEAREST_ALLOWED_YEARS_IN_PAST, currentYear);
	}

	private static final int LATEST_ALLOWED_YEARS_IN_PAST = 99;

	/**
	 * Extend a two digit year to the nearest year that ends in
	 * those two digits.
	 * <p>
	 * When it is the year 2000:
	 * <ul>
	 * <li> 01 to 1901
	 * <li> 49 to 1949
	 * <li> 50 to 1950
	 * <li> 99 to 1999
	 * <li> 00 to 2000
	 * </ul>
	 *
	 * @since ostermillerutils 1.08.00
	 */
	public static final YearExtensionAround LATEST = new YearExtensionAround(
		LATEST_ALLOWED_YEARS_IN_PAST
	);

	/**
	 * Extend a two digit year to the nearest year that ends in
	 * those two digits.
	 * <p>
	 * When it is the year 2000:
	 * <ul>
	 * <li> 01 to 1901
	 * <li> 49 to 1949
	 * <li> 50 to 1950
	 * <li> 99 to 1999
	 * <li> 00 to 2000
	 * </ul>
	 *
	 * @param currentYear the current year
	 * @since ostermillerutils 1.08.00
	 */
	public static final YearExtensionAround getLatest(int currentYear){
		return new YearExtensionAround(LATEST_ALLOWED_YEARS_IN_PAST, currentYear);
	}

	private static final int CENTURY_ALLOWED_YEARS_IN_PAST = 0;
	private static final int CENTURY_1900_START = 1900;

	/**
	 * Extend a two digit year to a year in the 1900s
	 * <ul>
	 * <li> 00 to 1900
	 * <li> 01 to 1901
	 * <li> 49 to 1949
	 * <li> 50 to 1950
	 * <li> 99 to 1999
	 * </ul>
	 *
	 * @since ostermillerutils 1.08.00
	 */
	public static final YearExtensionAround CENTURY_1900 = new YearExtensionAround(
		CENTURY_ALLOWED_YEARS_IN_PAST,
		CENTURY_1900_START
	);
	private static final int CENTURY_2000_START = 2000;
	/**
	 * Extend a two digit year to a year in the 2000s
	 * <ul>
	 * <li> 00 to 2000
	 * <li> 01 to 2001
	 * <li> 49 to 2049
	 * <li> 50 to 2050
	 * <li> 99 to 2099
	 * </ul>
	 *
	 * @since ostermillerutils 1.08.00
	 */
	public static final YearExtensionAround CENTURY_2000 = new YearExtensionAround(
		CENTURY_ALLOWED_YEARS_IN_PAST,
		CENTURY_2000_START
	);

	private int currentYear;
	private int baseYear;
	private int allowedYearsInPast;

	/**
	 * Create a year extension nearest policy with the current year taken from the
	 * system time.
	 *
	 * @since ostermillerutils 1.08.00
	 */
	public YearExtensionAround(int allowedYearsInPast){
		this(allowedYearsInPast, Calendar.getInstance().get(Calendar.YEAR));
	}

	/**
	 * Create a year extension nearest policy with the given current year
	 *
	 * @param currentYear full year eg 2000
	 *
	 * @since ostermillerutils 1.08.00
	 */
	public YearExtensionAround(int allowedYearsInPast, int currentYear){
		setAllowedYearsInPast(allowedYearsInPast);
		setCurrentYear(currentYear);
	}

	/**
	 * Number of years in the past to allow dates
	 * @param allowedYearsInPast number between 0 and 99
	 */
	public void setAllowedYearsInPast(int allowedYearsInPast){
		if (allowedYearsInPast < 0 || allowedYearsInPast >= 100){
			throw new IllegalArgumentException(
				"Allowed years in past must be in 0 to 99 range"
			);
		}
		this.allowedYearsInPast = allowedYearsInPast;
	}

	/**
	 * Set the current year.
	 *
	 * @param currentYear full year eg 2000
	 *
	 * @since ostermillerutils 1.08.00
	 */
	public void setCurrentYear(int currentYear){
		this.currentYear = currentYear;
		calculateBaseYear();
	}

	private void calculateBaseYear(){
		baseYear = currentYear/100*100 - 100;
	}

	/**
	 * Extend a two digit year to the nearest year that ends in
	 * those two digits.
	 * <p>
	 * Will choose a year within 100 years of the current year.
	 * Whether the year is in the past or future is determined
	 * by how many years are allowed in the past.
	 *
	 * @since ostermillerutils 1.08.00
	 */
	public int extendYear(int twoDigitYear) {
		int year = twoDigitYear + baseYear;
		if (currentYear - year > allowedYearsInPast) year += 100;
		return year;
	}
}
