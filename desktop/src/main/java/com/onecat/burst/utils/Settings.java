package com.onecat.burst.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {

	private static Preferences preferences;

	public static final int DEFAULT_FOV = 67;
	public static final boolean DEFAULT_GRID_ENABLED = true;
	public static final boolean DEFAULT_GIZMO_ENABLED = true;
	public static final boolean DEFAULT_PRETTY_PRINT_ENABLED = true;
	public static final String DEFAULT_OUTPUT_MODE = "minimal";
	public static final String DEFAULT_BG_COLOR = "38384C";

	private static Preferences getPrefs() {
		if (preferences == null)
			preferences = Gdx.app.getPreferences("BurstEditor");
		return preferences;
	}

	public static int getInteger(String key, int defValue) {
		return getPrefs().getInteger(key, defValue);
	}

	public static String getString(String key, String defValue) {
		return getPrefs().getString(key, defValue);
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return getPrefs().getBoolean(key, defValue);
	}

	public static float getFloat(String key, float defValue) {
		return getPrefs().getFloat(key, defValue);
	}

	public static void putInteger(String key, int value) {
		getPrefs().putInteger(key, value);
		getPrefs().flush();
	}

	public static void putString(String key, String value) {
		getPrefs().putString(key, value);
		getPrefs().flush();
	}

	public static void putBoolean(String key, boolean value) {
		getPrefs().putBoolean(key, value);
		getPrefs().flush();
	}

	public static void putFloat(String key, float value) {
		getPrefs().putFloat(key, value);
		getPrefs().flush();
	}

}
