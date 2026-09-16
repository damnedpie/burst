package com.onecat.burst.ui.widgets;

import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.values.RangedNumericValue;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.onecat.burst.utils.UserInput;
import java.util.Locale;

public abstract class DisplayComponent extends Table {

	public static final float DEFAULT_SPACING = 4f;
	public static final float NAME_LABEL_WIDTH = 80f;
	public static final float INPUT_FIELD_WIDTH = 52f;

	public DisplayComponent(Skin skin) {
		super(skin);
		applyTableDefaults(this);
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
			applyComponentNameSizing(add(rangeLabel));
			Label minLabel = new Label("min", skin.get("small", Label.LabelStyle.class));
			minLabel.setAlignment(Align.center);
			add(minLabel);
			oldMin = String.valueOf(emitter.minParticleCount);
			TextField minField = textField(oldMin, skin);
			minField.setTextFieldFilter(UserInput.integerFilter());
			minField.addListener(UserInput.confirmInputListener(minField));
			minField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onMinCountEdited(minField);
				}
			});
			add(minField);
			Label maxLabel = new Label("max", skin.get("small", Label.LabelStyle.class));
			maxLabel.setAlignment(Align.center);
			add(maxLabel);
			oldMax = String.valueOf(emitter.maxParticleCount);
			TextField maxField = textField(oldMax, skin);
			maxField.setTextFieldFilter(UserInput.integerFilter());
			maxField.addListener(UserInput.confirmInputListener(maxField));
			maxField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onMaxCountEdited(maxField);
				}
			});
			add(maxField);
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
		private final boolean forcePositive;
		private final boolean forceInteger;
		private final boolean autoActivate;

		public RangedNumericComponent(String name, RangedNumericValue value, Skin skin, boolean forcePositive, boolean forceInteger, boolean autoActivate) {
			super(skin);
			this.value = value;
			this.forcePositive = forcePositive;
			this.forceInteger = forceInteger;
			this.autoActivate = autoActivate;
			Label rangeLabel = new Label(String.format(Locale.ROOT, "%s:", name), skin);
			applyComponentNameSizing(add(rangeLabel));
			Label minLabel = new Label("min", skin.get("small", Label.LabelStyle.class));
			minLabel.setAlignment(Align.center);
			add(minLabel);
			oldMin = forceInteger ? String.valueOf(MathUtils.round(value.getLowMin())) : String.valueOf(value.getLowMin());
			TextField minField = textField(oldMin, skin);
			minField.setTextFieldFilter(UserInput.floatFilter());
			minField.addListener(UserInput.confirmInputListener(minField));
			minField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onMinCountEdited(minField);
				}
			});
			add(minField);
			Label maxLabel = new Label("max", skin.get("small", Label.LabelStyle.class));
			maxLabel.setAlignment(Align.center);
			add(maxLabel);
			oldMax = forceInteger ? String.valueOf(MathUtils.round(value.getLowMax())) : String.valueOf(value.getLowMax());
			TextField maxField = textField(oldMax, skin);
			maxField.setTextFieldFilter(UserInput.floatFilter());
			maxField.addListener(UserInput.confirmInputListener(maxField));
			maxField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onMaxCountEdited(maxField);
				}
			});
			add(maxField);
			if (!autoActivate) {
				CheckBox activeCheckbox = new CheckBox("Active", skin);
				activeCheckbox.setChecked(value.isActive());
				activeCheckbox.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						value.setActive(activeCheckbox.isChecked());
					}
				});
				add(activeCheckbox);
			}
		}

		private void onMinCountEdited(TextField inputField) {
			if (UserInput.isFloat(inputField.getText())) {
				float newValue = Float.parseFloat(inputField.getText());
				boolean isValid = !forcePositive || !(newValue < 0);
				if (isValid) {
					value.setLowMin(forceInteger ? MathUtils.round(newValue) : newValue);
					oldMin = forceInteger ? String.valueOf(MathUtils.round(newValue)) : String.valueOf(newValue);
					inputField.setText(oldMin);
					if (autoActivate) value.setActive(true);
					return;
				}
			}
			inputField.setText(oldMin);
		}

		private void onMaxCountEdited(TextField inputField) {
			if (UserInput.isFloat(inputField.getText())) {
				float newValue = Float.parseFloat(inputField.getText());
				boolean isValid = !forcePositive || !(newValue < 0);
				if (isValid) {
					value.setLowMax(forceInteger ? MathUtils.round(newValue) : newValue);
					oldMax = forceInteger ? String.valueOf(MathUtils.round(newValue)) : String.valueOf(newValue);
					inputField.setText(oldMax);
					if (autoActivate) value.setActive(true);
					return;
				}
			}
			inputField.setText(oldMax);
		}

	}

	public static class ScaledNumericComponent extends DisplayComponent {

		private String oldHighMin;
		private String oldHighMax;
		private String oldLowMin;
		private String oldLowMax;
		private final ScaledNumericValue value;
		private final boolean forcePositive;
		private final boolean forceInteger;
		private final boolean autoActivate;

		public ScaledNumericComponent(String name, ScaledNumericValue value, Skin skin, boolean forcePositive, boolean forceInteger, boolean autoActivate) {
			super(skin);
			this.value = value;
			this.forcePositive = forcePositive;
			this.forceInteger = forceInteger;
			this.autoActivate = autoActivate;
			Label rangeLabel = new Label(String.format(Locale.ROOT, "%s:", name), skin);
			DisplayComponent.applyComponentNameSizing(add(rangeLabel));
			Table nestedTable = new Table();
			applyTableDefaults(nestedTable);
			Label highMinLabel = new Label("high min", skin.get("small", Label.LabelStyle.class));
			highMinLabel.setAlignment(Align.center);
			nestedTable.add(highMinLabel);
			oldHighMin = forceInteger ? String.valueOf(MathUtils.round(value.getHighMin())) : String.valueOf(value.getHighMin());
			TextField highMinField = textField(oldHighMin, skin);
			highMinField.setTextFieldFilter(UserInput.floatFilter());
			highMinField.addListener(UserInput.confirmInputListener(highMinField));
			highMinField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onHighMinEdited(highMinField);
				}
			});
			nestedTable.add(highMinField);
			Label highMaxLabel = new Label("high max", skin.get("small", Label.LabelStyle.class));
			highMaxLabel.setAlignment(Align.center);
			nestedTable.add(highMaxLabel);
			oldHighMax = forceInteger ? String.valueOf(MathUtils.round(value.getHighMax())) : String.valueOf(value.getHighMax());
			TextField highMaxField = textField(oldHighMax, skin);
			highMaxField.setTextFieldFilter(UserInput.floatFilter());
			highMaxField.addListener(UserInput.confirmInputListener(highMaxField));
			highMaxField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onHighMaxEdited(highMaxField);
				}
			});
			nestedTable.add(highMaxField).row();

			Label lowMinLabel = new Label("low min", skin.get("small", Label.LabelStyle.class));
			lowMinLabel.setAlignment(Align.center);
			nestedTable.add(lowMinLabel);
			oldLowMin = forceInteger ? String.valueOf(MathUtils.round(value.getLowMin())) : String.valueOf(value.getLowMin());
			TextField lowMinField = textField(oldLowMin, skin);
			lowMinField.setTextFieldFilter(UserInput.floatFilter());
			lowMinField.addListener(UserInput.confirmInputListener(lowMinField));
			lowMinField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onLowMinEdited(lowMinField);
				}
			});
			nestedTable.add(lowMinField);
			Label lowMaxLabel = new Label("low max", skin.get("small", Label.LabelStyle.class));
			lowMaxLabel.setAlignment(Align.center);
			nestedTable.add(lowMaxLabel);
			oldLowMax = forceInteger ? String.valueOf(MathUtils.round(value.getLowMax())) : String.valueOf(value.getLowMax());
			TextField lowMaxField = textField(oldLowMax, skin);
			lowMaxField.setTextFieldFilter(UserInput.floatFilter());
			lowMaxField.addListener(UserInput.confirmInputListener(lowMaxField));
			lowMaxField.addListener(new FocusListener() {

				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
					if (!focused)
						onLowMaxEdited(lowMaxField);
				}
			});
			nestedTable.add(lowMaxField);
			add(nestedTable).growX().row();

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
			if (UserInput.isFloat(inputField.getText())) {
				float newValue = Float.parseFloat(inputField.getText());
				boolean isValid = !forcePositive || !(newValue < 0);
				if (isValid) {
					value.setHighMin(forceInteger ? MathUtils.round(newValue) : newValue);
					oldHighMin = forceInteger ? String.valueOf(MathUtils.round(newValue)) : String.valueOf(newValue);
					inputField.setText(oldHighMin);
					if (autoActivate) value.setActive(true);
					return;
				}
			}
			inputField.setText(oldHighMin);
		}

		private void onHighMaxEdited(TextField inputField) {
			if (UserInput.isFloat(inputField.getText())) {
				float newValue = Float.parseFloat(inputField.getText());
				boolean isValid = !forcePositive || !(newValue < 0);
				if (isValid) {
					value.setHighMax(forceInteger ? MathUtils.round(newValue) : newValue);
					oldHighMax = forceInteger ? String.valueOf(MathUtils.round(newValue)) : String.valueOf(newValue);
					inputField.setText(oldHighMax);
					if (autoActivate) value.setActive(true);
					return;
				}
			}
			inputField.setText(oldHighMax);
		}

		private void onLowMinEdited(TextField inputField) {
			if (UserInput.isFloat(inputField.getText())) {
				float newValue = Float.parseFloat(inputField.getText());
				boolean isValid = !forcePositive || !(newValue < 0);
				if (isValid) {
					value.setLowMin(forceInteger ? MathUtils.round(newValue) : newValue);
					oldLowMin = forceInteger ? String.valueOf(MathUtils.round(newValue)) : String.valueOf(newValue);
					inputField.setText(oldLowMin);
					if (autoActivate) value.setActive(true);
					return;
				}
			}
			inputField.setText(oldLowMin);
		}

		private void onLowMaxEdited(TextField inputField) {
			if (UserInput.isFloat(inputField.getText())) {
				float newValue = Float.parseFloat(inputField.getText());
				boolean isValid = !forcePositive || !(newValue < 0);
				if (isValid) {
					value.setLowMax(forceInteger ? MathUtils.round(newValue) : newValue);
					oldLowMax = forceInteger ? String.valueOf(MathUtils.round(newValue)) : String.valueOf(newValue);
					inputField.setText(oldLowMax);
					if (autoActivate) value.setActive(true);
					return;
				}
			}
			inputField.setText(oldLowMax);
		}

	}

	private static TextField textField(String text, Skin skin) {
		return new TextField(text, skin) {

			@Override
			public float getPrefWidth() {
				// libGDX forces hardcoded 150px by default which fucks up Table layout a bit
				return INPUT_FIELD_WIDTH;
			}
		};
	}

	private static void applyTableDefaults(Table table) {
		table.defaults().space(DEFAULT_SPACING).growX();
	}

	private static void applyComponentNameSizing(Cell<Label> cell) {
		cell.expand(false, false).width(NAME_LABEL_WIDTH);
	}

}
