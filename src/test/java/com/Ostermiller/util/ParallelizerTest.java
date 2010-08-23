/*
 * Copyright (C) 2005-2007 Stephen Ostermiller
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

import junit.framework.TestCase;
import java.util.*;

/**
 * Regression test for Parallelizer.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/Parallelizer.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.04.00
 */
public class ParallelizerTest extends TestCase {

	public void testSuccessfulRun() throws InterruptedException {
		final HashMap<String,Date> results = new HashMap<String,Date>();
		final Random random = new Random();
		Parallelizer pll = new Parallelizer(8);
		for(int i=0; i<100; i++){
			final String hashKey = Integer.toString(i);
			pll.run(
				new Runnable(){
					public void run(){
						try {
							Thread.sleep(random.nextInt(50));
							results.put(hashKey,new Date());
						} catch (InterruptedException x){
							throw new RuntimeException(x);
						}
					}
				}
			);
		}
		assertFalse(results.size() == 100);
		pll.join();
		assertEquals(100, results.size());
		for(int i=0; i<100; i++){
			assertNotNull(results.get(Integer.toString(i)));
		}
	}

	public void testRunWithException() throws InterruptedException {
		Parallelizer pll = new Parallelizer();
		pll.run(
			new Runnable(){
				public void run(){
					throw new RuntimeException("Testing Parallelizer");
				}
			}
		);
		RuntimeException rx = null;
		try {
			pll.join();
		} catch (RuntimeException rtx){
			rx = rtx;
		}
		assertNotNull(rx);
		assertEquals("Testing Parallelizer", rx.getMessage());
	}
}
