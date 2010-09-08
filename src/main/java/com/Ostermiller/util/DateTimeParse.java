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

	public enum Field {
		YEAR,
		MONTH,
		DAY
	}

	public DateTimeParse(){
	}

	private Field[] fieldOrder = new Field[]{Field.MONTH,Field.DAY,Field.YEAR};

	public void setFieldOrder(List<Field> fieldOrder){
		setFieldOrder((Field[])fieldOrder.toArray());
	}

	public void setFieldOrder(Field[] fieldOrder){
		this.fieldOrder = copyAndVerify(fieldOrder);
	}

	public List<Field> getFieldOrder(){
		List<Field> l = new ArrayList<Field>();
		for (Field f: fieldOrder){
			l.add(f);
		}
		return l;
	}

	private static Field[] copyAndVerify(Field[] fieldOrder){
		boolean y = false;
		boolean m = false;
		boolean d = false;
		Field[] result = new Field[3];
		int i = 0;
		for (Field f: fieldOrder){
			switch (f){
				case YEAR: {
					if (!y){
						y = true;
						result[i] = f;
						i++;
					}
				} break;
				case MONTH: {
					if (!m){
						m = true;
						result[i] = f;
						i++;
					}
				} break;
				case DAY: {
					if (!d){
						d = true;
						result[i] = f;
						i++;
					}
				} break;
			}
		}
		for (int j=i; j<3; j++){
			if (!y){
				y = true;
				result[j] = Field.YEAR;
			} else if (!m){
				m = true;
				result[j] = Field.MONTH;
			} else if (!d){
				d = true;
				result[j] = Field.DAY;
			}
		}
		return result;
	}

	private static List<Field> setFieldOrder(String s){
		List<Field> l = new ArrayList<Field>();
		for(String name: s.split("[^A-Za-z]+")){
			if (name.length() > 0){
				l.add(Field.valueOf(name.toUpperCase()));
			}
		}
		return l;
	}

	private static final Map<String,Integer> MONTH_WORDS = getStringIntMap(
		"jan>1,january>1,feb>2,february>2,mar>3,march>3,apr>4,april>4,may>5,jun>6,"+
		"june>6,jul>7,july>7,aug>8,august>8,sep>9,sept>9,september>9,oct>10,october>10,"+
		"nov>11,november>11,dec>12,december>12"
	);

	private static final Map<String,Integer> ERA_WORDS = getStringIntMap(
		"bc>0,bce>0,ad>1,ce>1"
	);


	private static final Set<String> WEEKDAY_WORDS = getStringSet(
		"sunday,sun,su,monday,mon,mo,tuesday,tues,tue,tu,wednesday,wed,we,thursday,thur,"+
		"thu,th,friday,fri,fr,saturday,sat,sa,sunday,sun,su"
	);

	private static final Map<String,Integer> ORDINAL_WORDS = getStringIntMap(
		"1st>1,1rst>1,first>1,2nd>2,second>2,3rd>3,third>3,4th>4,4rth>4,fourth>4,5th>5,"+
		"fifth>5,6th>6,sixth>6,7th>7,seventh>7,8th>8,eighth>8,9th>9,ninth>9,10th>10,"+
		"tenth>10,11th>11,eleventh>11,12th>12,twelth>12,twelvth>12,twelveth>12,"+
		"twelfth>12,13th>13,thirteenth>13,14th>14,fourteenth>14,four-teenth>14,15th>15,"+
		"fifteenth>15,16th>16,sixteenth>16,six-teenth>16,17th>17,seventeenth>17,"+
		"seven-teenth>17,18th>18,eighteenth>18,19th>19,ninteenth>19,nineteenth>19,"+
		"nine-teenth>19,20th>20,twentieth>20,21st>21,twentyfirst>21,twenty-first>21,"+
		"22nd>22,twentysecond>22,twenty-second>22,23rd>23,twentythird>23,"+
		"twenty-third>23,24th>24,twentyfourth>24,twenty-fourth>24,25th>25,"+
		"twentyfifth>25,twenty-fifth>25,26th>26,twentysixth>26,twenty-sixth>26,27th>27,"+
		"twentyseventh>27,twenty-seventh>27,28th>28,twentyeighth>28,twenty-eighth>28,"+
		"29th>29,twentyninth>29,twenty-ninth>29,30th>30,thirtieth>30,31st>31,"+
		"thirtyfirst>31,thirty-first>31"
	);

	private static Map<String,Integer> getStringIntMap(String s){
		HashMap<String,Integer> m = new HashMap<String,Integer>();
		for(String pair: s.split("\\,")){
			int sep = pair.lastIndexOf(">");
			m.put(pair.substring(0, sep), Integer.valueOf(pair.substring(sep+1)));
		}
		return m;
	}

	private static Set<String> getStringSet(String s){
		HashSet<String> m = new HashSet<String>();
		for(String v: s.split("\\,")){
			m.add(v);
		}
		return m;
	}

	private YearExtensionPolicy yearExtensionPolicy = YearExtensionAround.NEAREST;

	private int defaultYear = Calendar.getInstance().get(Calendar.YEAR);

	/**
	 * Set the default year to use when there is no year in the parsed date.
	 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
	 * @since ostermillerutils 1.08.00
	 */
	public void setDefaultYear(int defaultYear){
		this.defaultYear = defaultYear;
	}

	/**
	 * Set the year extension policy.  This policy is responsible for extending two digit
	 * years into full years.  eg. 99 to 1999
	 * <p>
	 * The default policy is <code>YearExtensionAround.NEAREST</code>.  Several other
	 * pre-canned policies are available.
	 *
	 * @see com.Ostermiller.util.YearExtensionAround.NEAREST
	 * @see com.Ostermiller.util.YearExtensionAround.LATEST
	 * @see com.Ostermiller.util.YearExtensionAround.CENTURY_1999
	 * @see com.Ostermiller.util.YearExtensionAround.CENTURY_2000
	 * @see com.Ostermiller.util.YearExtensionNone.YEAR_EXTENSION_NONE
	 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
	 * @since ostermillerutils 1.08.00
	 */
	public DateTimeParse setYearExtensionPolicy(YearExtensionPolicy yearExtensionPolicy){
		this.yearExtensionPolicy = yearExtensionPolicy;
		return this;
	}

	public Date parse(String dateString){
		if (dateString == null) return null;
		try {
			DateTimeLexer lex = new DateTimeLexer(new StringReader(dateString));
			DateTimeToken tok;
			WorkingDateTime work = new WorkingDateTime();
			LinkedList<String> poss = null;
			while((tok=lex.getNextToken()) != null){
				String text = tok.getText();
				switch(tok.getType()){
					case NUMBER: {
						int value = Integer.parseInt(text);
						if (work.hasYear() && poss == null && !work.hasMonth() && !work.hasDay() && fieldOrder[0] != Field.YEAR && value <= 12){
							// Support YYYY-MM-DD format unless
							// the order is specifically YYYY-DD-MM
							work.setMonth(value);
						} else {
							Boolean set = work.setObviousDateField(text, value);
							if (set == null){
								if (poss == null) poss = new LinkedList<String>();
								poss.add(text);
							} else if (Boolean.FALSE.equals(set)) return null;
						}
					} break;
					case WORD: {
						text = text.toLowerCase();
						if (MONTH_WORDS.containsKey(text)){
							if (!work.setMonth(MONTH_WORDS.get(text).intValue())) return null;
						} else if (ORDINAL_WORDS.containsKey(text)){
							if (!work.setDay(ORDINAL_WORDS.get(text).intValue())) return null;
						} else if (WEEKDAY_WORDS.contains(text)){
							// ignore weekday words
						} else if (ERA_WORDS.containsKey(text)){
							if (!work.setEra(ERA_WORDS.get(text).intValue())) return null;
						} else {
							return null;
						}
					} break;
					case APOS_YEAR: {
						if (!work.setYear(text, Integer.parseInt(text))) return null;
					} break;
					case PUNCTUATION: break;
					default: return null;
				}
			}
			boolean assigned = true;
			while (assigned && poss != null){
				assigned = false;
				for (Iterator<String> i = poss.iterator(); i.hasNext(); ){
					String text = i.next();
					int value = Integer.parseInt(text);
					Boolean set = work.setObviousDateField(text, value);
					if (set != null){
						if (Boolean.FALSE.equals(set)) return null;
						assigned = true;
						i.remove();
					}
				}
			}
			while (poss != null && poss.size() > 0){
				String text = poss.removeFirst();
				work.setPreferredField(text, Integer.parseInt(text));
			}
			return work.getDate();
		} catch (Exception x){
			return null;
		}
	}

	private class WorkingDateTime {
		int year = -1;
		int month = -1;
		int day = -1;
		int era = -1;

		public Date getDate(){
			if (hasYear() && !hasMonth()){
				month = 1;
			}
			if (hasMonth() && !hasDay()){
				day = 1;
			}
			if (hasMonth() && !hasYear()){
				year = defaultYear;
			}
			if (!hasYear() || !hasMonth() || !hasDay()){
				return null;
			}
			Calendar c = new GregorianCalendar();
			c.clear();
			c.set(year, month-1, day);
			if (hasEra()){
				c.set(Calendar.ERA, era);
			}
			return c.getTime();
		}

		public boolean hasEra(){
			return era != -1;
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

		public boolean setEra(int value){
			if (hasEra()) return false;
			era = value;
			return true;
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

		public boolean setYear(String text, int value){
			if (hasYear()) return false;
			if (text.length() <= 2 && value < 100){
				value = yearExtensionPolicy.extendYear(value);
			}
			year = value;
			return true;
		}

		public boolean setPreferredField(String text, int value){
			for (Field field: fieldOrder){
				switch (field){
					case MONTH:{
						if (!hasMonth() && value <= 12){
							return setMonth(value);
						}
					} break;
					case DAY:{
						if (!hasDay() && value <= 31){
							return setDay(value);
						}
					} break;
					case YEAR:{
						if (!hasYear()){
							return setYear(text, value);
						}
					} break;
				}
			}
			return false;
		}

		public Boolean setObviousDateField(String text, int value){
			if (text.length() >=3 || value > 31){
				return setYear(text, value);
			} else if (hasYear() && value > 12 && value <= 31){
				return setDay(value);
			} else if (hasYear() && hasDay() && value <= 12){
				return setMonth(value);
			} else if (hasYear() && hasMonth() && value <= 31){
				return setDay(value);
			} else if (hasDay() && hasMonth()){
				return setYear(text, value);
			}
			return null;
		}
	}
}
