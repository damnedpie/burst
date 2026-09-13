package com.onecat.burst.ui.displays;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.onecat.burst.Main;
import com.onecat.burst.world.ParticleManager;
import java.util.ArrayList;
import java.util.Objects;

public class RegionInfluencerDisplay extends ControllerComponentDisplay {

	private final ParticleManager particleManager;
	private final ParticleController controller;
	private RegionInfluencer influencer;

	private enum Type {
		SINGLE,
		RANDOM,
		ANIMATED
	}

	private Type type;

	private TextureAtlas projectAtlas;
	private String[] regionNames;

	private final Array<CheckBox> randomRegionCheckboxes = new Array<>();

	public RegionInfluencerDisplay(ParticleController controller, RegionInfluencer influencer, Skin skin) {
		super("Region Influencer", skin);
		this.influencer = influencer;
		this.controller = controller;
		this.particleManager = Main.getParticleManager();
		checkIfUnfoldedBefore();
	}

	public void refreshAtlas() {
		fetchAtlasData();
		if (foldButton.isChecked()) {
			onFolded();
			onUnfolded();
			foldButton.setChecked(true);
		}
	}

	private void fetchAtlasData() {
		influencer = controller.findInfluencer(RegionInfluencer.class);
		projectAtlas = particleManager.getAtlas();
		if (projectAtlas == null) return;
		regionNames = new String[projectAtlas.getRegions().size];
		for (int i = 0; i < projectAtlas.getRegions().size; i++) {
			regionNames[i] = projectAtlas.getRegions().get(i).name;
		}
	}

	private void replaceWithSingle(String regionName) {
		particleManager.replaceRegionInfluencerWithSingle(controller.name, regionName);
		influencer = controller.findInfluencer(RegionInfluencer.class);
	}

	private void replaceWithRandom(String... regionNames) {
		particleManager.replaceRegionInfluencerWithRandom(controller.name, regionNames);
		influencer = controller.findInfluencer(RegionInfluencer.class);
	}

	private void replaceWithAnimated(String... regionNames) {
		particleManager.replaceRegionInfluencerWithAnimated(controller.name, regionNames);
		influencer = controller.findInfluencer(RegionInfluencer.class);
	}

	private void onTypeButtonPressed(Type newType) {
		if (newType == type) return;
		type = newType;
		switch (type) {
			case SINGLE -> replaceWithSingle(projectAtlas.getRegions().first().name);
			case RANDOM -> replaceWithRandom(regionNames);
			case ANIMATED -> replaceWithAnimated(regionNames);
		}
		onFolded();
		onUnfolded();
		foldButton.setChecked(true);
	}

	private void onRandomRegionsListChanged() {
		ArrayList<String> selectedRegionsList = new ArrayList<>();
		for (CheckBox checkBox : randomRegionCheckboxes) {
			if (checkBox.isChecked()) selectedRegionsList.add(checkBox.getText().toString());
		}
		String[] selectedRegionsNames = selectedRegionsList.toArray(new String[0]);
		replaceWithRandom(selectedRegionsNames);
	}

	private void onAnimatedInfluencerRegionsListChanged(String... items) {
		replaceWithAnimated(items);
	}

