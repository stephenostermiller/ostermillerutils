/*
 * Parse CGI query data.
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
import java.util.*;
import java.io.*;

/**
 * Parses query string data from a CGI request into name value pairs.
 * <p>
 * More information about this class is available from <a href=
 * "http://ostermiller.org/utils/CGIParser.html">ostermiller.org</a>.
 */
public class CGIParser{
	protected Hashtable cgi = new Hashtable();

	/**
	 * Extract the name, value pairs from the given input stream and
	 * make them available for retrieval.
	 * <p>
	 * The stream is read until the stream contains no more bytes.
	 * <p>
	 * Byte to character conversion is done according the platforms
	 * default character encoding.
	 *
	 * @param in Stream containing CGI Encoded name value pairs.
	 */
	public CGIParser (InputStream in) throws IOException {
		CGILexer lex = new CGILexer(in);
		parse(lex);
	}

	/**
	 * Extract the name, value pairs from the given reader and
	 * make them available for retrieval.
	 * <p>
	 * The reader is read until the stream contains no more characters.
	 *
	 * @param in Reader containing CGI Encoded name value pairs.
	 */
	public CGIParser (Reader in) throws IOException {
		CGILexer lex = new CGILexer(in);
		parse(lex);
	}

	/**
	 * Extract the name, value pairs from the given string and
	 * make them available for retrieval.
	 *
	 * @param s CGI Encoded name value pairs.
	 */
	public CGIParser (String s) {
		try {
			CGILexer lex = new CGILexer(new StringReader(s));
			parse(lex);
		} catch (IOException x){
			// This shouldn't be able to happen from a string.
		}
	}

	/**
	 * Retrieve the name value pairs from the lexer and hash
	 * them in the cgi hashtable.
	 *
	 * @param lex Lexer that will return the tokens.
	 */
	private void parse(CGILexer lex) throws IOException {
		String name, value;
		Vector values;
		while ((name = lex.nextToken()) != null) {
			value = lex.nextToken();
			values = (Vector)cgi.get(name);
			if (values == null){
				values = new Vector();
			}
			values.add(value);
			cgi.put(name, values);
		}
	}

	/**
	 * Returns an array of String objects containing all of the values the given reques
	 * parameter has, or null if the parameter does not exist.
	 * <p>
	 * If the parameter has a single value, the array has a length of 1.
	 * @param name a String containing the name of the parameter whose value is requested
	 * @return an array of String objects containing the parameter's values
	 */
	public String[] getParameterValues(String name){
		Vector values = (Vector)cgi.get(name);
		if (values == null){
			return null;
		}
		String[] valArray = new String[values.size()];
		return (String[])(values.toArray(valArray));
	}

	/**
	 * Returns the value of a request parameter as a String, or null if the parameter does
	 * not exist. Request parameters are extra information sent with the request.
	 * <p>
	 * You should only use this method when you are sure the parameter has only one value.
	 * If the parameter might have more than one value, use getParameterValues(java.lang.String).
	 * <P>
	 * If you use this method with a multivalued parameter, the value returned is equal to
	 * the first value in the array returned by getParameterValues.
	 *
	 * @param name a String specifying the name of the parameter
	 * @return a String representing the single value of the parameter
	 */
	public String getParameter(String name){
		Vector values = (Vector)cgi.get(name);
		if (values == null){
			return null;
		}
		return (String)(values.elementAt(0));
	}

	/**
	 * Returns an Enumeration of String  objects containing the names of the
	 * parameters contained in this request. If the request has no parameters,
	 * the method returns an empty Enumeration.
	 *
	 * @return an Enumeration of String objects, each String containing the name
	 *     of a request parameter; or an empty Enumeration if the request has
	 *     no parameters
	 */
	public Enumeration getParameterNames(){
		return cgi.keys();
	}
}
