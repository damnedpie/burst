package com.onecat.burst.ui.displays;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.onecat.burst.Main;

public abstract class ControllerComponentDisplay extends Table {

	protected final ParticleController controller;
	protected final String name;
	protected final boolean optional;
	protected boolean isActive;

	private Table contentTable;
	protected ImageButton foldButton;
	private float opacity;

	public ControllerComponentDisplay(ParticleController controller, String name, Skin skin, boolean optional, boolean isActive) {
		super(skin);
		this.controller = controller;
		this.name = name;
		this.optional = optional;
		this.isActive = isActive;
		setBackground(skin.getDrawable("panel_thin_default"));
		defaults().growX().space(8f);
		initialize();
		createHeader();
		checkIfUnfoldedBefore();
	}

	public ControllerComponentDisplay(ParticleController controller, String name, Skin skin) {
		this(controller, name, skin, false, false);
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	private void createHeader() {
		Table headerTable = new Table();
		headerTable.defaults().space(4f);

		foldButton = new ImageButton(getSkin().get("fold", ImageButton.ImageButtonStyle.class));
		foldButton.setProgrammaticChangeEvents(false);
		foldButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (foldButton.isChecked()) {
					Main.markAsUnfolded(name);
					onUnfolded();
				}
				else {
					Main.markAsFolded(name);
					onFolded();
				}
			}
		});
		headerTable.add(foldButton);

		Label header = new Label(name, getSkin().get("default", Label.LabelStyle.class));
		//header.setAlignment(Align.center);
		header.setTouchable(Touchable.enabled);
		header.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (foldButton.isDisabled()) return;
				if (!foldButton.isChecked()) {
					Main.markAsUnfolded(name);
					onUnfolded();
					foldButton.setChecked(true);
				}
				else {
					Main.markAsFolded(name);
					onFolded();
				}
			}
		});
		headerTable.add(header).spaceLeft(16f).grow();

		if (optional) {
			TextButton optionalButton = new TextButton("", getSkin());
			optionalButton.setText(isActive ? "Remove" : "Add");
			foldButton.setDisabled(!isActive);
			optionalButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					isActive = !isActive;
					optionalButton.setText(isActive ? "Remove" : "Add");
					foldButton.setDisabled(!isActive);
					// Apply changes first
					if (isActive) onAdded();
					else onRemoved();
					// Fold / unfold later
					if (isActive) {
						Main.markAsUnfolded(name);
						onUnfolded();
					}
					else {
						onFolded();
					}
					foldButton.setChecked(isActive);
				}
			});
			headerTable.add(optionalButton).width(96f);
		}

		add(headerTable);
	}

	protected void checkIfUnfoldedBefore() {
		if (Main.getUnfolded().contains(name)) {
			if (!optional || isActive) {
				onUnfolded();
				foldButton.setChecked(true);
			}
		}
	}

	protected void addContent(Actor actor) {
		if (contentTable == null) {
			row();
			contentTable = new Table();
			contentTable.defaults().growX().minHeight(24f).space(4f);
			add(contentTable);
		}
		contentTable.add(actor).row();
	}

	protected void onFolded() {
		clearChildren(true);
		contentTable = null;
		createHeader();
	}

	protected void onAdded() { }

	protected void onRemoved() { }

	protected abstract void initialize();
	protected abstract void onUnfolded();

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
		super.drawBackground(batch, opacity, x, y);
	}

}
