package com.onecat.burst.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.onecat.burst.ui.displays.EditableGraph;
import com.onecat.burst.ui.displays.RegularEmitterDisplay;
import java.util.Locale;

public class ControllerPanel extends Table {

	private float opacity = 0.9f;

	public ControllerPanel(ParticleController controller, Skin skin) {
		super(skin);
		setName("ControllerPanel");
		setBackground("panel_default_default");
		setTouchable(Touchable.enabled);
		addCaptureListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		defaults().space(4f).growX();
		left();
		top();
		Label headerLabel = new Label(String.format(Locale.ROOT, "Editing %s", controller.name), skin.get("header", Label.LabelStyle.class));
		headerLabel.setAlignment(Align.center);
		add(headerLabel).row();
		Table controllerDisplaysTable = new Table();
		controllerDisplaysTable.defaults().growX().space(8f);
		controllerDisplaysTable.top();
		ScrollPane scroll = new ScrollPane(controllerDisplaysTable);
		scroll.setFlickScroll(false);
		scroll.addListener(new InputListener() {

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				getStage().setScrollFocus(scroll);
				super.enter(event, x, y, pointer, fromActor);
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				if (!(toActor instanceof EditableGraph))
					getStage().setScrollFocus(null);
				super.exit(event, x, y, pointer, toActor);
			}
		});
		add(scroll).grow();
		controllerDisplaysTable.add(new RegularEmitterDisplay((RegularEmitter) controller.emitter, getSkin()));
		/* Billboard Controller and PointSprite Controller have following Influencers
		 * Regular Emitter (always)
		 * Region Influencer (always)
		 * 	- Single
		 * 	- Animated
		 *  - Random
		 * Spawn Influencer (always)
		 * 	- Point
		 * 	- Line
		 * 	- Rectangle
		 * 	- Ellipse
		 * 	- Cylinder
		 * 	- Unweighted Mesh
		 * 	- Weighted Mesh
		 * Scale Influencer (optional)
		 * Color (optional)
		 * 	- Single Color Influencer
		 * 	- Random Color Influencer
		 * Dynamics (optional) (this is tricky because it contains sub-influencers of any kind and number)
		 * 	- Angular Velocity 3D
		 * 	- Centripetal
		 * 	- Tangential
		 * 	- Polar
		 * 	- Brownian
		 * 	- Face
		 *
		 * ModelInstance Controller has following Influencers
		 * RegularEmitter (always)
		 * Model Influencer
		 * 	- Single
		 * 	- Random
		 * Spawn Influencer (always)
		 * 	- Point
		 * 	- Line
		 * 	- Rectangle
		 * 	- Ellipse
		 * 	- Cylinder
		 * 	- Unweighted Mesh
		 * 	- Weighted Mesh
		 * Scale Influencer (optional)
		 * Color (optional)
		 * 	- Single Color Influencer
		 * 	- Random Color Influencer
		 * Dynamics (optional)
		 *
		 *
		 * Which leaves us with a list of influencers:
		 * 	Regular Emitter
		 * 	Region Influencer (Single, Random, Animated)
		 * 	Model Influencer (Single, Random)
		 * 	Spawn Influencer (Point, Line, Rectangle, Ellipse, Cylinder, Unweighted Mesh, Weighted Mesh)
		 * 	Scale Influencer
		 * 	Color Influencer (Single, Random)
		 * 	Dynamics (mix of all kinds)
		 */

	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	@Override
	public void layout() {
		setHeight(getStage().getHeight() - getStage().getRoot().findActor("TopBar").getHeight() - 16f);
		setWidth(412f);
		setX(getStage().getWidth() - getWidth() - 8f);
		setY(8f);
		super.layout();
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
		super.drawBackground(batch, opacity, x, y);
	}

}
