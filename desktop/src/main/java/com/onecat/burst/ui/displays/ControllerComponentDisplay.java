package com.onecat.burst.ui.displays;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.onecat.burst.Main;

public abstract class ControllerComponentDisplay extends Table {

	private Table contentTable;
	protected final String name;
	protected ImageButton foldButton;
	private float opacity;

	public ControllerComponentDisplay(String name, Skin skin) {
		super(skin);
		this.name = name;
		setBackground(skin.getDrawable("panel_thin_default"));
		defaults().growX().space(8f);
		createHeader();
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
		header.setAlignment(Align.center);
		header.setTouchable(Touchable.enabled);
		header.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
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
		headerTable.add(header).grow();
		add(headerTable);
	}

	protected void checkIfUnfoldedBefore() {
		if (Main.getUnfolded().contains(name)) {
			onUnfolded();
			foldButton.setChecked(true);
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

	abstract void onUnfolded();

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
		super.drawBackground(batch, opacity, x, y);
	}

}