	@Override
	void onUnfolded() {
		fetchAtlasData();
		Table typeTable = new Table();
		typeTable.defaults().growX().uniformX().minHeight(26f).space(4f);

		if (influencer instanceof RegionInfluencer.Single) type = Type.SINGLE;
		if (influencer instanceof RegionInfluencer.Random) type = Type.RANDOM;
		if (influencer instanceof RegionInfluencer.Animated) type = Type.ANIMATED;

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
		CheckBox animatedCheckbox = new CheckBox("Animated", getSkin());
		animatedCheckbox.setChecked(type == Type.ANIMATED);
		animatedCheckbox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onTypeButtonPressed(Type.ANIMATED);
			}
		});
		typeTable.add(animatedCheckbox);

		ButtonGroup<CheckBox> buttonGroup = new ButtonGroup<>(singleCheckbox, randomCheckbox, animatedCheckbox);
		buttonGroup.setMaxCheckCount(1);
		buttonGroup.setMinCheckCount(1);

		addContent(typeTable);

		randomRegionCheckboxes.clear();

		switch (type) {
			case SINGLE -> {
				Table singleTable = new Table();
				singleTable.setBackground(getSkin().getDrawable("button_flat_default_disabled"));
				singleTable.defaults().growX().uniformX().minHeight(26f).space(4f);
				Label label = new Label("Region", getSkin());
				singleTable.add(label);
				SelectBox<String> atlasRegionList = new SelectBox<>(getSkin());
				atlasRegionList.setItems(regionNames);
				atlasRegionList.setSelected(influencer.regions.get(0).imageName);
				atlasRegionList.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						replaceWithSingle(atlasRegionList.getSelected());
					}
				});
				singleTable.add(atlasRegionList);
				addContent(singleTable);
			}
			case RANDOM -> {
				Label pickRegionsLabel = new Label("Pick Regions", getSkin());
				pickRegionsLabel.setAlignment(Align.center);
				addContent(pickRegionsLabel);
				Table regionsTable = new Table();
				regionsTable.setBackground(getSkin().getDrawable("button_flat_default_disabled"));
				ButtonGroup<CheckBox> checkBoxButtonGroup = new ButtonGroup<>();
				checkBoxButtonGroup.setMinCheckCount(1);
				checkBoxButtonGroup.setMaxCheckCount(-1);
				regionsTable.defaults().growX().uniformX().minHeight(26f).space(4f);
				for (int i = 0; i < regionNames.length; i++) {
					if (i > 0 && i % 4 == 0) regionsTable.row();
					CheckBox regionCheckbox = new CheckBox(regionNames[i], getSkin());
					regionCheckbox.setProgrammaticChangeEvents(false);
					for (int j = 0; j < influencer.regions.size; j++) {
						if (Objects.equals(influencer.regions.get(j).imageName, regionNames[i])) {
							regionCheckbox.setChecked(true);
							break;
						}
					}
					regionCheckbox.addListener(new ChangeListener() {

						@Override
						public void changed(ChangeEvent event, Actor actor) {
							onRandomRegionsListChanged();
						}
					});
					randomRegionCheckboxes.add(regionCheckbox);
					checkBoxButtonGroup.add(regionCheckbox);
					regionsTable.add(regionCheckbox);
				}
				addContent(regionsTable);
				Table controlsTable = new Table();
				controlsTable.defaults().growX().uniformX().minHeight(26f).space(4f);
				TextButton selectAllButton = new TextButton("Select All", getSkin());
				selectAllButton.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						for (CheckBox checkBox : randomRegionCheckboxes) {
							checkBox.setChecked(true);
						}
						onRandomRegionsListChanged();
					}
				});
				controlsTable.add(selectAllButton);
				TextButton clearSelectionButton = new TextButton("Clear Selection", getSkin());
				clearSelectionButton.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						for (CheckBox checkBox : randomRegionCheckboxes) {
							if (checkBox.equals(randomRegionCheckboxes.first())) checkBox.setChecked(true);
							else checkBox.setChecked(false);
						}
						onRandomRegionsListChanged();
					}
				});
				controlsTable.add(clearSelectionButton);
				addContent(controlsTable);
			}
			case ANIMATED -> {
				Table hintsTable = new Table();
				hintsTable.setBackground(getSkin().getDrawable("button_flat_default_disabled"));
				hintsTable.defaults().growX().uniformX();
				Label hintAtlasLabel = new Label("Atlas regions", getSkin().get("default", Label.LabelStyle.class));
				hintAtlasLabel.setAlignment(Align.center);
				hintsTable.add(hintAtlasLabel);
				Label hintSelectedLabel = new Label("Selected regions", getSkin().get("default", Label.LabelStyle.class));
				hintSelectedLabel.setAlignment(Align.center);
				hintsTable.add(hintSelectedLabel);
				addContent(hintsTable);
				ListPicker listPicker = new ListPicker(getSkin());
				addContent(listPicker);
				listPicker.setItems(regionNames);
				for (RegionInfluencer.AspectTextureRegion region : influencer.regions) {
					listPicker.makeItemSelected(region.imageName);
				}
				Table controlsTable = new Table();
				controlsTable.defaults().growX().uniformX().minHeight(26f).space(4f);
				TextButton selectAllButton = new TextButton("Select All", getSkin());
				selectAllButton.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						listPicker.selectAll();
					}
				});
				controlsTable.add(selectAllButton);
				TextButton clearSelectionButton = new TextButton("Clear Selection", getSkin());
				clearSelectionButton.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						listPicker.clearSelection();
					}
				});
				controlsTable.add(clearSelectionButton);
				addContent(controlsTable);
				listPicker.addListener(() -> onAnimatedInfluencerRegionsListChanged(listPicker.getSelectedItems()));
			}
		}
	}

}
