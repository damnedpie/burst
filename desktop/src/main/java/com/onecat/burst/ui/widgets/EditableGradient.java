package com.onecat.burst.ui.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.particles.values.GradientColorValue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.ray3k.stripe.PopColorPicker;
import java.util.Comparator;

public class EditableGradient extends WidgetGroup {

	private static final float POINT_SIZE = 12f;
	private static final float SNAP_STEP = 0.01f;

	private final Skin skin;
	private final Image gradientImage;
	private final Array<GradientPoint> points = new Array<>(true, 8);

	private GradientColorValue gradientValue;
	private Texture gradientTexture;

	private PopColorPicker colorPicker;

	private static Comparator<GradientPoint> gradientSorter;

	public EditableGradient(Skin skin) {
		gradientImage = new Image();
		gradientImage.setTouchable(Touchable.disabled);
		this.skin = skin;
		addActor(gradientImage);
		setTouchable(Touchable.enabled);
		addListener(createInputListener());
	}

	public void set(GradientColorValue value) {
		for (GradientPoint point : points) {
			removeActor(point);
		}
		points.clear();
		gradientValue = value;
		float[] timeline = value.getTimeline();
		float[] colors = value.getColors();
		for (int i = 0; i < timeline.length; i++) {
			points.add(new GradientPoint(colors[i], colors[i + 1], colors[i + 2], timeline[i], skin));
			addActor(points.get(i));
		}
		updateTexture();
	}

	private void updateTexture() {
		if (gradientTexture != null) {
			gradientTexture.dispose();
			gradientTexture = null;
		}
		Pixmap pixmap = new Pixmap(Math.round(getWidth()), 1, Pixmap.Format.RGB888);
		for (int i = 0; i < pixmap.getWidth(); i++) {
			float[] color = gradientValue.getColor(i / (float) pixmap.getWidth());
			pixmap.setColor(color[0], color[1], color[2], 1f);
			pixmap.drawPixel(i, 0);
		}
		gradientTexture = new Texture(pixmap);
		gradientTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		pixmap.dispose();
		gradientImage.setDrawable(new TextureRegionDrawable(gradientTexture));
	}

	private void addPoint(float time) {
		time = snapToStep(time);
		float[] color = gradientValue.getColor(time);
		GradientPoint point = new GradientPoint(color[0], color[1], color[2], time, skin);
		points.add(point);
		if (points.size == 2) point.time = 1.0f;
		addActor(point);
		sortPoints();
		syncPoints();
		updateTexture();
	}

	private void removePoint(GradientPoint point) {
		points.removeValue(point, true);
		removeActor(point);
		sortPoints();
		syncPoints();
		updateTexture();
	}

	private void sortPoints() {
		if (gradientSorter == null) gradientSorter = (o1, o2) -> Float.compare(o1.time, o2.time);
		points.sort(gradientSorter);
		for (GradientPoint point : points) {
			removeActor(point);
			addActor(point);
		}
	}

	private void syncPoints() {
		float[] colors = new float[3 * points.size];
		float[] timeline = new float[points.size];
		for (int i = 0; i < points.size; i++) {
			GradientPoint point = points.get(i);
			Color color = point.getColor();
			colors[3 * i] = color.r;
			colors[3 * i + 1] = color.g;
			colors[3 * i + 2] = color.b;
			timeline[i] = point.time;
		}
		gradientValue.setColors(colors);
		gradientValue.setTimeline(timeline);
	}

	private boolean gradientContainsPosition(float x, float y) {
		return x >= getGradientX() && x <= getGradientX() + getGradientWidth() && y >= getGradientY() && y <= getGradientY() + getGradientHeight();
	}

	private float getGradientX() {
		return 0f;
	}

	private float getGradientY() {
		return POINT_SIZE;
	}

	private float getGradientWidth() {
		return getWidth();
	}

	private float getGradientHeight() {
		return getHeight() - POINT_SIZE;
	}

	private float snapToStep(float value) {
		return MathUtils.clamp(Math.round(value / SNAP_STEP) * SNAP_STEP, 0.0f, 1.0f);
	}

