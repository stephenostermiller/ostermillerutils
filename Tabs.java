/*
 * Adjusts tabs and spaces.
 * Copyright (C) 2002-2007 Stephen Ostermiller
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
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Stream editor to alter the line separators on text to match
 * that of a given platform.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/LineEnds.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */
public class Tabs {

	/**
	 * Version number of this program
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static final String version = "1.1";

	/**
	 * Locale specific strings displayed to the user.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	 protected static ResourceBundle labels = ResourceBundle.getBundle("com.Ostermiller.util.Tabs",  Locale.getDefault());


	/**
	 * Can be passed instead of a spaces argument to use tabs instead.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public final static int TABS = -1;

	private enum TabsCmdLnOption {
		/** --help */
		HELP(new CmdLnOption(labels.getString("help.option")).setDescription( labels.getString("help.message"))),
		/** --version */
		VERSION(new CmdLnOption(labels.getString("version.option")).setDescription(labels.getString("version.message"))),
		/** --about */
		ABOUT(new CmdLnOption(labels.getString("about.option")).setDescription(labels.getString("about.message"))),
		/** --width */
		WIDTH(new CmdLnOption(labels.getString("width.option"), 'w').setDescription(labels.getString("w.message")).setRequiredArgument()),
		/** --guess */
		GUESS(new CmdLnOption(labels.getString("guess.option"), 'g').setDescription(labels.getString("g.message") + " (" + labels.getString("default") + ")")),
		/** --tabs */
		TABS(new CmdLnOption(labels.getString("tabs.option"), 't').setDescription(labels.getString("t.message"))),
		/** --spaces */
		SPACES(new CmdLnOption(labels.getString("spaces.option"), 's').setDescription(labels.getString("s.message") + " (" + labels.getString("default") + "=4)").setRequiredArgument()),
		/** --force */
		FORCE(new CmdLnOption(labels.getString("force.option"), 'f').setDescription(labels.getString("f.message"))),
		/** --noforce */
		NOFORCE(new CmdLnOption(labels.getString("noforce.option")).setDescription(labels.getString("noforce.message") + " (" + labels.getString("default") + ")")),
		/** --reallyverbose */
		REALLYVERBOSE(new CmdLnOption(labels.getString("reallyverbose.option"), 'V').setDescription(labels.getString("V.message"))),
		/** --verbose */
		VERBOSE(new CmdLnOption(labels.getString("verbose.option"), 'v').setDescription(labels.getString("v.message"))),
		/** --quiet */
		QUIET(new CmdLnOption(labels.getString("quiet.option"), 'q').setDescription(labels.getString("q.message") + " (" + labels.getString("default") + ")")),
		/** --reallyquiet */
		REALLYQUIET(new CmdLnOption(labels.getString("reallyquiet.option"), 'Q').setDescription(labels.getString("Q.message")));

		private CmdLnOption option;

		private TabsCmdLnOption(CmdLnOption option){
			option.setUserObject(this);
			this.option = option;
		}

