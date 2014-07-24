/*
 * Copyright (C) 2007-2010 Stephen Ostermiller
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

import junit.framework.TestCase;

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
public class CmdLnTest extends TestCase {

	private CmdLnOption fullHelpOption = new CmdLnOption("help",'h').setDescription("this is the description, it can be long");

	public void test80CharHelp(){
		assertEquals("  -h --help  this is the description, it can be long", fullHelpOption.getHelp("--","-",0,80));
	}

	public void testTextToEdge(){
		assertEquals("  -h --help  this is the description, it can be long", fullHelpOption.getHelp("--","-",0,52));
	}

	public void testTextOnePastEdge(){
		assertEquals("  -h --help  this is the description, it can be\n        long", fullHelpOption.getHelp("--","-",0,51));
	}

	public void test37CharHelp(){
		assertEquals("  -h --help  this is the description,\n        it can be long", fullHelpOption.getHelp("--","-",0,37));
	}

	public void test38CharHelp(){
		assertEquals("  -h --help  this is the description,\n        it can be long", fullHelpOption.getHelp("--","-",0,38));
	}

	public void test39CharHelp(){
		assertEquals("  -h --help  this is the description,\n        it can be long", fullHelpOption.getHelp("--","-",0,39));
	}

	private CmdLnOption shortHelpOption = new CmdLnOption('h').setDescription("description");

	public void test10CharHelp(){
		assertEquals("  -h  description", shortHelpOption.getHelp("--","-",0,10));
	}

	private CmdLnOption shortHelpOptionalOption = new CmdLnOption('h').setDescription("description").setOptionalArgument();

	public void test60CharHelp(){
		assertEquals("  -h <?>  description", shortHelpOptionalOption.getHelp("--","-",0,60));
	}

	private CmdLn dashDashHelpCmdLn = new CmdLn(
		new String[]{"--help"}
	).addOption(
		new CmdLnOption("help", 'h')
	);

	public void testDashDashHelpH(){
		assertNotNull(dashDashHelpCmdLn.getResult('h'));
	}

	public void testDashDashHelpHelp(){
		assertNotNull(dashDashHelpCmdLn.getResult("help"));
	}

	public void testDashDashHelpCapH(){
		assertNull(dashDashHelpCmdLn.getResult('H'));
	}

	public void testDashDashHelpCapHelp(){
		assertNull(dashDashHelpCmdLn.getResult("HELP"));
	}

	public void testDashDashHelpLongH(){
		assertNull(dashDashHelpCmdLn.getResult("h"));
	}

	public void testDashDashHelpNoLeftOvers(){
		assertEquals(0, dashDashHelpCmdLn.getNonOptionArguments().size());
	}

	private CmdLn dashHAndOptionalArgumentCmdLn = new CmdLn(
		new String[]{"-h"}
	).addOption(
		new CmdLnOption("help", 'h')
	).addOption(
		new CmdLnOption("argument").setOptionalArgument()
	);

	public void testDashHandOptionArgumentH(){
		assertNotNull(dashHAndOptionalArgumentCmdLn.getResult('h'));
	}

	public void testDashHandOptionArgumentHelp(){
		assertNotNull(dashHAndOptionalArgumentCmdLn.getResult("help"));
	}

	public void testDashHandOptionArgumentNotPresent(){
		assertNull(dashHAndOptionalArgumentCmdLn.getResult("argument"));
	}

	public void testDashHandOptionArgumentNoLeftOvers(){
		assertEquals(0, dashHAndOptionalArgumentCmdLn.getNonOptionArguments().size());
	}

	private CmdLn fileAndLeftoverCmdLn = new CmdLn(
		new String[]{"-f","file","one"}
	).addOption(
		new CmdLnOption('f').setRequiredArgument()
	);

	public void testfileAndLeftoverCmdLnF(){
		assertNotNull(fileAndLeftoverCmdLn.getResult('f'));
	}

	public void testfileAndLeftoverCmdLnFArgumentCount(){
		assertEquals(1, fileAndLeftoverCmdLn.getResult('f').getArgumentCount());
	}

	public void testfileAndLeftoverCmdLnFArgument(){
		assertEquals("file", fileAndLeftoverCmdLn.getResult('f').getArgument());
	}

	public void testfileAndLeftoverCmdLnLeftoverSize(){
		assertEquals(1, fileAndLeftoverCmdLn.getNonOptionArguments().size());
	}

	public void testfileAndLeftoverCmdLnLeftoverArgument(){
		assertEquals("one", fileAndLeftoverCmdLn.getNonOptionArguments().get(0));
	}

	private CmdLn manyArgsCmdLn = new CmdLn(
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

	public void testManyArgsCmdLnF(){
		assertNotNull(manyArgsCmdLn.getResult('f') == null);
	}

	public void testManyArgsCmdLnFArgumentCount(){
		assertEquals(3, manyArgsCmdLn.getResult('f').getArgumentCount());
	}

	public void testManyArgsCmdLnFArgument(){
		assertEquals("-", manyArgsCmdLn.getResult('f').getArgument());
	}

	public void testManyArgsCmdLnFFirstArgument(){
		assertEquals("-", manyArgsCmdLn.getResult('f').getArguments().get(0));
	}

	public void testManyArgsCmdLnFSecondArgument(){
		assertEquals("2", manyArgsCmdLn.getResult('f').getArguments().get(1));
	}

	public void testManyArgsCmdLnFThirdArgument(){
		assertEquals("3", manyArgsCmdLn.getResult('f').getArguments().get(2));
	}

	public void testManyArgsCmdLnNonOptionArgumentsSize(){
		assertEquals(0, manyArgsCmdLn.getNonOptionArguments().size());
	}

	public void testManyArgsCmdLnT(){
		assertTrue(manyArgsCmdLn.present('t'));
	}

	public void testManyArgsCmdLnTArgument(){
		assertEquals("hello", manyArgsCmdLn.getResult('t').getArgument());
	}

	public void testManyArgsCmdLnCar(){
		assertNotNull(manyArgsCmdLn.present("car"));
	}

	public void testManyArgsCmdLnCarArgument(){
		assertEquals("thirty", manyArgsCmdLn.getResult("car").getArgument());
	}

	public void testManyArgsCmdLnP(){
		assertNotNull(manyArgsCmdLn.present('p'));
	}

	public void testManyArgsCmdLnPArgument(){
		assertEquals("", manyArgsCmdLn.getResult('p').getArgument());
	}
	private CmdLn dashHelpCmdLn = new CmdLn(
		new String[]{"-help"}
	).addOption(
		new CmdLnOption("help")
	).setOptionStarts("-", null);

	public void testDashHelpCmdLnHelp(){
		assertTrue(dashHelpCmdLn.present("help"));
	}

	public void testDashHelpCmdLnH(){
		assertFalse(dashHelpCmdLn.present('h'));
	}

	CmdLn startCharsCmdLn = new CmdLn(
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

	public void testStartCharsCmdLnA(){
		assertTrue(startCharsCmdLn.present('a'));
	}

	public void testStartCharsCmdLnI(){
		assertTrue(startCharsCmdLn.present('i'));
	}

	public void testStartCharsCmdLnR(){
		assertTrue(startCharsCmdLn.present('r'));
	}

	public void testStartCharsCmdLnRArgument(){
		assertEquals("wall", startCharsCmdLn.getResult('r').getArgument());
	}

	public void testStartCharsCmdLnBed(){
		assertTrue(startCharsCmdLn.present("bed"));
	}

	public void testStartCharsCmdLnBedArgument(){
		assertEquals("soft",startCharsCmdLn.getResult("bed").getArgument());
	}

	public void testStartCharsCmdLnF(){
		assertTrue(startCharsCmdLn.present('f'));
	}

	public void testStartCharsCmdLnO(){
		assertTrue(startCharsCmdLn.present('o'));
	}

	public void testStartCharsCmdLnG(){
		assertTrue(startCharsCmdLn.present('g'));
	}

	public void testStartCharsCmdLnHum(){
		assertTrue(startCharsCmdLn.present("hum"));
	}

	CmdLn argBoundsCmdLn = new CmdLn(
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

	public void testArgBoundsCmdLn(){
		assertEquals(5, argBoundsCmdLn.getNonOptionArguments().size());
	}

	public void testArgBoundsCmdLnR(){
		assertNull(argBoundsCmdLn.getResult('r'));
	}

	public void testArgBoundsCmdLnP(){
		assertNotNull(argBoundsCmdLn.getResult('p'));
	}

	public void testArgBoundsCmdLnPArgument(){
		assertNull(argBoundsCmdLn.getResult('p').getArgument());
	}

	public void testArgBoundsCmdLnTArgument(){
		assertEquals("2", argBoundsCmdLn.getResult('t').getArgument());
	}

	public void testArgBoundsCmdLnFirstNonOptionAgument(){
		assertEquals("3", argBoundsCmdLn.getNonOptionArguments().get(0));
	}

	public void testArgBoundsCmdLnSecondNonOptionArgument(){
		assertEquals("4", argBoundsCmdLn.getNonOptionArguments().get(1));
	}

	public void testArgBoundsCmdLnIArgument(){
		assertEquals("5", argBoundsCmdLn.getResult('i').getArgument());
	}

	public void testArgBoundsCmdLnSArgument(){
		assertEquals("6", argBoundsCmdLn.getResult('s').getArgument());
	}

	public void testArgBoundsCmdLnSArgumentCount(){
		assertEquals(4, argBoundsCmdLn.getResult('s').getArgumentCount());
	}

	public void testArgBoundsCmdLnThirdNonOptionArgument(){
		assertEquals("10", argBoundsCmdLn.getNonOptionArguments().get(2));
	}

	private CmdLn endOptionsCmdLn = new CmdLn(
		new String[]{"-f","--","-t"}
	).addOption(
		new CmdLnOption('f')
	);

	public void testEndOptionsCmdLnF(){
		assertTrue(endOptionsCmdLn.present('f'));
	}

	public void testEndOptionsCmdLnFArgumentCount(){
		assertEquals(0, endOptionsCmdLn.getResult('f').getArgumentCount());
	}

	public void testEndOptionsCmdLnT(){
		assertFalse(endOptionsCmdLn.present('t'));
	}

	public void testEndOptionsCmdLnNonOptionArgumentsSize(){
		assertEquals(1, endOptionsCmdLn.getNonOptionArguments().size());
	}

	public void testEndOptionsCmdLnFirstNonOptionArgument(){
		assertEquals("-t", endOptionsCmdLn.getNonOptionArguments().get(0));
	}

	private UnknownCmdLnOptionException uclox = generateUnknownCmdLnOptionException();
	private UnknownCmdLnOptionException generateUnknownCmdLnOptionException() {
		CmdLn cmdLn = new CmdLn(
			new String[]{"-f"}
		);
		try {
			cmdLn.parse();
		} catch (UnknownCmdLnOptionException x){
			return x;
		}
		return null;
	}

	public void testUnknownCmdLnOptionExceptionGenerated(){
		assertNotNull(uclox);
	}

	public void testUnknownCmdLnOptionExceptionForF(){
		assertEquals("f", uclox.getOption());
	}

	private ExtraCmdLnArgumentException eclax = generateExtraCmdLnArgumentException();
	private ExtraCmdLnArgumentException generateExtraCmdLnArgumentException(){
		CmdLn cmdLn = new CmdLn(
			new String[]{"-f=oops"}
		).addOption(
			new CmdLnOption('f')
		);
		try {
			cmdLn.parse();
		} catch (ExtraCmdLnArgumentException x){
			return x;
		}
		return null;
	}

	public void testExtraCmdLnArgumentExceptionGenerated(){
		assertNotNull(eclax);
	}

	public void testExtraCmdLnArgumentExceptionForF(){
		assertEquals("f", eclax.getOption().toString());
	}

	private MissingCmdLnArgumentException mclax = generateMissingCmdLnArgumentException();
	private MissingCmdLnArgumentException generateMissingCmdLnArgumentException(){
		CmdLn cmdLn = new CmdLn(
			new String[]{"-f"}
		).addOption(
			new CmdLnOption('f').setRequiredArgument()
		);
		try {
			cmdLn.parse();
		} catch (MissingCmdLnArgumentException x){
			return x;
		}
		return null;
	}

	public void testMissingCmdLnArgumentExceptionGenerated(){
		assertNotNull(mclax);
	}

	public void MissingCmdLnArgumentExceptionForF(){
		assertEquals("f", mclax.getOption().toString());
	}
}
