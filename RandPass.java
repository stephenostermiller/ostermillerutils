/*
 * Generate random passwords.
 * Copyright (C) 2001 Stephen Ostermiller <utils@Ostermiller.com>
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

import java.security.SecureRandom;

/** 
 * Generates a random String using a cryptographically 
 * secure random number generator.
 * <p>
 * The alphabet (characters used in the passwords generated)
 * may be specified, and the random number generator can be
 * externally supplied.
 * <p>
 * More information about this class is available from <a href=
 * "http://ostermiller.org/utils/RandPass.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 */
public class RandPass {

    /** 
     * Default length for passwords
     */
    private static final int DEFAULT_PASSWORD_LENGTH = 8;
    
    /**
     * Alphabet consisting of upper and lowercase letters A-Z and
     * the digits 0-9.
     */
    public static final char[] NUMBERS_AND_LETTERS_ALPHABET = { 
        'A','B','C','D','E','F','G','H',
		'I','J','K','L','M','N','O','P',
		'Q','R','S','T','U','V','W','X',
		'Y','Z','a','b','c','d','e','f',
		'g','h','i','j','k','l','m','n',
		'o','p','q','r','s','t','u','v',
		'w','x','y','z','0','1','2','3',
		'4','5','6','7','8','9',
	};
    
    /**
     * Alphabet consisting of the lowercase letters A-Z.
     */
    public static final char[] LOWERCASE_LETTERS_ALPHABET = { 
        'a','b','c','d','e','f','g','h',
        'i','j','k','l','m','n','o','p',
        'q','r','s','t','u','v','w','x',
        'y','z',
	};
    
    /**
     * Alphabet consisting of the lowercase letters A-Z and
     * the digits 0-9.
     */
    public static final char[] LOWERCASE_LETTERS_AND_NUMBERS_ALPHABET = { 
        'a','b','c','d','e','f','g','h',
        'i','j','k','l','m','n','o','p',
        'q','r','s','t','u','v','w','x',
        'y','z','0','1','2','3','4','5',
        '6','7','8','9',
	};
    
    /**
     * Alphabet consisting of upper and lowercase letters A-Z.
     */
    public static final char[] LETTERS_ALPHABET = { 
        'A','B','C','D','E','F','G','H',
		'I','J','K','L','M','N','O','P',
		'Q','R','S','T','U','V','W','X',
		'Y','Z','a','b','c','d','e','f',
		'g','h','i','j','k','l','m','n',
		'o','p','q','r','s','t','u','v',
		'w','x','y','z',
	};
    
    /**
     * Alphabet consisting of the upper letters A-Z.
     */
    public static final char[] UPPERCASE_LETTERS_ALPHABET = { 
        'A','B','C','D','E','F','G','H',
        'I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X',
        'Y','Z',
	};
    
    /**
     * Alphabet consisting of upper and lowercase letters A-Z and
     * the digits 0-9 but with characters that are often mistaken
     * for each other when typed removed. (I,L,O,U,V,i,l,o,u,v,0,1)
     */
    public static final char[] NONCONFUSING_ALPHABET = { 
        'A','B','C','D','E','F','G','H',
		'J','K','M','N','P','Q','R','S',
        'T','W','X','Y','Z','a','b','c',
        'd','e','f','g','h','j','k','m',
        'n','p','q','r','s','t','w','x',
        'y','z','2','3','4','5','6','7',
        '8','9',
	};
    
    /**
     * Random number generator used.
     */
    protected SecureRandom rand;
    
    /**
     * Set of characters which may be
     * used in the generated passwords.
     */
    protected char[] alphabet;
    
    /**
     * Create a new random password generator
     * with the default secure random number generator
     * and default NONCONFUSING alphabet.
     */ 
    public RandPass(){
        this(new SecureRandom(), NONCONFUSING_ALPHABET);
    }
    
    /**
     * Create a new random password generator
     * with the given secure random number generator
     * and default NONCONFUSING alphabet.
     *
     * @param rand Secure random number generator to use when generating passwords.
     */
    public RandPass(SecureRandom rand){
        this(rand, NONCONFUSING_ALPHABET);
    }
    
