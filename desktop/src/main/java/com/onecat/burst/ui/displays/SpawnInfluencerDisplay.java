package com.onecat.burst.ui.displays;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.values.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.onecat.burst.ui.widgets.DisplayComponent;

public class SpawnInfluencerDisplay extends ControllerComponentDisplay {

	private static final String POINT = "Point";
	private static final String LINE = "Line";
	private static final String RECTANGLE = "Rectangle";
	private static final String ELLIPSE = "Ellipse";
	private static final String CYLINDER = "Cylinder";
	private static final String UNWEIGHTED_MESH = "Unweighted mesh";
	private static final String WEIGHTED_MESH = "Weighted mesh";

	SpawnInfluencer influencer;

	// Different spawn shape values can be cached for convenience
	private PointSpawnShapeValue pointSpawnShapeValue;
	private LineSpawnShapeValue lineSpawnShapeValue;
	private RectangleSpawnShapeValue rectangleSpawnShapeValue;
	private EllipseSpawnShapeValue ellipseSpawnShapeValue;
	private CylinderSpawnShapeValue cylinderSpawnShapeValue;
	private UnweightedMeshSpawnShapeValue unweightedMeshSpawnShapeValue;
	private WeightMeshSpawnShapeValue weightMeshSpawnShapeValue;

	public SpawnInfluencerDisplay(ParticleController controller, Skin skin) {
		super(controller, "Spawn Influencer", skin);
	}

	@Override
	protected void initialize() {
		influencer = controller.findInfluencer(SpawnInfluencer.class);
	}

	private void createXYZRangedComponents(SpawnShapeValue value) {
		Label xyzOffsetsHeader = new Label("Offsets", getSkin());
		xyzOffsetsHeader.setAlignment(Align.center);
		addContent(xyzOffsetsHeader);
		addContent(new DisplayComponent.RangedNumericComponent(
				"X",
				value.xOffsetValue,
				getSkin(),
				false,
				false,
				false
		));
		addContent(new DisplayComponent.RangedNumericComponent(
				"Y",
				value.yOffsetValue,
				getSkin(),
				false,
				false,
				false
		));
		addContent(new DisplayComponent.RangedNumericComponent(
				"Z",
				value.zOffsetValue,
				getSkin(),
				false,
				false,
				false
		));
		if (value instanceof PrimitiveSpawnShapeValue val) {
			Label dimensionsHeader = new Label("Dimensions", getSkin());
			dimensionsHeader.setAlignment(Align.center);
			addContent(dimensionsHeader);
			addContent(new DisplayComponent.ScaledNumericComponent(
					"Width",
					val.spawnWidthValue,
					getSkin(),
					false,
					false,
					true
			));
			addContent(new DisplayComponent.ScaledNumericComponent(
					"Height",
					val.spawnHeightValue,
					getSkin(),
					false,
					false,
					true
			));
			addContent(new DisplayComponent.ScaledNumericComponent(
					"Depth",
					val.spawnDepthValue,
					getSkin(),
					false,
					false,
					true
			));
		}
	}

