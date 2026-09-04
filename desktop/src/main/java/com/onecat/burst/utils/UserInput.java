package com.onecat.burst.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class UserInput {

	public static TextField.TextFieldFilter stringFilter() {
		return (textField, c) -> Character.isLetterOrDigit(c) || c == '-' || c == '_';
	}

	public static TextField.TextFieldFilter floatFilter() {
		return (textField, c) -> Character.isDigit(c) || c == '.';
	}

	public static TextField.TextFieldFilter integerFilter() {
		return new TextField.TextFieldFilter.DigitsOnlyFilter();
	}

	public static InputListener confirmInputListener(Actor actor) {
		return new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
					actor.getStage().setKeyboardFocus(null);
					return true;
				}
				return false;
			}
		};
	}

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