		private CmdLnOption getCmdLineOption(){
			return option;
		}
	}

	/**
	 * Converts the tabs in files, or standard input.
	 * Run with --help argument for more information.
	 *
	 * @param args Command line arguments.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static void main(String[] args){
		CmdLn commandLine = new CmdLn(
			args
		).setDescription(
			labels.getString("tabs") + labels.getString("purpose.message")
		);
		for (TabsCmdLnOption option: TabsCmdLnOption.values()){
			commandLine.addOption(option.getCmdLineOption());
		}
		int inputTabWidth = TABS;
		int outputTabWidth = 4;
		boolean force = false;
		boolean printMessages = false;
		boolean printExtraMessages = false;
		boolean printErrors = true;
		for(CmdLnResult result: commandLine.getResults()){
			switch((TabsCmdLnOption)result.getOption().getUserObject()){
				case HELP:{
					commandLine.printHelp();
					System.exit(0);
				} break;
				case VERSION:{
					// print out the version message
					System.out.println(MessageFormat.format(labels.getString("version"), (Object[])new String[] {version}));
					System.exit(0);
				} break;
				case ABOUT:{
					System.out.println(
						labels.getString("tabs") + " -- " + labels.getString("purpose.message") + "\n" +
						MessageFormat.format(labels.getString("copyright"), (Object[])new String[] {"2002-2007", "Stephen Ostermiller (http://ostermiller.org/contact.pl?regarding=Java+Utilities)"}) + "\n\n" +
						labels.getString("license")
					);
					System.exit(0);
				} break;
				case WIDTH:{
					try {
						inputTabWidth = Integer.parseInt(commandLine.getResult('w').getArgument());
					} catch (NumberFormatException x){
						inputTabWidth = -1;
					}
					if (inputTabWidth<1 || inputTabWidth>20){
						System.err.println(labels.getString("widtherror"));
						System.exit(1);
					}
				} break;
				case GUESS:{
					inputTabWidth = TABS;
				} break;
				case SPACES:{
					try {
						outputTabWidth = Integer.parseInt(commandLine.getResult('s').getArgument());
					} catch (NumberFormatException x){
						outputTabWidth = -1;
					}
					if (outputTabWidth<1 || outputTabWidth>20){
						System.err.println("widtherror");
						System.exit(1);
					}
				} break;
				case TABS:{
					outputTabWidth = TABS;
				} break;
				case FORCE:{
					force = true;
				} break;
				case NOFORCE:{
					force = false;
				} break;
				case REALLYVERBOSE:{
					printExtraMessages = true;
					printMessages = true;
					printErrors = true;
				} break;
				case VERBOSE:{
					printExtraMessages = false;
					printMessages = true;
					printErrors = true;
				} break;
				case QUIET:{
					printExtraMessages = false;
					printMessages = false;
					printErrors = true;
				} break;
				case REALLYQUIET:{
					printExtraMessages = false;
					printMessages = false;
					printErrors = false;
				} break;
			}
		}

		int exitCond = 0;
		boolean done = false;
		for (String argument: commandLine.getNonOptionArguments()){
			done = true;
			File source = new File(argument);
			if (!source.exists()){
				if(printErrors){
					System.err.println(MessageFormat.format(labels.getString("doesnotexist"), (Object[])new String[] {argument}));
				}
				exitCond = 1;
			} else if (!source.canRead()){
				if(printErrors){
					System.err.println(MessageFormat.format(labels.getString("cantread"), (Object[])new String[] {argument}));
				}
				exitCond = 1;
			} else if (!source.canWrite()){
				if(printErrors){
					System.err.println(MessageFormat.format(labels.getString("cantwrite"), (Object[])new String[] {argument}));
				}
				exitCond = 1;
			} else {
				try {
					if(convert (source, inputTabWidth, outputTabWidth, !force)){
						if (printMessages){
							System.out.println(MessageFormat.format(labels.getString("modified"), (Object[])new String[] {argument}));
						}
					} else {
						if (printExtraMessages){
							System.out.println(MessageFormat.format(labels.getString("alreadycorrect"), (Object[])new String[] {argument}));
						}
					}
				} catch (IOException x){
					if(printErrors){
						System.err.println(argument + ": " + x.getMessage());
					}
					exitCond = 1;
				}
			}
		}
		if (!done){
			if(inputTabWidth == TABS){
				System.err.println(labels.getString("stdinguess"));
				exitCond = 1;
			} else {
				try {
					convert (System.in, System.out, inputTabWidth, outputTabWidth, !force);
				} catch (IOException x){
					System.err.println(x.getMessage());
					exitCond = 1;
				}
			}
		}
		System.exit(exitCond);
	}

	private final static int DEFAULT_INPUT_TAB_WIDTH = 4;
	private final static int DEFAULT_INPUT_FILE_TAB_WIDTH = TABS;
	private final static int DEFAULT_OUTPUT_TAB_WIDTH = 4;

	private final static boolean DEFAULT_MODIFY_BINARY = false;

	/**
	 * Read form the input stream, changing the tabs at the beginning of each line
	 * to four spaces, write the result to the output stream.
	 *
	 * @param in stream that contains the text which needs line number conversion.
	 * @param out stream where converted text is written.
	 * @return true if the output was modified from the input, false if it is exactly the same
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(InputStream in, OutputStream out) throws IOException {
		return convert(in, out, DEFAULT_INPUT_TAB_WIDTH, DEFAULT_OUTPUT_TAB_WIDTH, DEFAULT_MODIFY_BINARY);
	}

	/**
	 * Read form the input stream, changing the tabs at the beginning of each line
	 * to the specified number of spaces, write the result to the output stream.
	 *
	 * @param in stream that contains the text which needs line number conversion.
	 * @param out stream where converted text is written.
	 * @param inputTabWidth number of spaces used instead of a tab in the input.
	 * @return true if the output was modified from the input, false if it is exactly the same
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 * @throws IllegalArgumentException if tab widths are not between 1 and 20 or TABS.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(InputStream in, OutputStream out, int inputTabWidth) throws IOException {
		return convert(in, out, inputTabWidth, DEFAULT_OUTPUT_TAB_WIDTH, DEFAULT_MODIFY_BINARY);
	}

	/**
	 * Read form the input stream, changing the tabs at the beginning of each line
	 * to the specified number of spaces or the other way around, write the result
	 * to the output stream.
	 *
	 * The current system's line separator is used.
	 *
	 * @param in stream that contains the text which needs line number conversion.
	 * @param out stream where converted text is written.
	 * @param inputTabWidth number of spaces used instead of a tab in the input.
	 * @param outputTabWidth TABS if tabs should be used, otherwise, number of spaces to use.
	 * @return true if the output was modified from the input, false if it is exactly the same
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(InputStream in, OutputStream out, int inputTabWidth, int outputTabWidth) throws IOException {
		return convert(in, out, inputTabWidth, outputTabWidth, DEFAULT_MODIFY_BINARY);
	}

	/**
	 * Read form the input stream, changing the tabs at the beginning of each line
	 * to the specified number of spaces or the other way around, write the result
	 * to the output stream.
	 *
	 * The current system's line separator is used.
	 *
	 * @param in stream that contains the text which needs line number conversion.
	 * @param out stream where converted text is written.
	 * @param inputTabWidth number of spaces used instead of a tab in the input.
	 * @param outputTabWidth TABS if tabs should be used, otherwise, number of spaces to use.
	 * @param binaryException throw an exception and abort the operation if binary data is encountered and binaryExcepion is false.
	 * @return true if the output was modified from the input, false if it is exactly the same.
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(InputStream in, OutputStream out, int inputTabWidth, int outputTabWidth, boolean binaryException) throws IOException {
		if ((inputTabWidth < 1 || inputTabWidth > 20) && inputTabWidth != TABS){
			throw new IllegalArgumentException(labels.getString("widtherror"));
		}
		if ((outputTabWidth < 1 || outputTabWidth > 20) && outputTabWidth != TABS){
			throw new IllegalArgumentException(labels.getString("widtherror"));
		}
		int state = STATE_INIT;
		int spaces = 0;
		int tabs = 0;
		int tabStops = 0;
		int extraSpaces = 0;
		boolean modified = false;

		byte[] buffer = new byte[BUFFER_SIZE];
		int read;
		while((read = in.read(buffer)) != -1){
			for (int i=0; i<read; i++){
				byte b = buffer[i];
				if(binaryException && b!='\r' && b!='\n' && b!='\t' && b!='\f' && (b & 0xff)<32){
					throw new BinaryDataException(labels.getString("binaryexcepion"));
				}
				switch (b){
					case ' ': {
						if (state == STATE_INIT) {
							spaces++;
							extraSpaces++;
							if (extraSpaces == inputTabWidth){
								tabStops++;
								extraSpaces = 0;
							}
						} else {
							out.write(b);
						}
					} break;
					case '\t': {
						if (state == STATE_INIT) {
							if (spaces > 0){
								// put tabs before spaces
								modified = true;
							}
							tabs++;
							tabStops++;
							extraSpaces = 0;
						} else {
							out.write(b);
						}
					} break;
					case '\r': case '\n': {
						out.write(b);
						spaces = 0;
						tabs = 0;
						tabStops = 0;
						extraSpaces = 0;
						state = STATE_INIT;
					} break;
					default: {
						if (state == STATE_INIT){
							if (outputTabWidth == TABS){
								for (int j=0; j<tabStops; j++){
									out.write((byte)'\t');
								}
							} else {
								extraSpaces += tabStops * outputTabWidth;
								tabStops = 0;
							}
							for (int j=0; j<extraSpaces; j++){
								out.write((byte)' ');
							}
							if (extraSpaces != spaces || tabStops != tabs) modified = true;
						}
						out.write(b);
						state = STATE_SOMETHING;
					} break;
				}
			}
		}
		return modified;
	}

	/**
	 * Change the tabs at the beginning of each line of the file to four spaces.
	 * Guess the tab width of the input file.
	 *
	 * @param f File to be converted.
	 * @return true if the file was modified, false if it was already in the correct format
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(File f) throws IOException {
		return convert(f, DEFAULT_INPUT_FILE_TAB_WIDTH, DEFAULT_OUTPUT_TAB_WIDTH, DEFAULT_MODIFY_BINARY);
	}

	/**
	 * Change the tabs at the beginning of each line of the file
	 * to the specified number of spaces.
	 *
	 * @param f File to be converted.
	 * @param inputTabWidth number of spaces used instead of a tab in the input, or TAB to guess.
	 * @return true if the output was modified from the input, false if it is exactly the same
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 * @throws IllegalArgumentException if tab widths are not between 1 and 20 or TABS.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(File f, int inputTabWidth) throws IOException {
		return convert(f, inputTabWidth, DEFAULT_OUTPUT_TAB_WIDTH, DEFAULT_MODIFY_BINARY);
	}

	/**
	 * Change the tabs at the beginning of each line of the file
	 * to the specified number of spaces or the other way around.
	 *
	 * @param f File to be converted.
	 * @param inputTabWidth number of spaces used instead of a tab in the input, or TAB to guess.
	 * @param outputTabWidth true if tabs should be used, false if spaces should be used.
	 * @return true if the output was modified from the input, false if it is exactly the same
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(File f, int inputTabWidth, int outputTabWidth) throws IOException {
		return convert(f, inputTabWidth, outputTabWidth, DEFAULT_MODIFY_BINARY);
	}

	/**
	 * Change the tabs at the beginning of each line of the file
	 * to the specified number of spaces or the other way around.
	 *
	 * @param f File to be converted.
	 * @param inputTabWidth number of spaces used instead of a tab in the input, or TABS to guess.
	 * @param outputTabWidth true if tabs should be used, false if spaces should be used.
	 * @param binaryException throw an exception and abort the operation if binary data is encountered and binaryExcepion is false.
	 * @return true if the file was modified, false if it was already in the correct format
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 * @throws IllegalArgumentException if tab widths are not between 1 and 20 or TABS.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(File f, int inputTabWidth, int outputTabWidth, boolean binaryException) throws IOException {
		File temp = null;
		InputStream in = null;
		OutputStream out = null;
		boolean modified = false;
		try {
			if (inputTabWidth == TABS){
				inputTabWidth = guessTabWidth(new FileInputStream(f));
			}
			in = new FileInputStream(f);
			temp = File.createTempFile("LineEnds", null, null);
			out = new FileOutputStream(temp);
			modified = convert(in, out, inputTabWidth, outputTabWidth, binaryException);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			if (modified){
				FileHelper.move(temp, f, true);
			} else {
				if (!temp.delete()){
					throw new IOException(
						MessageFormat.format(
							labels.getString("tempdeleteerror"),
							(Object[])new String[] {temp.toString()}
						)
					);
				}
			}
		} finally {
			if (in != null){
				in.close();
				in = null;
			}
			if (out != null){
				out.flush();
				out.close();
				out = null;
			}
		}
		return modified;
	}

	/**
	 * Buffer size when reading from input stream.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	private final static int BUFFER_SIZE = 1024;
	private final static int STATE_INIT = 0;
	private final static int STATE_SOMETHING = 1;

	final private static int MAX_SPACES = 128;
	final private static int MAX_TABS = 16;
	final private static int MAX_COMBINED = 256;

	/**
	 * Guess the number of spaces per tab at the beginning of each line.
	 *
	 * @param in Input stream for which to guess tab width
	 * @return the least value (two or greater) which has some line that starts with n times spaces for n zero to max spaces starting a line.
	 * @throws IOException if an input or output error occurs.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static int guessTabWidth(InputStream in) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int[][] data = new int[MAX_SPACES][MAX_TABS];
		int[] spaceData = new int[MAX_SPACES*MAX_TABS];
		int read;
		int state = STATE_INIT;
		int tabs = 0;
		int spaces = 0;
		int mostTabs = 0;
		int mostSpaces = 0;
		while((read = in.read(buffer)) != -1){
			for (int i=0; i<read; i++){
				byte b = buffer[i];
				switch (b){
					case ' ': {
						if (state == STATE_INIT) spaces++;
					} break;
					case '\t': {
						if (state == STATE_INIT) tabs++;
					} break;
					case '\r': case '\n': {
						state = STATE_INIT;
						if (spaces < MAX_SPACES && tabs < MAX_TABS){
							data[spaces][tabs]++;
							if (tabs > mostTabs) mostTabs = tabs;
							if (spaces > mostSpaces) mostSpaces = spaces;
							spaces = 0;
							tabs = 0;
						}
					} break;
					default: {
						state = STATE_SOMETHING;
					} break;
				}
			}
		}
		for (int tabWidth=2; tabWidth<=20; tabWidth++){
			int mostCombined=0;
			for (int tabInd=0; tabInd <= mostTabs; tabInd++){
				for (int spaceInd=0; spaceInd <= mostSpaces; spaceInd++){
					int totInd = spaceInd + (tabInd * tabWidth);
					if (totInd < MAX_COMBINED){
						int numLines = data[spaceInd][tabInd];
						if (numLines > 0){
							if (mostCombined < totInd) mostCombined = totInd;
							spaceData[totInd] += numLines;
						}
					}
				}
			}
			boolean found = true;
			for(int combInd=0; found && combInd < mostCombined; combInd+=tabWidth){
				found = spaceData[combInd] > 0;
			}
			if (found) return tabWidth;
		}
		return 2;
	}
}