	private void onShapeTypeSelected(String type) {
		// Cache current spawn shape settings
		if (influencer.spawnShapeValue instanceof PointSpawnShapeValue val) {
			pointSpawnShapeValue = val;
		}
		if (influencer.spawnShapeValue instanceof LineSpawnShapeValue val) {
			lineSpawnShapeValue = val;
		}
		if (influencer.spawnShapeValue instanceof RectangleSpawnShapeValue val) {
			rectangleSpawnShapeValue = val;
		}
		if (influencer.spawnShapeValue instanceof EllipseSpawnShapeValue val) {
			ellipseSpawnShapeValue = val;
		}
		if (influencer.spawnShapeValue instanceof CylinderSpawnShapeValue val) {
			cylinderSpawnShapeValue = val;
		}
		if (influencer.spawnShapeValue instanceof UnweightedMeshSpawnShapeValue val) {
			unweightedMeshSpawnShapeValue = val;
		}
		if (influencer.spawnShapeValue instanceof WeightMeshSpawnShapeValue val) {
			weightMeshSpawnShapeValue = val;
		}
		// Load cached or create new
		switch (type) {
			case POINT -> {
				if (pointSpawnShapeValue != null) {
					influencer.spawnShapeValue = pointSpawnShapeValue;
				}
				else {
					influencer.spawnShapeValue = new PointSpawnShapeValue();
				}
			}
			case LINE -> {
				if (lineSpawnShapeValue != null) {
					influencer.spawnShapeValue = lineSpawnShapeValue;
				}
				else {
					LineSpawnShapeValue spawnShapeValue = new LineSpawnShapeValue();
					spawnShapeValue.spawnWidthValue.setHigh(3f);
					influencer.spawnShapeValue = spawnShapeValue;
				}
			}
			case RECTANGLE -> {
				if (rectangleSpawnShapeValue != null) {
					influencer.spawnShapeValue = rectangleSpawnShapeValue;
				}
				else {
					RectangleSpawnShapeValue spawnShapeValue = new RectangleSpawnShapeValue();
					spawnShapeValue.spawnDepthValue.setHigh(1f);
					spawnShapeValue.spawnWidthValue.setHigh(1f);
					influencer.spawnShapeValue = spawnShapeValue;
				}
			}
			case ELLIPSE -> {
				if (ellipseSpawnShapeValue != null) {
					influencer.spawnShapeValue = ellipseSpawnShapeValue;
				}
				else {
					EllipseSpawnShapeValue spawnShapeValue = new EllipseSpawnShapeValue();
					spawnShapeValue.spawnWidthValue.setHigh(3f);
					spawnShapeValue.spawnHeightValue.setHigh(3f);
					spawnShapeValue.spawnDepthValue.setHigh(3f);
					influencer.spawnShapeValue = spawnShapeValue;
				}
			}
			case CYLINDER -> {
				if (cylinderSpawnShapeValue != null) {
					influencer.spawnShapeValue = cylinderSpawnShapeValue;
				}
				else {
					CylinderSpawnShapeValue spawnShapeValue = new CylinderSpawnShapeValue();
					spawnShapeValue.spawnWidthValue.setHigh(3f);
					spawnShapeValue.spawnHeightValue.setHigh(3f);
					spawnShapeValue.spawnDepthValue.setHigh(3f);
					influencer.spawnShapeValue = spawnShapeValue;
				}
			}
			// TODO implement
			case UNWEIGHTED_MESH -> {
				if (unweightedMeshSpawnShapeValue != null) {
					influencer.spawnShapeValue = unweightedMeshSpawnShapeValue;
				}
				else {

				}
			}
			// TODO implement
			case WEIGHTED_MESH -> {
				if (weightMeshSpawnShapeValue != null) {
					influencer.spawnShapeValue = weightMeshSpawnShapeValue;
				}
				else {

				}
			}
		}
		influencer.start();
		onFolded();
		onUnfolded();
		foldButton.setChecked(true);
	}

	@Override
	protected void onUnfolded() {
		Table typeTable = new Table();
		typeTable.setBackground(getSkin().getDrawable("button_flat_default_disabled"));
		Label typeLabel = new Label("Shape", getSkin());
		typeTable.defaults().growX().uniformX().minHeight(26f).space(4f);
		typeTable.add(typeLabel);
		SelectBox<String> typeSelectBox = new SelectBox<>(getSkin());
		typeSelectBox.setItems(
				POINT,
				LINE,
				RECTANGLE,
				ELLIPSE,
				CYLINDER,
				UNWEIGHTED_MESH,
				WEIGHTED_MESH
		);
		typeTable.add(typeSelectBox);
		addContent(typeTable);
		if (influencer.spawnShapeValue instanceof PointSpawnShapeValue) {
			typeSelectBox.setSelected(POINT);
			createXYZRangedComponents(influencer.spawnShapeValue);
		}
		if (influencer.spawnShapeValue instanceof LineSpawnShapeValue) {
			typeSelectBox.setSelected(LINE);
			createXYZRangedComponents(influencer.spawnShapeValue);
		}
		if (influencer.spawnShapeValue instanceof RectangleSpawnShapeValue) {
			typeSelectBox.setSelected(RECTANGLE);
			createXYZRangedComponents(influencer.spawnShapeValue);
		}
		if (influencer.spawnShapeValue instanceof EllipseSpawnShapeValue) {
			typeSelectBox.setSelected(ELLIPSE);
			createXYZRangedComponents(influencer.spawnShapeValue);
		}
		if (influencer.spawnShapeValue instanceof CylinderSpawnShapeValue) {
			typeSelectBox.setSelected(CYLINDER);
			createXYZRangedComponents(influencer.spawnShapeValue);
		}
		if (influencer.spawnShapeValue instanceof UnweightedMeshSpawnShapeValue) {
			typeSelectBox.setSelected(UNWEIGHTED_MESH);
		}
		if (influencer.spawnShapeValue instanceof WeightMeshSpawnShapeValue) {
			typeSelectBox.setSelected(WEIGHTED_MESH);
		}
		typeSelectBox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onShapeTypeSelected(typeSelectBox.getSelected());
			}
		});
	}

}
