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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * See COPYING.TXT for details.
 */
package com.Ostermiller.util;

import java.io.*;
import java.util.*;

/**
 * Command line argument parser for Java command line programs.
 *
 * More information about this class and code samples for suggested use are
 * available from <a target="_top" href=
 * "http://ostermiller.org/utils/CmdLn.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.07.00
 */
public final class CmdLn {

	/**
	 * The original unparsed command line arguments as
	 * passed in to the constructor.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private String[] arguments;

	/**
	 * Description to be put in the help.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private String description;

	/**
	 * A list of all the options.
	 * Filled by the public addOptions() methods.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private ArrayList<CmdLnOption> options = new ArrayList<CmdLnOption>();

	/**
	 * Map the options to their found results.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private HashMap<CmdLnOption, ArrayList<CmdLnResult>> optionsToResults = new HashMap<CmdLnOption, ArrayList<CmdLnResult>>();

	/**
	 * List of all results that have been found.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private ArrayList<CmdLnResult> results = new ArrayList<CmdLnResult>();

	/**
	 * A list of the arguments that do not belong to any option.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private ArrayList<String> leftOverArguments = new ArrayList<String>();

	/**
	 * Mapping of long options to their command line option object.
	 * Created during the parse method.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private HashMap<String,CmdLnOption> longOptions;

	/**
	 * Mapping of short options to their command line option object.
	 * Created during the parse method.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private HashMap<Character,CmdLnOption> shortOptions;

	/**
	 * An ordered set of what long options start with. Typically contains
	 * a single entry "--".
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private LinkedHashSet<String> longOptionsStart = new LinkedHashSet<String>(1);

	/**
	 * An ordered set of what short options start with. Typically contains
	 * a single entry "-".
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private LinkedHashSet<String> shortOptionsStart = new LinkedHashSet<String>(1);

	/**
	 * A set of strings that indicate that everything following should be regarded
	 * as non-option arguments.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private HashSet<String> nonOptionSeparators = new HashSet<String>(1);

	/**
	 * A set of strings that break an option up into an option and an argument.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private HashSet<Character> optionArgumentDelimiters = new HashSet<Character>(3);

	/**
	 * Whether or not the parse method has been called yet.
	 * Used to determine if setters are appropriate or should throw illegal state exceptions.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private boolean parsed = false;

	/**
	 * New command line options with the given command line arguments
	 *
	 * @param arguments command line arguments from main method
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLn(String[] arguments){
		if (arguments == null) throw new IllegalArgumentException("Command line arguments cannot be null.");
		for(String argument: arguments){
			if (argument == null) throw new IllegalArgumentException("Each command line argument cannot be null.");
		}
		this.arguments = arguments.clone();

		// Defaults
		longOptionsStart.add("--");
		shortOptionsStart.add("-");
		nonOptionSeparators.add("--");
		optionArgumentDelimiters.add('=');
		optionArgumentDelimiters.add(':');
		optionArgumentDelimiters.add(' ');
	}

	/**
	 * Set the delimiters that separate a option name from a value
	 * within a single argument.
	 *
	 * This is to support arguments of the form "--name=value".  The
	 * equals sign can separate the option name ("name") from the
	 * option argument ("value").
	 *
	 * By default, the delimiters are '=', ':', and ' '.
	 * @param delimiters list of delimiters
	 * @return this for method chaining.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLn setOptionArgumentDelimiters(char[] delimiters){
		optionArgumentDelimiters.clear();
		for (char c: delimiters){
			optionArgumentDelimiters.add(c);
		}
		return this;
	}

	/**
	 * Set what long options and short options start with.
	 * Typically long options start with "--" and short options with "-"
	 * (this is the default).
	 *
	 * @param longOptionsStart What long options start with (default "--") or null for no long options;
	 * @param shortOptionsStart What short options start with (default "-") or null for no short options;
	 * @throws IllegalArgumentException if the long or short start is the empty string
	 * @return this for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLn setOptionStarts(String longOptionsStart, String shortOptionsStart){
		setOptionStarts(
			new String[]{longOptionsStart},
			new String[]{shortOptionsStart}
		);
		return this;
	}

	/**
	 * Set what long options and short options start with.
	 * Typically long options start with "--" and short options with "-" and
	 * this is the default.
	 * <p>
	 * The first option start in each array will be the canonical option start
	 * that is used in the help message.
	 *
	 * @param longOptionsStart What long options start with (default "--") or null or empty for no long options;
	 * @param shortOptionsStart What short options start with (default "-") or null or empty for no short options;
	 * @throws IllegalArgumentException if the long or short start is the empty string
	 * @return this for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLn setOptionStarts(String[] longOptionsStart, String[] shortOptionsStart){
		this.longOptionsStart.clear();
		if (longOptionsStart != null){
			for (String optionStart: longOptionsStart){
				if (optionStart != null){
					if ("".equals(optionStart)) throw new IllegalArgumentException("long option start cannot be the empty string");
					this.longOptionsStart.add(optionStart);
				}
			}
		}
		this.shortOptionsStart.clear();
		if (shortOptionsStart != null){
			for (String optionStart: shortOptionsStart){
				if (optionStart != null){
					if ("".equals(optionStart)) throw new IllegalArgumentException("short option start cannot be the empty string");
					this.shortOptionsStart.add(optionStart);
				}
			}
		}
		return this;
	}

	/**
	 * Set the "stop" option that causes any following arguments to be treated
	 * as non-option arguments, even if they look like an option.
	 * <p>
	 * The default non-option separator is "--".
	 *
	 * @param nonOptionSeparators List of arguments that stop processing options and treat remaining arguments as non option arguments.
	 * @return this for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLn setNonOptionSeparators(String[] nonOptionSeparators){
		this.nonOptionSeparators.clear();
		if (nonOptionSeparators != null){
			for (String nonOptionSeparator: nonOptionSeparators){
				this.nonOptionSeparators.add(nonOptionSeparator);
			}
		}
		return this;
	}

	/**
	 * Set the description for the program. This description will be printed
	 * on the first line of the help message.
	 *
	 * @param description short description about the program
	 * @return this for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLn setDescription(String description){
		this.description = description;
		return this;
	}

	/**
	 * Add options.
	 *
	 * @param options options to be added.
	 * @throws NullPointerException if the options are null.
	 * @throws NullPointerException if any option in the collection is null.
	 * @throws IllegalStateException if the command line has already been parsed.
	 * @return this for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLn addOptions(Collection<CmdLnOption> options){
		for(CmdLnOption option: options){
			addOption(option);
		}
		return this;
	}

	/**
	 * Add options.
	 *
	 * @param options options to be added.
	 * @throws NullPointerException if the options are null.
	 * @throws NullPointerException if any option in the collection is null.
	 * @throws IllegalStateException if the command line has already been parsed.
	 * @return this for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLn addOptions(CmdLnOption[] options){
		for(CmdLnOption option: options){
			addOption(option);
		}
		return this;
	}

	/**
	 * Add option.
	 *
	 * @param option option to be added.
	 * @throws NullPointerException if the option is null.
	 * @throws IllegalStateException if the command line has already been parsed.
	 * @return this for method chaining
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLn addOption(CmdLnOption option){
		if (parsed) throw new IllegalStateException("Can no longer add options");
		options.add(option);
		return this;
	}

	/**
	 * Get the option associated with the given argument.
	 * Parses the command line if not already parsed.
	 *
	 * @param s long argument
	 * @return option associated with the given argument.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private CmdLnOption getOption(String s){
		parse();
		return longOptions.get(s);
	}

	/**
	 * Get the option associated with the given argument.
	 * Parses the command line if not already parsed.
	 *
	 * @param c short argument
	 * @return option associated with the given argument.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private CmdLnOption getOption(Character c){
		parse();
		return shortOptions.get(c);
	}

	/**
	 * Get the option associated with the given argument.
	 * Parses the command line if not already parsed.
	 *
	 * @param c short argument
	 * @return option associated with the given argument.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private CmdLnOption getOption(char c){
		parse();
		return shortOptions.get(c);
	}

	/**
	 * Get the last result associated with the given argument.
	 * If a option is in the command line multiple times, typically
	 * the last one should be the one that wins as an override mechanism.
	 * Parses the command line if not already parsed.
	 *
	 * @param s long argument
	 * @return result for argument, or null if not associated with an option or not present in the command line.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnResult getResult(String s){
		parse();
		return getResult(getOption(s));
	}

	/**
	 * Get the last result associated with the given argument.
	 * If a option is in the command line multiple times, typically
	 * the last one should be the one that wins as an override mechanism.
	 * Parses the command line if not already parsed.
	 *
	 * @param c short argument
	 * @return result for argument, or null if not associated with an option or not present in the command line.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnResult getResult(Character c){
		parse();
		return getResult(getOption(c));
	}

	/**
	 * Get the last result associated with the given argument.
	 * If a option is in the command line multiple times, typically
	 * the last one should be the one that wins as an override mechanism.
	 * Parses the command line if not already parsed.
	 *
	 * @param c short argument
	 * @return result for argument, or null if not associated with an option or not present in the command line.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnResult getResult(char c){
		parse();
		return getResult(getOption(c));
	}

	/**
	 * Get the last result associated with the option.
	 * If a option is in the command line multiple times, typically
	 * the last one should be the one that wins as an override mechanism.
	 * Parses the command line if not already parsed.
	 *
	 * @param option command line option
	 * @return result for argument, or null if option not added to this command line or not present in the command line.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public CmdLnResult getResult(CmdLnOption option){
		parse();
		if (option == null) return null;
		ArrayList<CmdLnResult> results = optionsToResults.get(option);
		if (results == null) return null;
		return results.get(results.size()-1);
	}

	/**
	 * Get all the results in
	 * the order in which they appear in the command line.
	 * Parses the command line if not already parsed.
	 * @return unmodifiable list of all options and arguments that were specified on the command line
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public List<CmdLnResult> getResults(){
		parse();
		return Collections.unmodifiableList(results);
	}

	/**
	 * Get all the results associated with the given argument in
	 * the order in which they appear in the command line.
	 * If a option is in the command line multiple times, typically
	 * the last one should be the one that wins as an override mechanism,
	 * however it may sometimes be useful to know about all of them.
	 * Parses the command line if not already parsed.
	 *
	 * @param s long argument
	 * @return results for argument, or null if not associated with an option or not present in the command line.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public List<CmdLnResult> getResults(String s){
		parse();
		return getResults(getOption(s));
	}

	/**
	 * Get all the results associated with the given argument in
	 * the order in which they appear in the command line.
	 * If a option is in the command line multiple times, typically
	 * the last one should be the one that wins as an override mechanism,
	 * however it may sometimes be useful to know about all of them.
	 * Parses the command line if not already parsed.
	 *
	 * @param c short argument
	 * @return results for argument, or null if not associated with an option or not present in the command line.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public List<CmdLnResult> getResults(Character c){
		parse();
		return getResults(getOption(c));
	}

	/**
	 * Get all the results associated with the given argument in
	 * the order in which they appear in the command line.
	 * If a option is in the command line multiple times, typically
	 * the last one should be the one that wins as an override mechanism,
	 * however it may sometimes be useful to know about all of them.
	 * Parses the command line if not already parsed.
	 *
	 * @param c short argument
	 * @return results for argument, or null if not associated with an option or not present in the command line.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public List<CmdLnResult> getResults(char c){
		parse();
		return getResults(getOption(c));
	}

	/**
	 * Get all the results associated with the given argument in
	 * the order in which they appear in the command line.
	 * If a option is in the command line multiple times, typically
	 * the last one should be the one that wins as an override mechanism,
	 * however it may sometimes be useful to know about all of them.
	 * Parses the command line if not already parsed.
	 *
	 * @param option command line option
	 * @return results for option, or null if option not added to this command line or not present in the command line.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public List<CmdLnResult> getResults(CmdLnOption option){
		parse();
		if (option == null) return null;
		ArrayList<CmdLnResult> results = optionsToResults.get(option);
		if (results == null) return null;
		return Collections.unmodifiableList(results);
	}

	/**
	 * Whether or not the specified option is present in the command line.
	 * Parses the command line if not already parsed.
	 *
	 * @param s long argument
	 * @return true iff the option (or its synonyms) is present.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public boolean present(String s){
		parse();
		return present(getOption(s));
	}

	/**
	 * Whether or not the specified option is present in the command line.
	 * Parses the command line if not already parsed.
	 *
	 * @param c short argument
	 * @return true iff the option (or its synonyms) is present.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public boolean present(Character c){
		parse();
		return present(getOption(c));
	}

	/**
	 * Whether or not the specified option is present in the command line.
	 * Parses the command line if not already parsed.
	 *
	 * @param c short argument
	 * @return true iff the option (or its synonyms) is present.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public boolean present(char c){
		parse();
		return present(getOption(c));
	}

	/**
	 * Whether or not the specified option is present in the command line.
	 * Parses the command line if not already parsed.
	 *
	 * @param option command line option
	 * @return true iff the option is present.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public boolean present(CmdLnOption option){
		parse();
		if (option == null) return false;
		ArrayList<CmdLnResult> results = optionsToResults.get(option);
		return (results != null);
	}

	/**
	 * The number of times the specified option is present in the command line.
	 * Parses the command line if not already parsed.
	 *
	 * @param s long argument
	 * @return true iff the option (or its synonyms) is present.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public int occurrences(String s){
		parse();
		return occurrences(getOption(s));
	}

	/**
	 * The number of times the specified option is present in the command line.
	 * Parses the command line if not already parsed.
	 *
	 * @param c short argument
	 * @return true iff the option (or its synonyms) is present.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public int occurrences(Character c){
		parse();
		return occurrences(getOption(c));
	}

	/**
	 * The number of times the specified option is present in the command line.
	 * Parses the command line if not already parsed.
	 *
	 * @param c short argument
	 * @return true iff the option (or its synonyms) is present.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public int occurrences(char c){
		parse();
		return occurrences(getOption(c));
	}

	/**
	 * The number of times the specified option is present in the command line.
	 * Parses the command line if not already parsed.
	 *
	 * @param option command line option
	 * @return true iff the option is present.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public int occurrences(CmdLnOption option){
		parse();
		if (option == null) return 0;
		ArrayList<CmdLnResult> results = optionsToResults.get(option);
		if (results == null) return 0;
		return results.size();
	}

	/**
	 * Get the left over arguments -- the arguments that are not
	 * associated with any arguments.
	 * Parses the command line if not already parsed.
	 *
	 * @return unmodifiable list of arguments
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public List<String> getNonOptionArguments(){
		parse();
		return Collections.unmodifiableList(leftOverArguments);
	}

	/**
	 * Go through the options and sort out the long and the short options
	 * into their own hash maps.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private void processOptions(){
		longOptions = new HashMap<String,CmdLnOption>();
		shortOptions= new HashMap<Character,CmdLnOption>();
		for (CmdLnOption option: options){
			option.setImmutable();
			for (String name: option.getLongNames()){
				if (longOptions.containsKey(name)){
					throw new IllegalArgumentException("More than one long option has the name: '" + name + "'");
				}
				longOptions.put(name, option);
			}
			for (Character c: option.getShortNames()){
				if (shortOptions.containsKey(c)){
					throw new IllegalArgumentException("More than one short option has the character: '" + c + "'");
				}
				shortOptions.put(c, option);
			}
		}
	}

	/**
	 * Determine whether the given argument is a long option.
	 * Check all the long argument starts to see if it starts with any of them.
	 * However, if it *is* a option start, it is not a long option.
	 *
	 * @param argument command line argument to check to see if it is a long argument
	 * @return the argument name (strip off the argument start) or null if not a long argument
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private String getLongOptionName(String argument){
		if (shortOptionsStart.contains(argument) || longOptionsStart.contains(argument)){
			return null;
		}
		for (String optionStart: longOptionsStart){
			if (argument.startsWith(optionStart)){
				return argument.substring(optionStart.length());
			}
		}
		return null;
	}

	/**
	 * Determine whether the given argument is a short option.
	 * Check all the short argument starts to see if it starts with any of them.
	 * However, if it *is* a option start, it is not a short option.
	 *
	 * @param argument command line argument to check to see if it is a short argument
	 * @return the argument name (strip off the argument start) or null if not a short argument
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private String getShortOptionName(String sArgument){
		if (shortOptionsStart.contains(sArgument) || longOptionsStart.contains(sArgument)){
			return null;
		}
		for (String optionStart: shortOptionsStart){
			if (sArgument.startsWith(optionStart)){
				return sArgument.substring(optionStart.length());
			}
		}
		return null;
	}

	/**
	 * Record the result in all needed places
	 *
	 * @param result to record
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private void addResult(CmdLnResult result){
		ArrayList<CmdLnResult> results = optionsToResults.get(result.getOption());
		if (results == null){
			results = new ArrayList<CmdLnResult>(1);
			optionsToResults.put(result.getOption(), results);
		}
		results.add(result);
		this.results.add(result);
	}

	/**
	 * Find the index of of a option argument delimiter
	 * @param argument argument to search
	 * @return index of delimiter or -1 if none found
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private int getIndexOfDelimiter(String argument){
		int index = -1;
		for(Character delimiter: optionArgumentDelimiters){
			int i = argument.indexOf(delimiter);
			if (i > 0 && (index==-1 || i<index)) index = i;
		}
		return index;
	}

	/**
	 * Look through the arguments in order, see if they are a long option, a short
	 * option, an argument to an option, or a non-option argument.
	 *
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private void runThroughArguments(){
		CmdLnResult lastResult = null;
		String optionName = null;
		boolean seenNonOptionSeparator = false;
		for(String argument: arguments){
			if (seenNonOptionSeparator){
				// Non-option argument
				// No arguments after the separator are options or option arguments
				leftOverArguments.add(argument);
			} else if (nonOptionSeparators.contains(argument)){
				// No arguments after the separator will be options or option arguments
				seenNonOptionSeparator = true;
			} else if ((optionName = getLongOptionName(argument)) != null){
				int delimiterIndex = getIndexOfDelimiter(optionName);
				String optionArgument = null;
				if (delimiterIndex > 0){
					optionArgument = optionName.substring(delimiterIndex+1);
					optionName = optionName.substring(0, delimiterIndex);
				}
				// Long option
				CmdLnOption option = longOptions.get(optionName);
				if (option == null){
					throw new UnknownCmdLnOptionException().setArgument(argument).setOption(optionName);
				}
				lastResult = new CmdLnResult(option);
				addResult(lastResult);
				if (optionArgument != null){
					if (lastResult.hasAllArguments()){
						throw new ExtraCmdLnArgumentException().setResult(lastResult);
					}
					lastResult.addArgument(optionArgument);
				}
			} else if ((optionName = getShortOptionName(argument)) != null){
				// Short options
				int delimiterIndex = getIndexOfDelimiter(optionName);
				for (int i=0; i<optionName.length(); i++){
					// Short option
					Character c = optionName.charAt(i);
					CmdLnOption option = shortOptions.get(c);
					if (option == null){
						throw new UnknownCmdLnOptionException().setArgument(argument).setOption(Character.toString(c));
					}
					lastResult = new CmdLnResult(option);
					addResult(lastResult);
					if (delimiterIndex == i+1){
						String optionArgument = optionName.substring(delimiterIndex+1);
						if (lastResult.hasAllArguments()){
							throw new ExtraCmdLnArgumentException().setResult(lastResult);
						}
						lastResult.addArgument(optionArgument);
						i = optionName.length(); // break out of loop
					}
				}
			} else {
				// Argument
				if (lastResult != null && !lastResult.hasAllArguments()){
					// Option argument
					lastResult.addArgument(argument);
				} else {
					// Non-option argument
					leftOverArguments.add(argument);
				}
			}
		}
	}

	/**
	 * Parse the command line options if they have not already been parsed.
	 * Process the options,
	 * run through the arguments,
	 * ensure that all the arguments are good,
	 * call any listeners.
	 * <p>
	 * Once this command line has been parsed, options may no longer be added.
	 *
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments.
	 * @throws ExtraCmdLnArgumentException if a command line option has too many arguments.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public void parse(){
		if (parsed) return;
		parsed = true;
		processOptions();
		runThroughArguments();
		ensureOptionsSatisfied();
		callListeners();
	}

	/**
	 * Throw an exception if any option doesn't have enough arguments.
	 *
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private void ensureOptionsSatisfied(){
		for (CmdLnResult result: results){
			if (result.requiresMoreArguments()){
				throw new MissingCmdLnArgumentException().setResult(result);
			}
		}
	}

	/**
	 * Call the call back methods in the results
	 *
	 * @since ostermillerutils 1.07.00
	 */
	private void callListeners() {
		for (CmdLnResult result: results){
			CmdLnListener callback = result.getOption().getListener();
			if (callback != null){
				callback.found(result);
			}
		}
	}

