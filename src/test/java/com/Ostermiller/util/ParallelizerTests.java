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

import java.util.*;

/**
 * Regression test for Parallelizer.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/Parallelizer.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.04.00
 */
class ParallelizerTests {
	/**
	 * Main method for tests
	 * @param args command line arguments (ignored)
	 */
	public static void main (String[] args){
		try {
			final HashMap<String,Date> results = new HashMap<String,Date>();
			final Random random = new Random();
			Parallelizer pll = new Parallelizer(8);
			for(int i=0; i<100; i++){
				final String hashKey = Integer.toString(i);
				pll.run(
					new Runnable(){
						public void run(){
							try {
								Thread.sleep(random.nextInt(5000));
								results.put(hashKey,new Date());
							} catch (RuntimeException rx){
								throw rx;
							} catch (Exception x){
								throw new RuntimeException(x);
							}
						}
					}
				);
			}
			if (results.size() == 100) throw new Exception("Expected results to not yet have 100 items in it.");
			pll.join();
			if (results.size() != 100) throw new Exception("Expected results to have 100 items, not " + results.size());
			for(int i=0; i<100; i++){
				String hashKey = Integer.toString(i);
				Date result = results.get(hashKey);
				if (result == null) throw new Exception(hashKey + " not in map");
			}
			System.exit(0);

			pll = new Parallelizer();
			pll.run(
				new Runnable(){
					public void run(){
						throw new RuntimeException("Testing Parallelizer");
					}
				}
			);

			try {
				pll.join();
				throw new Exception("Parallelizer appears not to have thrown expected exception");
			} catch (RuntimeException rtx){
				if (!"Testing Parallelizer".equals(rtx.getMessage())){
					throw new Exception("Expected Testing Parallelizer as message to exception");
				}
			}

		} catch (Throwable x){
			x.printStackTrace(System.err);
			System.exit(1);
		}
	}
}
