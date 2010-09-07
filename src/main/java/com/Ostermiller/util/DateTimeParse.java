/*
 * Copyright (C) 2010 Stephen Ostermiller
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

import java.io.StringReader;
import java.util.*;

/**
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.08.00
 */
public class DateTimeParse {

	public DateTimeParse(){
	}
	
	private static final Map<String,Integer> MONTH_WORDS = getMonthWords();
	
	private static Map<String,Integer> getMonthWords(){
		HashMap<String,Integer> m = new HashMap<String,Integer>();
		m.put("jan", 1);
		m.put("january", 1);
		m.put("feb", 2);
		m.put("february", 2);
		m.put("mar", 3);
		m.put("march", 3);
		m.put("apr", 4);
		m.put("april", 4);
		m.put("may", 5);
		m.put("jun", 6);
		m.put("june", 6);
		m.put("jul", 7);
		m.put("july", 7);
		m.put("aug", 8);
		m.put("august", 8);
		m.put("sep", 9);
		m.put("sept", 9);
		m.put("september", 9);
		m.put("oct", 10);
		m.put("october", 10);
		m.put("nov", 11);
		m.put("november", 11);
		m.put("dec", 12);
		m.put("december", 12);
		return m;
	}

	public Date parse(String dateString){
		if (dateString == null) return null;
		try {
			DateTimeLexer lex = new DateTimeLexer(new StringReader(dateString));
			DateTimeToken tok;
			WorkingDateTime work = new WorkingDateTime();
			LinkedList<Integer> poss = null;
			while((tok=lex.getNextToken()) != null){
				String text = tok.getText();
				switch(tok.getType()){
					case ERROR: return null;
					case NUMBER: {
						Integer value = Integer.valueOf(text);
						Boolean set = work.setObviousDateField(value.intValue());
						if (set == null){
							if (poss == null) poss = new LinkedList<Integer>();
							poss.add(new Integer(value));
						} else if (Boolean.FALSE.equals(set)) return null;
					} break;
					case WORD: {
						text = text.toLowerCase();
						if (MONTH_WORDS.containsKey(text)){
							if (!work.setMonth(MONTH_WORDS.get(text).intValue())) return null;
						} else {
							return null;
						}
					} break;
					case PUNCTUATION: break;
				}
			}
			boolean assigned = true;
			while (assigned && poss != null){
				assigned = false;
				for (Iterator<Integer> i = poss.iterator(); i.hasNext(); ){
					Integer value = i.next();
					Boolean set = work.setObviousDateField(value.intValue());
					if (set != null){
						if (Boolean.FALSE.equals(set)) return null;
						assigned = true;
						i.remove();
					}
				}
			}
			while (poss != null && poss.size() > 0){
				work.setPreferredField(poss.removeFirst().intValue());
			}
			return work.getDate();
		} catch (Exception x){
			return null;
		}
	}
}

class WorkingDateTime {
	int year = -1;
	int month = -1;
	int day = -1;
	
	public Date getDate(){
		if (!hasYear() || !hasMonth() || !hasDay()){
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month-1, day);
		return c.getTime();
	}
	
	public boolean hasYear(){
		return year != -1;
	}
	public boolean hasMonth(){
		return month != -1;
	}
	public boolean hasDay(){
		return day != -1;
	}
	
	public boolean setMonth(int value){
		if (hasMonth()) return false;
		month = value;
		return true;
	}
	
	public boolean setDay(int value){
		if (hasDay()) return false;
		day = value;
		return true;
	}
	
	public boolean setYear(int value){
		if (hasYear()) return false;
		year = value;
		return true;
	}
	
	public boolean setPreferredField(int value){
		if (!hasMonth() && value <= 12){
			return setMonth(value);		
		}
		if (!hasDay() && value <= 31){
			return setDay(value);		
		}
		if (!hasYear()){
			return setYear(value);		
		}
		return false;
	}
	
	public Boolean setObviousDateField(int value){
		if (value > 31){
			return setYear(value);
		} else if (hasYear() && value > 12 && value <= 31){
			return setDay(value);
		} else if (hasYear() && hasDay() && value <= 12){
			return setMonth(value);
		} else if (hasYear() && hasMonth() && value <= 31){
			return setDay(value);
		} else if (hasDay() && hasMonth()){
			return setYear(value);
		}
		return null;
	}
}
