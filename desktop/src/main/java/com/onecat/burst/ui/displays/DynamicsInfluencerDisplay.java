package com.onecat.burst.ui.displays;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsInfluencer;
import com.badlogic.gdx.graphics.g3d.particles.influencers.DynamicsModifier;
import com.badlogic.gdx.graphics.g3d.particles.renderers.ModelInstanceRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.onecat.burst.ui.widgets.DisplayComponent;

public class DynamicsInfluencerDisplay extends ControllerComponentDisplay {

	private static final String ANGULAR_2D = "Angular 2D";
	private static final String ANGULAR_3D = "Angular 3D";
	private static final String FACE = "Face";
	private static final String CENTRIPETAL = "Centripetal";
	private static final String TANGENTIAL = "Tangential";
	private static final String POLAR = "Polar";
	private static final String BROWNIAN = "Brownian";

	private boolean is3d = false;
	private DynamicsInfluencer influencer;
	private DynamicsModifier pickedModifier;

	public DynamicsInfluencerDisplay(ParticleController controller, Skin skin, boolean isActive) {
		super(controller, "Dynamics Influencer", skin, true, isActive);
	}

	private void setSelectedModifier(int index) {
		if (index == -1 || influencer.velocities.size == 0) {
			pickedModifier = null;
			return;
		}
		pickedModifier = influencer.velocities.get(index);
	}

	private void onRemoveButtonPressed() {
		influencer.velocities.removeValue(pickedModifier, true);
		pickedModifier = null;
		onFolded();
		onUnfolded();
		foldButton.setChecked(true);
	}

	private void onAddButtonPressed(String type) {
		DynamicsModifier mod = null;
		switch (type) {
			case ANGULAR_2D -> mod = new DynamicsModifier.Rotational2D();
			case ANGULAR_3D -> mod = new DynamicsModifier.Rotational3D();
			case FACE -> mod = new DynamicsModifier.FaceDirection();
			case CENTRIPETAL -> mod = new DynamicsModifier.CentripetalAcceleration();
			case TANGENTIAL -> mod = new DynamicsModifier.TangentialAcceleration();
			case POLAR -> mod = new DynamicsModifier.PolarAcceleration();
			case BROWNIAN -> mod = new DynamicsModifier.BrownianAcceleration();
		}
		influencer.velocities.add(mod);
		controller.init();
		setSelectedModifier(influencer.velocities.size - 1);
		onFolded();
		onUnfolded();
		foldButton.setChecked(true);
	}

	@Override
	protected void initialize() {
		if (controller.renderer instanceof ModelInstanceRenderer) is3d = true;
		influencer = controller.findInfluencer(DynamicsInfluencer.class);
	}

