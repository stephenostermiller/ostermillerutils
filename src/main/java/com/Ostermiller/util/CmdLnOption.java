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

import java.util.*;

/**
 * A command line option used by the CommandLineOptions parser.
 *
 * More information about this class and code samples for suggested use are
 * available from <a target="_top" href=
 * "http://ostermiller.org/utils/CmdLn.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.07.00
 */
public final class CmdLnOption {

	/**
	 * The list of long names that specify this option.
	 * May be null, but here must be at least one long
	 * or short name.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private List<String> longNames;

	/**
	 * The list of short names that specify this option.
	 * May be null, but here must be at least one long
	 * or short name.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private List<Character> shortNames;

	/**
	 * The minimum number of arguments that this
	 * option must have.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private int minArguments = 0;

	/**
	 * The maximum number of arguments that this
	 * option may have.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private int maxArguments = 0;

	/**
	 * Whether public setters can be called on this option.
	 * This option becomes immutable once it is used
	 * by a command line.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private boolean mutable = true;

	/**
	 * The description of this object for use in the help
	 * message (may be null).
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private String description = null;

	/**
	 * Listener associated with this object that should
	 * be called when a result is found (may be null).
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private CmdLnListener callback;

	/**
	 * @param longNames list long names for this option
	 * @throws IllegalArgumentException if the the list does not contain at least one long name
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption(String[] longNames){
		this(longNames, null);
	}

	/**
	 * @param shortNames list short names for this option
	 * @throws IllegalArgumentException if the the list does not contain at least one short name
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption(char[] shortNames){
		this(null, shortNames);
	}

	/**
	 * @param longName the long name for this option
	 * @throws IllegalArgumentException if the name is null
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption(String longName){
		this(longName, null);
	}

	/**
	 * @param shortName the short name for this option
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption(Character shortName){
		this(null, shortName);
	}

	/**
	 * @param longNames list long names for this option
	 * @param shortNames list short names for this option
	 * @throws IllegalArgumentException if the the lists do not contain at least one name
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption(String[] longNames, char[] shortNames){

		if ((longNames == null || longNames.length == 0) && (shortNames == null || shortNames.length == 0)){
			throw new IllegalArgumentException("At least one long name or short name must be specified");
		}

		if (longNames == null){
			this.longNames = new ArrayList<String>(0);
		} else {
			this.longNames = new ArrayList<String>(longNames.length);
			addLongNames(longNames);
		}

		if (shortNames == null){
			this.shortNames = new ArrayList<Character>(0);
		} else {
			this.shortNames = new ArrayList<Character>(shortNames.length);
			addShortNames(shortNames);
		}
	}

	/**
	 * @param longName the long name for this option
	 * @param shortName the short name for this option
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption(String longName, Character shortName){

		if (longName == null  && shortName == null){
			throw new IllegalArgumentException("A long name or short name must be specified");
		}


		if (longName == null){
			this.longNames = new ArrayList<String>(0);
		} else {
			this.longNames = new ArrayList<String>(1);
			addLongName(longName);
		}

		if (shortName == null){
			this.shortNames = new ArrayList<Character>(0);
		} else {
			this.shortNames = new ArrayList<Character>(1);
			addShortName(shortName);
		}
	}

	/**
	 * Called by the command line options parser
	 * to set this option to not modifiable.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	void setImmutable(){
		if (!mutable){
			throw new IllegalStateException("Command line argument already immutable (Used by more than one CommandLineOption?)");
		}
		mutable = false;
	}

	/**
	 * Throw an exception if no longer mutable
	 *
	 * @throws IllegalStateException if not mutable
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private void checkState(){
		if (!mutable){
			throw new IllegalStateException("option no longer modifiable");
		}
	}

	/**
	 * Sets the argument bounds to require no arguments
	 * (zero arguments minimum, zero arguments maximum).
	 * This is the default state for a new command line option.
	 *
	 * @throws IllegalStateException if this argument has already been used in parsing CommandLineOptions
	 * @return this command line option for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption setNoArguments(){
		setArgumentBounds(0, 0);
		return this;
	}

	/**
	 * Sets the argument bounds for a single optional argument
	 * (zero arguments minimum, one argument maximum).
	 *
	 * @throws IllegalStateException if this argument has already been used in parsing CommandLineOptions
	 * @return this command line option for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption setOptionalArgument(){
		setArgumentBounds(0, 1);
		return this;
	}

	/**
	 * Sets the argument bounds for a single required argument
	 * (one argument minimum, one argument maximum).
	 *
	 * @throws IllegalStateException if this argument has already been used in parsing CommandLineOptions
	 * @return this command line option for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption setRequiredArgument(){
		setArgumentBounds(1, 1);
		return this;
	}

	/**
	 * Sets the argument bounds for unlimited (but optional) arguments
	 * (zero arguments minimum, Integer.MAX_VALUE arguments maximum).
	 *
	 * @throws IllegalStateException if this argument has already been used in parsing CommandLineOptions
	 * @return this command line option for method chaining
	 */
	public CmdLnOption setUnlimitedArguments(){
		setArgumentBounds(0, Integer.MAX_VALUE);
		return this;
	}

