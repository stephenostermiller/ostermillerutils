/*
 * Regression tests for RandPass
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
import java.security.SecureRandom;

/**
 * Unit test for RandPass.
 * More information about this class is available from <a target="_top" href=
 * "https://ostermiller.org/utils/RandPass.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 *         https://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.04
 */
public class RandPassTest extends TestCase {

	private RandPass randPass;

	protected void setUp() throws Exception {
		super.setUp();
		randPass = new RandPass();
	}

	protected void tearDown() throws Exception {
		randPass = null;
		super.tearDown();
	}

	public void testDefaultConstructor() {
		RandPass rp = new RandPass();
		assertNotNull(rp);
		char[] pass = rp.getPassChars(8);
		assertEquals(8, pass.length);
	}

	public void testConstructorWithSecureRandom() {
		SecureRandom rand = new SecureRandom();
		RandPass rp = new RandPass(rand);
		assertNotNull(rp);
		char[] pass = rp.getPassChars(8);
		assertEquals(8, pass.length);
	}

	public void testConstructorWithAlphabet() {
		char[] alphabet = {'a', 'b', 'c'};
		RandPass rp = new RandPass(alphabet);
		assertNotNull(rp);
		char[] pass = rp.getPassChars(8);
		for (char c : pass) {
			assertTrue("Character should be in alphabet",
				c == 'a' || c == 'b' || c == 'c');
		}
	}

	public void testConstructorWithBoth() {
		SecureRandom rand = new SecureRandom();
		char[] alphabet = {'x', 'y', 'z'};
		RandPass rp = new RandPass(rand, alphabet);
		assertNotNull(rp);
		char[] pass = rp.getPassChars(8);
		for (char c : pass) {
			assertTrue("Character should be in alphabet",
				c == 'x' || c == 'y' || c == 'z');
		}
	}

	public void testGetPassCharsDefaultLength() {
		char[] pass = randPass.getPassChars();
		assertEquals("Default length should be 8", 8, pass.length);
	}

	public void testGetPassCharsCustomLength() {
		char[] pass = randPass.getPassChars(12);
		assertEquals("Length should be 12", 12, pass.length);
	}

	public void testGetPassCharsZeroLength() {
		char[] pass = randPass.getPassChars(0);
		assertEquals("Length should be 0", 0, pass.length);
	}

	public void testGetPassCharsLargeLength() {
		char[] pass = randPass.getPassChars(1000);
		assertEquals("Length should be 1000", 1000, pass.length);
	}

	public void testGetPassCharsWithBuffer() {
		char[] buffer = new char[10];
		char[] pass = randPass.getPassChars(buffer);
		assertSame("Should return same buffer", buffer, pass);
		assertEquals("Buffer should be filled", 10, pass.length);
		for (char c : pass) {
			assertNotNull("Character should not be null", c);
		}
	}

	public void testGetPassStringDefaultLength() {
		String pass = randPass.getPass();
		assertEquals("Default length should be 8", 8, pass.length());
	}

	public void testGetPassStringCustomLength() {
		String pass = randPass.getPass(15);
		assertEquals("Length should be 15", 15, pass.length());
	}

	public void testGetPassCharacterVariety() {
		// Generate multiple passwords to check for variety
		char[] pass1 = randPass.getPassChars(20);
		char[] pass2 = randPass.getPassChars(20);

		// They should not be identical (with extremely high probability)
		boolean same = true;
		for (int i = 0; i < 20; i++) {
			if (pass1[i] != pass2[i]) {
				same = false;
				break;
			}
		}
		assertFalse("Passwords should differ", same);
	}

	public void testSetAlphabetNonConfusing() {
		randPass.setAlphabet(RandPass.NONCONFUSING_ALPHABET);
		char[] pass = randPass.getPassChars(20);

		// Check that no confusing characters are present
		for (char c : pass) {
			assertFalse("Should not contain I", c == 'I');
			assertFalse("Should not contain L", c == 'L');
			assertFalse("Should not contain O", c == 'O');
			assertFalse("Should not contain U", c == 'U');
			assertFalse("Should not contain V", c == 'V');
			assertFalse("Should not contain i", c == 'i');
			assertFalse("Should not contain l", c == 'l');
			assertFalse("Should not contain o", c == 'o');
			assertFalse("Should not contain u", c == 'u');
			assertFalse("Should not contain v", c == 'v');
			assertFalse("Should not contain 0", c == '0');
			assertFalse("Should not contain 1", c == '1');
		}
	}

