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

import java.util.Locale;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import com.Ostermiller.util.DateTimeParse.Field;

/**
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.00
 */
public class DateTimeParseTest extends TestCase {

	public void testNull(){
		assertNull(parse(null));
	}

	public void testEmpty(){
		assertNull(parse(""));
	}

	public void testNotDateFoo(){
		assertNull(parse("foo"));
	}

	public void testTooLongForYear(){
		assertNull(parse("10000"));
		assertNull(parse("999999"));
	}

	public void testNotDateBadMonthName(){
		assertNull(new DateTimeParse().parse("1 festival, 1032"));
	}

	public void testYyyyMmDdObvious(){
		assertJustDateEquals("1982-11-30", parse("1982-11-30"));
	}

	public void testYyyyDdMmObvious(){
		assertJustDateEquals("2012-03-15", parse("2012-15-03"));
	}

	public void testDdMmYyyyObvious(){
		assertJustDateEquals("1676-02-13", parse("13-02-1676"));
	}

	public void testMmDdYyyyObvious(){
		assertJustDateEquals("1676-02-13", parse("02-13-1676"));
	}

	public void testSlashesMmDdYyyyObvious(){
		assertJustDateEquals("1998-11-23", parse("11/23/1998"));
	}

	public void testDotsMmDdYyyyObvious(){
		assertJustDateEquals("2005-12-25", parse("25.12.2005"));
	}

	public void testYearExtend(){
		assertJustDateEquals("1999-01-01", parse("Jan 1st 99"));
	}

	public void testFullMonthDayYear(){
		assertJustDateEquals("1545-01-01", parse("January 1, 1545"));
		assertJustDateEquals("1545-02-01", parse("February 1, 1545"));
		assertJustDateEquals("1545-03-01", parse("March 1, 1545"));
		assertJustDateEquals("1545-04-01", parse("April 1, 1545"));
		assertJustDateEquals("1545-05-01", parse("May 1, 1545"));
		assertJustDateEquals("1545-06-01", parse("June 1, 1545"));
		assertJustDateEquals("1545-07-01", parse("July 1, 1545"));
		assertJustDateEquals("1545-08-01", parse("August 1, 1545"));
		assertJustDateEquals("1545-09-01", parse("September 1, 1545"));
		assertJustDateEquals("1545-10-01", parse("October 1, 1545"));
		assertJustDateEquals("1545-11-01", parse("November 1, 1545"));
		assertJustDateEquals("1545-12-01", parse("December 1, 1545"));
	}

	public void testPartMonthDayYear(){
		assertJustDateEquals("1545-01-01", parse("Jan 1, 1545"));
		assertJustDateEquals("1545-02-01", parse("Feb 1, 1545"));
		assertJustDateEquals("1545-03-01", parse("Mar 1, 1545"));
		assertJustDateEquals("1545-04-01", parse("Apr 1, 1545"));
		assertJustDateEquals("1545-05-01", parse("May 1, 1545"));
		assertJustDateEquals("1545-06-01", parse("Jun 1, 1545"));
		assertJustDateEquals("1545-07-01", parse("Jul 1, 1545"));
		assertJustDateEquals("1545-08-01", parse("Aug 1, 1545"));
		assertJustDateEquals("1545-09-01", parse("Sep 1, 1545"));
		assertJustDateEquals("1545-10-01", parse("Oct 1, 1545"));
		assertJustDateEquals("1545-11-01", parse("Nov 1, 1545"));
		assertJustDateEquals("1545-12-01", parse("Dec 1, 1545"));
	}

	public void testPartMonthPeriodDayYear(){
		assertJustDateEquals("1545-02-01", parse("Feb. 1, 1545"));
	}

	public void testFullMonthDayYearDayFirst(){
		assertJustDateEquals("1232-07-04", parse("4 July 1232"));
	}

	public void testPartMonthDayYearDayFirst(){
		assertJustDateEquals("2028-01-26", parse("26 Jan 2028"));
	}

