/*
 * Static String formatting and query routines.
 * Copyright (C) 2001,2002 Stephen Ostermiller <utils@Ostermiller.com>
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
 * More information about this class is available from <a href=
 * "http://ostermiller.org/utils/StringHelper.html">ostermiller.org</a>.
 */
public class StringHelper {

	/**
	 * Pad the beginning of the given String with spaces until
	 * the String is of the given length.
	 * <p>
	 * If a String is longer than the desired length,
	 * it will not be truncated, however no padding
	 * will be added.
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
	 * Pre-pend the given character to the String until
	 * the result is the desired length.
	 * <p>
	 * If a String is longer than the desired length,
	 * it will not be truncated, however no padding
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
	 * <p>
	 * If a String is longer than the desired length,
	 * it will not be truncated, however no padding
	 * will be added.
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
	 * it will not be truncated, however no padding
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
	 * Pad the beginning and end of the given String with spaces until
	 * the String is of the given length.  The result is that the original
	 * String is centered in the middle of the new string.
	 * <p>
	 * If the number of characters to pad is even, then the padding
	 * will be split evenly between the beginning and end, otherwise,
	 * the extra character will be added to the end.
	 * <p>
	 * If a String is longer than the desired length,
	 * it will not be truncated, however no padding
	 * will be added.
	 *
	 * @param s String to be padded.
	 * @param length desired length of result.
	 * @return padded String.
	 * @throws NullPointerException if s is null.
	 */
	public static String midpad(String s, int length){
		return midpad(s, length, ' ');
	}

	/**
	 * Pad the beginning and end of the given String with the given character
	 * until the result is  the desired length.  The result is that the original
	 * String is centered in the middle of the new string.
	 * <p>
	 * If the number of characters to pad is even, then the padding
	 * will be split evenly between the beginning and end, otherwise,
	 * the extra character will be added to the end.
	 * <p>
	 * If a String is longer than the desired length,
	 * it will not be truncated, however no padding
	 * will be added.
	 *
	 * @param s String to be padded.
	 * @param length desired length of result.
	 * @param c padding character.
	 * @return padded String.
	 * @throws NullPointerException if s is null.
	 */
	public static String midpad(String s, int length, char c){
		int needed = length - s.length();
		if (needed <= 0){
			return s;
		}
		int beginning = needed / 2;
		int end = beginning + needed % 2;
		StringBuffer sb = new StringBuffer(length);
		for (int i=0; i<beginning; i++){
			sb.append(c);
		}
		sb.append(s);
		for (int i=0; i<end; i++){
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
			// a String.  However, it can be found many times there and we don'
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
	 * Replace occurrences of a substring.
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
	 * @return a string with all the occurrences of the string to find replaced.
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
	 * that comes from untrusted sources such as Internet users.
	 * A common mistake in CGI programming is to ask a user for
	 * data and then put that data on a web page.  For example:<pre>
	 * Server: What is your name?
	 * User: &lt;b&gt;Joe&lt;b&gt;
	 * Server: Hello <b>Joe</b>, Welcome</pre>
	 * If the name is put on the page without checking that it doesn't
	 * contain HTML code or without sanitizing that HTML code, the user
	 * could reformat the page, insert scripts, and control the the
	 * content on your web server.
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
				case '\"':{
					newLength += 5;
				} break;
				case '&':
				case '\'':{
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
					sb.append("&#39;");
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
	 * that comes from untrusted sources such as Internet users.
	 * <p>
	 * For example if you had the following SQL query:<br>
	 * <code>"SELECT * FROM addresses WHERE name='" + name + "' AND private='N'"</code><br>
	 * Without this function a user could give <code>" OR 1=1 OR ''='"</code>
	 * as their name causing the query to be:<br>
	 * <code>"SELECT * FROM addresses WHERE name='' OR 1=1 OR ''='' AND private='N'"</code><br>
	 * which will give all addresses, including private ones.<br>
	 * Correct usage would be:<br>
	 * <code>"SELECT * FROM addresses WHERE name='" + StringHelper.escapeSQL(name) + "' AND private='N'"</code><br>
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
					sb.append("\\\\");
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

	/**
	 * Replaces characters that are not allowed in a Java style
	 * string literal with their escape characters.  Specifically
	 * quote ("), single quote ('), new line (\n), carriage return (\r),
	 * and backslash (\), and tab (\t) are escaped.
	 *
	 * @param s String to be escaped
	 * @return escaped String
	 * @throws NullPointerException if s is null.
	 */
	public static String escapeJavaLiteral(String s){
		int length = s.length();
		int newLength = length;
		// first check for characters that might
		// be dangerous and calculate a length
		// of the string that has escapes.
		for (int i=0; i<length; i++){
			char c = s.charAt(i);
			switch(c){
				case '\"':
				case '\'':
				case '\n':
				case '\r':
				case '\t':
				case '\\':{
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
				case '\"':{
					sb.append("\\\"");
				} break;
				case '\'':{
					sb.append("\\\'");
				} break;
				case '\n':{
					sb.append("\\n");
				} break;
				case '\r':{
					sb.append("\\r");
				} break;
				case '\t':{
					sb.append("\\t");
				} break;
				case '\\':{
					sb.append("\\\\");
				} break;
				default: {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Trim any of the characters contained in the second
	 * string from the beginning and end of the first.
	 *
	 * @param s String to be trimmed.
	 * @param c list of characters to trim from s.
	 * @return trimmed String.
	 * @throws NullPointerException if s is null.
	 */
	public static String trim(String s, String c){
		int length = s.length();
		if (c == null){
			return s;
		}
		int cLength = c.length();
		if (c.length() == 0){
			return s;
		}
		int start = 0;
		int end = length;
		boolean found; // trim-able character found.
		int i;
		// Start from the beginning and find the
		// first non-trim-able character.
		found = false;
		for (i=0; !found && i<length; i++){
			char ch = s.charAt(i);
			found = true;
			for (int j=0; found && j<cLength; j++){
				if (c.charAt(j) == ch) found = false;
			}
		}
		// if all characters are trim-able.
		if (!found) return "";
		start = i-1;
		// Start from the end and find the
		// last non-trim-able character.
		found = false;
		for (i=length-1; !found && i>=0; i--){
			char ch = s.charAt(i);
			found = true;
			for (int j=0; found && j<cLength; j++){
				if (c.charAt(j) == ch) found = false;
			}
		}
		end = i+2;
		return s.substring(start, end);
	}
}
