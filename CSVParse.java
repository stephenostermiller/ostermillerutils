/*
 * Read files in comma separated value format.
 * Copyright (C) 2002 Stephen Ostermiller <utils@Ostermiller.com>
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
 * More information about this class is available from <a href=
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
	 *
	 * @return all the values from the line or null if there are no more values.
     * @throws IOException if an error occurs while reading
	 */
	public String[] getLine() throws IOException;
    
    /**
	 * Get the line number that the last token came from.
	 *
	 * @return line number or -1 if no tokens have been returned yet.
	 */
	public int getLastLineNumber();
    
}
