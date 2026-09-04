package com.onecat.burst.ui.displays;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g3d.particles.values.ScaledNumericValue;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;
import java.util.Locale;

public class EditableGraph extends Widget {

	private static final Color BG_COLOR = new Color(Color.valueOf("222222"));
	private static final Color GRID_COLOR = new Color(Color.valueOf("666666"));
	private static final Color LINE_COLOR = new Color(Color.CYAN);
	private static final Color POINT_HIGHLIGHT_COLOR = new Color(Color.RED);

	private static final float POINT_RADIUS = 4f;

	private static final float PAD_LEFT = 32f;
	private static final float PAD_BOTTOM = 20f;
	private static final float PAD_RIGHT = 6f;
	private static final float PAD_TOP = 6f;

	private static final float SNAP_STEP = 0.01f;

	private static Comparator<GraphPoint> graphSorter;

	@SuppressWarnings("GDXJavaStaticResource")
	public static ShapeRenderer shapeRenderer = null;
	private final Matrix4 originalBatchTransform = new Matrix4();
	private final Matrix4 originalShapeTransform = new Matrix4();

	private final ScaledNumericValue value;
	private final BitmapFont font;
	private final GlyphLayout layout = new GlyphLayout();

	private final Array<GraphPoint> points = new Array<>(true, 10);

	public EditableGraph(ScaledNumericValue value, Skin skin) {
		this.value = value;
		font = skin.getFont("font_tiny");
		for (int i = 0; i < value.getScaling().length; i++) {
			points.add(new GraphPoint(value.getTimeline()[i], value.getScaling()[i]));
		}
		addListener(createInputListener());
	}

	private class GraphPoint {

		private float x;
		private float y;
		private boolean highlighted;
		private boolean clicked;

		public GraphPoint(float timeline, float scaling) {
			x = timeline;
			y = scaling;
		}

		public float getGraphX() {
			return EditableGraph.this.getGraphX() + x * getGraphWidth();
		}

		public float getGraphY() {
			return EditableGraph.this.getGraphY() + y * getGraphHeight();
		}

		public boolean containsPosition(float x, float y) {
			float graphX = getGraphX();
			float graphY = getGraphY();
			float threshold = POINT_RADIUS * 1.5f;
			return x >= graphX - threshold && x <= graphX + threshold && y >= graphY - threshold && y <= graphY + threshold;
		}

		public void setHighlighted(boolean highlighted) {
			this.highlighted = highlighted;
		}

		public void setClicked(boolean clicked) {
			this.clicked = clicked;
		}

	}

	private float getGraphX() {
		return PAD_LEFT;
	}

	private float getGraphY() {
		return PAD_BOTTOM;
	}

	private boolean graphContainsPosition(float x, float y) {
		return x >= getGraphX() && x <= getGraphX() + getGraphWidth() && y >= getGraphY() && y <= getGraphY() + getGraphHeight();
	}

	private float normalizeMouseX(float value) {
		float normalized = MathUtils.clamp((value - PAD_LEFT) / getGraphWidth(), 0f, 1f);
		return Math.round(normalized / SNAP_STEP) * SNAP_STEP;
	}

	private float normalizeMouseY(float value) {
		float normalized = MathUtils.clamp((value - PAD_BOTTOM) / getGraphHeight(), 0f, 1f);
		return Math.round(normalized / SNAP_STEP) * SNAP_STEP;
	}

	private float getGraphWidth() {
		return getWidth() - PAD_LEFT - PAD_RIGHT;
	}

	private float getGraphHeight() {
		return getHeight() - PAD_BOTTOM - PAD_TOP;
	}

	private void addPoint(float timeline, float scaling) {
		points.add(new GraphPoint(timeline, scaling));
		sortPoints();
		syncPoints();
		// TODO update emitter Value
	}

	private void sortPoints() {
		if (graphSorter == null) graphSorter = (o1, o2) -> {
			if (o1.x < o2.x) return -1;
			if (o1.x > o2.x) return 1;
			return 0;
		};
		points.sort(graphSorter);
	}

	private void syncPoints() {
		float[] timeline = new float[points.size];
		float[] scaling = new float[points.size];
		GraphPoint point;
		for (int i = 0; i < points.size; i++) {
			point = points.get(i);
			timeline[i] = point.x;
			scaling[i] = point.y;
		}
		value.setTimeline(timeline);
		value.setScaling(scaling);
	}