	@Override
	protected void onUnfolded() {
		// A three column table
		Table mainTable = new Table();
		mainTable.defaults().space(4f).grow().uniformX();
		mainTable.setBackground(getSkin().getDrawable("panel_thin_default"));

		// Left column with buttons to add new forces
		Table addTable = new Table();
		addTable.defaults().space(4f).growX();
		addTable.top();
		Label addLabel = new Label("Add...", getSkin());
		addLabel.setAlignment(Align.center);
		addTable.add(addLabel).row();
		addTable.defaults().growX().uniformX().minHeight(26f).space(4f);
		String[] forces2d = new String[]{
				ANGULAR_2D
		};
		String[] forces3d = new String[]{
				ANGULAR_3D,
				FACE
		};
		String[] forcesCommon = new String[]{
				CENTRIPETAL,
				TANGENTIAL,
				POLAR,
				BROWNIAN
		};
		Array<String> forces = new Array<>();
		if (!is3d) forces.addAll(forces2d);
		else forces.addAll(forces3d);
		forces.addAll(forcesCommon);
		for (String force : forces) {
			TextButton button = new TextButton(force, getSkin());
			button.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					onAddButtonPressed(button.getText().toString());
				}
			});
			addTable.add(button).row();
		}
		mainTable.add(addTable);

		// Central column with a list of existing forces
		Table forcesTable = new Table();
		forcesTable.defaults().space(4f);
		Label forcesLabel = new Label("Forces", getSkin());
		forcesTable.add(forcesLabel).row();
		List<String> modifierList = new List<>(getSkin());
		Array<String> modifierNames = new Array<>();
		int i = 1;
		for (DynamicsModifier modifier : influencer.velocities) {
			String modName = "";
			if (modifier instanceof DynamicsModifier.CentripetalAcceleration) modName = "Centripetal";
			else if (modifier instanceof DynamicsModifier.TangentialAcceleration) modName = "Tangential";
			else if (modifier instanceof DynamicsModifier.PolarAcceleration) modName = "Polar";
			else if (modifier instanceof DynamicsModifier.BrownianAcceleration) modName = "Brownian";
			else if (modifier instanceof DynamicsModifier.Rotational2D) modName = "Angular 2D";
			else if (modifier instanceof DynamicsModifier.Rotational3D) modName = "Angular 3D";
			else if (modifier instanceof DynamicsModifier.FaceDirection) modName = "Face";
			modName = String.format("%s - %s", i, modName);
			modifierNames.add(modName);
			i++;
		}
		modifierList.setItems(modifierNames);
		modifierList.setSelectedIndex(influencer.velocities.indexOf(pickedModifier, true));
		modifierList.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setSelectedModifier(modifierList.getSelectedIndex());
				onFolded();
				onUnfolded();
				foldButton.setChecked(true);
			}
		});
		forcesTable.add(modifierList).grow();
		mainTable.add(forcesTable);

		// Right column with editing options
		Table controlsTable = new Table();
		controlsTable.defaults().space(4f).growX();
		controlsTable.top();
		Label controlsLabel = new Label("Edit...", getSkin());
		controlsLabel.setAlignment(Align.center);
		controlsTable.add(controlsLabel).row();
		TextButton removeButton = new TextButton("Remove", getSkin());
		removeButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onRemoveButtonPressed();
			}
		});
		controlsTable.add(removeButton);
		mainTable.add(controlsTable);
		addContent(mainTable);

		// Display for currently edited velocity properties
		if (pickedModifier != null) {
			boolean addGlobalCheckbox = true;
			Table propertiesTable = new Table();
			propertiesTable.defaults().growX().space(4f);
			if (pickedModifier instanceof DynamicsModifier.CentripetalAcceleration mod) {
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Strength",
						mod.strengthValue,
						getSkin(),
						false,
						false,
						true
				));
			}
			else if (pickedModifier instanceof DynamicsModifier.TangentialAcceleration mod) {
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Strength",
						mod.strengthValue,
						getSkin(),
						false,
						false,
						true
				)).row();
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Polar Angle",
						mod.thetaValue,
						getSkin(),
						false,
						false,
						true
				)).row();
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Azimuth",
						mod.phiValue,
						getSkin(),
						false,
						false,
						true
				));
			}
			else if (pickedModifier instanceof DynamicsModifier.PolarAcceleration mod) {
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Strength",
						mod.strengthValue,
						getSkin(),
						false,
						false,
						true
				)).row();
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Polar Angle",
						mod.thetaValue,
						getSkin(),
						false,
						false,
						true
				)).row();
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Azimuth",
						mod.phiValue,
						getSkin(),
						false,
						false,
						true
				));
			}
			else if (pickedModifier instanceof DynamicsModifier.BrownianAcceleration mod) {
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Strength",
						mod.strengthValue,
						getSkin(),
						false,
						false,
						true
				));
			}
			else if (pickedModifier instanceof DynamicsModifier.Rotational2D mod) {
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Strength",
						mod.strengthValue,
						getSkin(),
						false,
						false,
						true
				));
			}
			else if (pickedModifier instanceof DynamicsModifier.Rotational3D mod) {
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Strength",
						mod.strengthValue,
						getSkin(),
						false,
						false,
						true
				)).row();
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Polar Angle",
						mod.thetaValue,
						getSkin(),
						false,
						false,
						true
				)).row();
				propertiesTable.add(new DisplayComponent.ScaledNumericComponent(
						"Azimuth",
						mod.phiValue,
						getSkin(),
						false,
						false,
						true
				));
			}
			else if (pickedModifier instanceof DynamicsModifier.FaceDirection mod) {
				// TODO this modifier doesn't work together with other angular modifiers and this should be let known to user
				addGlobalCheckbox = false;
			}
			if (addGlobalCheckbox) {
				CheckBox globalCheckbox = new CheckBox("Global", getSkin());
				globalCheckbox.setProgrammaticChangeEvents(false);
				globalCheckbox.setChecked(pickedModifier.isGlobal);
				globalCheckbox.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						pickedModifier.isGlobal = globalCheckbox.isChecked();
					}
				});
				addContent(globalCheckbox);
			}
			addContent(propertiesTable);
		}
	}

	@Override
	protected void onAdded() {
		influencer = new DynamicsInfluencer();
		if (!controller.replaceInfluencer(DynamicsInfluencer.class, influencer)) {
			controller.influencers.add(influencer);
			controller.init();
		}
	}

	@Override
	protected void onRemoved() {
		controller.removeInfluencer(DynamicsInfluencer.class);
		pickedModifier = null;
		influencer = null;
		controller.init();
	}

}
