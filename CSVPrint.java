/*
 * Write files in comma separated value format.
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
 * Print values as a comma separated list
 * More information about this class is available from <a href=
 * "http://ostermiller.org/utils/CSVLexer.html">ostermiller.org</a>.
 * This interface is designed to be set of general methods that all
 * CSV parsers should implement.
 */
public interface CSVPrint {

    /**
	 * Print the string as the last value on the line.	The value
	 * will be quoted if needed.
	 *
	 * @param value value to be outputted.
	 */
	public void println(String value);
    
    /**
	 * Output a blank line
	 */
	public void println();
    
    /**
	 * Print a single line of comma separated values.
	 * The values will be quoted if needed.  Quotes and
	 * newLine characters will be escaped.
	 *
	 * @param values values to be outputted.
	 */
	public void println(String[] values);
    
    /**
	 * Print the string as the next value on the line.	The value
	 * will be quoted if needed.
	 *
	 * @param value value to be outputted.
	 */
	public void print(String value);
    
}
