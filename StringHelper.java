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
 * Utilities for String formatting, manipulation, and queries.
 */
public class StringHelper {

    /**
	 * Pad the beginning of the given String with spaces until
     * the String is of the given length.
	 *
	 * @param s String to be padded.
	 * @param length desired length of result.
	 * @return padded String.
	 * @throws NullPointerException if s is null.
	 */
	public static String prepad(String s, int length){
		return prepad(s, length, ' ');
	}

	/**
	 * Prepend the given character to the String until
	 * the result is the desired length.
	 * <p>
	 * If a String is longer than the desired length,
	 * it will not be trunkated, however no padding
	 * will be added.
	 *
	 * @param s String to be padded.
	 * @param length desired length of result.
	 * @param c padding character.
	 * @return padded String.
	 * @throws NullPointerException if s is null.
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
	 * Pad the end of the given String with spaces until
     * the String is of the given length.
	 *
	 * @param s String to be padded.
	 * @param length desired length of result.
	 * @return padded String.
	 * @throws NullPointerException if s is null.
	 */
	public static String postpad(String s, int length){
		return postpad(s, length, ' ');
	}
    
	/**
	 * Append the given character to the String until
	 * the result is  the desired length.
	 * <p>
	 * If a String is longer than the desired length,
	 * it will not be trunkated, however no padding
	 * will be added.
	 *
	 * @param s String to be padded.
	 * @param length desired length of result.
	 * @param c padding character.
	 * @return padded String.
	 * @throws NullPointerException if s is null.
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

	/**
	 * Split the given String into tokens.
	 * <P>
	 * This method is meant to be similar to the split
	 * function in other programming languages but it does
	 * not use regular expressions.  Rather the String is
	 * split on a single String literal.
	 * <P>
	 * Unlike java.util.StringTokenizer which accepts
	 * multiple character tokens as delimiters, the delimiter
	 * here is a single String literal.
	 * <P>
	 * Each null token is returned as an empty String.
	 * Delimiters are never returned as tokens.
	 * <P>
	 * If there is no delimiter because it is either empty or
	 * null, the only element in the result is the original String.
	 * <P>
	 * StringHelper.split("1-2-3", "-");<br>
	 * result: {"1", "2", "3"}<br>
	 * StringHelper.split("-1--2-", "-");<br>
	 * result: {"", "1", ,"", "2", ""}<br>
	 * StringHelper.split("123", "");<br>
	 * result: {"123"}<br>
	 * StringHelper.split("1-2---3----4", "--");<br>
	 * result: {"1-2", "-3", "", "4"}<br>
	 *
	 * @param s String to be split.
	 * @param delimiter String literal on which to split.
	 * @return an array of tokens.
	 * @throws NullPointerException if s is null.
	 */
	public static String[] split(String s, String delimiter){
		int delimiterLength;
		// the next statement has the side effect of throwing a null pointer
		// exception if s is null.
		int stringLength = s.length();
		if (delimiter == null || (delimiterLength = delimiter.length()) == 0){
			// it is not inherently clear what to do if there is no delimiter
			// On one hand it would make sense to return each character because
			// the null String can be found between each pair of characters in
			// a String.  However, it can be found many times there and we don't
			// want to be returning multiple null tokens.
			// returning the whole String will be defined as the correct behavior
			// in this instance.
			return new String[] {s};
		}

		// a two pass solution is used because a one pass solution would
		// require the possible resizing and copying of memory structures
		// In the worst case it would have to be resized n times with each
		// resize having a O(n) copy leading to an O(n^2) algorithm.

		int count;
		int start;
		int end;

		// Scan s and count the tokens.
		count = 0;
		start = 0;
		while((end = s.indexOf(delimiter, start)) != -1) {
			count++;
			start = end + delimiterLength;
		}
		count++;

		// allocate an array to return the tokens,
		// we now know how big it should be
		String[] result = new String[count];

		// Scan s again, but this time pick out the tokens
		count = 0;
		start = 0;
		while((end = s.indexOf(delimiter, start)) != -1) {
			result[count] = (s.substring(start, end));
			count++;
			start = end + delimiterLength;
		}
		end = stringLength;
		result[count] = s.substring(start, end);

		return (result);
	}
    
