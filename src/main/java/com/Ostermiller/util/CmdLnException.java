/*
 * Copyright (C) 2007 Stephen Ostermiller
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

/**
 * Exception thrown when a command line cannot be parsed.
 *
 * More information about this class and code samples for suggested use are
 * available from <a target="_top" href=
 * "http://ostermiller.org/utils/CmdLn.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.07.00
 */
public class CmdLnException extends IllegalArgumentException
{
	/**
	 * serial version id
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private static final long serialVersionUID = 3984942697362044497L;

	/**
	 * @param message detail message
	 *
	 * @since ostermillerutils 1.07.00
	 */
	CmdLnException(String message) {
		super(message);
	}
}
