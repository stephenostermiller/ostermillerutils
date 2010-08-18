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
 * Regression tests for the command line options.
 *
 * More information about this class and code samples for suggested use are
 * available from <a target="_top" href=
 * "http://ostermiller.org/utils/CmdLn.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.07.00
 */
class CmdLnTests {

	/**
	 * Main method for tests
	 * @param args command line options (ignored)
	 *
	 * @since ostermillerutils 1.07.00
	 */
	public static void main(String[] args){
		try {

			CmdLnOption option;

			option = new CmdLnOption("help",'h').setDescription("this is the description, it can be long");
			if (!"  -h --help  this is the description, it can be long".equals(option.getHelp("--","-",0,80))){
				throw new Exception("80 char help string should be: '  -h --help  this is the description, it can be long'");
			}
			if (!"  -h --help  this is the description,\n        it can be long".equals(option.getHelp("--","-",0,37))){
				throw new Exception("37 char help string should be: '  -h --help  this is the description,\n        it can be long'");
			}
			if (!"  -h --help  this is the description,\n        it can be long".equals(option.getHelp("--","-",0,38))){
				throw new Exception("38 char help string should be: '  -h --help  this is the description,\n        it can be long'");
			}
			if (!"  -h --help  this is the description,\n        it can be long".equals(option.getHelp("--","-",0,39))){
				throw new Exception("39 char help string should be: '  -h --help  this is the description,\n        it can be long'");
			}

			option = new CmdLnOption('h').setDescription("description");
			if (!"  -h  description".equals(option.getHelp("--","-",0,10))){
				throw new Exception("10 char help string should be: '  -h  description'");
			}
			option = new CmdLnOption('h').setDescription("description").setOptionalArgument();
			if (!"  -h <?>  description".equals(option.getHelp("--","-",0,60))){
				throw new Exception("60 char help string should be: '  -h <?>  description'");
			}

			CmdLn clo;

			clo = new CmdLn(
				new String[]{"--help"}
			).addOption(
				new CmdLnOption("help", 'h')
			);
			if (clo.getResult('h') == null){
				throw new Exception("h option should have been present");
			}
			if (clo.getResult("help") == null){
				throw new Exception("help option should have been present");
			}
			if (clo.present('H')){
				throw new Exception("H option should not have been present");
			}
			if (clo.present("HELP")){
				throw new Exception("HELP option should not have been present");
			}
			if (clo.present("h")){
				throw new Exception("h long option should not have been present");
			}
			if (clo.getNonOptionArguments().size() != 0){
				throw new Exception("there should not have been left over arguments");
			}

			clo = new CmdLn(
				new String[]{"-h"}
			).addOption(
				new CmdLnOption("help", 'h')
			).addOption(
				new CmdLnOption("argument").setOptionalArgument()
			);
			if (clo.getResult('h') == null){
				throw new Exception("h option should have been present");
			}
			if (clo.getResult("help") == null){
				throw new Exception("help option should have been present");
			}
			if (clo.getResult("argument") != null){
				throw new Exception("argument option should not have been present");
			}
			if (clo.getNonOptionArguments().size() != 0){
				throw new Exception("there should not have been left over arguments");
			}

			clo = new CmdLn(
				new String[]{"-f","file","one"}
			).addOption(
				new CmdLnOption('f').setRequiredArgument()
			);
			if (clo.getResult('f') == null){
				throw new Exception("f option should have been present");
			}
			if (clo.getResult('f').getArgumentCount() != 1){
				throw new Exception("f should have had one argument");
			}
			if (!"file".equals(clo.getResult('f').getArgument())){
				throw new Exception("f should have had an argument 'file'");
			}
			if (clo.getNonOptionArguments().size() != 1){
				throw new Exception("there should have been one left over argument");
			}
			if (!"one".equals(clo.getNonOptionArguments().get(0))){
				throw new Exception("left over argument should have been 'one'");
			}

			clo = new CmdLn(
				new String[]{"-f","-","2","3","-it=hello","--car:thirty","-p "}
			).addOption(
				new CmdLnOption('f').setUnlimitedArguments()
			).addOption(
				new CmdLnOption('i').setUnlimitedArguments()
			).addOption(
				new CmdLnOption('t').setOptionalArgument()
			).addOption(
				new CmdLnOption("car").setRequiredArgument()
			).addOption(
				new CmdLnOption('p').setRequiredArgument()
			);
			if (clo.getResult('f') == null){
				throw new Exception("f option should have been present");
			}
			if (clo.getResult('f').getArgumentCount() != 3){
				throw new Exception("f should have had three arguments");
			}
			if (!"-".equals(clo.getResult('f').getArgument())){
				throw new Exception("f should have had an argument '-'");
			}
			if (!"-".equals(clo.getResult('f').getArguments().get(0))){
				throw new Exception("f should have had an argument '-'");
			}
			if (!"2".equals(clo.getResult('f').getArguments().get(1))){
				throw new Exception("f should have had an argument '1'");
			}
			if (!"3".equals(clo.getResult('f').getArguments().get(2))){
				throw new Exception("f should have had an argument '2'");
			}
			if (clo.getNonOptionArguments().size() != 0){
				throw new Exception("there should have been no left over arguments");
			}
			if (!clo.present('t')){
				throw new Exception("t option should have been present");
			}
			if (!"hello".equals(clo.getResult('t').getArgument())){
				throw new Exception("t option should have had argument 'hello'");
			}
			if (!clo.present("car")){
				throw new Exception("t option should have been present");
			}
			if (!"thirty".equals(clo.getResult("car").getArgument())){
				throw new Exception("car option should have had argument 'thirty'");
			}
			if (!clo.present('p')){
				throw new Exception("p option should have been present");
			}
			if (!"".equals(clo.getResult('p').getArgument())){
				throw new Exception("p option should have had argument ''");
			}

			clo = new CmdLn(
				new String[]{"-help"}
			).addOption(
				new CmdLnOption("help")
			).setOptionStarts("-", null);
			if (!clo.present("help")){
				throw new Exception("help option should have been present");
			}
			if (clo.present('h')){
				throw new Exception("h option should not have been present");
			}

			clo = new CmdLn(
				new String[]{"!!!air=wall","@@@bed=soft","###fog","$$$hum"}
			).addOptions(
				new CmdLnOption[]{
					new CmdLnOption('a'),
					new CmdLnOption('i'),
					new CmdLnOption('r').setOptionalArgument(),
					new CmdLnOption("bed").setOptionalArgument(),
					new CmdLnOption('f'),
					new CmdLnOption('o'),
					new CmdLnOption('g'),
					new CmdLnOption("hum"),
				}
			).setOptionStarts(new String[]{"@@@","$$$"},new String[]{"!!!","###"});
			if (!clo.present('a')){
				throw new Exception("a option should have been present");
			}
			if (!clo.present('i')){
				throw new Exception("i option should have been present");
			}
			if (!clo.present('r')){
				throw new Exception("r option should have been present");
			}
			if (!"wall".equals(clo.getResult('r').getArgument())){
				throw new Exception("r should have had arument 'wall'");
			}
			if (!clo.present("bed")){
				throw new Exception("bed option should have been present");
			}
			if (!"soft".equals(clo.getResult("bed").getArgument())){
				throw new Exception("bed should have had arument 'soft'");
			}
			if (!clo.present('f')){
				throw new Exception("f option should have been present");
			}
			if (!clo.present('o')){
				throw new Exception("o option should have been present");
			}
			if (!clo.present('g')){
				throw new Exception("g option should have been present");
			}
			if (!clo.present("hum")){
				throw new Exception("hum option should have been present");
			}

			clo = new CmdLn(
				new String[]{"-i","1","-t","2","3","4","-i","5","-s","6","7","8","9","10","11","12","-p"}
			).addOptions(
				new CmdLnOption[]{
					new CmdLnOption('i').setRequiredArgument(),
					new CmdLnOption('s').setArgumentBounds(1,4),
					new CmdLnOption('t').setOptionalArgument(),
					new CmdLnOption('p').setOptionalArgument(),
					new CmdLnOption('r').setRequiredArgument(),
				}
			);
			if (clo.getNonOptionArguments().size() != 5){
				throw new Exception("there should have been five left over arguments");
			}
			if (clo.getResult('r') != null){
				throw new Exception("r should not have been present");
			}
			if (clo.getResult('p') == null){
				throw new Exception("p should have been present");
			}
			if (clo.getResult('p').getArgument() != null){
				throw new Exception("p should not have had an argument");
			}
			if (!"2".equals(clo.getResult('t').getArgument())){
				throw new Exception("t should have had an argument '2'");
			}
			if (!"3".equals(clo.getNonOptionArguments().get(0))){
				throw new Exception("'3' should have been a left over argument");
			}
			if (!"4".equals(clo.getNonOptionArguments().get(1))){
				throw new Exception("'4' should have been a left over argument");
			}
			if (!"5".equals(clo.getResult('i').getArgument())){
				throw new Exception("i should have had an argument '5'");
			}
			if (!"6".equals(clo.getResult('s').getArgument())){
				throw new Exception("i should have had an argument '6'");
			}
			if (clo.getResult('s').getArgumentCount() != 4){
				throw new Exception("s should have had 4 arguments");
			}
			if (!"10".equals(clo.getNonOptionArguments().get(2))){
				throw new Exception("'10' should have been a left over argument");
			}

			clo = new CmdLn(
				new String[]{"-f","--","-t"}
			).addOption(
				new CmdLnOption('f')
			);
			if (!clo.present('f')){
				throw new Exception("f option should have been present");
			}
			if (clo.getResult('f').getArgumentCount() != 0){
				throw new Exception("f should have had no arguments");
			}
			if (clo.present('t')){
				throw new Exception("t option should not have been present");
			}
			if (clo.getNonOptionArguments().size() != 1){
				throw new Exception("should have had one non optional argument");
			}
			if (!"-t".equals(clo.getNonOptionArguments().get(0))){
				throw new Exception("'-t' should have been non-optional argument");
			}

			clo = new CmdLn(
				new String[]{"-f"}
			);
			UnknownCmdLnOptionException uclox = null;
			try {
				clo.parse();
			} catch (UnknownCmdLnOptionException x){
				uclox = x;
			}
			if (uclox == null){
				throw new Exception("f option should have thrown exception");
			}
			if (!"f".equals(uclox.getOption())){
				throw new Exception("exception should have been for option f");
			}

			clo = new CmdLn(
				new String[]{"-f=oops"}
			).addOption(
				new CmdLnOption('f')
			);
			ExtraCmdLnArgumentException eclax = null;
			try {
				clo.parse();
			} catch (ExtraCmdLnArgumentException x){
				eclax = x;
			}
			if (eclax == null){
				throw new Exception("f option should have thrown exception");
			}
			if (!"f".equals(eclax.getOption().toString())){
				throw new Exception("exception should have been for option f");
			}

			clo = new CmdLn(
				new String[]{"-f"}
			).addOption(
				new CmdLnOption('f').setRequiredArgument()
			);
			MissingCmdLnArgumentException mclax = null;
			try {
				clo.parse();
			} catch (MissingCmdLnArgumentException x){
				mclax = x;
			}
			if (mclax == null){
				throw new Exception("f option should have thrown exception");
			}
			if (!"f".equals(mclax.getOption().toString())){
				throw new Exception("exception should have been for option f");
			}

		} catch (Exception x){
			x.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
