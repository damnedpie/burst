package com.onecat.burst.utils;

import com.badlogic.gdx.Gdx;

public class Log {

	private static final String TAG = "Burst";

	public static void d(String msg, Object... args) {
		if (msg == null) msg = "null";
		Gdx.app.debug(TAG, String.format(msg, args));
	}

	public static void l(String msg, Object... args) {
		if (msg == null) msg = "null";
		Gdx.app.log(TAG, String.format(msg, args));
	}

	public static void e(String msg, Object... args) {
		if (msg == null) msg = "null";
		Gdx.app.error(TAG, String.format(msg, args));
	}

	public static void e(String msg, Throwable t) {
		if (msg == null) msg = "null";
		Gdx.app.error(TAG, msg, t);
	}

}
