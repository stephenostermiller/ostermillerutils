/*
 * Copyright (C) 2010-2012 Stephen Ostermiller
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

import java.util.TimeZone;
import com.Ostermiller.util.DateTimeToken.DateTimeTokenType;
import java.io.*;
import java.util.*;

/**
 * Parses a variety of formatted date strings with minimal configuration.  Unlike other
 * date parsers, there are no formats to specify.  There is a single parse string method.
 * <p>
 * A wide variety of formats are supported:
 * <ul>
 * <li> Four digit years (1999)
 * <li> Two digit years with an apostrophe ('99) with customizable year extension policy
 * <li> Month numbers (11 for November; 7 or 07 for July)
 * <li> Full month names spelled out (November)
 * <li> Abbreviated month names (Nov and No)
 * <li> Month names in several languages (Noviembre)
 * <li> Day numbers (1 or 01 for the first day of the month)
 * <li> Ordinal day of the month numbers (June first or 1st)
 * <li> Ordinal day of the month in several languages (primera, 1o)
 * <li> Locale appropriate parsing for ambiguous ordering (01-02-1999) with set-able locale
 * <li> Day of the week in several languages
 * <li> Era in several languages (AD, BC, BCE)
 * <li> Hour minute time (11:52)
 * <li> Hour minute second time (11:52:33)
 * <li> Standard date format with a "T" separating date and time (1997-07-16T19:20)
 * <li> AM/PM in several languages
 * <li> Numeric time zones (-0500)
 * </ul>
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
	private Map<String,Integer> ampmWords = new HashMap<String,Integer>();

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
				addStringInts(allKeys, ampmWords, prop.getProperty("ampmWords"));
				addStrings(allKeys, weekdayWords, prop.getProperty("weekdayWords"));
				addStringInts(allKeys, eraWords, prop.getProperty("eraWords"));
				addStringInts(allKeys, monthWords, prop.getProperty("monthWords"));
				if (fieldOrder == null && prop.getProperty("fieldOrder") != null){
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
			String key = pair.substring(0, sep).trim().toLowerCase();
			if (!allKeys.contains(key)){
				String value = pair.substring(sep+1).trim();
				if (!key.equals(value)){ // Don't map numbers to themselves eg 1>1
					allKeys.add(key);
					addTo.put(key, Integer.valueOf(value));
				}
			}
		}
	}


	private static void addStrings(Set<String> allKeys, Set<String> addTo, String s){
		if (s == null) return;
		for(String key: s.split("\\,")){
			key = key.trim().toLowerCase();
			if (!allKeys.contains(key)){
				allKeys.add(key);
				addTo.add(key);
			}
		}
	}

	private YearExtensionPolicy yearExtensionPolicy = YearExtensionAround.NEAREST;

	private int defaultYear = Calendar.getInstance().get(Calendar.YEAR);
	private TimeZone defaultZone = TimeZone.getDefault();

	/**
	 * Set the default year to use when there is no year in the parsed date.
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
	 * policies are implemented:
	 * <ul>
	 * <li>YearExtensionAround.NEAREST
	 * <li>YearExtensionAround.LATEST
	 * <li>YearExtensionAround.CENTURY_1999
	 * <li>YearExtensionAround.CENTURY_2000
	 * <li>YearExtensionNone.YEAR_EXTENSION_NONE
	 * </ul>
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
			if(!setZone(work, tokens)) work.setZone(defaultZone);
			if(!setObviousDateFields(work, tokens)) return null;
			if(!setPreferredDateNumberFields(work, tokens)) return null;
			if(!containsOnlySpacesAndPunctuation(tokens)) return null;
			return work.getDate();
		} catch (Exception x){
			x.printStackTrace(System.err);
			return null;
		}
	}

	public DateTimeParse setTimeZone(TimeZone zone){
		this.defaultZone = zone;
		return this;
	}

	private static final int ZONE_STATE_INIT = 0;
	private static final int ZONE_STATE_PLUS_MINUS = 1;
	private static final int ZONE_STATE_HOUR = 2;
	private static final int ZONE_STATE_HOUR_SEP = 3;
	private static final int ZONE_STATE_DONE = 4;

	private boolean setZone(WorkingDateTime work, LinkedList<DateTimeToken> tokens){
		int start = 0;
		int end = 0;
		int hour = -1;
		int minute = -1;
		boolean plusMinus = true;
		int state = ZONE_STATE_INIT;
		{
			int position = 0;
			Iterator<DateTimeToken> i;
			for(i = tokens.iterator(); i.hasNext() && state != ZONE_STATE_DONE; position++){
				DateTimeToken token = i.next();
				switch(token.getType()){
					case NUMBER: {
						switch(state){
							case ZONE_STATE_PLUS_MINUS: {
								String tokenText = token.getText();
								if (tokenText.length() == 4){
									String hourPart = tokenText.substring(0,2);
									String minutePart = tokenText.substring(2,4);
									try {
										int hours = Integer.parseInt(hourPart);
										int minutes = Integer.parseInt(minutePart);
										if (hours <=24 && minutes <= 60){
											hour = hours;
											minute = minutes;
											end = position;
											state = ZONE_STATE_DONE;
										} else {
											start = 0;
											end = 0;
											hour = -1;
											minute = -1;
											plusMinus = true;
											state = ZONE_STATE_INIT;
										}
									} catch (NumberFormatException nfx){
										start = 0;
										end = 0;
										hour = -1;
										minute = -1;
										plusMinus = true;
										state = ZONE_STATE_INIT;
									}
								} else if (token.getValue() <= 24){
									hour = token.getValue();
									state = ZONE_STATE_HOUR;
								} else {
									start = 0;
									end = 0;
									hour = -1;
									minute = -1;
									plusMinus = true;
									state = ZONE_STATE_INIT;
								}
							} break;
							case ZONE_STATE_HOUR_SEP: {
								if (token.getValue() <= 60){
									minute = token.getValue();
									end = position;
									state = ZONE_STATE_DONE;
								} else {
									start = 0;
									end = 0;
									hour = -1;
									minute = -1;
									plusMinus = true;
									state = ZONE_STATE_INIT;
								}
							} break;
							default: {
								start = 0;
								end = 0;
								hour = -1;
								minute = -1;
								plusMinus = true;
								state = ZONE_STATE_INIT;
							} break;
						}
					} break;
					case PUNCTUATION: {
						switch(state){
							case ZONE_STATE_INIT:  {
								if ("+".equals(token.getText())){
									start = position;
									plusMinus = true;
									state = ZONE_STATE_PLUS_MINUS;
								} else if ("-".equals(token.getText())){
									start = position;
									plusMinus = false;
									state = ZONE_STATE_PLUS_MINUS;
								}
							} break;
							case ZONE_STATE_HOUR_SEP:
							case ZONE_STATE_HOUR: {
								if (":".equals(token.getText())){
									state = ZONE_STATE_HOUR_SEP;
								} else {
									start = 0;
									end = 0;
									hour = -1;
									minute = -1;
									plusMinus = true;
									state = ZONE_STATE_INIT;
								}
							} break;
							default: {
								start = 0;
								end = 0;
								hour = -1;
								minute = -1;
								plusMinus = true;
								state = ZONE_STATE_INIT;
							} break;
						}
					} break;
					case SPACE: {
						switch(state){
							case ZONE_STATE_PLUS_MINUS: break;
							case ZONE_STATE_HOUR_SEP:
							case ZONE_STATE_HOUR: {
								state = ZONE_STATE_HOUR_SEP;
							} break;
							default: {
								start = 0;
								end = 0;
								hour = -1;
								minute = -1;
								plusMinus = true;
								state = ZONE_STATE_INIT;
							} break;
						}
					} break;
					default: {
						start = 0;
						end = 0;
						hour = -1;
						minute = -1;
						plusMinus = true;
						state = ZONE_STATE_INIT;
					} break;
				}
			}
		}
		if (state == ZONE_STATE_DONE){
			String zoneOffset = (plusMinus?"+":"-") + StringHelper.prepad(hour, 2) + ":" + StringHelper.prepad(minute, 2);
			TimeZone zone = TimeZone.getTimeZone("GMT"+zoneOffset);
			work.setZone(zone);
			int position = 0;
			for(Iterator<DateTimeToken> i = tokens.iterator(); i.hasNext(); position++){
				i.next();
				if (position >= start && position <= end){
					i.remove();
				}
			}
			return true;
		}
		return false;
	}

	private static final int TIME_STATE_INIT = 0;
	private static final int TIME_STATE_HOUR = 1;
	private static final int TIME_STATE_HOUR_SEP = 2;
	private static final int TIME_STATE_MINUTE= 3;
	private static final int TIME_STATE_MINUTE_SEP = 4;
	private static final int TIME_STATE_SECOND= 5;
	private static final int TIME_STATE_DONE=6;

	private boolean setTime(WorkingDateTime work, LinkedList<DateTimeToken> tokens){
		int start = 0;
		int end = 0;
		int state = TIME_STATE_INIT;
		{
			int hour = -1;
			int position = 0;
			Iterator<DateTimeToken> i;
			for(i = tokens.iterator(); i.hasNext() && state != TIME_STATE_DONE; position++){
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
			for(ListIterator<DateTimeToken> i = tokens.listIterator(); i.hasNext();){
				DateTimeToken token = i.next();
				switch(token.getType()){
					case NUMBER: {
						if (!setWords(token, work, i)){
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
						}
					} break;
					case WORD: {
						if (!setWords(token, work, i)) return false;
					} break;
					case APOS_YEAR: {
						if (!work.setYear(token)) return false;
						i.remove();
					} break;
					case ORDINAL_DAY: {
						if (!work.setDay(token.getValue())) return false;
						i.remove();
					} break;
					default: break;
				}
			}
		}
		return true;
	}

	private boolean setWord(String text, WorkingDateTime work) {
		text = text.toLowerCase();
		if (monthWords.containsKey(text)){
			return work.setMonth(monthWords.get(text).intValue());
		} else if (ampmWords.containsKey(text)){
			return work.setAmPm(ampmWords.get(text).intValue());
		} else if (ordinalWords.containsKey(text)){
			return work.setDay(ordinalWords.get(text).intValue());
		} else if (weekdayWords.contains(text)){
			// ignore weekday words
			return true;
		} else if (eraWords.containsKey(text)){
			return work.setEra(eraWords.get(text).intValue());
		}
		return false;
	}

	private boolean setWords(DateTimeToken token, WorkingDateTime work, ListIterator<DateTimeToken> iterator){
		if (setWord(token.getText(), work)){
			iterator.remove();
			return true;
		}
		if (!iterator.hasNext()){
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(token.getText().toLowerCase());
		int additionalWords = 0;
		additionalWordsLoop: while (additionalWords<4){
			if (!iterator.hasNext()){
				break additionalWordsLoop;
			}
			token = iterator.next();
			additionalWords++;
			switch(token.getType()){
				case NUMBER:
				case WORD: {
					sb.append(token.getText().toLowerCase());
				} break;
				case SPACE:{
					sb.append(" ");
				} break;
				default: {
					sb.append(token.getText().toLowerCase());
					break additionalWordsLoop;
				}
			}
			if (setWord(sb.toString(), work)){
				for (int i=0; i<additionalWords; i++){
					iterator.remove();
					iterator.previous();
				}
				iterator.remove();
				return true;
			}
		}

		for (int i=0; i<=additionalWords; i++){
			iterator.previous();
		}
		iterator.next();
		return false;
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
		TimeZone zone = null;

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
			c.setTimeZone(zone);
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

		public boolean hasZone(){
			return zone != null;
		}

		public boolean hasMillisecond(){
			return millisecond != -1;
		}

		public boolean setEra(int value){
			if (hasEra()) return false;
			era = value;
			return true;
		}

		public boolean setZone(TimeZone zone){
			if (hasZone()) return false;
			this.zone = zone;
			return true;
		}

		public boolean isPm(){
			return amPm == Calendar.PM;
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