    /**
	 * Replace occurances of a substring.
	 * 
	 * StringHelper.replace("1-2-3", "-", "|");<br>
	 * result: "1|2|3"<br>
	 * StringHelper.replace("-1--2-", "-", "|");<br>
	 * result: "|1||2|"<br>
	 * StringHelper.replace("123", "", "|");<br>
	 * result: "123"<br>
	 * StringHelper.replace("1-2---3----4", "--", "|");<br>
	 * result: "1-2|-3||4"<br>
     * StringHelper.replace("1-2---3----4", "--", "---");<br>
	 * result: "1-2----3------4"<br>
	 *
	 * @param s String to be modified.
	 * @param find String to find.
     * @param replace String to replace.
	 * @return a string with all the occurances of the string to find replaced.
	 * @throws NullPointerException if s is null.
	 */
	public static String replace(String s, String find, String replace){
		int findLength;
		// the next statement has the side effect of throwing a null pointer
		// exception if s is null.
		int stringLength = s.length();
		if (find == null || (findLength = find.length()) == 0){
			// If there is nothing to find, we won't try and find it.
            return s;
		}
        if (replace == null){
            // a null string and an empty string are the same
            // for replacement purposes.
            replace = "";
        }
        int replaceLength = replace.length();       
        
        // We need to figure out how long our resulting string will be.
        // This is required because without it, the possible resizing 
        // and copying of memory structures could lead to an unacceptable runtime.
		// In the worst case it would have to be resized n times with each
		// resize having a O(n) copy leading to an O(n^2) algorithm.
        int length;
        if (findLength == replaceLength){
            // special case in which we don't need to count the replacements
            // because the count falls out of the length formula.
            length = stringLength;
        } else {
		    int count;
		    int start;
		    int end;

		    // Scan s and count the number of times we find our target.
		    count = 0;
		    start = 0;
		    while((end = s.indexOf(find, start)) != -1) {
			    count++;
			    start = end + findLength;
		    }
            if (count == 0){
                // special case in which on first pass, we find there is nothing
                // to be replaced.  No need to do a second pass or create a string buffer.
                return s;
            }
            length = stringLength - (count * (replaceLength - findLength));
        }
        
        int start = 0;
        int end = s.indexOf(find, start);
        if (end == -1){
            // nothing was found in the string to replace.
            // we can get this if the find and replace strings
            // are the same length because we didn't check before.
            // in this case, we will return the original string  
            return s;
        }
        // it looks like we actually have something to replace
        // *sigh* allocate memory for it.
        StringBuffer sb = new StringBuffer(length);
        
        // Scan s and do the replacements
		while (end != -1) {
            sb.append(s.substring(start, end));
            sb.append(replace);
			start = end + findLength;
            end = s.indexOf(find, start);
		}
		end = stringLength;
		sb.append(s.substring(start, end));

		return (sb.toString());
	}
    
