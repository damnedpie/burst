package com.onecat.burst.ui.displays;

import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.values.RangedNumericValue;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.onecat.burst.utils.UserInput;
import java.util.Locale;

public abstract class DisplayComponent extends Table {

	public static final float DEFAULT_SPACING = 4f;
	public static final float NAME_LABEL_WIDTH = 90f;
	public static final float RANGE_LABEL_WIDTH = 64f;
	public static final float INPUT_FIELD_WIDTH = 64f;

	public DisplayComponent(Skin skin) {
		super(skin);
		defaults().space(DEFAULT_SPACING).expandX();
		setBackground(skin.getDrawable("button_flat_default_disabled"));
	}

	public static class CountComponent extends DisplayComponent {

		private String oldMin;
		private String oldMax;
		private final RegularEmitter emitter;

		public CountComponent(RegularEmitter emitter, Skin skin) {
			super(skin);
			this.emitter = emitter;
			Label rangeLabel = new Label("Count", skin);
			add(rangeLabel).width(NAME_LABEL_WIDTH);
			Label minLabel = new Label("min", skin.get("small", Label.LabelStyle.class));
			minLabel.setAlignment(Align.center);
			add(minLabel).width(RANGE_LABEL_WIDTH);
			oldMin = String.valueOf(emitter.minParticleCount);
			TextField minField = new TextField(oldMin, skin);
			minField.setTextFieldFilter(UserInput.integerFilter());
			minField.addListener(UserInput.confirmInputListener(minField));
			minField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onMinCountEdited(minField);
				}
			});
			add(minField).width(INPUT_FIELD_WIDTH);
			Label maxLabel = new Label("max", skin.get("small", Label.LabelStyle.class));
			maxLabel.setAlignment(Align.center);
			add(maxLabel).width(RANGE_LABEL_WIDTH);
			oldMax = String.valueOf(emitter.maxParticleCount);
			TextField maxField = new TextField(oldMax, skin);
			maxField.setTextFieldFilter(UserInput.integerFilter());
			maxField.addListener(UserInput.confirmInputListener(maxField));
			maxField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onMaxCountEdited(maxField);
				}
			});
			add(maxField).width(INPUT_FIELD_WIDTH);
		}

		private void onMinCountEdited(TextField inputField) {
			int newValue;
			if (UserInput.isInteger(inputField.getText())) {
				newValue = Integer.parseInt(inputField.getText());
				if (newValue >= 0) {
					emitter.minParticleCount = newValue;
					oldMin = String.valueOf(newValue);
					return;
				}
			}
			// Invalid input
			inputField.setText(oldMin);
		}

		private void onMaxCountEdited(TextField inputField) {
			int newValue;
			if (UserInput.isInteger(inputField.getText())) {
				newValue = Integer.parseInt(inputField.getText());
				if (newValue >= 0) {
					emitter.maxParticleCount = newValue;
					oldMax = String.valueOf(newValue);
					return;
				}
			}
			// Invalid input
			inputField.setText(oldMax);
		}

	}

	public static class RangedNumericComponent extends DisplayComponent {

		private String oldMin;
		private String oldMax;
		private final RangedNumericValue value;

		public RangedNumericComponent(String name, RangedNumericValue value, Skin skin) {
			super(skin);
			this.value = value;
			Label rangeLabel = new Label(String.format(Locale.ROOT, "%s:", name), skin);
			add(rangeLabel).width(NAME_LABEL_WIDTH);
			Label minLabel = new Label("min", skin.get("small", Label.LabelStyle.class));
			minLabel.setAlignment(Align.center);
			add(minLabel).width(RANGE_LABEL_WIDTH);
			oldMin = String.valueOf(value.getLowMin());
			TextField minField = new TextField(oldMin, skin);
			minField.setTextFieldFilter(UserInput.floatFilter());
			minField.addListener(UserInput.confirmInputListener(minField));
			minField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onMinCountEdited(minField);
				}
			});
			add(minField).width(INPUT_FIELD_WIDTH);
			Label maxLabel = new Label("max", skin.get("small", Label.LabelStyle.class));
			maxLabel.setAlignment(Align.center);
			add(maxLabel).width(RANGE_LABEL_WIDTH);
			oldMax = String.valueOf(value.getLowMax());
			TextField maxField = new TextField(oldMax, skin);
			maxField.setTextFieldFilter(UserInput.floatFilter());
			maxField.addListener(UserInput.confirmInputListener(maxField));
			maxField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onMaxCountEdited(maxField);
				}
			});
			add(maxField).width(INPUT_FIELD_WIDTH);
		}

		private void onMinCountEdited(TextField inputField) {
			float newValue;
			if (UserInput.isFloat(inputField.getText())) {
				newValue = Float.parseFloat(inputField.getText());
				if (newValue >= 0) {
					value.setLowMin(newValue);
					oldMin = String.valueOf(newValue);
					return;
				}
			}
			// Invalid input
			inputField.setText(oldMin);
		}

		private void onMaxCountEdited(TextField inputField) {
			float newValue;
			if (UserInput.isFloat(inputField.getText())) {
				newValue = Float.parseFloat(inputField.getText());
				if (newValue >= 0) {
					value.setLowMax(newValue);
					oldMax = String.valueOf(newValue);
					return;
				}
			}
			// Invalid input
			inputField.setText(oldMax);
		}

	}

	public static class ScaledNumericComponent extends DisplayComponent {

		private String oldHighMin;
		private String oldHighMax;
		private String oldLowMin;
		private String oldLowMax;
		private final ScaledNumericValue value;

		public ScaledNumericComponent(String name, ScaledNumericValue value, Skin skin) {
			super(skin);
			this.value = value;
			Label rangeLabel = new Label(String.format(Locale.ROOT, "%s:", name), skin);
			add(rangeLabel).width(NAME_LABEL_WIDTH);
			Table nestedTable = new Table();
			nestedTable.defaults().space(DEFAULT_SPACING).expandX();
			Label highMinLabel = new Label("high min", skin.get("small", Label.LabelStyle.class));
			highMinLabel.setAlignment(Align.center);
			nestedTable.add(highMinLabel).width(RANGE_LABEL_WIDTH);
			oldHighMin = String.valueOf(value.getHighMin());
			TextField highMinField = new TextField(oldHighMin, skin);
			highMinField.setTextFieldFilter(UserInput.floatFilter());
			highMinField.addListener(UserInput.confirmInputListener(highMinField));
			highMinField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onHighMinEdited(highMinField);
				}
			});
			nestedTable.add(highMinField).width(INPUT_FIELD_WIDTH);
			Label highMaxLabel = new Label("high max", skin.get("small", Label.LabelStyle.class));
			highMaxLabel.setAlignment(Align.center);
			nestedTable.add(highMaxLabel).width(RANGE_LABEL_WIDTH);
			oldHighMax = String.valueOf(value.getHighMax());
			TextField highMaxField = new TextField(oldHighMax, skin);
			highMaxField.setTextFieldFilter(UserInput.floatFilter());
			highMaxField.addListener(UserInput.confirmInputListener(highMaxField));
			highMaxField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onHighMaxEdited(highMaxField);
				}
			});
			nestedTable.add(highMaxField).width(INPUT_FIELD_WIDTH).row();

			Label lowMinLabel = new Label("low min", skin.get("small", Label.LabelStyle.class));
			lowMinLabel.setAlignment(Align.center);
			nestedTable.add(lowMinLabel).width(RANGE_LABEL_WIDTH);
			oldLowMin = String.valueOf(value.getLowMin());
			TextField lowMinField = new TextField(oldLowMin, skin);
			lowMinField.setTextFieldFilter(UserInput.floatFilter());
			lowMinField.addListener(UserInput.confirmInputListener(lowMinField));
			lowMinField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onLowMinEdited(lowMinField);
				}
			});
			nestedTable.add(lowMinField).width(INPUT_FIELD_WIDTH);
			Label lowMaxLabel = new Label("low max", skin.get("small", Label.LabelStyle.class));
			lowMaxLabel.setAlignment(Align.center);
			nestedTable.add(lowMaxLabel).width(RANGE_LABEL_WIDTH);
			oldLowMax = String.valueOf(value.getLowMax());
			TextField lowMaxField = new TextField(oldLowMax, skin);
			lowMaxField.setTextFieldFilter(UserInput.floatFilter());
			lowMaxField.addListener(UserInput.confirmInputListener(lowMaxField));
			lowMaxField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onLowMaxEdited(lowMaxField);
				}
			});
			nestedTable.add(lowMaxField).width(INPUT_FIELD_WIDTH);
			add(nestedTable).row();

			EditableGraph graph = new EditableGraph(value, skin);
			add(graph).colspan(2).growX().row();

			CheckBox relativeCheckbox = new CheckBox("Relative", skin);
			relativeCheckbox.setChecked(value.isRelative());
			relativeCheckbox.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					value.setRelative(relativeCheckbox.isChecked());
				}
			});
			add(relativeCheckbox).colspan(2).growX();
		}

		private void onHighMinEdited(TextField inputField) {
			float newValue;
			if (UserInput.isFloat(inputField.getText())) {
				newValue = Float.parseFloat(inputField.getText());
				if (newValue >= 0) {
					value.setHighMin(newValue);
					oldHighMin = String.valueOf(newValue);
					return;
				}
			}
			// Invalid input
			inputField.setText(oldHighMin);
		}

		private void onHighMaxEdited(TextField inputField) {
			float newValue;
			if (UserInput.isFloat(inputField.getText())) {
				newValue = Float.parseFloat(inputField.getText());
				if (newValue >= 0) {
					value.setHighMax(newValue);
					oldHighMax = String.valueOf(newValue);
					return;
				}
			}
			// Invalid input
			inputField.setText(oldHighMax);
		}

		private void onLowMinEdited(TextField inputField) {
			float newValue;
			if (UserInput.isFloat(inputField.getText())) {
				newValue = Float.parseFloat(inputField.getText());
				if (newValue >= 0) {
					value.setLowMin(newValue);
					oldLowMin = String.valueOf(newValue);
					return;
				}
			}
			// Invalid input
			inputField.setText(oldLowMin);
		}

		private void onLowMaxEdited(TextField inputField) {
			float newValue;
			if (UserInput.isFloat(inputField.getText())) {
				newValue = Float.parseFloat(inputField.getText());
				if (newValue >= 0) {
					value.setLowMax(newValue);
					oldLowMax = String.valueOf(newValue);
					return;
				}
			}
			// Invalid input
			inputField.setText(oldLowMax);
		}

	}

}
