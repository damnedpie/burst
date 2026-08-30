package com.onecat.burst.utils;

public class InputChecks {

	public static boolean isInteger(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isFloat(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		try {
			Float.parseFloat(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
