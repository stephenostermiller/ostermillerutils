/*
 * Adjusts line endings.
 * Copyright (C) 2001 Stephen Ostermiller
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
public class LineEnds {

	/**
	 * Version number of this program
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static final String version = "1.2";

	/**
	 * Locale specific strings displayed to the user.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	protected static ResourceBundle labels = ResourceBundle.getBundle("com.Ostermiller.util.LineEnds",  Locale.getDefault());

	private enum LineEndsCmdLnOption {
		/** --help */
		HELP(new CmdLnOption(labels.getString("help.option")).setDescription( labels.getString("help.message"))),
		/** --version */
		VERSION(new CmdLnOption(labels.getString("version.option")).setDescription(labels.getString("version.message"))),
		/** --about */
		ABOUT(new CmdLnOption(labels.getString("about.option")).setDescription(labels.getString("about.message"))),
		/** --dos --windows */
		DOS(new CmdLnOption(new String[]{labels.getString("dos.option"),labels.getString("windows.option")}, new char[]{'d'}).setDescription(labels.getString("d.message"))),
		/** --unix --java */
		UNIX(new CmdLnOption(new String[]{labels.getString("unix.option"),labels.getString("java.option")}, new char[]{'n'}).setDescription(labels.getString("n.message"))),
		/** --mac */
		MAC(new CmdLnOption(labels.getString("mac.option"), 'r').setDescription(labels.getString("r.message"))),
		/** --system */
		SYSTEM(new CmdLnOption(labels.getString("system.option"), 's').setDescription(labels.getString("s.message") + " (" + labels.getString("default") + ")")),
		/** --force */
		FORCE(new CmdLnOption(labels.getString("force.option"), 'f').setDescription(labels.getString("f.message"))),
		/** --quiet */
		QUIET(new CmdLnOption(labels.getString("quiet.option"), 'q').setDescription(labels.getString("q.message") + " (" + labels.getString("default") + ")")),
		/** --reallyquiet */
		REALLYQUIET(new CmdLnOption(labels.getString("reallyquiet.option"), 'Q').setDescription(labels.getString("Q.message"))),
		/** --verbose */
		VERBOSE(new CmdLnOption(labels.getString("verbose.option"), 'v').setDescription(labels.getString("v.message"))),
		/** --reallyverbose */
		REALLYVERBOSE(new CmdLnOption(labels.getString("reallyverbose.option"), 'V').setDescription(labels.getString("V.message"))),
		/** --noforce */
		NOFORCE(new CmdLnOption(labels.getString("noforce.option")).setDescription(labels.getString("noforce.message") + " (" + labels.getString("default") + ")"));

		private CmdLnOption option;

		private LineEndsCmdLnOption(CmdLnOption option){
			option.setUserObject(this);
			this.option = option;
		}

		private CmdLnOption getCmdLineOption(){
			return option;
		}
	}

	/**
	 * Converts the line ending on files, or standard input.
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
			labels.getString("lineends") + labels.getString("purpose.message")
		);
		for (LineEndsCmdLnOption option: LineEndsCmdLnOption.values()){
			commandLine.addOption(option.getCmdLineOption());
		}
		int style = STYLE_SYSTEM;
		boolean force = false;
		boolean printMessages = false;
		boolean printExtraMessages = false;
		boolean printErrors = true;
		for(CmdLnResult result: commandLine.getResults()){
			switch((LineEndsCmdLnOption)result.getOption().getUserObject()){
				case HELP:{
					// print out the help message
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
						labels.getString("lineends") + " -- " + labels.getString("purpose.message") + "\n" +
						MessageFormat.format(labels.getString("copyright"), (Object[])new String[] {"2001", "Stephen Ostermiller (http://ostermiller.org/contact.pl?regarding=Java+Utilities)"}) + "\n\n" +
						labels.getString("license")
					);
					System.exit(0);
				} break;
				case DOS:{
					style = STYLE_RN;
				} break;
				case UNIX:{
					style = STYLE_N;
				} break;
				case MAC:{
					style = STYLE_R;
				} break;
				case SYSTEM:{
					style = STYLE_SYSTEM;
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
					if(convert (source, style, !force)){
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
			try {
				convert (System.in, System.out, style, !force);
			} catch (IOException x){
				System.err.println(x.getMessage());
				exitCond = 1;
			}
		}
		System.exit(exitCond);
	}

	/**
	 * The system line ending as determined
	 * by System.getProperty("line.separator")
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public final static int STYLE_SYSTEM = 0;
	/**
	 * The Windows and DOS line ending ("\r\n")
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public final static int STYLE_WINDOWS = 1;
	/**
	 * The Windows and DOS line ending ("\r\n")
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public final static int STYLE_DOS = 1;
	/**
	 * The Windows and DOS line ending ("\r\n")
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public final static int STYLE_RN = 1;
	/**
	 * The UNIX and Java line ending ("\n")
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public final static int STYLE_UNIX = 2;
	/**
	 * The UNIX and Java line ending ("\n")
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public final static int STYLE_N = 2;
	/**
	 * The UNIX and Java line ending ("\n")
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public final static int STYLE_JAVA = 2;
	/**
	 * The MacIntosh line ending ("\r")
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public final static int STYLE_MAC = 3;
	/**
	 * The MacIntosh line ending ("\r")
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public final static int STYLE_R = 3;

	/**
	 * Buffer size when reading from input stream.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	private final static int BUFFER_SIZE = 1024;
	private final static int STATE_INIT = 0;
	private final static int STATE_R = 1;

	private final static int MASK_N = 0x01;
	private final static int MASK_R = 0x02;
	private final static int MASK_RN = 0x04;

	/**
	 * Change the line endings of the text on the input stream and write
	 * it to the output stream.
	 *
	 * The current system's line separator is used.
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
		return convert(in, out, STYLE_SYSTEM, true);
	}

	/**
	 * Change the line endings of the text on the input stream and write
	 * it to the output stream.
	 *
	 * @param in stream that contains the text which needs line number conversion.
	 * @param out stream where converted text is written.
	 * @param style line separator style.
	 * @return true if the output was modified from the input, false if it is exactly the same
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 * @throws IllegalArgumentException if an unknown style is requested.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(InputStream in, OutputStream out, int style) throws IOException {
		return convert(in, out, style, true);
	}

	/**
	 * Change the line endings of the text on the input stream and write
	 * it to the output stream.
	 *
	 * The current system's line separator is used.
	 *
	 * @param in stream that contains the text which needs line number conversion.
	 * @param out stream where converted text is written.
	 * @param binaryException throw an exception and abort the operation if binary data is encountered and binaryExcepion is false.
	 * @return true if the output was modified from the input, false if it is exactly the same
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(InputStream in, OutputStream out, boolean binaryException) throws IOException {
		return convert(in, out, STYLE_SYSTEM, binaryException);
	}

	/**
	 * Change the line endings of the text on the input stream and write
	 * it to the output stream.
	 *
	 * @param in stream that contains the text which needs line number conversion.
	 * @param out stream where converted text is written.
	 * @param style line separator style.
	 * @param binaryException throw an exception and abort the operation if binary data is encountered and binaryExcepion is false.
	 * @return true if the output was modified from the input, false if it is exactly the same
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 * @throws IllegalArgumentException if an unknown style is requested.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(InputStream in, OutputStream out, int style, boolean binaryException) throws IOException {
		byte[] lineEnding;
		switch (style) {
			case STYLE_SYSTEM: {
				 lineEnding = System.getProperty("line.separator").getBytes();
			} break;
			case STYLE_RN: {
				 lineEnding = new byte[]{(byte)'\r',(byte)'\n'};
			} break;
			case STYLE_R: {
				 lineEnding = new byte[]{(byte)'\r'};
			} break;
			case STYLE_N: {
				 lineEnding = new byte[]{(byte)'\n'};
			} break;
			default: {
				throw new IllegalArgumentException("Unknown line break style: " + style);
			}
		}
		byte[] buffer = new byte[BUFFER_SIZE];
		int read;
		int state = STATE_INIT;
		int seen = 0x00;
		while((read = in.read(buffer)) != -1){
			for (int i=0; i<read; i++){
				byte b = buffer[i];
				if (state==STATE_R){
					if(b!='\n'){
						out.write(lineEnding);
						seen |= MASK_R;
					}
				}
				if (b=='\r'){
					state = STATE_R;
				} else {
					if (b=='\n'){
						if (state==STATE_R){
							seen |= MASK_RN;
						} else {
							seen |= MASK_N;
						}
						out.write(lineEnding);
					} else if(binaryException && b!='\t' && b!='\f' && (b & 0xff)<32){
						throw new BinaryDataException(labels.getString("binaryexcepion"));
					} else {
						out.write(b);
					}
					state = STATE_INIT;
				}
			}
		}
		if (state==STATE_R){
			out.write(lineEnding);
			seen |= MASK_R;
		}
		if (lineEnding.length==2 && lineEnding[0]=='\r' && lineEnding[1]=='\n'){
			return ((seen & ~MASK_RN)!=0);
		} else if (lineEnding.length==1 && lineEnding[0]=='\r'){
			return ((seen & ~MASK_R)!=0);
		} else if (lineEnding.length==1 && lineEnding[0]=='\n'){
			return ((seen & ~MASK_N)!=0);
		} else {
			return true;
		}
	}

	/**
	 * Change the line endings on given file.
	 *
	 * The current system's line separator is used.
	 *
	 * @param f File to be converted.
	 * @return true if the file was modified, false if it was already in the correct format
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(File f) throws IOException {
		return convert(f, STYLE_SYSTEM, true);
	}

	/**
	 * Change the line endings on given file.
	 *
	 * @param f File to be converted.
	 * @param style line separator style.
	 * @return true if the file was modified, false if it was already in the correct format
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 * @throws IllegalArgumentException if an unknown style is requested.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(File f, int style) throws IOException {
		return convert(f, style, true);
	}

	/**
	 * Change the line endings on given file.
	 *
	 * The current system's line separator is used.
	 *
	 * @param f File to be converted.
	 * @param binaryException throw an exception and abort the operation if binary data is encountered and binaryExcepion is false.
	 * @return true if the file was modified, false if it was already in the correct format
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(File f, boolean binaryException) throws IOException {
		return convert(f, STYLE_SYSTEM, binaryException);
	}

	/**
	 * Change the line endings on given file.
	 *
	 * @param f File to be converted.
	 * @param style line separator style.
	 * @param binaryException throw an exception and abort the operation if binary data is encountered and binaryExcepion is false.
	 * @return true if the file was modified, false if it was already in the correct format
	 * @throws BinaryDataException if non-text data is encountered.
	 * @throws IOException if an input or output error occurs.
	 * @throws IllegalArgumentException if an unknown style is requested.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static boolean convert(File f, int style, boolean binaryException) throws IOException {
		File temp = null;
		InputStream in = null;
		OutputStream out = null;
		boolean modified = false;
		try {
			in = new FileInputStream(f);
			temp = File.createTempFile("LineEnds", null, null);
			out = new FileOutputStream(temp);
			modified = convert (in, out, style, binaryException);
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
}