	/**
	 * Sets the bounds for command line arguments.
	 *
	 * @param minArguments the minimum number of arguments this command line option should expect
	 * @param maxArguments the maximum number of arguments this command line option will accept
	 * @throws IllegalArgumentException if minimum arguments is negative
	 * @throws IllegalArgumentException if maximum arguments is less than minimum arguments
	 * @throws IllegalStateException if this argument has already been used in parsing CommandLineOptions
	 * @return this command line option for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption setArgumentBounds(int minArguments, int maxArguments){
		checkState();
		if (minArguments < 0) throw new IllegalArgumentException("min arguments cannot be negative");
		if (maxArguments < minArguments) throw new IllegalArgumentException("max arguments cannot be less than min arguments");
		this.minArguments = minArguments;
		this.maxArguments = maxArguments;
		return this;
	}

	/**
	 * @param longNames long names to be added
	 * @return this for method chaining
	 * @throws IllegalArgumentException if the name is null or blank
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption addLongNames(Collection<String> longNames){
		checkState();
		for(String name: longNames){
			addLongName(name);
		}
		return this;
	}

	/**
	 * @param longNames long names to be added
	 * @return this for method chaining
	 * @throws IllegalArgumentException if the name is null or blank
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption addLongNames(String[] longNames){
		checkState();
		for(String name: longNames){
			addLongName(name);
		}
		return this;
	}

	/**
	 * @param name long name to be added
	 * @return this for method chaining
	 * @throws IllegalArgumentException if the name is null or blank
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption addLongName(String name){
		checkState();
		if (name == null){
			throw new IllegalArgumentException("long name cannot be null");
		}
		if ("".equals(name)){
			throw new IllegalArgumentException("long name cannot be blank");
		}
		longNames.add(name);
		return this;
	}

	/**
	 * @param shortNames short names to be added
	 * @return this for method chaining
	 * @throws IllegalArgumentException if the name is null or blank
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption addShortNames(Collection<Character> shortNames){
		checkState();
		for(Character name: shortNames){
			addShortName(name);
		}
		return this;
	}

	/**
	 * @param shortNames short names to be added
	 * @return this for method chaining
	 * @throws IllegalArgumentException if the name is null or blank
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption addShortNames(char[] shortNames){
		checkState();
		for(char name: shortNames){
			addShortName(name);
		}
		return this;
	}

	/**
	 * @param shortNames short names to be added
	 * @return this for method chaining
	 * @throws IllegalArgumentException if the name is null or blank
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption addShortNames(Character[] shortNames){
		checkState();
		for(char name: shortNames){
			addShortName(name);
		}
		return this;
	}

	/**
	 * @param name short name to be added
	 * @return this for method chaining
	 * @throws IllegalArgumentException if the name is null or blank
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption addShortName(Character name){
		checkState();
		if (name == null){
			throw new IllegalArgumentException("short name cannot be null");
		}
		if ("".equals(name)){
			throw new IllegalArgumentException("short name cannot be blank");
		}
		shortNames.add(name);
		return this;
	}

	/**
	 * Get the first long name or null if no long names
	 *
	 * @return long name
	 *
	 * @since ostermillerutils 1.07.00
	 */
	String getLongName(){
		if (longNames.size() > 1) return null;
		return longNames.get(0);
	}

	/**
	 * Get the entire list of long names
	 * @return unmodifiable list of long names
	 */
	List<String> getLongNames(){
		return Collections.unmodifiableList(longNames);
	}

	/**
	 * Get the first short name or null if no short names
	 *
	 * @return short name
	 *
	 * @since ostermillerutils 1.07.00
	 */
	Character getShortName(){
		if (shortNames.size() > 1) return null;
		return shortNames.get(0);
	}

	/**
	 * Get the entire list of short names
	 *
	 * @return unmodifiable list of short names
	 *
	 * @since ostermillerutils 1.07.00
	 */
	List<Character> getShortNames(){
		return Collections.unmodifiableList(shortNames);
	}

	/**
	 * @return the minimum number of arguments allowed
	 *
	 * @since ostermillerutils 1.07.00
	 */
	int getMinArguments(){
		return minArguments;
	}

	/**
	 * @return the maximum number of arguments allowed
	 *
	 * @since ostermillerutils 1.07.00
	 */
	int getMaxArguments(){
		return maxArguments;
	}

