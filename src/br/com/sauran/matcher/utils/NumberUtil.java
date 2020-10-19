package br.com.sauran.matcher.utils;

public class NumberUtil {

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		} 

	}

}
