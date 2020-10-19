package br.com.sauran.matcher.utils;

public class TimerManager {


	private static void prependTimeAndUnit(StringBuffer timeBuf, long time, String unit, String separate) {
		if (time < 1) {
			return;
		}

		if (timeBuf.length() > 0) {
			timeBuf.insert(0, separate);
		}

		timeBuf.insert(0, unit);
		timeBuf.insert(0, time);
	}

	public static String toYYYYHHmmssS2(long timeInMillis) {

		if (timeInMillis < 1) {
			return String.valueOf(timeInMillis);
		}

        StringBuffer timeBuf = new StringBuffer();
        
		long time = timeInMillis / 1000;
		if (time < 1) {
			prependTimeAndUnit(timeBuf, timeInMillis, "ms", " ");
			return timeBuf.toString();
		}

		long seconds = time % 60;

		time = time / 60;
		if (time < 1) {
			prependTimeAndUnit(timeBuf, seconds, "s", " ");
			return timeBuf.toString();
		}

		long minutes = time % 60;
		prependTimeAndUnit(timeBuf, minutes, "m", " ");

		time = time / 60;
		if (time < 1) {
			return timeBuf.toString();
		}

		long hours = time % 24;
		prependTimeAndUnit(timeBuf, hours, "h", " ");

		time = time / 24;
		if (time < 1) {
			return timeBuf.toString();
		}

		long day = time % 365;
		prependTimeAndUnit(timeBuf, day, "d", " ");

		time = time / 365;
		if (time < 1) {
			return timeBuf.toString();
		}

		prependTimeAndUnit(timeBuf, time, "y", " ");

		return timeBuf.toString();
	}
	

	public static String toYYYYHHmmssS(long timeInMillis) {

		if (timeInMillis < 1) {
			return String.valueOf(timeInMillis);
		}

        StringBuffer timeBuf = new StringBuffer();
        
		long time = timeInMillis / 1000;
		if (time < 1) {
			prependTimeAndUnit(timeBuf, timeInMillis, "ms", ", ");
			return timeBuf.toString();
		}

		long seconds = time % 60;
		prependTimeAndUnit(timeBuf, seconds, "s", ", ");

		time = time / 60;
		if (time < 1) {
			return timeBuf.toString();
		}

		long minutes = time % 60;
		prependTimeAndUnit(timeBuf, minutes, "m", ", ");

		time = time / 60;
		if (time < 1) {
			return timeBuf.toString();
		}

		long hours = time % 24;
		prependTimeAndUnit(timeBuf, hours, "h", ", ");

		time = time / 24;
		if (time < 1) {
			return timeBuf.toString();
		}

		long day = time % 365;
		prependTimeAndUnit(timeBuf, day, "d", ", ");

		time = time / 365;
		if (time < 1) {
			return timeBuf.toString();
		}

		prependTimeAndUnit(timeBuf, time, "y", ", ");

		return timeBuf.toString();
	}
	
}
