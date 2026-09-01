package com.onecat.burst.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.onecat.burst.utils.InputChecks;
import com.onecat.burst.utils.Settings;
import com.ray3k.stripe.PopColorPicker;
import java.util.ArrayList;
import java.util.List;

public class EditorPanel extends Table {

	private float opacity = 0.9f;
	private float deltaMultiplierOldValue = 1.0f;
	private final Button bgColorButton;
	private PopColorPicker colorPicker;

	public interface EventListener {

		void onFovChanged(int value);
		void onDeltaMultiplierChanged(float value);
		void onBackgroundColorPicked(String hex);
		void onBackgroundColorUpdated(String hex);
		void onGridToggled(boolean enabled);
		void onGizmoToggled(boolean enabled);
		void onPrettyPrintToggled(boolean enabled);
		void onOutputStyleChanged(String outputStyle);

	}

	private final List<EventListener> listeners = new ArrayList<>();

	public EditorPanel(Skin skin, Stage stage) {
		super(skin);
		setName("EditorPanel");
		setTouchable(Touchable.enabled);
		addCaptureListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		defaults().space(4f).growX();
		left();
		setBackground("panel_default_default");
		top();
		Label header = new Label("Editor Settings", skin.get("header", Label.LabelStyle.class));
		header.setAlignment(Align.center);
		add(header).growX().row();
		Table subTable = new Table();
		subTable.defaults().growX().uniformX().uniformY().space(4f);
		add(subTable);

		Label fovLabel = new Label("FOV", skin);
		subTable.add(fovLabel);
		TextField fovInput = new TextField(String.valueOf(Settings.getInteger(Settings.SET_FOV)), skin);
		fovInput.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
		fovInput.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
					getStage().setKeyboardFocus(null);
					return true;
				}
				return false;
			}
		});
		fovInput.addListener(new FocusListener() {

			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (!focused) {
					onFovEdited(fovInput);
				}
			}
		});
		subTable.add(fovInput);
		subTable.row();

		Label deltaLabel = new Label("Delta multiplier", skin);
		subTable.add(deltaLabel);
		TextField deltaInput = new TextField("1.0", skin);
		deltaInput.setTextFieldFilter((textField, c) -> Character.isDigit(c) || c == '.');
		deltaInput.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
					getStage().setKeyboardFocus(null);
					return true;
				}
				return false;
			}
		});
		deltaInput.addListener(new FocusListener() {

			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (!focused) {
					onDeltaEdited(deltaInput);
				}
			}
		});
		subTable.add(deltaInput);
		subTable.row();

		Label bgColorLabel = new Label("Background color", skin);
		subTable.add(bgColorLabel);
		bgColorButton = new Button(skin.get("color_pick", Button.ButtonStyle.class));
		bgColorButton.setColor(Color.valueOf(Settings.getString(Settings.SET_BG_COLOR)));
		bgColorButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackgroundColorPressed();
			}
		});
		subTable.add(bgColorButton).fillY();
		subTable.row();

		CheckBox gridCheckbox = new CheckBox("Grid", skin);
		gridCheckbox.setProgrammaticChangeEvents(false);
		gridCheckbox.setChecked(Settings.getBoolean(Settings.SET_GRID_ENABLED));
		gridCheckbox.align(Align.left);
		gridCheckbox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onGridToggled(gridCheckbox.isChecked());
			}
		});
		subTable.add(gridCheckbox);

		CheckBox gizmoCheckbox = new CheckBox("Gizmo", skin);
		gizmoCheckbox.setProgrammaticChangeEvents(false);
		gizmoCheckbox.setChecked(Settings.getBoolean(Settings.SET_GIZMO_ENABLED));
		gizmoCheckbox.align(Align.left);
		gizmoCheckbox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onGizmoToggled(gizmoCheckbox.isChecked());
			}
		});
		subTable.add(gizmoCheckbox);
		subTable.row();

		CheckBox prettyPrintCheckbox = new CheckBox("Pretty print", skin);
		prettyPrintCheckbox.setProgrammaticChangeEvents(false);
		prettyPrintCheckbox.setChecked(Settings.getBoolean(Settings.SET_PRETTY_PRINT_ENABLED));
		prettyPrintCheckbox.align(Align.left);
		prettyPrintCheckbox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onPrettyPrintToggled(prettyPrintCheckbox.isChecked());
			}
		});
		subTable.add(prettyPrintCheckbox);
		SelectBox<String> outputModeSelectBox = new SelectBox<>(skin);
		outputModeSelectBox.setItems("Output: JSON", "Output: JS", "Output: Minimal");
		switch (Settings.getString(Settings.SET_OUTPUT_MODE)) {
			case "json":
				outputModeSelectBox.setSelectedIndex(0);
				break;
			case "js":
				outputModeSelectBox.setSelectedIndex(1);
				break;
			case "minimal":
				outputModeSelectBox.setSelectedIndex(2);
				break;
		}
		outputModeSelectBox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String newOutputStyle = "";
				switch (outputModeSelectBox.getSelectedIndex()) {
					case 0:
						newOutputStyle = "json";
						break;
					case 1:
						newOutputStyle = "js";
						break;
					case 2:
						newOutputStyle = "minimal";
						break;
				}
				for (EventListener listener : getEventListeners()) listener.onOutputStyleChanged(newOutputStyle);
			}
		});
		subTable.add(outputModeSelectBox);
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public void addListener(EventListener listener) {
		listeners.add(listener);
	}

	public void removeListener(EventListener listener) {
		listeners.remove(listener);
	}

	private void onBackgroundColorPressed() {
		if (colorPicker != null && !colorPicker.isHidden()) return;
		colorPicker = new PopColorPicker(Color.valueOf(Settings.getString(Settings.SET_BG_COLOR)), getSkin());
		colorPicker.setBackground(getSkin().getDrawable("panel_default_default"));
		colorPicker.addListener(new PopColorPicker.PopColorPickerListener() {

			@Override
			public void picked(Color color) {
				for (EventListener listener : getEventListeners()) listener.onBackgroundColorPicked(color.toString());
				bgColorButton.setColor(color);
			}

			@Override
			public void updated(Color color) {
				for (EventListener listener : getEventListeners()) listener.onBackgroundColorUpdated(color.toString());
				bgColorButton.setColor(color);
			}

			@Override
			public void cancelled(Color oldColor) {
				for (EventListener listener : getEventListeners()) listener.onBackgroundColorPicked(oldColor.toString());
				bgColorButton.setColor(oldColor);
			}
		});
		colorPicker.show(getStage());
	}

	private void onFovEdited(TextField input) {
		if (InputChecks.isInteger(input.getText())) {
			int newFov = MathUtils.clamp(Integer.parseInt(input.getText()), 30, 120);
			input.setText(String.valueOf(newFov));
			for (EventListener listener : getEventListeners()) listener.onFovChanged(newFov);
		}
	}

	private void onDeltaEdited(TextField input) {
		if (InputChecks.isFloat(input.getText())) {
			float newDelta = MathUtils.clamp(Float.parseFloat(input.getText()), 0.01f, 10.0f);
			input.setText(String.valueOf(newDelta));
			for (EventListener listener : getEventListeners()) listener.onDeltaMultiplierChanged(newDelta);
			deltaMultiplierOldValue = newDelta;
		}
		else {
			input.setText(String.valueOf(deltaMultiplierOldValue));
		}
	}

	private List<EventListener> getEventListeners() {
		return new ArrayList<>(listeners);
	}

	@Override
	public void layout() {
		TopBar topBar = getStage().getRoot().findActor("TopBar");
		setHeight(getPrefHeight());
		setWidth(getPrefWidth());
		setX(8f);
		setY(getStage().getHeight() - getHeight() - topBar.getHeight() - 8f);
		super.layout();
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
		super.drawBackground(batch, opacity, x, y);
	}

}
