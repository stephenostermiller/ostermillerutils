/*
 * Copyright (C) 2005-2010 Stephen Ostermiller
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
import java.util.*;

/**
 * Regression test for Parallelizer. More information about this class is
 * available from <a target="_top" href=
 * "http://ostermiller.org/utils/Parallelizer.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller
 *         http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.04.00
 */
public class ParallelizerTest extends TestCase {

	private static final int NUMBER_OF_RUNS = 20;
	private static final int THREADS_PER_RUN = 6;
	private static final int SIMULTANEOUS_THREADS = 3;

	public void testSuccessfulRun() throws InterruptedException {
		for (int j = 0; j < NUMBER_OF_RUNS; j++) {
			final Date[] results = new Date[THREADS_PER_RUN];
			final Random random = new Random();
			Parallelizer pll = new Parallelizer(SIMULTANEOUS_THREADS);
			for (int i = 0; i < THREADS_PER_RUN; i++) {
				final int threadNum = i;
				pll.run(new Runnable() {
					public void run() {
						try {
							Thread.sleep(random.nextInt(50));
							results[threadNum] = new Date();
						} catch (InterruptedException x) {
							throw new RuntimeException(x);
						}
					}
				});
			}
			boolean somethingDidntRun = false;
			for (int i = 0; i < THREADS_PER_RUN; i++) {
				if (results[i] == null) {
					somethingDidntRun = true;
				}
			}
			assertTrue(somethingDidntRun);
			pll.join();
			for (int i = 0; i < THREADS_PER_RUN; i++) {
				assertNotNull("Thread " + i + " never ran", results[i]);
			}
		}
	}

	public void testRunWithException() throws InterruptedException {
		Parallelizer pll = new Parallelizer();
		pll.run(new Runnable() {
			public void run() {
				throw new RuntimeException("Testing Parallelizer");
			}
		});
		RuntimeException rx = null;
		try {
			pll.join();
		} catch (RuntimeException rtx) {
			rx = rtx;
		}
		assertNotNull(rx);
		assertEquals("Testing Parallelizer", rx.getMessage());
	}
}
