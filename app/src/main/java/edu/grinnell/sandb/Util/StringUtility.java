package edu.grinnell.sandb.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class StringUtility {

	/* Capitalize the first letter of each word in a string */
	public static String captializeWords(String s) {
		String[] words = s.split(" ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			sb.append(words[i].substring(0, 1).toUpperCase())
					.append(words[i].substring(1).toLowerCase());

			if (i != words.length - 1)
				sb.append(" ");
		}
		return sb.toString();
	}

	/* Form a nicely formatted date string */
	public static String dateString(GregorianCalendar c) {
		StringBuilder sb = new StringBuilder();
		sb.append(c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
		sb.append(" ");
		sb.append(c.get(Calendar.DAY_OF_MONTH));
		sb.append(", ");
		sb.append(c.get(Calendar.YEAR));
		sb.append(" | ");
		sb.append(c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
		return sb.toString();
	}

	public static Date iso8601StringToDate(String stringDate) {
		Date result = null;
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		try {
			 result= df1.parse(stringDate);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
}