    /**
     * Create a new random password generator
     * with the default secure random number generator
     * and given alphabet.
     *
     * @param alphabet Characters allowed in generated passwords.
     */
    public RandPass(char[] alphabet){
        this(new SecureRandom(), alphabet);
    }
    
    /**
     * Create a new random password generator
     * with the given secure random number generator
     * and given alphabet.
     *
     * @param rand Secure random number generator to use when generating passwords.
     * @param alphabet Characters allowed in generated passwords.
     */
    public RandPass(SecureRandom rand, char[] alphabet){
        this.rand = rand;
        this.alphabet = alphabet;
    }
    
    /** 
     * Generate a random password of the default length and
     * with the default alphabet and display it on standard
     * output.
     *
     * @param args Command line arguments - ignored.
     */ 
    public static void main(String[] args){
        RandPass randPass = new RandPass();
        System.out.println(randPass.getPass());
    }
    
    /** 
     * Set the alphabet used by this random password generator.
     *
     * @param alphabet Characters allowed in generated passwords.
     */
    public void setAlphabet(char[] alphabet){
        this.alphabet = alphabet;
    }
    
    /** 
     * Set the random number generator used by this random password generator.
     *
     * @param rand Secure random number generator to use when generating passwords.
     */
    public void setRandomGenerator(SecureRandom rand){
        this.rand = rand;
    }
    
    /** 
     * Fill the given buffer with random characters.
     * <p>
     * Using this method, the password character array can easily
     * be reused for efficiency, or overwritten with new random
     * characters for security.
     * <p>
     * NOTE: If it is possible for a hacker to examine memory to find passwords,
     * the password should be overwritten in memory as soon as possible after it
     * is no longer in use.
     *
     * @param pass buffer that will hold the password.
     * @return the buffer, filled with random characters.
     */
    public char[] getPassChars(char[] pass){
        for (int i=0; i<pass.length; i++){
            pass[i] = alphabet[Math.abs(rand.nextInt() % alphabet.length)]; 
        }
        return(pass);
    }
    
    /** 
     * Generate a random password of the given length.
     * <p>
     * NOTE: If it is possible for a hacker to examine memory to find passwords,
     * the password should be overwritten in memory as soon as possible after it
     * is no longer in use.
     *
     * @param length The desired length of the generated password.
     * @return a random password
     */
    public char[] getPassChars(int length){
        return(getPassChars(new char[length]));
    }
    
    /** 
     * Generate a random password of the default length (8).
     * <p>
     * NOTE: If it is possible for a hacker to examine memory to find passwords,
     * the password should be overwritten in memory as soon as possible after it
     * is no longer in use.
     *
     * @param length The desired length of the generated password.
     * @return a random password
     */
    public char[] getPassChars(){
         return(getPassChars(DEFAULT_PASSWORD_LENGTH));
    }
    
    /** 
     * Generate a random password of the given length.
     * <p>
     * NOTE: Strings can not be modified.  If it is possible
     * for a hacker to examine memory to find passwords, getPassChars()
     * should be used so that the password can be zeroed out of memory
     * when no longer in use.
     *
     * @param length The desired length of the generated password.
     * @return a random password
     * @see #getPassChars(int)
     */
    public String getPass(int length){
        StringBuffer pass = new StringBuffer(length);
        for (int i=0; i<length; i++){
            pass.append(alphabet[Math.abs(rand.nextInt() % alphabet.length)]); 
        }
        return(pass.toString());
    }
    
    /** 
     * Generate a random password of the default length (8).
     * <p>
     * NOTE: Strings can not be modified.  If it is possible
     * for a hacker to examine memory to find passwords, getPassChars()
     * should be used so that the password can be zeroed out of memory
     * when no longer in use.
     *
     * @return a random password     
     * @see #getPassChars()
     */
    public String getPass(){
        return(getPass(DEFAULT_PASSWORD_LENGTH));
    }
}
