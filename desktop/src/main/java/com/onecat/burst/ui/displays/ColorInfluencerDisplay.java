package com.onecat.burst.ui.displays;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.onecat.burst.ui.widgets.DisplayComponent;
import com.onecat.burst.ui.widgets.EditableGradient;

public class ColorInfluencerDisplay extends ControllerComponentDisplay {

	ColorInfluencer influencer;
	// We can use this to cache the settings in case user accidentally switches between Single and Random
	ColorInfluencer.Single singleInfluencer;

	private enum Type {
		SINGLE,
		RANDOM
	}

	private Type type = null;

	public ColorInfluencerDisplay(ParticleController controller, Skin skin, boolean isActive) {
		super(controller, "Color Influencer", skin, true, isActive);
	}

	private void onTypeButtonPressed(Type newType) {
		if (newType == type) return;
		type = newType;
		switch (type) {
			case SINGLE -> replaceWithSingle();
			case RANDOM -> replaceWithRandom();
		}
		onFolded();
		onUnfolded();
		foldButton.setChecked(true);
	}

	private void replaceWithSingle() {
		if (singleInfluencer == null) {
			singleInfluencer = new ColorInfluencer.Single();
		}
		influencer = singleInfluencer;
		controller.replaceInfluencer(ColorInfluencer.class, influencer);
		controller.init();
	}

	private void replaceWithRandom() {
		influencer = new ColorInfluencer.Random();
		controller.replaceInfluencer(ColorInfluencer.class, influencer);
		controller.init();
	}

	@Override
	protected void initialize() {
		influencer = controller.findInfluencer(ColorInfluencer.class);
		if (influencer instanceof ColorInfluencer.Single single) singleInfluencer = single;
		controller.init();
	}

	@Override
	protected void onUnfolded() {

		Table typeTable = new Table();
		typeTable.defaults().growX().uniformX().minHeight(26f).space(4f);

		if (influencer instanceof ColorInfluencer.Single) type = Type.SINGLE;
		if (influencer instanceof ColorInfluencer.Random) type = Type.RANDOM;

		CheckBox singleCheckbox = new CheckBox("Single", getSkin());
		singleCheckbox.setChecked(type == Type.SINGLE);
		singleCheckbox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onTypeButtonPressed(Type.SINGLE);
			}
		});
		typeTable.add(singleCheckbox);
		CheckBox randomCheckbox = new CheckBox("Random", getSkin());
		randomCheckbox.setChecked(type == Type.RANDOM);
		randomCheckbox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onTypeButtonPressed(Type.RANDOM);
			}
		});
		typeTable.add(randomCheckbox);

		ButtonGroup<CheckBox> buttonGroup = new ButtonGroup<>(singleCheckbox, randomCheckbox);
		buttonGroup.setMaxCheckCount(1);
		buttonGroup.setMinCheckCount(1);

		addContent(typeTable);

		switch (type) {
			case SINGLE -> {
				EditableGradient editableGradient = new EditableGradient(getSkin());
				editableGradient.set(singleInfluencer.colorValue);
				addContent(editableGradient);
				addContent(new DisplayComponent.ScaledNumericComponent(
						"Alpha",
						singleInfluencer.alphaValue,
						getSkin(),
						true,
						false,
						true
				));
			}
			case RANDOM -> {
				Label noParamsLabel = new Label("No parameters.", getSkin());
				noParamsLabel.setAlignment(Align.center);
				addContent(noParamsLabel);
			}
		}
	}

	@Override
	protected void onAdded() {
		singleInfluencer = new ColorInfluencer.Single();
		influencer = singleInfluencer;
		if (!controller.replaceInfluencer(ColorInfluencer.class, influencer)) {
			controller.influencers.add(influencer);
			controller.init();
		}
	}

	@Override
	protected void onRemoved() {
		controller.removeInfluencer(ColorInfluencer.class);
		influencer = null;
		controller.init();
	}

}
