/*
 * Buffer Overflow Exception
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

import java.io.IOException;

/**
 * An indication that there was a buffer overflow.
 */
public class BufferOverflowException extends IOException {

	/**
	 * Create a new Exception
	 */
	public BufferOverflowException(){
		super();
	}

	/**
	 * Create a new Exception with the given message.
	 *
	 * @param msg Error message.
	 */
	public BufferOverflowException(String msg){
		super(msg);
	}
}
