package net.awired.visuwall.teamcityclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAdapter {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");

	public static Date parseDate(String s) {
		try {
			return sdf.parse(s);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String printDate(Date dt) {
		return sdf.format(dt);
	}
}
