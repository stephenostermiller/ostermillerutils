/*
 * Copyright (C) 2010 Stephen Ostermiller
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
 * See LICENSE.txt for details.
 */
package com.Ostermiller.util;


/**
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.00
 */
class DateTimeToken {

	public enum DateTimeTokenType {
		ERROR,
		NUMBER,
		WORD,
		PUNCTUATION,
		APOS_YEAR,
	}

	private String text;
	/**
	 * @return the text
	 * @since ostermillerutils 1.xx.xx
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 * @since ostermillerutils 1.xx.xx
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the type
	 * @since ostermillerutils 1.xx.xx
	 */
	public DateTimeTokenType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 * @since ostermillerutils 1.xx.xx
	 */
	public void setType(DateTimeTokenType type) {
		this.type = type;
	}

	private DateTimeTokenType type;

	public DateTimeToken(String text, DateTimeTokenType type){
		this.text = text;
		this.type = type;
	}

}
