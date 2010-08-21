/*
 * Copyright (C) 2004-2007 Stephen Ostermiller
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

import junit.framework.TestCase;


/**
 * Significant Figures regression test.
 */
public class SignificantFiguresTest extends TestCase {

	private void assertSigFigs(String input, int sigFigs, String output){
		assertEquals(output, SignificantFigures.format(input, sigFigs));
	}

	public void testIntegerFourSigFigsToThree(){
		assertSigFigs("1234", 3, "1230");
	}

	public void testFourSigfigsToThreeDecimal(){
		assertSigFigs("60.91", 3, "60.9");
	}

	public void testFourSigfigsToOneInteger(){
		assertSigFigs("3343", 1, "3000");
	}

	public void testNotRepresentableWithoutScientificNotation(){
		assertSigFigs("200", 2, "2.0E2");
	}

	public void testNotRepresentableWithoutScientificNotationLong(){
		assertSigFigs("5097.808073851760832954355668151943215272", 3, "5.10E3");
	}

	public void testRoundingOnAFiveSpecialEvenOddRule1(){
		assertSigFigs("6.15", 2, "6.2");
	}

	public void testRoundingOnAFiveSpecialEvenOddRule2(){
		assertSigFigs("6.25", 2, "6.2");
	}

	public void testRoundingOnAFiveSpecialEvenOddRule3(){
		assertSigFigs("6.150", 2, "6.2");
	}

	public void testRoundingOnAFiveSpecialEvenOddRule4(){
		assertSigFigs("6.250", 2, "6.2");
	}

	public void testRoundingOnAFiveSpecialEvenOddRule5(){
		assertSigFigs("6.1500", 2, "6.2");
	}

	public void testRoundingOnAFiveSpecialEvenOddRule6(){
		assertSigFigs("6.2500", 2, "6.2");
	}

	public void testWhenMoreDigitsSpecialEvenOddRuleDoesNotApplyWhenRoundingOnAFive1(){
		assertSigFigs("6.153", 2, "6.2");
	}

	public void testWhenMoreDigitsSpecialEvenOddRuleDoesNotApplyWhenRoundingOnAFive2(){
		assertSigFigs("6.253", 2, "6.3");
	}

	public void testDecimalPointAtEnd1(){
		assertSigFigs("200.123", 3, "200.");
	}

	public void testNoDecimalPointAtEnd(){
		assertSigFigs("234.123", 3, "234");
	}

	public void testDecimalPointAtEnd2(){
		assertSigFigs("199.87", 3, "200.");
	}

	public void testGetsLeadingZero(){
		assertSigFigs(".0033234324", 2, "0.0033");
	}

	public void testKeepsLeadingZeroCanBeRepresentedWithoutScientificNotation(){
		assertSigFigs("0.0033234324", 2, "0.0033");
	}

	public void testTooSmallWithoutScientificNotation(){
		assertSigFigs(".00033234324", 2, "3.3E-4");
	}

	public void testCanBeRepresentedWithoutScientificNotation(){
		assertSigFigs("1234567", 3, "1230000");
	}

	public void testTooLargeWithoutScientificNotation(){
		assertSigFigs("12345678", 3, "1.23E7");
	}
}
