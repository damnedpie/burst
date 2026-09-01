package com.onecat.burst.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {

	private static Preferences preferences;

	public static final Setting<Integer> SET_FOV = new Setting<>("fov", 67);
	public static final Setting<Boolean> SET_GRID_ENABLED = new Setting<>("grid_enabled", true);
	public static final Setting<Boolean> SET_GIZMO_ENABLED = new Setting<>("gizmo_enabled", true);
	public static final Setting<Boolean> SET_PRETTY_PRINT_ENABLED = new Setting<>("pretty_print_enabled", true);
	public static final Setting<String> SET_OUTPUT_MODE = new Setting<>("output_mode", "minimal");
	public static final Setting<String> SET_BG_COLOR = new Setting<>("bg_color", "38384C");

	private static Preferences getPrefs() {
		if (preferences == null)
			preferences = Gdx.app.getPreferences("BurstEditor");
		return preferences;
	}

	public static class Setting<T> {

		public final String key;
		public final T defValue;

		public Setting(String key, T defValue) {
			this.key = key;
			this.defValue = defValue;
		}

	}

	public static int getInteger(Setting<Integer> setting) {
		return getPrefs().getInteger(setting.key, setting.defValue);
	}

	public static String getString(Setting<String> setting) {
		return getPrefs().getString(setting.key, setting.defValue);
	}

	public static boolean getBoolean(Setting<Boolean> setting) {
		return getPrefs().getBoolean(setting.key, setting.defValue);
	}

	public static float getFloat(Setting<Float> setting) {
		return getPrefs().getFloat(setting.key, setting.defValue);
	}

	public static void putInteger(Setting<Integer> setting, int value) {
		getPrefs().putInteger(setting.key, value);
		getPrefs().flush();
	}

	public static void putString(Setting<String> setting, String value) {
		getPrefs().putString(setting.key, value);
		getPrefs().flush();
	}

	public static void putBoolean(Setting<Boolean> setting, boolean value) {
		getPrefs().putBoolean(setting.key, value);
		getPrefs().flush();
	}

	public static void putFloat(Setting<Float> setting, float value) {
		getPrefs().putFloat(setting.key, value);
		getPrefs().flush();
	}

}
