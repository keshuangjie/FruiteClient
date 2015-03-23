package com.shopping.fruit.client.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils {

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public static Date parseDate(String dateStr) throws ParseException {
		try {
			return dateFormat.parse(dateStr);
		} catch (ParseException e) {
				Log.i(FormatUtils.class.getName(), "parseDate() dateStr -->> " + dateStr);
			throw e;
		}
	}

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}

}
