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
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.00
 */
public class DateTimeParseTest extends TestCase {
	
	public void testNull(){
		assertNull(new DateTimeParse().parse(null));
	}
	
	public void testEmpty(){
		assertNull(new DateTimeParse().parse(""));
	}
	
	public void testNotDateFoo(){
		assertNull(new DateTimeParse().parse("foo"));
	}
	
	public void testNotDateBadMonthName(){
		assertNull(new DateTimeParse().parse("1 festival, 1032"));
	}
	
	public void testYyyyMmDdObvious(){
		assertEquals(
			"1982-11-30",
			parse("1982-11-30")
		);
	}
	
	public void testYyyyDdMmObvious(){
		assertEquals(
			"2012-03-15",
			parse("2012-15-03")
		);
	}
	
	public void testDdMmYyyyObvious(){
		assertEquals(
			"1676-02-13",
			parse("13-02-1676")
		);
	}
	
	public void testMmDdYyyyObvious(){
		assertEquals(
			"1676-02-13",
			parse("02-13-1676")
		);
	}
	
	public void testFullMonthDayYear(){
		assertEquals(
			"1545-06-01",
			parse("June 1, 1545")
		);
	}
	
	public void testPartMonthDayYear(){
		assertEquals(
			"1999-09-20",
			parse("Sep 20, 1999")
		);
	}
	
	public void testFullMonthDayYearDayFirst(){
		assertEquals(
			"1232-07-04",
			parse("4 July 1232")
		);
	}
	
	public void testPartMonthDayYearDayFirst(){
		assertEquals(
			"2028-01-26",
			parse("26 Jan 2028")
		);
	}
	
	public static String parse(String date){
		return formatDate(
			new DateTimeParse().parse(
				date
			)
		);
	}
	
	public static String formatDate(Date date){
		if (date == null) return "null";
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

}
