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
				System.out.println("Found field: " + field);
				l.add(field);
			}
		}
		return l;
	}

	private Map<String,Integer> monthWords = new HashMap<String,Integer>();
	private Map<String,Integer> eraWords = new HashMap<String,Integer>();
	private Set<String> weekdayWords = new HashSet<String>();
	private Map<String,Integer> ordinalWords = new HashMap<String,Integer>();
	
	private static final String[] ALL_PROPERTIES = {
		"","en",
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
			System.out.println("Loading properties: DateTimeParse" + propertiesLocale + ".properties");
			InputStream in = this.getClass().getResourceAsStream(
				"DateTimeParse" + propertiesLocale + ".properties"
			);
			if (in != null){
				UberProperties prop = new UberProperties();
				prop.load(new InputStreamReader(in, "UTF-8"));
				addStringInts(allKeys, ordinalWords, prop.getProperty("ordinalWords"));
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
			String key = pair.substring(0, sep);
			if (!allKeys.contains(key)){
				allKeys.add(key);
				addTo.put(key, Integer.valueOf(pair.substring(sep+1)));
			}
		}
	}


	private static void addStrings(Set<String> allKeys, Set<String> addTo, String s){
		if (s == null) return;
		for(String key: s.split("\\,")){
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
						if (monthWords.containsKey(text)){
							if (!work.setMonth(monthWords.get(text).intValue())) return null;
						} else if (ordinalWords.containsKey(text)){
							if (!work.setDay(ordinalWords.get(text).intValue())) return null;
						} else if (weekdayWords.contains(text)){
							// ignore weekday words
						} else if (eraWords.containsKey(text)){
							if (!work.setEra(eraWords.get(text).intValue())) return null;
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
