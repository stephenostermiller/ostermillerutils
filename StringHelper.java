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

import java.util.HashMap;

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

	private static HashMap htmlEntities = new HashMap();
	static {
		htmlEntities.put("nbsp", "160");
		htmlEntities.put("iexcl", "161");
		htmlEntities.put("cent", "162");
		htmlEntities.put("pound", "163");
		htmlEntities.put("curren", "164");
		htmlEntities.put("yen", "165");
		htmlEntities.put("brvbar", "166");
		htmlEntities.put("sect", "167");
		htmlEntities.put("uml", "168");
		htmlEntities.put("copy", "169");
		htmlEntities.put("ordf", "170");
		htmlEntities.put("laquo", "171");
		htmlEntities.put("not", "172");
		htmlEntities.put("shy", "173");
		htmlEntities.put("reg", "174");
		htmlEntities.put("macr", "175");
		htmlEntities.put("deg", "176");
		htmlEntities.put("plusmn", "177");
		htmlEntities.put("sup2", "178");
		htmlEntities.put("sup3", "179");
		htmlEntities.put("acute", "180");
		htmlEntities.put("micro", "181");
		htmlEntities.put("para", "182");
		htmlEntities.put("middot", "183");
		htmlEntities.put("cedil", "184");
		htmlEntities.put("sup1", "185");
		htmlEntities.put("ordm", "186");
		htmlEntities.put("raquo", "187");
		htmlEntities.put("frac14", "188");
		htmlEntities.put("frac12", "189");
		htmlEntities.put("frac34", "190");
		htmlEntities.put("iquest", "191");
		htmlEntities.put("Agrave", "192");
		htmlEntities.put("Aacute", "193");
		htmlEntities.put("Acirc", "194");
		htmlEntities.put("Atilde", "195");
		htmlEntities.put("Auml", "196");
		htmlEntities.put("Aring", "197");
		htmlEntities.put("AElig", "198");
		htmlEntities.put("Ccedil", "199");
		htmlEntities.put("Egrave", "200");
		htmlEntities.put("Eacute", "201");
		htmlEntities.put("Ecirc", "202");
		htmlEntities.put("Euml", "203");
		htmlEntities.put("Igrave", "204");
		htmlEntities.put("Iacute", "205");
		htmlEntities.put("Icirc", "206");
		htmlEntities.put("Iuml", "207");
		htmlEntities.put("ETH", "208");
		htmlEntities.put("Ntilde", "209");
		htmlEntities.put("Ograve", "210");
		htmlEntities.put("Oacute", "211");
		htmlEntities.put("Ocirc", "212");
		htmlEntities.put("Otilde", "213");
		htmlEntities.put("Ouml", "214");
		htmlEntities.put("times", "215");
		htmlEntities.put("Oslash", "216");
		htmlEntities.put("Ugrave", "217");
		htmlEntities.put("Uacute", "218");
		htmlEntities.put("Ucirc", "219");
		htmlEntities.put("Uuml", "220");
		htmlEntities.put("Yacute", "221");
		htmlEntities.put("THORN", "222");
		htmlEntities.put("szlig", "223");
		htmlEntities.put("agrave", "224");
		htmlEntities.put("aacute", "225");
		htmlEntities.put("acirc", "226");
		htmlEntities.put("atilde", "227");
		htmlEntities.put("auml", "228");
		htmlEntities.put("aring", "229");
		htmlEntities.put("aelig", "230");
		htmlEntities.put("ccedil", "231");
		htmlEntities.put("egrave", "232");
		htmlEntities.put("eacute", "233");
		htmlEntities.put("ecirc", "234");
		htmlEntities.put("euml", "235");
		htmlEntities.put("igrave", "236");
		htmlEntities.put("iacute", "237");
		htmlEntities.put("icirc", "238");
		htmlEntities.put("iuml", "239");
		htmlEntities.put("eth", "240");
		htmlEntities.put("ntilde", "241");
		htmlEntities.put("ograve", "242");
		htmlEntities.put("oacute", "243");
		htmlEntities.put("ocirc", "244");
		htmlEntities.put("otilde", "245");
		htmlEntities.put("ouml", "246");
		htmlEntities.put("divide", "247");
		htmlEntities.put("oslash", "248");
		htmlEntities.put("ugrave", "249");
		htmlEntities.put("uacute", "250");
		htmlEntities.put("ucirc", "251");
		htmlEntities.put("uuml", "252");
		htmlEntities.put("yacute", "253");
		htmlEntities.put("thorn", "254");
		htmlEntities.put("yuml", "255");
		htmlEntities.put("fnof", "402");
		htmlEntities.put("Alpha", "913");
		htmlEntities.put("Beta", "914");
		htmlEntities.put("Gamma", "915");
		htmlEntities.put("Delta", "916");
		htmlEntities.put("Epsilon", "917");
		htmlEntities.put("Zeta", "918");
		htmlEntities.put("Eta", "919");
		htmlEntities.put("Theta", "920");
		htmlEntities.put("Iota", "921");
		htmlEntities.put("Kappa", "922");
		htmlEntities.put("Lambda", "923");
		htmlEntities.put("Mu", "924");
		htmlEntities.put("Nu", "925");
		htmlEntities.put("Xi", "926");
		htmlEntities.put("Omicron", "927");
		htmlEntities.put("Pi", "928");
		htmlEntities.put("Rho", "929");
		htmlEntities.put("Sigma", "931");
		htmlEntities.put("Tau", "932");
		htmlEntities.put("Upsilon", "933");
		htmlEntities.put("Phi", "934");
		htmlEntities.put("Chi", "935");
		htmlEntities.put("Psi", "936");
		htmlEntities.put("Omega", "937");
		htmlEntities.put("alpha", "945");
		htmlEntities.put("beta", "946");
		htmlEntities.put("gamma", "947");
		htmlEntities.put("delta", "948");
		htmlEntities.put("epsilon", "949");
		htmlEntities.put("zeta", "950");
		htmlEntities.put("eta", "951");
		htmlEntities.put("theta", "952");
		htmlEntities.put("iota", "953");
		htmlEntities.put("kappa", "954");
		htmlEntities.put("lambda", "955");
		htmlEntities.put("mu", "956");
		htmlEntities.put("nu", "957");
		htmlEntities.put("xi", "958");
		htmlEntities.put("omicron", "959");
		htmlEntities.put("pi", "960");
		htmlEntities.put("rho", "961");
		htmlEntities.put("sigmaf", "962");
		htmlEntities.put("sigma", "963");
		htmlEntities.put("tau", "964");
		htmlEntities.put("upsilon", "965");
		htmlEntities.put("phi", "966");
		htmlEntities.put("chi", "967");
		htmlEntities.put("psi", "968");
		htmlEntities.put("omega", "969");
		htmlEntities.put("thetasym", "977");
		htmlEntities.put("upsih", "978");
		htmlEntities.put("piv", "982");
		htmlEntities.put("bull", "8226");
		htmlEntities.put("hellip", "8230");
		htmlEntities.put("prime", "8242");
		htmlEntities.put("Prime", "8243");
		htmlEntities.put("oline", "8254");
		htmlEntities.put("frasl", "8260");
		htmlEntities.put("weierp", "8472");
		htmlEntities.put("image", "8465");
		htmlEntities.put("real", "8476");
		htmlEntities.put("trade", "8482");
		htmlEntities.put("alefsym", "8501");
		htmlEntities.put("larr", "8592");
		htmlEntities.put("uarr", "8593");
		htmlEntities.put("rarr", "8594");
		htmlEntities.put("darr", "8595");
		htmlEntities.put("harr", "8596");
		htmlEntities.put("crarr", "8629");
		htmlEntities.put("lArr", "8656");
		htmlEntities.put("uArr", "8657");
		htmlEntities.put("rArr", "8658");
		htmlEntities.put("dArr", "8659");
		htmlEntities.put("hArr", "8660");
		htmlEntities.put("forall", "8704");
		htmlEntities.put("part", "8706");
		htmlEntities.put("exist", "8707");
		htmlEntities.put("empty", "8709");
		htmlEntities.put("nabla", "8711");
		htmlEntities.put("isin", "8712");
		htmlEntities.put("notin", "8713");
		htmlEntities.put("ni", "8715");
		htmlEntities.put("prod", "8719");
		htmlEntities.put("sum", "8721");
		htmlEntities.put("minus", "8722");
		htmlEntities.put("lowast", "8727");
		htmlEntities.put("radic", "8730");
		htmlEntities.put("prop", "8733");
		htmlEntities.put("infin", "8734");
		htmlEntities.put("ang", "8736");
		htmlEntities.put("and", "8743");
		htmlEntities.put("or", "8744");
		htmlEntities.put("cap", "8745");
		htmlEntities.put("cup", "8746");
		htmlEntities.put("int", "8747");
		htmlEntities.put("there4", "8756");
		htmlEntities.put("sim", "8764");
		htmlEntities.put("cong", "8773");
		htmlEntities.put("asymp", "8776");
		htmlEntities.put("ne", "8800");
		htmlEntities.put("equiv", "8801");
		htmlEntities.put("le", "8804");
		htmlEntities.put("ge", "8805");
		htmlEntities.put("sub", "8834");
		htmlEntities.put("sup", "8835");
		htmlEntities.put("nsub", "8836");
		htmlEntities.put("sube", "8838");
		htmlEntities.put("supe", "8839");
		htmlEntities.put("oplus", "8853");
		htmlEntities.put("otimes", "8855");
		htmlEntities.put("perp", "8869");
		htmlEntities.put("sdot", "8901");
		htmlEntities.put("lceil", "8968");
		htmlEntities.put("rceil", "8969");
		htmlEntities.put("lfloor", "8970");
		htmlEntities.put("rfloor", "8971");
		htmlEntities.put("lang", "9001");
		htmlEntities.put("rang", "9002");
		htmlEntities.put("loz", "9674");
		htmlEntities.put("spades", "9824");
		htmlEntities.put("clubs", "9827");
		htmlEntities.put("hearts", "9829");
		htmlEntities.put("diams", "9830");
		htmlEntities.put("quot", "34");
		htmlEntities.put("amp", "38");
		htmlEntities.put("lt", "60");
		htmlEntities.put("gt", "62");
		htmlEntities.put("OElig", "338");
		htmlEntities.put("oelig", "339");
		htmlEntities.put("Scaron", "352");
		htmlEntities.put("scaron", "353");
		htmlEntities.put("Yuml", "376");
		htmlEntities.put("circ", "710");
		htmlEntities.put("tilde", "732");
		htmlEntities.put("ensp", "8194");
		htmlEntities.put("emsp", "8195");
		htmlEntities.put("thinsp", "8201");
		htmlEntities.put("zwnj", "8204");
		htmlEntities.put("zwj", "8205");
		htmlEntities.put("lrm", "8206");
		htmlEntities.put("rlm", "8207");
		htmlEntities.put("ndash", "8211");
		htmlEntities.put("mdash", "8212");
		htmlEntities.put("lsquo", "8216");
		htmlEntities.put("rsquo", "8217");
		htmlEntities.put("sbquo", "8218");
		htmlEntities.put("ldquo", "8220");
		htmlEntities.put("rdquo", "8221");
		htmlEntities.put("bdquo", "8222");
		htmlEntities.put("dagger", "8224");
		htmlEntities.put("Dagger", "8225");
		htmlEntities.put("permil", "8240");
		htmlEntities.put("lsaquo", "8249");
		htmlEntities.put("rsaquo", "8250");
		htmlEntities.put("euro", "8364");
	}
	public static String unescapeHTML(String s){
		StringBuffer result = new StringBuffer(s.length());
		int ampInd = s.indexOf("&");
		int lastEnd = 0;
		while (ampInd >= 0){
			int nextAmp = s.indexOf("&", ampInd+1);
			int nextSemi = s.indexOf(";", ampInd+1);
			if (nextSemi != -1 && (nextAmp == -1 || nextSemi < nextAmp)){
				int value = -1;
				String escape = s.substring(ampInd+1,nextSemi);
				try {
					if (escape.startsWith("#")){
						value = Integer.parseInt(escape.substring(1), 10);
					} else {
						if (htmlEntities.containsKey(escape)){
							value = Integer.parseInt((String)(htmlEntities.get(escape)), 10);
						}
					}
				} catch (NumberFormatException x){
				}
				result.append(s.substring(lastEnd, ampInd));
				lastEnd = nextSemi + 1;
				if (value >= 0 && value <= 0xffff){
					result.append((char)value);
				} else {
					result.append("&").append(escape).append(";");
				}
			}
			ampInd = nextAmp;
		}
		result.append(s.substring(lastEnd));
		return result.toString();
	}
}