	/**
	 * Get the canonical start of long options. (Usually "--")
	 *
	 * @return the first long option start
	 *
	 * @since ostermillerutils 1.07.00
	 */
	String getFirstLongOptionsStart(){
		if (longOptionsStart.size() == 0) return null;
		return longOptionsStart.iterator().next();
	}

	/**
	 * Get the canonical start of short options. (Usually "-")
	 *
	 * @return the first short option start
	 *
	 * @since ostermillerutils 1.07.00
	 */
	String getFirstShortOptionsStart(){
		if (shortOptionsStart.size() == 0) return null;
		return shortOptionsStart.iterator().next();
	}

	/**
	 * Print help for the command line options. Help will be in this format:
	 * <pre>
	 * program description
	 *   --argument -a <?>  argument a description
	 *   --another -b       argument b description
	 *   --third -c <?>     argument c description
	 * </pre>
	 * Indentation is the default twenty, and line width is the default 80.
	 * Parses the command line if not already parsed.
	 *
	 * @return Help as a string.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public String getHelp(){
		StringWriter out = new StringWriter();
		printHelp(out);
		return out.toString();
	}

	/**
	 * Print help for the command line options to standard output. Help will be in this format:
	 * <pre>
	 * program description
	 *   --argument -a <?>  argument a description
	 *   --another -b       argument b description
	 *   --third -c <?>     argument c description
	 * </pre>
	 * Indentation is the default twenty, and line width is the default eighty.
	 * Parses the command line if not already parsed.
	 *
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public void printHelp(){
		printHelp(System.out);
	}

	/**
	 * Print help for the command line options.
	 * Indentation is the default twenty, and line width is the default eighty.
	 * Parses the command line if not already parsed.
	 *
	 * @param out destination to which the help is written.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public void printHelp(OutputStream out){
		printHelp(new PrintWriter(new OutputStreamWriter(out)));
	}

	/**
	 * Print help for the command line options. Help will be in this format:
	 * <pre>
	 * program description
	 *   --argument -a <?>  argument a description
	 *   --another -b       argument b description
	 *   --third -c <?>     argument c description
	 * </pre>
	 * Indentation is the default twenty, and line width is the default eighty.
	 * Parses the command line if not already parsed.
	 *
	 * @param out destination to which the help is written.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public void printHelp(Writer out){
		printHelp(new PrintWriter(out));
	}

	/**
	 * Print help for the command line options. Help will be in this format:
	 * <pre>
	 * program description
	 *   --argument -a <?>  argument a description
	 *   --another -b       argument b description
	 *   --third -c <?>     argument c description
	 * </pre>
	 * Indentation is the default twenty, and line width is the default eighty.
	 * Parses the command line if not already parsed.
	 *
	 * @param out destination to which the help is written.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public void printHelp(PrintWriter out){
		 printHelp(out, 20, 80);
	}

	/**
	 * Print help for the command line options. Help will be in this format:
	 * <pre>
	 * program description
	 *   --argument -a <?>  argument a description
	 *   --another -b       argument b description
	 *   --third -c <?>     argument c description
	 * </pre>
	 * Parses the command line if not already parsed.
	 *
	 * @return Help as a string.
	 * @param indent the maximum number of characters to which all descriptions should be indented.
	 * @param width the number of characters at which text should be wrapped.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public String getHelp(int indent, int width){
		StringWriter out = new StringWriter();
		printHelp(out, indent, width);
		return out.toString();
	}

	/**
	 * Print help for the command line options to standard output. Help will be in this format:
	 * <pre>
	 * program description
	 *   --argument -a <?>  argument a description
	 *   --another -b       argument b description
	 *   --third -c <?>     argument c description
	 * </pre>
	 * Parses the command line if not already parsed.
	 *
	 * @param indent the maximum number of characters to which all descriptions should be indented.
	 * @param width the number of characters at which text should be wrapped.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public void printHelp(int indent, int width){
		printHelp(System.out, indent, width);
	}

	/**
	 * Print help for the command line options. Help will be in this format:
	 * <pre>
	 * program description
	 *   --argument -a <?>  argument a description
	 *   --another -b       argument b description
	 *   --third -c <?>     argument c description
	 * </pre>
	 * Parses the command line if not already parsed.
	 *
	 * @param out destination to which the help is written.
	 * @param indent the maximum number of characters to which all descriptions should be indented.
	 * @param width the number of characters at which text should be wrapped.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public void printHelp(OutputStream out, int indent, int width){
		printHelp(new PrintWriter(new OutputStreamWriter(out)), indent, width);
	}

	/**
	 * Print help for the command line options. Help will be in this format:
	 * <pre>
	 * program description
	 *   --argument -a <?>  argument a description
	 *   --another -b       argument b description
	 *   --third -c <?>     argument c description
	 * </pre>
	 * Parses the command line if not already parsed.
	 *
	 * @param out destination to which the help is written.
	 * @param indent the maximum number of characters to which all descriptions should be indented.
	 * @param width the number of characters at which text should be wrapped.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public void printHelp(Writer out, int indent, int width){
		printHelp(new PrintWriter(out), indent, width);
	}

	/**
	 * Print help for the command line options. Help will be in this format:
	 * <pre>
	 * program description
	 *   --argument -a <?>  argument a description
	 *   --another -b       argument b description
	 *   --third -c <?>     argument c description
	 * </pre>
	 * Parses the command line if not already parsed.
	 *
	 * @param out destination to which the help is written.
	 * @param indent the maximum number of characters to which all descriptions should be indented.
	 * @param width the number of characters at which text should be wrapped.
	 * @throws UnknownCmdLnOptionException if an unexpected option is encountered during parsing.
	 * @throws MissingCmdLnArgumentException if a command line option does not have enough arguments during parsing.
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public void printHelp(PrintWriter out, int indent, int width){
		parse();
		String shortOptionsStart = getFirstShortOptionsStart();
		String longOptionsStart = getFirstLongOptionsStart();
		if (description != null){
			out.println(description);
		}
		int maxIndent = 0;
		for(CmdLnOption option: options){
			int optionIndent = option.getHelpArgumentsLength(longOptionsStart, shortOptionsStart);
			if (optionIndent > maxIndent){
				maxIndent = optionIndent;
			}
		}
		if (maxIndent < indent){
			indent = maxIndent;
		}
		for(CmdLnOption option: options){
			String optionHelp = option.getHelp(longOptionsStart, shortOptionsStart, indent, width);
			if (optionHelp != null){
				out.println(optionHelp);
			}
		}
		out.flush();
	}
}
