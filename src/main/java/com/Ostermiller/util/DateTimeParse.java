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

import com.Ostermiller.util.DateTimeToken.DateTimeTokenType;
import java.io.*;
import java.util.*;

/**
 * Parses a variety of formatted date strings with minimal configuration.  Unlike other
 * date parsers, there are no formats to specify.  There is a single parse string method.
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
		this(Locale.getDefault());
	}

	public DateTimeParse(Locale locale){
		loadResources(locale);
	}

	private Field[] fieldOrder = null;

	public void setFieldOrder(List<Field> fieldOrder){
		setFieldOrder((Field[])(fieldOrder.toArray(new Field[0])));
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

	private static List<Field> getFieldOrder(String s){
		List<Field> l = new ArrayList<Field>();
		for(String name: s.split("[^A-Za-z]+")){
			if (name.length() > 0){
				Field field = Field.valueOf(name.toUpperCase());
				l.add(field);
			}
		}
		return l;
	}

	private Map<String,Integer> monthWords = new HashMap<String,Integer>();
	private Map<String,Integer> eraWords = new HashMap<String,Integer>();
	private Set<String> weekdayWords = new HashSet<String>();
	private Map<String,Integer> ordinalWords = new HashMap<String,Integer>();
	private Map<String,Integer> amPmWords = new HashMap<String,Integer>();

	private static final String[] ALL_PROPERTIES = {
		"","da","de","en","es","fr","it","nl","pl","pt","ro","ru","sv","tr"
	};

	private void loadResources(Locale locale){
		Set<String> allKeys = new HashSet<String>();

		if (locale != null && locale.getLanguage() != null && locale.getLanguage().length() > 0){
			String langProp = locale.getLanguage();
			if (locale.getCountry() != null && locale.getCountry().length() > 0){
				String countryProp = langProp + "_" + locale.getCountry();
				if (locale.getVariant() != null && locale.getVariant().length() > 0){
					String variantProp = countryProp + "_" + locale.getVariant();
					loadProperties(allKeys, variantProp);
				}
				loadProperties(allKeys, countryProp);
			}
			loadProperties(allKeys, langProp);
		}

		for (String propertyName: ALL_PROPERTIES){
			loadProperties(allKeys, propertyName);
		}
	}

	private void loadProperties(Set<String> allKeys, String propertiesLocale){
		try {
			if (!"".equals(propertiesLocale)){
				propertiesLocale = "_" + propertiesLocale;
			}
			InputStream in = this.getClass().getResourceAsStream(
				"DateTimeParse" + propertiesLocale + ".properties"
			);
			if (in != null){
				UberProperties prop = new UberProperties();
				prop.load(new InputStreamReader(in, "UTF-8"));
				addStringInts(allKeys, ordinalWords, prop.getProperty("ordinalWords"));
				addStringInts(allKeys, amPmWords, prop.getProperty("amPmWords"));
				addStrings(allKeys, weekdayWords, prop.getProperty("weekdayWords"));
				addStringInts(allKeys, eraWords, prop.getProperty("eraWords"));
				addStringInts(allKeys, monthWords, prop.getProperty("monthWords"));
				if (fieldOrder == null){
					setFieldOrder(getFieldOrder(prop.getProperty("fieldOrder")));
				}
			}
		} catch (IOException iox){
			throw new RuntimeException(iox);
		}
	}

	private static void addStringInts(Set<String> allKeys, Map<String,Integer> addTo, String s){
		if (s == null) return;
		for(String pair: s.split("\\,")){
			int sep = pair.lastIndexOf(">");
			String key = pair.substring(0, sep).toLowerCase();
			if (!allKeys.contains(key)){
				allKeys.add(key);
				addTo.put(key, Integer.valueOf(pair.substring(sep+1)));
			}
		}
	}


	private static void addStrings(Set<String> allKeys, Set<String> addTo, String s){
		if (s == null) return;
		for(String key: s.split("\\,")){
			key = key.toLowerCase();
			if (!allKeys.contains(key)){
				allKeys.add(key);
				addTo.add(key);
			}
		}
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
	
	private LinkedList<DateTimeToken> getTokens(String dateString) throws IOException {
		DateTimeLexer lex = new DateTimeLexer(new StringReader(dateString));
		DateTimeToken token;
		LinkedList<DateTimeToken> l = new LinkedList<DateTimeToken>();
		while((token=lex.getNextToken()) != null){
			switch(token.getType()){
				case ERROR: return null;
				case SPACE: break;
				default: l.add(token);
			}
		}
		return l;		
	}

	/**
	 * Parse the given string into a Date.
	 *
	 * @param dateString String with a date representation.
	 * @return timestamp represented by the date string, or null if the date could not be parsed.
	 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
	 * @since ostermillerutils 1.08.00
	 */
	public Date parse(String dateString){
		if (dateString == null) return null;
		try {			
			LinkedList<DateTimeToken> tokens = getTokens(dateString);
			if (tokens == null || tokens.size() == 0){
				return null;
			}
			WorkingDateTime work = new WorkingDateTime();
			if(!setTime(work, tokens)) return null;
			if(!setObviousDateFields(work, tokens)) return null;
			if(!setPreferredDateNumberFields(work, tokens)) return null;
			if(!containsOnlySpacesAndPunctuation(tokens)) return null;
			return work.getDate();
		} catch (Exception x){
			return null;
		}
	}
	
	private static final int TIME_STATE_INIT = 0;
	private static final int TIME_STATE_HOUR = 1;
	private static final int TIME_STATE_HOUR_SEP = 2;
	private static final int TIME_STATE_MINUTE= 3;
	private static final int TIME_STATE_MINUTE_SEP = 4;
	private static final int TIME_STATE_SECOND= 5;
	private static final int TIME_STATE_DONE= 6;
	
	private boolean setTime(WorkingDateTime work, LinkedList<DateTimeToken> tokens){
		int start = 0;
		int end = 0;
	    int state = TIME_STATE_INIT;
		{
			int hour = -1;
			int position = 0;
		    Iterator<DateTimeToken> i;
			for(i = tokens.iterator(); i.hasNext(); position++){
				DateTimeToken token = i.next();
				switch(token.getType()){
					case NUMBER: {
						switch(state){
							case TIME_STATE_INIT: 
							case TIME_STATE_HOUR: {
								if (token.getValue() <= 24){
									start = position;
									hour = token.getValue();
									state = TIME_STATE_HOUR;
								}
							} break;
							case TIME_STATE_HOUR_SEP: {
								if (token.getValue() <= 60){
									work.setHour(hour);
									work.minute = token.getValue();
									state = TIME_STATE_MINUTE;
								} else {
									return false;
								}
							} break;
							case TIME_STATE_MINUTE_SEP: {
								if (token.getValue() <= 60){
									work.second = token.getValue();
									state = TIME_STATE_SECOND;
								} else {
									return false;
								}
							} break;
							default: {
								end = position;
								state = TIME_STATE_DONE;
							} break;
						}
					} break;
					case PUNCTUATION: {					
						switch(state){
							case TIME_STATE_INIT:  break;
							case TIME_STATE_HOUR: {
								if (":".equals(token.getText())){
									state = TIME_STATE_HOUR_SEP;
								} else {
									start = 0;
									end = 0;
									hour = -1;
									state = TIME_STATE_INIT;
								}
							} break;
							case TIME_STATE_HOUR_SEP: {
								start = 0;
								end = 0;
								hour = -1;
								state = TIME_STATE_INIT;
							} break;
							case TIME_STATE_MINUTE: {
								if (":".equals(token.getText())){					
									state = TIME_STATE_MINUTE_SEP;
								} else {
									end = position;
									state = TIME_STATE_DONE;
								}
							} break;
							default: {
								end = position;
								state = TIME_STATE_DONE;
							} break;
						}
					} break;
					case SPACE: break;
					default: {
						switch(state){
							case TIME_STATE_INIT: break;
							case TIME_STATE_HOUR: {
								start = 0;
								end = 0;
								hour = -1;
								state = TIME_STATE_INIT;
							} break;
							default: {
								end = position;
								state = TIME_STATE_DONE;
							} break;
						}
					}
				}		
			}
			if (!i.hasNext()){
				switch(state){
					case TIME_STATE_HOUR_SEP:
					case TIME_STATE_MINUTE_SEP: {
						return false;
					}
					case TIME_STATE_MINUTE:
					case TIME_STATE_SECOND: {
						end = tokens.size();
						state = TIME_STATE_DONE;
					}
					default: break;
				}
			}
		}
		if (state == TIME_STATE_DONE){
			int position = 0;
			for(Iterator<DateTimeToken> i = tokens.iterator(); i.hasNext(); position++){
				i.next();
				if (position >= start && position < end){
					i.remove();
				}
			}
		}
		return true;
	}
	
	private boolean setPreferredDateNumberFields(WorkingDateTime work, LinkedList<DateTimeToken> tokens){
		for(Iterator<DateTimeToken> i = tokens.iterator(); i.hasNext();){
			DateTimeToken token = i.next();
			if (token.getType() == DateTimeTokenType.NUMBER){
				if (!work.setPreferredField(token)) return false;
			}
			i.remove();
		}
		return true;
	}
	
	private boolean setObviousDateFields(WorkingDateTime work, LinkedList<DateTimeToken> tokens){
		int numberCount = 0;
		int tokensToExamine = 0;
		while (tokensToExamine != tokens.size()){
			tokensToExamine = tokens.size();
			for(Iterator<DateTimeToken> i = tokens.iterator(); i.hasNext();){
				DateTimeToken token = i.next();
				String text = token.getText();
				switch(token.getType()){
					case NUMBER: {
						int value = token.getValue();
						if (work.hasYear() && numberCount == 1 && !work.hasMonth() && !work.hasDay() && fieldOrder[0] != Field.YEAR && value <= 12){
							// Support YYYY-MM-DD format unless
							// the order is specifically YYYY-DD-MM
							if (!work.setMonth(value)) return false;
							i.remove();
						} else {
							Boolean set = work.setObviousDateNumberField(token);
							if (set != null){
								if (Boolean.FALSE.equals(set)) return false;
								i.remove();
							}
						}
						numberCount++;
					} break;
					case WORD: {
						text = text.toLowerCase();
						if (monthWords.containsKey(text)){
							if (!work.setMonth(monthWords.get(text).intValue())) return false;
						} else if (amPmWords.containsKey(text)){
							if (!work.setAmPm(amPmWords.get(text).intValue())) return false;
						} else if (ordinalWords.containsKey(text)){
							if (!work.setDay(ordinalWords.get(text).intValue())) return false;
						} else if (weekdayWords.contains(text)){
							// ignore weekday words
						} else if (eraWords.containsKey(text)){
							if (!work.setEra(eraWords.get(text).intValue())) return false;
						} else {
							return false;
						}
						i.remove();
					} break;
					case APOS_YEAR: {
						if (!work.setYear(token)) return false;
						i.remove();
					} break;
					case ORDINAL_DAY: {
						if (!work.setDay(token.getValue())) return false;
						i.remove();
					} break;
				}
			}
		}
		return true;
	}
	
	private boolean containsOnlySpacesAndPunctuation(LinkedList<DateTimeToken> tokens){
		for(Iterator<DateTimeToken> i = tokens.iterator(); i.hasNext();){
			DateTimeToken token = i.next();
			switch(token.getType()){
				case PUNCTUATION: 
				case SPACE: {
					i.remove();
				}break;
				default: return false;
			}
		}
		return true;
	}

	private class WorkingDateTime {
		int year = -1;
		int month = -1;
		int day = -1;
		int era = -1;
		int hour = -1;
		int minute = -1;
		int second = -1;
		int millisecond = -1;
		int amPm = -1;

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
			if (hasHour() && !hasYear() && !hasMonth() && !hasDay()){
				Calendar c = new GregorianCalendar();
				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH)+1;
				day = c.get(Calendar.DATE);
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
			if (hasHour()){
				int h = hour;
				if (isPm()){
					h += 12;
				}
				c.set(Calendar.HOUR, h);
			}
			if (hasMinute()){
				c.set(Calendar.MINUTE, minute);
			}
			if (hasSecond()){
				c.set(Calendar.SECOND, second);
			}
			if (hasMillisecond()){
				c.set(Calendar.MILLISECOND, millisecond);
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
		
		public boolean hasHour(){
			return hour != -1;
		}
		
		public boolean hasMinute(){
			return minute != -1;
		}
		
		public boolean hasSecond(){
			return second != -1;
		}
		
		public boolean hasAmPm(){
			return amPm != -1;
		}
		
		public boolean hasMillisecond(){
			return millisecond != -1;
		}

		public boolean setEra(int value){
			if (hasEra()) return false;
			era = value;
			return true;
		}
		
		public boolean isPm(){
			return amPm == 1;
		}
		
		public boolean setHour(int value){
			if (hasHour()) return false;
			if (isPm() && value > 12) return false;
			hour = value;
			return true;
		}
		
		public boolean setAmPm(int value){
			if (hasAmPm()) return false;
			if (hasHour() && hour > 12) return false;
			amPm = value;
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

		public boolean setYear(DateTimeToken t){
			if (hasYear()) return false;
			String text = t.getText();
			int value = t.getValue();
			if (text.length() <= 2 && value < 100){
				value = yearExtensionPolicy.extendYear(value);
			}
			year = value;
			return true;
		}

		public boolean setPreferredField(DateTimeToken t){
			int value = t.getValue();
			for (Field field: fieldOrder){
				switch (field){
					case MONTH:{
						if (!hasMonth() && value >= 1 && value <= 12){
							return setMonth(value);
						}
					} break;
					case DAY:{
						if (!hasDay() && value >= 1 && value <= 31){
							return setDay(value);
						}
					} break;
					case YEAR:{
						if (!hasYear()){
							return setYear(t);
						}
					} break;
				}
			}
			return false;
		}

		public Boolean setObviousDateNumberField(DateTimeToken t){
			String text = t.getText();
			int value = t.getValue();
			if (text.length() >=3 || value > 31){
				return setYear(t);
			} else if (hasYear() && value > 12 && value <= 31){
				return setDay(value);
			} else if (hasYear() && hasDay() && value <= 12){
				return setMonth(value);
			} else if (hasYear() && hasMonth() && value >= 1 && value <= 31){
				return setDay(value);
			} else if (hasDay() && hasMonth()){
				return setYear(t);
			}
			return null;
		}
	}
}
