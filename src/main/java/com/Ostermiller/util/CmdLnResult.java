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
 * Result when a command line option is found.
 * Contains the original option and all of its arguments.
 *
 * More information about this class and code samples for suggested use are
 * available from <a target="_top" href=
 * "http://ostermiller.org/utils/CmdLn.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.07.00
 */
public class CmdLnResult
{
	/**
	 * The option that caused this result
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private CmdLnOption option;

	/**
	 * The arguments that have been found
	 * for the option.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private ArrayList<String> arguments;

	/**
	 * New command line result
	 *
	 * @param option Option that caused this result
	 *
	 * @since ostermillerutils 1.07.00
	 */
	CmdLnResult(CmdLnOption option){
		this.option = option;
	}

	/**
	 * @return the option that caused this result
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnOption getOption(){
		return option;
	}

	/**
	 * @param argument add an argument to this result
	 * @throws IllegalStateException if too many arguments have been added
	 *
	 * @since ostermillerutils 1.07.00
	 */
	void addArgument(String argument){
		if (hasAllArguments()){
			throw new IllegalStateException("Too many arguments to option");
		}
		if (arguments == null){
			arguments = new ArrayList<String>(Math.min(option.getMaxArguments(), 16));
		}
		arguments.add(argument);
	}


	/**
	 * @return true iff enough arguments have been added (max not exceeded)
	 *
	 * @since ostermillerutils 1.07.00
	 */
	boolean hasAllArguments(){
		return (getArgumentCount() >= option.getMaxArguments());
	}

	/**
	 * @return true iff more arguments need to be added (min not satisfied)
	 *
	 * @since ostermillerutils 1.07.00
	 */
	boolean requiresMoreArguments(){
		return (getArgumentCount() < option.getMinArguments());
	}

	/**
	 * Get the argument count for this option
	 * @return number of arguments
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public int getArgumentCount(){
		if (arguments == null) return 0;
		return arguments.size();
	}

	/**
	 * Get all the arguments, in the order that
	 * they were specified.
	 * @return unmodifiable list of arguments or null if none
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public List<String> getArguments(){
		if (getArgumentCount() == 0) return null;
		return Collections.unmodifiableList(arguments);
	}

	/**
	 * get the first argument, or null if no arguments
	 * @return first argument
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public String getArgument(){
		if (getArgumentCount() == 0) return null;
		return arguments.get(0);
	}
}