	private void showColorPicker(GradientPoint point) {
		if (colorPicker != null) {
			colorPicker.remove();
		}
		colorPicker = new PopColorPicker(point.getColor(), skin);
		colorPicker.setDraggable(true);
		colorPicker.setKeepCenteredInWindow(false);
		colorPicker.setBackground(skin.getDrawable("panel_default_default"));
		colorPicker.addListener(new PopColorPicker.PopColorPickerListener() {

			@Override
			public void picked(Color color) {
				point.setColor(color.r, color.g, color.b, 1.0f);
				syncPoints();
				updateTexture();
			}

			@Override
			public void updated(Color color) {
				point.setColor(color.r, color.g, color.b, 1.0f);
				syncPoints();
				updateTexture();
			}

			@Override
			public void cancelled(Color oldColor) {
				point.setColor(oldColor.r, oldColor.g, oldColor.b, 1.0f);
				syncPoints();
				updateTexture();
			}
		});
		colorPicker.show(getStage());
		colorPicker.setPosition(8f, colorPicker.getY());
	}

	private InputListener createInputListener() {
		return new InputListener() {

			GradientPoint lmbPoint = null;
			GradientPoint rmbPoint = null;

			boolean gradientDown = false;

			private void resetState() {
				gradientDown = false;
			}

			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				for (GradientPoint point : points) {
					point.setScale(1.0f);
					if (point.containsPosition(x, y)) {
						point.setScale(1.5f);
					}
				}
				for (GradientPoint point : points) {
					if (point.containsPosition(x, y)) {
						point.setScale(1.5f);
						break;
					}
				}
				return super.mouseMoved(event, x, y);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				if (lmbPoint != null) {
					// Can't move first point of the gradient
					if (points.indexOf(lmbPoint, true) == 0) return;
					// Can't move last point of the gradient
					if (points.indexOf(lmbPoint, true) == points.size - 1) return;
					lmbPoint.time = snapToStep(x / getWidth());
					sortPoints();
					syncPoints();
					updateTexture();
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (button == Input.Buttons.LEFT) {
					lmbPoint = null;
					// Click in gradient bounds; create a new point
					if (gradientContainsPosition(x, y) && gradientDown) {
						addPoint(x / getWidth());
						resetState();
						lmbPoint = null;
					}
				}
				else if (button == Input.Buttons.RIGHT) {
					if (rmbPoint != null) {
						// Can't delete first point of the gradient
						if (points.indexOf(rmbPoint, true) != 0) {
							// Can't delete last point of the gradient unless there are only two
							if (points.indexOf(rmbPoint, true) != points.size - 1 || points.size == 2) {
								if (rmbPoint.containsPosition(x, y)) {
									removePoint(rmbPoint);
								}
							}
						}
					}
					rmbPoint = null;
				}
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (button == Input.Buttons.LEFT) {
					if (gradientContainsPosition(x, y)) {
						gradientDown = true;
					}
					else {
						for (GradientPoint point : points) {
							if (point.containsPosition(x, y)) {
								lmbPoint = point;
								break;
							}
						}
					}
					return true;
				}
				if (button == Input.Buttons.RIGHT) {
					for (GradientPoint point : points) {
						if (point.containsPosition(x, y)) {
							rmbPoint = point;
							break;
						}
					}
					return true;
				}
				return false;
			}
		};
	}

	private class GradientPoint extends Image {

		public float time;

		public GradientPoint(float r, float g, float b, float time, Skin skin) {
			super();
			setDrawable(skin.getDrawable("color_picker_slider_knob_h"));
			setSize(POINT_SIZE, POINT_SIZE);
			setOrigin(POINT_SIZE / 2f, POINT_SIZE / 2f);
			setColor(r, g, b, 1f);
			this.time = time;
			addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (getTapCount() == 2) {
						EditableGradient.this.showColorPicker(GradientPoint.this);
					}
				}
			});
		}

		public boolean containsPosition(float x, float y) {
			return x >= getX() && x <= getX() + getWidth() && y >= getY() && y <= getY() + getHeight();
		}

	}

	@Override
	public float getPrefWidth() {
		return 64f;
	}

	@Override
	public float getPrefHeight() {
		return 52f;
	}

	@Override
	public void layout() {
		gradientImage.setPosition(getGradientX(), getGradientY());
		gradientImage.setSize(getGradientWidth(), getGradientHeight());
		for (GradientPoint point : points) {
			point.setPosition((-point.getWidth() / 2) + getWidth() * point.time, 0f);
		}
		updateTexture();
	}

	@Override
	protected void setStage(Stage stage) {
		Stage oldStage = getStage();
		super.setStage(stage);
		if (stage == null && oldStage != null) {
			if (gradientTexture != null) {
				gradientTexture.dispose();
				gradientTexture = null;
			}
		}
	}

}