	/**
	 * Get the length of the argument specification portion of
	 * the help message in characters.  Does not include any space
	 * between the specification and the description.
	 *
	 * @param longStart What long options start with (typically "--")
	 * @param shortStart What short options start with (typically "-")
	 * @return number of characters in the argument specification
	 *
	 * @since ostermillerutils 1.07.00
	 */
	int getHelpArgumentsLength(String longStart, String shortStart){
		if (description == null){
			return 0;
		}
		int length = 1;
		if (shortStart != null && shortNames.size()>0){
			length += 1;
			length += shortStart.length();
			length += 1;
		}
		if (longStart != null && longNames.size()>0){
			length += 1;
			length += longStart.length();
			length += longNames.get(0).length();
		}
		if (getMaxArguments() > 0){
			length += 4;
		}
		length += 3;
		return length;
	}

	/**
	 * Get the help message for this option appropriate for inclusion in
	 * "print help".
	 * <p>
	 * It will be formatted like this:
	 * <pre>  --option -o <?>  description</pre>
	 * Two spaces at the beginning, and at least two spaces after the option
	 * specification before the description.  If the indent is large,
	 * there may be more spaces before the description.
	 * <p>
	 * If it is longer that the line width and must wrap, the description will
	 * continue on the next line which will start with eight spaces.
	 *
	 * @param longStart What long options start with (typically "--")
	 * @param shortStart What short options start with (typically "-")
	 * @param indent Minimum character count at which to start the description after specifying the option
	 * @param lineWidth Character count at which to wrap (if possible)
	 * @return help message
	 *
	 * @since ostermillerutils 1.07.00
	 */
	String getHelp(String longStart, String shortStart, int indent, int lineWidth){
		if (description == null){
			return null;
		}
		int expectedLength = 32;
		expectedLength += description.length();
		StringBuffer sb = new StringBuffer(expectedLength);
		sb.append(" ");
		if (shortStart != null && shortNames.size()>0){
			sb.append(" ").append(shortStart).append(shortNames.get(0));
		}
		if (longStart != null && longNames.size()>0){
			sb.append(" ").append(longStart).append(longNames.get(0));
		}
		if (getMaxArguments() > 0){
			sb.append(" ").append("<?>");
		}
		while(indent > sb.length() + 2){
			sb.append(" ");
		}
		sb.append("  ");
		int descriptionIndex = 0;
		int charactersLeft = lineWidth - sb.length();
		for (int lineNumber=1; descriptionIndex < description.length(); lineNumber++){
			int endIndex = descriptionIndex + charactersLeft;
			if (endIndex > description.length()){
				endIndex = description.length();
			} else {
				if (description.charAt(endIndex) == ' '){
					// Space right at the break
				} else if (description.lastIndexOf(' ', endIndex) > descriptionIndex){
					// Space sometime before the break
					endIndex = description.lastIndexOf(' ', endIndex);
				} else if (description.indexOf(' ', endIndex) != -1){
					// Space sometime after the break
					endIndex = description.lastIndexOf(' ', endIndex);
				} else {
					// No remaining spaces
					endIndex = description.length();
				}
			}
			if (lineNumber != 1){
				sb.append("\n        ");
			}
			sb.append(description.substring(descriptionIndex,endIndex).trim());
			descriptionIndex = endIndex + 1;
			charactersLeft = lineWidth - 8;
		}

		return sb.toString();
	}

	/**
	 * Get a short string description this option.
	 * It will be either the long name (if it has one)
	 * or the short name if it does not have a long name
	 *
	 * @return string representation
	 *
	 * @since ostermillerutils 1.07.00
	 */
	@Override public String toString(){
		if (longNames.size() > 0){
			return longNames.get(0);
		}
		return shortNames.get(0).toString();
	}

	/**
	 * Get the call back object
	 *
	 * @return the call back object
	 *
	 * @since ostermillerutils 1.07.00
	 */
	CmdLnListener getListener(){
		return callback;
	}

	/**
	 * Set the call back object
	 *
	 * @param callback the call back object
	 * @return this for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption setListener(CmdLnListener callback){
		this.callback = callback;
		return this;
	}

	/**
	 * An object that may be set by the user.
	 * Suggested use: set the user object to an enum
	 * value that can be used in a switch statement.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private Object userObject;

	/**
	 * An object that may be set by the user.
	 * Suggested use: set the user object to an enum
	 * value that can be used in a switch statement.
	 *
	 * @since ostermillerutils 1.07.00
	 *
	 * @return the userObject
	 */
	public Object getUserObject() {
		return userObject;
	}

	/**
	 * An object that may be set by the user.
	 * Suggested use: set the user object to an enum
	 * value that can be used in a switch statement.
	 *
	 * @param userObject the userObject to set
	 * @return this for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption setUserObject(Object userObject) {
		this.userObject = userObject;
		return this;
	}

	/**
	 * @return the description used in the help message or null if
	 * no description has been set.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description used in the help message
	 * @return this for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption setDescription(String description) {
		this.description = description;
		return this;
	}
}
