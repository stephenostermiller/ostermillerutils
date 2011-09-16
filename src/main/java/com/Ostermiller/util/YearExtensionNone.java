/*
 * Copyright (C) 2002-2011 Stephen Ostermiller
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

/**
 * Null object for year extension policy.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.00
 */
public class YearExtensionNone implements YearExtensionPolicy {

	public static final YearExtensionNone YEAR_EXTEND_NONE = new YearExtensionNone();

	private YearExtensionNone(){
		// singleton, private constructor
	}

	/**
	 * Does NOT extend the year.
	 *
	 * @return a two digit year unaltered.
	 * @since ostermillerutils 1.08.00
	 */
	public int extendYear(int twoDigitYear) {
		return twoDigitYear;
	}
}