	public void testSetAlphabetNumbers() {
		randPass.setAlphabet(RandPass.NUMBERS_AND_LETTERS_ALPHABET);
		char[] pass = randPass.getPassChars(20);

		for (char c : pass) {
			boolean isValid = (
				(c >= 'A' && c <= 'Z') ||
				(c >= 'a' && c <= 'z') ||
				(c >= '0' && c <= '9')
			);
			assertTrue("Character should be letter or digit", isValid);
		}
	}

	public void testSetAlphabetLettersOnly() {
		randPass.setAlphabet(RandPass.LETTERS_ALPHABET);
		char[] pass = randPass.getPassChars(20);

		for (char c : pass) {
			boolean isValid = (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
			assertTrue("Character should be letter", isValid);
		}
	}

	public void testSetAlphabetLowercaseOnly() {
		randPass.setAlphabet(RandPass.LOWERCASE_LETTERS_ALPHABET);
		char[] pass = randPass.getPassChars(20);

		for (char c : pass) {
			assertTrue("Character should be lowercase", c >= 'a' && c <= 'z');
		}
	}

	public void testSetAlphabetUppercaseOnly() {
		randPass.setAlphabet(RandPass.UPPERCASE_LETTERS_ALPHABET);
		char[] pass = randPass.getPassChars(20);

		for (char c : pass) {
			assertTrue("Character should be uppercase", c >= 'A' && c <= 'Z');
		}
	}

	public void testSetAlphabetSymbols() {
		randPass.setAlphabet(RandPass.SYMBOLS_ALPHABET);
		char[] pass = randPass.getPassChars(20);

		for (char c : pass) {
			boolean isSymbol = false;
			for (char sym : RandPass.SYMBOLS_ALPHABET) {
				if (c == sym) {
					isSymbol = true;
					break;
				}
			}
			assertTrue("Character should be symbol", isSymbol);
		}
	}

	public void testSetAlphabetNullThrows() {
		try {
			randPass.setAlphabet(null);
			fail("Should throw NullPointerException");
		} catch (NullPointerException e) {
			// Expected
		}
	}

	public void testSetAlphabetEmptyThrows() {
		try {
			randPass.setAlphabet(new char[0]);
			fail("Should throw ArrayIndexOutOfBoundsException");
		} catch (ArrayIndexOutOfBoundsException e) {
			// Expected
		}
	}

	public void testSetAlphabetCustom() {
		char[] custom = {'@', '#', '%'};
		randPass.setAlphabet(custom);
		char[] pass = randPass.getPassChars(50);

		for (char c : pass) {
			assertTrue("Character should be @, #, or %",
				c == '@' || c == '#' || c == '%');
		}
	}

	public void testSetRandomGenerator() {
		SecureRandom rand1 = new SecureRandom();
		randPass.setRandomGenerator(rand1);
		char[] pass = randPass.getPassChars(8);
		assertEquals("Password should have correct length", 8, pass.length);
	}

	public void testSetFirstAlphabet() {
		char[] firstAlph = {'A', 'B', 'C'};
		randPass.setFirstAlphabet(firstAlph);
		char[] pass = randPass.getPassChars(10);

		// First character should be from firstAlphabet
		assertTrue("First character should be A, B, or C",
			pass[0] == 'A' || pass[0] == 'B' || pass[0] == 'C');
	}

	public void testSetFirstAlphabetNull() {
		randPass.setFirstAlphabet(null);
		char[] pass = randPass.getPassChars(8);
		assertEquals("Should generate password", 8, pass.length);
	}

	public void testSetFirstAlphabetEmpty() {
		randPass.setFirstAlphabet(new char[0]);
		char[] pass = randPass.getPassChars(8);
		assertEquals("Should generate password with empty first alphabet", 8, pass.length);
	}

	public void testSetLastAlphabet() {
		char[] lastAlph = {'X', 'Y', 'Z'};
		randPass.setLastAlphabet(lastAlph);
		char[] pass = randPass.getPassChars(10);

		// Last character should be from lastAlphabet
		assertTrue("Last character should be X, Y, or Z",
			pass[9] == 'X' || pass[9] == 'Y' || pass[9] == 'Z');
	}

	public void testSetLastAlphabetNull() {
		randPass.setLastAlphabet(null);
		char[] pass = randPass.getPassChars(8);
		assertEquals("Should generate password", 8, pass.length);
	}

	public void testSetLastAlphabetEmpty() {
		randPass.setLastAlphabet(new char[0]);
		char[] pass = randPass.getPassChars(8);
		assertEquals("Should generate password with empty last alphabet", 8, pass.length);
	}

	public void testSetMaxRepetitionZero() {
		randPass.setMaxRepetition(0);
		char[] pass = randPass.getPassChars(10);
		// Should still generate password (repetition is disabled when set to 0)
		assertEquals("Should generate 10 character password", 10, pass.length);
	}

	public void testSetMaxRepetitionOne() {
		randPass.setAlphabet(new char[] {'a', 'b', 'c', 'd', 'e'});
		randPass.setMaxRepetition(1);
		char[] pass = randPass.getPassChars(10);

		// With repetition 1, no character should repeat
		for (int i = 0; i < pass.length - 1; i++) {
			assertFalse("No adjacent characters should be the same",
				pass[i] == pass[i + 1]);
		}
	}

	public void testSetMaxRepetitionPositive() {
		randPass.setMaxRepetition(3);
		char[] pass = randPass.getPassChars(20);
		assertEquals("Should generate password", 20, pass.length);
	}

	public void testAddRequirement() {
		char[] digitAlphabet = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		randPass.setAlphabet(RandPass.NUMBERS_AND_LETTERS_ALPHABET);
		randPass.addRequirement(digitAlphabet, 2);

		// Generate multiple passwords and check that each has at least 2 digits
		for (int trial = 0; trial < 5; trial++) {
			char[] pass = randPass.getPassChars(10);
			int digitCount = 0;
			for (char c : pass) {
				if (c >= '0' && c <= '9') {
					digitCount++;
				}
			}
			assertTrue("Password should have at least 2 digits", digitCount >= 2);
		}
	}

	public void testAddMultipleRequirements() {
		char[] digitAlphabet = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		char[] uppercase = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

		randPass.setAlphabet(RandPass.NUMBERS_AND_LETTERS_ALPHABET);
		randPass.addRequirement(digitAlphabet, 2);
		randPass.addRequirement(uppercase, 2);

		for (int trial = 0; trial < 5; trial++) {
			char[] pass = randPass.getPassChars(12);
			int digitCount = 0;
			int uppercaseCount = 0;

			for (char c : pass) {
				if (c >= '0' && c <= '9') digitCount++;
				if (c >= 'A' && c <= 'Z') uppercaseCount++;
			}

			assertTrue("Should have at least 2 digits", digitCount >= 2);
			assertTrue("Should have at least 2 uppercase", uppercaseCount >= 2);
		}
	}

	public void testAddRequirementWithSingleChar() {
		char[] singleChar = {'X'};
		randPass.addRequirement(singleChar, 1);

		for (int trial = 0; trial < 5; trial++) {
			char[] pass = randPass.getPassChars(10);
			boolean hasX = false;
			for (char c : pass) {
				if (c == 'X') {
					hasX = true;
					break;
				}
			}
			assertTrue("Password should contain X", hasX);
		}
	}

	public void testPasswordsNotNull() {
		char[] pass = randPass.getPassChars(10);
		for (char c : pass) {
			assertNotNull("No character should be null", c);
		}
	}

	public void testPasswordsNotEmpty() {
		String pass = randPass.getPass(10);
		assertTrue("Password should not be empty", pass.length() > 0);
	}

	public void testLongPasswordGeneration() {
		char[] pass = randPass.getPassChars(1000);
		assertEquals("Should generate 1000 character password", 1000, pass.length);
	}

	public void testSingleCharacterPassword() {
		char[] pass = randPass.getPassChars(1);
		assertEquals("Should generate single character", 1, pass.length);
		assertNotNull("Character should not be null", pass[0]);
	}

	public void testPasswordConsistency() {
		// Generate multiple passwords from same instance
		RandPass rp1 = new RandPass();

		// Verify that passwords are generated successfully
		char[] pass1 = rp1.getPassChars(10);
		char[] pass2 = rp1.getPassChars(10);

		assertEquals("Both passwords should have correct length", 10, pass1.length);
		assertEquals("Both passwords should have correct length", 10, pass2.length);
	}

	public void testDifferentRandomInstances() {
		RandPass rp1 = new RandPass();
		RandPass rp2 = new RandPass();

		char[] pass1 = rp1.getPassChars(20);
		char[] pass2 = rp2.getPassChars(20);

		// Verify both are valid, they should likely be different
		assertEquals("First password length", 20, pass1.length);
		assertEquals("Second password length", 20, pass2.length);
	}

	public void testGetPassCharsPreservesBuffer() {
		char[] originalBuffer = new char[10];
		for (int i = 0; i < 10; i++) {
			originalBuffer[i] = ' ';
		}

		char[] result = randPass.getPassChars(originalBuffer);

		// Verify it's the same object
		assertSame("Should be same buffer object", originalBuffer, result);

		// Verify buffer was filled (no spaces should remain from initialization)
		for (char c : result) {
			assertNotSame("Buffer should be filled", ' ', c);
		}
	}

	public void testMultiplePasswordsFromSameInstance() {
		// Generating multiple passwords should work without issues
		for (int i = 0; i < 100; i++) {
			char[] pass = randPass.getPassChars(8);
			assertEquals("Each password should be length 8", 8, pass.length);
		}
	}

	public void testStringPasswords() {
		String pass1 = randPass.getPass(10);
		String pass2 = randPass.getPass(10);

		assertEquals("First password length", 10, pass1.length());
		assertEquals("Second password length", 10, pass2.length());

		// Passwords should likely be different
		assertFalse("Passwords should be different (with high probability)",
			pass1.equals(pass2));
	}

	public void testAlphabetCharacterSetPrintable() {
		randPass.setAlphabet(RandPass.PRINTABLE_ALPHABET);
		char[] pass = randPass.getPassChars(100);

		for (char c : pass) {
			assertTrue("Character should be printable", c >= 33 && c <= 126);
		}
	}

	public void testVersionConstant() {
		assertNotNull("Version should not be null", RandPass.version);
		assertTrue("Version should be non-empty", RandPass.version.length() > 0);
	}

	public void testPredefinedAlphabets() {
		assertNotNull("NUMBERS_AND_LETTERS_ALPHABET should exist",
			RandPass.NUMBERS_AND_LETTERS_ALPHABET);
		assertNotNull("SYMBOLS_ALPHABET should exist",
			RandPass.SYMBOLS_ALPHABET);
		assertNotNull("PRINTABLE_ALPHABET should exist",
			RandPass.PRINTABLE_ALPHABET);
		assertNotNull("LOWERCASE_LETTERS_ALPHABET should exist",
			RandPass.LOWERCASE_LETTERS_ALPHABET);
		assertNotNull("LOWERCASE_LETTERS_AND_NUMBERS_ALPHABET should exist",
			RandPass.LOWERCASE_LETTERS_AND_NUMBERS_ALPHABET);
		assertNotNull("LETTERS_ALPHABET should exist",
			RandPass.LETTERS_ALPHABET);
		assertNotNull("UPPERCASE_LETTERS_ALPHABET should exist",
			RandPass.UPPERCASE_LETTERS_ALPHABET);
		assertNotNull("NONCONFUSING_ALPHABET should exist",
			RandPass.NONCONFUSING_ALPHABET);
	}

	public void testAlphabetsHaveElements() {
		assertTrue("NUMBERS_AND_LETTERS_ALPHABET should have elements",
			RandPass.NUMBERS_AND_LETTERS_ALPHABET.length > 0);
		assertTrue("SYMBOLS_ALPHABET should have elements",
			RandPass.SYMBOLS_ALPHABET.length > 0);
		assertTrue("PRINTABLE_ALPHABET should have elements",
			RandPass.PRINTABLE_ALPHABET.length > 0);
	}
}
