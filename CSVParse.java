/*
 * Read files in comma separated value format.
 * Copyright (C) 2002 Stephen Ostermiller
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

import java.io.*;

/**
 * Read files in comma separated value format.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/CSVLexer.html">ostermiller.org</a>.
 * This interface is designed to be set of general methods that all
 * CSV parsers should implement.
 */
public interface CSVParse {

	/**
	 * get the next value.
	 *
	 * @return the next value or null if there are no more values.
	 * @throws IOException if an error occurs while reading
	 */
	public String nextValue() throws IOException;

	/**
	 * Get the line number that the last token came from.
	 *
	 * @return line number or -1 if no tokens have been returned yet.
	 */
	public int lastLineNumber();

	/**
	 * Get all the values from a line.
	 * <p>
	 * If the line has already been partially read, only the
	 * values that have not already been read will be included.
	 *
	 * @return all the values from the line or null if there are no more values.
	 * @throws IOException if an error occurs while reading
	 */
	public String[] getLine() throws IOException;

	/**
	 * Get the line number that the last token came from.
	 * <p>
	 * New line breaks that occur in the middle of a token are no
	 * counted in the line number count.
	 *
	 * @return line number or -1 if no tokens have been returned yet.
	 */
	public int getLastLineNumber();

	/**
	 * Get all the values from the file.
	 * <p>
	 * If the file has already been partially read, only the
	 * values that have not already been read will be included.
	 * <p>
	 * Each line of the file that has at least one value will be
	 * represented.  Comments and empty lines are ignored.
	 * <p>
	 * The resulting double array may be jagged.
	 *
	 * @return all the values from the file or null if there are no more values.
	 * @throws IOException if an error occurs while reading
	 */
	public String[][] getAllValues() throws IOException;
}