	public void testOrdinals(){
		assertJustDateEquals("1900-01-01", parse("Jan 1st 1900"));
		assertJustDateEquals("1900-01-02", parse("Jan 2nd 1900"));
		assertJustDateEquals("1900-01-03", parse("Jan 3rd 1900"));
		assertJustDateEquals("1900-01-04", parse("Jan 4th 1900"));
		assertJustDateEquals("1900-01-05", parse("Jan 5th 1900"));
		assertJustDateEquals("1900-01-06", parse("Jan 6th 1900"));
		assertJustDateEquals("1900-01-07", parse("Jan 7th 1900"));
		assertJustDateEquals("1900-01-08", parse("Jan 8th 1900"));
		assertJustDateEquals("1900-01-09", parse("Jan 9th 1900"));
		assertJustDateEquals("1900-01-10", parse("Jan 10th 1900"));
		assertJustDateEquals("1900-01-11", parse("Jan 11th 1900"));
		assertJustDateEquals("1900-01-12", parse("Jan 12th 1900"));
		assertJustDateEquals("1900-01-13", parse("Jan 13th 1900"));
		assertJustDateEquals("1900-01-14", parse("Jan 14th 1900"));
		assertJustDateEquals("1900-01-15", parse("Jan 15th 1900"));
		assertJustDateEquals("1900-01-16", parse("Jan 16th 1900"));
		assertJustDateEquals("1900-01-17", parse("Jan 17th 1900"));
		assertJustDateEquals("1900-01-18", parse("Jan 18th 1900"));
		assertJustDateEquals("1900-01-19", parse("Jan 19th 1900"));
		assertJustDateEquals("1900-01-20", parse("Jan 20th 1900"));
		assertJustDateEquals("1900-01-21", parse("Jan 21st 1900"));
		assertJustDateEquals("1900-01-22", parse("Jan 22nd 1900"));
		assertJustDateEquals("1900-01-23", parse("Jan 23rd 1900"));
		assertJustDateEquals("1900-01-24", parse("Jan 24th 1900"));
		assertJustDateEquals("1900-01-25", parse("Jan 25th 1900"));
		assertJustDateEquals("1900-01-26", parse("Jan 26th 1900"));
		assertJustDateEquals("1900-01-27", parse("Jan 27th 1900"));
		assertJustDateEquals("1900-01-28", parse("Jan 28th 1900"));
		assertJustDateEquals("1900-01-29", parse("Jan 29th 1900"));
		assertJustDateEquals("1900-01-30", parse("Jan 30th 1900"));
		assertJustDateEquals("1900-01-31", parse("Jan 31st 1900"));
	}

	public void testOrdinalsWords(){
		assertJustDateEquals("1900-01-01", parse("Jan First 1900"));
		assertJustDateEquals("1900-01-02", parse("Jan Second 1900"));
		assertJustDateEquals("1900-01-03", parse("Jan Third 1900"));
		assertJustDateEquals("1900-01-04", parse("Jan Fourth 1900"));
		assertJustDateEquals("1900-01-05", parse("Jan Fifth 1900"));
		assertJustDateEquals("1900-01-06", parse("Jan Sixth 1900"));
		assertJustDateEquals("1900-01-07", parse("Jan Seventh 1900"));
		assertJustDateEquals("1900-01-08", parse("Jan Eighth 1900"));
		assertJustDateEquals("1900-01-09", parse("Jan Ninth 1900"));
		assertJustDateEquals("1900-01-10", parse("Jan Tenth 1900"));
		assertJustDateEquals("1900-01-11", parse("Jan Eleventh 1900"));
		assertJustDateEquals("1900-01-12", parse("Jan Twelfth 1900"));
		assertJustDateEquals("1900-01-13", parse("Jan Thirteenth 1900"));
		assertJustDateEquals("1900-01-14", parse("Jan Fourteenth 1900"));
		assertJustDateEquals("1900-01-15", parse("Jan Fifteenth 1900"));
		assertJustDateEquals("1900-01-16", parse("Jan Sixteenth 1900"));
		assertJustDateEquals("1900-01-17", parse("Jan Seventeenth 1900"));
		assertJustDateEquals("1900-01-18", parse("Jan Eighteenth 1900"));
		assertJustDateEquals("1900-01-19", parse("Jan Ninteenth 1900"));
		assertJustDateEquals("1900-01-20", parse("Jan Twentieth 1900"));
		assertJustDateEquals("1900-01-21", parse("Jan Twenty-first 1900"));
		assertJustDateEquals("1900-01-22", parse("Jan Twenty-second 1900"));
		assertJustDateEquals("1900-01-23", parse("Jan Twenty-third  1900"));
		assertJustDateEquals("1900-01-24", parse("Jan Twenty-fourth 1900"));
		assertJustDateEquals("1900-01-25", parse("Jan Twenty-fifth 1900"));
		assertJustDateEquals("1900-01-26", parse("Jan Twenty-sixth 1900"));
		assertJustDateEquals("1900-01-27", parse("Jan Twenty-seventh 1900"));
		assertJustDateEquals("1900-01-28", parse("Jan Twenty-eighth 1900"));
		assertJustDateEquals("1900-01-29", parse("Jan Twenty-ninth 1900"));
		assertJustDateEquals("1900-01-30", parse("Jan Thirtieth 1900"));
		assertJustDateEquals("1900-01-31", parse("Jan Thirty-first 1900"));
	}