	private InputListener createInputListener() {
		return new InputListener() {

			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				for (GraphPoint point : points) {
					point.setHighlighted(point.containsPosition(x, y));
				}
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				getStage().setScrollFocus(EditableGraph.this);
				for (GraphPoint point : points) {
					if (point.clicked) {
						if (points.indexOf(point, true) != 0)
							point.x = normalizeMouseX(x);
						point.y = normalizeMouseY(y);
						sortPoints();
						syncPoints();
					}
				}
				super.touchDragged(event, x, y, pointer);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				getStage().setScrollFocus(EditableGraph.this);
				if (button == Input.Buttons.LEFT) {
					boolean pointWasClicked = false;
					for (GraphPoint point : points) {
						if (point.clicked) pointWasClicked = true;
						point.setClicked(false);
					}
					if (pointWasClicked) return;
					if (!graphContainsPosition(x, y)) return;
					addPoint(normalizeMouseX(x), normalizeMouseY(y));
				}
				else if (button == Input.Buttons.RIGHT) {
					// Point 0 can't be deleted
					for (int i = points.size - 1; i > 0; i--) {
						if (points.get(i).containsPosition(x, y)) {
							points.removeIndex(i);
							break;
						}
					}
				}
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (button == Input.Buttons.LEFT) {
					for (GraphPoint point : points) {
						if (point.containsPosition(x, y)) {
							point.setClicked(true);
							break;
						}
					}
				}
				return true;
			}
		};
	}

	@Override
	public float getMinWidth() {
		return getPrefWidth();
	}

	@Override
	public float getMinHeight() {
		return getPrefHeight();
	}

	@Override
	public float getPrefWidth() {
		return 200f;
	}

	@Override
	public float getPrefHeight() {
		return 196f;
	}

	@Override
	public float getMaxWidth() {
		return 0;
	}

	@Override
	public float getMaxHeight() {
		return 0;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();

		originalBatchTransform.set(batch.getTransformMatrix());
		originalShapeTransform.set(shapeRenderer.getTransformMatrix());
		batch.getTransformMatrix().translate(getX(), getY(), 0);
		shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

		int steps = 4;
		shapeRenderer.setAutoShapeType(true);
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.begin();
		// Background
		shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(BG_COLOR);
		shapeRenderer.rect(getGraphX(), getGraphY(), getGraphWidth(), getGraphHeight());
		// Grid
		shapeRenderer.set(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(GRID_COLOR);
		for (int i = 0; i <= steps; i++) {
			float stepX = getGraphX() + ((getGraphWidth() / steps) * i);
			float stepY = getGraphY() + ((getGraphHeight() / steps) * i);
			shapeRenderer.line(getGraphX(), stepY, getGraphX() + getGraphWidth(), stepY);
			shapeRenderer.line(stepX, getGraphY(), stepX, getGraphY() + getGraphHeight());
		}
		shapeRenderer.end();
		// Text indicators for steps
		batch.begin();
		float marginX = -8f;
		float marginY = -2f;
		for (int i = 0; i <= steps; i++) {
			String text = i * (100 / steps) + "%";
			layout.setText(font, text);
			float textWidth = layout.width;
			float textHeight = layout.height;
			if (i == 0) {
				float xPos = getGraphX() - textWidth + marginX;
				float yPos = getGraphY() - textHeight;
				font.draw(batch, text, xPos, yPos);
				continue;
			}
			// Horizontal
			float xPos = getGraphX() + (getGraphWidth() / steps) * i - textWidth / 2;
			float yPos = getGraphY() - textHeight;
			if (i == steps) xPos -= textWidth / 2f;
			font.draw(batch, text, xPos, yPos);
			// Vertical
			xPos = getGraphX() - textWidth - 8f;
			yPos = getGraphY() + (getGraphHeight() / steps) * i + textHeight / 2 + marginY;
			if (i == steps) yPos -= textHeight / 2f;
			font.draw(batch, text, xPos, yPos);
		}
		batch.end();
		// Draw graph line and points
		shapeRenderer.begin();
		// Lines
		shapeRenderer.setColor(LINE_COLOR);
		shapeRenderer.set(ShapeRenderer.ShapeType.Line);
		GraphPoint point;
		for (int i = 0; i < points.size; i++) {
			point = points.get(i);
			if (i + 1 < points.size) {
				float x2 = points.get(i + 1).getGraphX();
				float y2 = points.get(i + 1).getGraphY();
				shapeRenderer.line(point.getGraphX(), point.getGraphY(), x2, y2);
			}
			else {
				shapeRenderer.line(point.getGraphX(), point.getGraphY(), getGraphX() + getGraphWidth(), point.getGraphY());
			}
		}
		// Points
		shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
		for (int i = 0; i < points.size; i++) {
			point = points.get(i);
			if (point.highlighted) shapeRenderer.setColor(POINT_HIGHLIGHT_COLOR);
			else shapeRenderer.setColor(LINE_COLOR);
			shapeRenderer.circle(point.getGraphX(), point.getGraphY(), POINT_RADIUS, 12);
		}
		shapeRenderer.end();
		batch.begin();
		font.setColor(Color.YELLOW);
		for (int i = 0; i < points.size; i++) {
			point = points.get(i);
			if (point.highlighted) {
				String text = String.format(Locale.ROOT, "%1.0f%%", point.x * 100.0f);
				layout.setText(font, text);
				float textWidth = layout.width;
				float textHeight = layout.height;
				float xPos = point.getGraphX() - textWidth - 12f;
				float yPos = point.getGraphY() + textHeight / 2f;
				font.draw(batch, text, xPos, yPos);

				text = String.format(Locale.ROOT, "%1.0f%%", point.y * 100.0f);
				layout.setText(font, text);
				textWidth = layout.width;
				textHeight = layout.height;
				xPos = point.getGraphX() - textWidth / 2f;
				yPos = point.getGraphY() + textHeight + 8f;
				font.draw(batch, text, xPos, yPos);
			}
		}
		font.setColor(Color.WHITE);
		batch.end();
		batch.setTransformMatrix(originalBatchTransform);
		shapeRenderer.setTransformMatrix(originalShapeTransform);
		batch.begin();
	}

}
