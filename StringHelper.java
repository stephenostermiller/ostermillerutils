/*
 * Static String formatting and query routines.
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

/** 
 * Utilities for String formatting and queries.
 */
public class StringHelper {

    /**
     *
     */
    public static void main(String[] args){
        for (int i=0; i<args.length; i++){
            System.out.println(prepad(args[i], 10, '*'));
            System.out.println(postpad(args[i], 10, '*'));            
        }
    }
    
    /**
     * Prepend the given character to string until
     * the result is long enough.
     *
     * If a string is too long, it will not be trunkated,
     * however no padding will be added.
     *
     * @param s String to be padded.
     * @param length desired length of result.
     * @param c padding character
     * @return padded string.
     */
    public static String prepad(String s, int length, char c){
        int needed = length - s.length();
        if (needed <= 0){
            return s;
        }
        StringBuffer sb = new StringBuffer(length);
        for (int i=0; i<needed; i++){
            sb.append(c);
        }
        sb.append(s);
        return (sb.toString());
    }
    /**
     * Append the given character to string until
     * the result is long enough.
     *
     * If a string is too long, it will not be trunkated,
     * however no padding will be added.
     *
     * @param s String to be padded.
     * @param length desired length of result.
     * @param c padding character
     * @return padded string.
     */
    public static String postpad(String s, int length, char c){
        int needed = length - s.length();
        if (needed <= 0){
            return s;
        }
        StringBuffer sb = new StringBuffer(length);        
        sb.append(s);
        for (int i=0; i<needed; i++){
            sb.append(c);
        }
        return (sb.toString());
    }
}