	public void testOrdinalsCaseInsensitive(){
		assertJustDateEquals("1900-01-28", parse("Jan 28Th 1900"));
		assertJustDateEquals("1900-01-28", parse("Jan 28TH 1900"));
		assertJustDateEquals("1900-01-28", parse("Jan 28tH 1900"));
		assertJustDateEquals("1900-01-02", parse("Jan second 1900"));
		assertJustDateEquals("1900-01-02", parse("Jan SECOND 1900"));
		assertJustDateEquals("1900-01-02", parse("Jan SeCoNd 1900"));
	}

	public void testOrdinalOrder(){
		assertJustDateEquals("1900-01-02", parse("Jan 1900 2nd"));
		assertJustDateEquals("1900-01-02", parse("2nd Jan 1900"));
		assertJustDateEquals("1900-01-02", parse("2nd 1900 Jan"));
		assertJustDateEquals("1900-01-02", parse("1900 2nd Jan"));
		assertJustDateEquals("1900-01-02", parse("1900 Jan 2nd"));
	}

	public void testOrdinalOrderNonObvious(){
		assertJustDateEquals("1900-01-02", parse("1 2nd 1900"));
		assertJustDateEquals("1900-01-02", parse("1 1900 2nd"));
		assertJustDateEquals("1900-01-02", parse("2nd 1 1900"));
		assertJustDateEquals("1900-01-02", parse("2nd 1900 1"));
		assertJustDateEquals("1900-01-02", parse("1900 2nd 1"));
		assertJustDateEquals("1900-01-02", parse("1900 1 2nd"));
	}

	public void testObviousYearExtension(){
		assertJustDateEquals("1976-02-03", parse("Feb 3 76"));
	}

	public void testYearExtensionApos(){
		assertJustDateEquals("1976-02-03", parse("Feb 3 '76"));
	}

	public void testYearExtensionAposMakesItObvious(){
		assertJustDateEquals("1902-01-31", parse("'02 31 1"));
		assertJustDateEquals("1902-01-31", parse("31 1 '02"));
		assertJustDateEquals("1902-01-31", parse("31 '02 1"));
	}

	public void testYearExtensionSmartApos(){
		assertJustDateEquals("1976-02-03", parse("Feb 3 \u821676"));
		assertJustDateEquals("1976-02-03", parse("Feb 3 \u821776"));
	}

	public void testJustYear(){
		assertJustDateEquals("1988-01-01", parse("1988"));
	}

	public void testJustYearTwoDigit(){
		assertJustDateEquals("1987-01-01", parse("87"));
	}

	public void testJustYearApos(){
		assertJustDateEquals("1908-01-01", parse("'08"));
	}

	public void testJustMonth(){
		assertJustDateEquals("1981-01-01", parse("January"));
		assertJustDateEquals("1981-02-01", parse("Feb"));
	}

	public void testJustMonthAndDay(){
		assertJustDateEquals("1981-01-01", parse("January 1st"));
		assertJustDateEquals("1981-02-28", parse("Feb 28th"));
	}