    /** 
     * Replaces characters that may be confused by a HTML
     * parser with their equivalent character entity references.
     * <p>
     * Any data that will appear as text on a web page should
     * be be escaped.  This is especially important for data 
     * that comes from untrusted sources such as internet users.
     * A common mistake in CGI programming is to ask a user for
     * data and then put that data on a web page.  For example:<pre>
     * Server: What is your name?
     * User: &lt;b&gt;Joe&lt;b&gt;
     * Server: Hello <b>Joe</b>, Welcome</pre>
     * If the name is put on the page without checking that it doesn't
     * contain HTML code or without sanitizing that HTML code, the user
     * could reformat the page, insert scripts, and control the the
     * content on your webserver.
     * <p>
     * This method will replace HTML characters such as &gt; with their
     * HTML entity reference (&amp;gt;) so that the html parser will
     * be sure to interpret them as plain text rather than HTML or script.
     * <p>
     * This method should be used for both data to be displayed in text
     * in the html document, and data put in form elements. For example:<br>
     * <code>&lt;html&gt;&lt;body&gt;<i>This in not a &amp;lt;tag&amp;gt; 
     * in HTML</i>&lt;/body&gt;&lt;/html&gt;</code><br>
     * and<br>
     * <code>&lt;form&gt;&lt;input type="hidden" name="date" value="<i>This data could
     * be &amp;quot;malicious&amp;quot;</i>"&gt;&lt;/form&gt;</code><br>
     * In the second example, the form data would be properly be resubmitted
     * to your cgi script in the URLEncoded format:<br>
     * <code><i>This data could be %22malicious%22</i></code>
     * 
     * @param s String to be escaped
     * @return escaped String
	 * @throws NullPointerException if s is null.
     */
    public static String escapeHTML(String s){
        int length = s.length();
        int newLength = length;
        // first check for characters that might
        // be dangerous and calculate a length
        // of the string that has escapes.
        for (int i=0; i<length; i++){
            char c = s.charAt(i);
            switch(c){
                case '\"':
                case '\'':{
                    newLength += 5;
                } break;
                case '&':{
                    newLength += 4;
                } break;   
                case '<':
                case '>':{
                    newLength += 3;
                } break;             
            }
        }
        if (length == newLength){
            // nothing to escape in the string
            return s;
        }
        StringBuffer sb = new StringBuffer(newLength);
        for (int i=0; i<length; i++){
            char c = s.charAt(i);
            switch(c){
                case '\"':{
                    sb.append("&quot;");
                } break;
                case '\'':{
                    sb.append("&apos;");
                } break;
                case '&':{
                    sb.append("&amp;");
                } break; 
                case '<':{
                    sb.append("&lt;");
                } break;
                case '>':{
                    sb.append("&gt;");
                } break;
                default: {
                    sb.append(c);
                }               
            }
        }
        return sb.toString();
    }
    
    /** 
     * Replaces characters that may be confused by an SQL
     * parser with their equivalent escape characters.
     * <p>
     * Any data that will be put in an SQL query should
     * be be escaped.  This is especially important for data 
     * that comes from untrusted sources such as internet users.
     * <p>
     * For example if you had the following SQL query:<br>
     * <code>"SELECT * FROM adresses WHERE name='" + name + "' AND private='N'"</code><br>
     * Without this function a user could give <code>" OR 1=1 OR ''='"</code>
     * as their name causing the query to be:<br>
     * <code>"SELECT * FROM adresses WHERE name='' OR 1=1 OR ''='' AND private='N'"</code><br>
     * which will give all adresses, including private ones.<br>
     * Correct usage would be:<br>
     * <code>"SELECT * FROM adresses WHERE name='" + StringHelper.escapeSQL(name) + "' AND private='N'"</code><br>
     * <p>
     * Another way to avoid this problem is to use a PreparedStatement 
     * with appropriate placeholders.
     * 
     * @param s String to be escaped
     * @return escaped String
	 * @throws NullPointerException if s is null.
     */
    public static String escapeSQL(String s){
        int length = s.length();
        int newLength = length;
        // first check for characters that might
        // be dangerous and calculate a length
        // of the string that has escapes.
        for (int i=0; i<length; i++){
            char c = s.charAt(i);
            switch(c){
                case '\\':
                case '\"':
                case '\'':
                case '0':{
                    newLength += 1;
                } break; 
            }
        }
        if (length == newLength){
            // nothing to escape in the string
            return s;
        }
        StringBuffer sb = new StringBuffer(newLength);
        for (int i=0; i<length; i++){
            char c = s.charAt(i);
            switch(c){                
                case '\\':{
                    sb.append("\\\"");
                } break;
                case '\"':{
                    sb.append("\\\"");
                } break;
                case '\'':{
                    sb.append("\\\'");
                } break;
                case '0':{
                    sb.append("\\0");
                } break; 
                default: {
                    sb.append(c);
                }               
            }
        }
        return sb.toString();
    }
}