	public void testIgnoreWeekdayWords(){
		assertJustDateEquals("1999-01-01", parse("Monday, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Tuesday, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Wednesday, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Thursday, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Friday, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Saturday, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Sunday, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Mon, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Tue, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Wed, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Thu, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Fri, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Sat, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Sun, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Mo, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Tu, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("We, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Th, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Fr, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Sa, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("Su, Jan 1st 1999"));
	}

	public void testIgnoreWeekdayWordsCaseInsensitive(){
		assertJustDateEquals("1999-01-01", parse("monday, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("MONDAY, Jan 1st 1999"));
		assertJustDateEquals("1999-01-01", parse("MoNdAy, Jan 1st 1999"));
	}

	public void testIgnoreWeekdayWordsAbbreviated(){
		assertJustDateEquals("1999-01-01", parse("mon., Jan 1st 1999"));
	}

	public void testFailOnFakeWeekdayWords(){
		assertNull(parse("Workday, Jan 1st 1999"));
	}

	public void testFieldOrderMonthDayYear(){
		assertJustDateEquals("1903-01-02", parseMonthDayYear("1 2 3"));
	}

	public void testFieldOrderDayMonthYear(){
		assertJustDateEquals("1903-02-01", parseDayMonthYear("1 2 3"));
	}

	public void testFieldOrderMonthYearDay(){
		assertJustDateEquals("1902-01-03", parseMonthYearDay("1 2 3"));
	}

	public void testFieldOrderDayYearMonth(){
		assertJustDateEquals("1902-03-01", parseDayYearMonth("1 2 3"));
	}

	public void testFieldOrderYearMonthDay(){
		assertJustDateEquals("1901-02-03", parseYearMonthDay("1 2 3"));
	}

	public void testFieldOrderYearDayMonth(){
		assertJustDateEquals("1901-03-02", parseYearDayMonth("1 2 3"));
	}

	public void testMonthDayOrderMonthDayYear(){
		assertJustDateEquals("1903-01-02", parseMonthDayYear("1 2 1903"));
		assertJustDateEquals("1903-01-02", parseMonthDayYear("1 1903 2"));
	}

	public void testMonthDayOrderDayMonthYear(){
		assertJustDateEquals("1903-02-01", parseDayMonthYear("1 2 1903"));
		assertJustDateEquals("1903-02-01", parseDayMonthYear("1 1903 2"));
	}

	public void testMonthDayOrderMonthYearDay(){
		assertJustDateEquals("1903-01-02", parseMonthYearDay("1 2 1903"));
		assertJustDateEquals("1903-01-02", parseMonthYearDay("1 1903 2"));
	}

	public void testMonthDayOrderDayYearMonth(){
		assertJustDateEquals("1903-02-01", parseDayYearMonth("1 2 1903"));
		assertJustDateEquals("1903-02-01", parseDayYearMonth("1 1903 2"));
	}

	public void testMonthDayOrderYearMonthDay(){
		assertJustDateEquals("1903-01-02", parseYearMonthDay("1 2 1903"));
		assertJustDateEquals("1903-01-02", parseYearMonthDay("1 1903 2"));
	}

	public void testMonthDayOrderYearDayMonth(){
		assertJustDateEquals("1903-02-01", parseYearDayMonth("1 2 1903"));
		assertJustDateEquals("1903-02-01", parseYearDayMonth("1 1903 2"));
	}

	public void testDayYearOrderMonthDayYear(){
		assertJustDateEquals("1903-01-02", parseMonthDayYear("Jan 2 3"));
		assertJustDateEquals("1903-01-02", parseMonthDayYear("2 Jan 3"));
		assertJustDateEquals("1903-01-02", parseMonthDayYear("2 3 Jan"));
	}

	public void testDayYearOrderDayMonthYear(){
		assertJustDateEquals("1903-01-02", parseDayMonthYear("Jan 2 3"));
		assertJustDateEquals("1903-01-02", parseDayMonthYear("2 Jan 3"));
		assertJustDateEquals("1903-01-02", parseDayMonthYear("2 3 Jan"));
	}

	public void testDayYearOrderMonthYearDay(){
		assertJustDateEquals("1902-01-03", parseMonthYearDay("Jan 2 3"));
		assertJustDateEquals("1902-01-03", parseMonthYearDay("2 Jan 3"));
		assertJustDateEquals("1902-01-03", parseMonthYearDay("2 3 Jan"));
	}

	public void testDayYearOrderDayYearMonth(){
		assertJustDateEquals("1903-01-02", parseDayYearMonth("Jan 2 3"));
		assertJustDateEquals("1903-01-02", parseDayYearMonth("2 Jan 3"));
		assertJustDateEquals("1903-01-02", parseDayYearMonth("2 3 Jan"));
	}

	public void testDayYearOrderYearMonthDay(){
		assertJustDateEquals("1902-01-03", parseYearMonthDay("Jan 2 3"));
		assertJustDateEquals("1902-01-03", parseYearMonthDay("2 Jan 3"));
		assertJustDateEquals("1902-01-03", parseYearMonthDay("2 3 Jan"));
	}

	public void testDayYearOrderYearDayMonth(){
		assertJustDateEquals("1902-01-03", parseYearDayMonth("Jan 2 3"));
		assertJustDateEquals("1902-01-03", parseYearDayMonth("2 Jan 3"));
		assertJustDateEquals("1902-01-03", parseYearDayMonth("2 3 Jan"));
	}

	public void testYearMonthOrderMonthDayYear(){
		assertJustDateEquals("1903-02-01", parseMonthDayYear("1st 2 3"));
		assertJustDateEquals("1903-02-01", parseMonthDayYear("2 1st 3"));
		assertJustDateEquals("1903-02-01", parseMonthDayYear("2 3 1st"));
	}

	public void testYearMonthOrderDayMonthYear(){
		assertJustDateEquals("1903-02-01", parseDayMonthYear("1st 2 3"));
		assertJustDateEquals("1903-02-01", parseDayMonthYear("2 1st 3"));
		assertJustDateEquals("1903-02-01", parseDayMonthYear("2 3 1st"));
	}

	public void testYearMonthOrderMonthYearDay(){
		assertJustDateEquals("1903-02-01", parseMonthYearDay("1st 2 3"));
		assertJustDateEquals("1903-02-01", parseMonthYearDay("2 1st 3"));
		assertJustDateEquals("1903-02-01", parseMonthYearDay("2 3 1st"));
	}

	public void testYearMonthOrderDayYearMonth(){
		assertJustDateEquals("1902-03-01", parseDayYearMonth("1st 2 3"));
		assertJustDateEquals("1902-03-01", parseDayYearMonth("2 1st 3"));
		assertJustDateEquals("1902-03-01", parseDayYearMonth("2 3 1st"));
	}

	public void testYearMonthOrderYearMonthDay(){
		assertJustDateEquals("1902-03-01", parseYearMonthDay("1st 2 3"));
		assertJustDateEquals("1902-03-01", parseYearMonthDay("2 1st 3"));
		assertJustDateEquals("1902-03-01", parseYearMonthDay("2 3 1st"));
	}

	public void testYearMonthOrderYearDayMonth(){
		assertJustDateEquals("1902-03-01", parseYearDayMonth("1st 2 3"));
		assertJustDateEquals("1902-03-01", parseYearDayMonth("2 1st 3"));
		assertJustDateEquals("1902-03-01", parseYearDayMonth("2 3 1st"));
	}

	public void testYearFirstThenMonthDay(){
		assertJustDateEquals("1997-01-02", parseMonthDayYear("1997-01-02"));
		assertJustDateEquals("1997-01-02", parseDayMonthYear("1997-01-02"));
		assertJustDateEquals("1997-01-02", parseMonthYearDay("1997-01-02"));
		assertJustDateEquals("1997-01-02", parseYearMonthDay("1997-01-02"));
		assertJustDateEquals("1997-01-02", parseYearMonthDay("1997-01-02"));
	}

	public void testYearFirstOverride(){
		assertJustDateEquals("1997-02-01", parseYearDayMonth("1997-01-02"));
	}

	public void testEarlyYear(){
		assertJustDateEquals("0009-01-01", parse("0009"));
		assertJustDateEquals("0009-01-01", parse("Jan 0009"));
		assertJustDateEquals("0009-01-02", parse("Jan 0009 2"));
		assertJustDateEquals("0009-02-03", parse("0009-02-03"));
	}

	public void testBc(){
		assertJustDateEqualsBc("0200-01-01", parse("200 BC"));
		assertJustDateEqualsBc("1431-01-10", parse("Jan 10 1431 BC"));
	}

	public void testBce(){
		assertJustDateEqualsBc("0200-01-01", parse("200 BCE"));
		assertJustDateEqualsBc("1431-01-10", parse("Jan 10 1431 BCE"));
	}

	public void testAd(){
		assertJustDateEquals("0200-01-01", parse("200 AD"));
		assertJustDateEquals("1431-01-10", parse("Jan 10 1431 AD"));
	}

	public void testCe(){
		assertJustDateEquals("0200-01-01", parse("200 CE"));
		assertJustDateEquals("1431-01-10", parse("Jan 10 1431 CE"));
	}

	public void testAdBcCaseInsensitive(){
		assertJustDateEqualsBc("0200-01-01", parse("200 bc"));
		assertJustDateEqualsBc("1431-01-10", parse("Jan 10 1431 BcE"));
		assertJustDateEquals("0200-01-01", parse("200 aD"));
		assertJustDateEquals("1431-01-10", parse("Jan 10 1431 Ce"));
	}

	public void testFieldOrderMonth(){
		DateTimeParse p = getParser(new Field[]{Field.MONTH});
		List<Field> fields = p.getFieldOrder();
		assertEquals(3, fields.size());
		assertEquals(Field.MONTH, fields.get(0));
		assertEquals(Field.YEAR, fields.get(1));
		assertEquals(Field.DAY, fields.get(2));
	}

	public void testFieldOrderDay(){
		DateTimeParse p = getParser(new Field[]{Field.DAY});
		List<Field> fields = p.getFieldOrder();
		assertEquals(3, fields.size());
		assertEquals(Field.DAY, fields.get(0));
		assertEquals(Field.YEAR, fields.get(1));
		assertEquals(Field.MONTH, fields.get(2));
	}

	public void testFieldOrderYear(){
		DateTimeParse p = getParser(new Field[]{Field.YEAR});
		List<Field> fields = p.getFieldOrder();
		assertEquals(3, fields.size());
		assertEquals(Field.YEAR, fields.get(0));
		assertEquals(Field.MONTH, fields.get(1));
		assertEquals(Field.DAY, fields.get(2));
	}

	public void testFieldOrderMonthDay(){
		DateTimeParse p = getParser(new Field[]{Field.MONTH, Field.DAY});
		List<Field> fields = p.getFieldOrder();
		assertEquals(3, fields.size());
		assertEquals(Field.MONTH, fields.get(0));
		assertEquals(Field.DAY, fields.get(1));
		assertEquals(Field.YEAR, fields.get(2));
	}

	public void testFieldOrderMonthYear(){
		DateTimeParse p = getParser(new Field[]{Field.MONTH, Field.YEAR});
		List<Field> fields = p.getFieldOrder();
		assertEquals(3, fields.size());
		assertEquals(Field.MONTH, fields.get(0));
		assertEquals(Field.YEAR, fields.get(1));
		assertEquals(Field.DAY, fields.get(2));
	}

	public void testFieldOrderDayMonth(){
		DateTimeParse p = getParser(new Field[]{Field.DAY, Field.MONTH});
		List<Field> fields = p.getFieldOrder();
		assertEquals(3, fields.size());
		assertEquals(Field.DAY, fields.get(0));
		assertEquals(Field.MONTH, fields.get(1));
		assertEquals(Field.YEAR, fields.get(2));
	}

	public void testFieldOrderDayYear(){
		DateTimeParse p = getParser(new Field[]{Field.DAY, Field.YEAR});
		List<Field> fields = p.getFieldOrder();
		assertEquals(3, fields.size());
		assertEquals(Field.DAY, fields.get(0));
		assertEquals(Field.YEAR, fields.get(1));
		assertEquals(Field.MONTH, fields.get(2));
	}

	public void testFieldOrderYearMonth(){
		DateTimeParse p = getParser(new Field[]{Field.YEAR, Field.MONTH});
		List<Field> fields = p.getFieldOrder();
		assertEquals(3, fields.size());
		assertEquals(Field.YEAR, fields.get(0));
		assertEquals(Field.MONTH, fields.get(1));
		assertEquals(Field.DAY, fields.get(2));
	}

	public void testFieldOrderYearDay(){
		DateTimeParse p = getParser(new Field[]{Field.YEAR, Field.DAY});
		List<Field> fields = p.getFieldOrder();
		assertEquals(3, fields.size());
		assertEquals(Field.YEAR, fields.get(0));
		assertEquals(Field.DAY, fields.get(1));
		assertEquals(Field.MONTH, fields.get(2));
	}

	public void testFieldOrderDuplicate(){
		DateTimeParse p = getParser(new Field[]{Field.YEAR, Field.YEAR});
		List<Field> fields = p.getFieldOrder();
		assertEquals(3, fields.size());
		assertEquals(Field.YEAR, fields.get(0));
		assertEquals(Field.MONTH, fields.get(1));
		assertEquals(Field.DAY, fields.get(2));
	}

	public void testGermanAscii(){
		assertJustDateEquals("2100-05-27", parse("27 Mai 2100"));
	}

	public void testGermanUmlaut(){
		assertJustDateEquals("2054-03-25", parse("25 M\u00e4r 2054"));
	}


	// END TESTS

	private static DateTimeParse getParser(Field[] fieldOrder){
		DateTimeParse p = new DateTimeParse(Locale.US);
		p.setDefaultYear(1981);
		p.setFieldOrder(fieldOrder);
		p.setYearExtensionPolicy(YearExtensionAround.CENTURY_1900);
		return p;
	}

	public static String parse(String date){
		return parseDayMonthYear(date);
	}

	private static final DateTimeParse PARSER_DAY_MONTH_YEAR = getParser(
		new Field[]{Field.DAY, Field.MONTH, Field.YEAR}
	);

	public static String parseDayMonthYear(String date){
		return formatDate(PARSER_DAY_MONTH_YEAR.parse(date));
	}

	private static final DateTimeParse PARSER_MONTH_DAY_YEAR = getParser(
		new Field[]{Field.MONTH, Field.DAY, Field.YEAR}
	);

	public static String parseMonthDayYear(String date){
		return formatDate(PARSER_MONTH_DAY_YEAR.parse(date));
	}

	private static final DateTimeParse PARSER_DAY_YEAR_MONTH = getParser(
		new Field[]{Field.DAY, Field.YEAR, Field.MONTH}
	);

	public static String parseDayYearMonth(String date){
		return formatDate(PARSER_DAY_YEAR_MONTH.parse(date));
	}

	private static final DateTimeParse PARSER_MONTH_YEAR_DAY = getParser(
		new Field[]{Field.MONTH, Field.YEAR, Field.DAY}
	);

	public static String parseMonthYearDay(String date){
		return formatDate(PARSER_MONTH_YEAR_DAY.parse(date));
	}

	private static final DateTimeParse PARSER_YEAR_MONTH_DAY = getParser(
		new Field[]{Field.YEAR, Field.MONTH, Field.DAY}
	);

	public static String parseYearMonthDay(String date){
		return formatDate(PARSER_YEAR_MONTH_DAY.parse(date));
	}

	private static final DateTimeParse PARSER_YEAR_DAY_MONTH = getParser(
		new Field[]{Field.YEAR, Field.DAY, Field.MONTH}
	);

	public static String parseYearDayMonth(String date){
		return formatDate(PARSER_YEAR_DAY_MONTH.parse(date));
	}

	public void assertJustDateEquals(String s, String d){
		assertEquals(s + " AD 00:00:00", d);
	}

	public void assertJustDateEqualsBc(String s, String d){
		assertEquals(s + " BC 00:00:00", d);
	}

	public static String formatDate(Date date){
		if (date == null) return null;
		return new SimpleDateFormat("yyyy-MM-dd G HH:mm:ss").format(date);
	}
}
