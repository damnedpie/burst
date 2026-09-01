package com.onecat.burst.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.renderers.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.*;
import com.onecat.burst.world.ParticleManager;
import java.util.ArrayList;
import java.util.List;

public class ControllersPanel extends Table {

	private float opacity = 0.9f;
	private final ScrollPane controllersScroll;
	private final Table controllersTable;
	private final Image atlasPreviewImage;

	String renameOldName = "";

	public interface EventListener {

		void onAddNew(String typeName);
		void onEdit(String name);
		void onClone(String name);
		void onDelete(String name);
		void onVisibilityToggled(String name, boolean enabled);
		void onRename(String oldName, String newName);
		void onLoadAtlasPressed();

	}

	private final List<EventListener> listeners = new ArrayList<>();

	public ControllersPanel(Skin skin) {
		super(skin);
		TextButton.TextButtonStyle flatStyle = getSkin().get("default_flat", TextButton.TextButtonStyle.class);
		setName("ControllersPanel");
		setTouchable(Touchable.enabled);
		addCaptureListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		defaults().space(4f).growX();
		left();
		setBackground("panel_default_default");
		top();
		Label header = new Label("Controllers", skin.get("header", Label.LabelStyle.class));
		header.setAlignment(Align.center);
		add(header).growX().row();
		controllersScroll = new ScrollPane(controllersTable = new Table());
		controllersScroll.addListener(new InputListener() {

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				getStage().setScrollFocus(controllersScroll);
				super.enter(event, x, y, pointer, fromActor);
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				getStage().setScrollFocus(null);
				super.exit(event, x, y, pointer, toActor);
			}
		});
		add(controllersScroll).maxHeight(466f).row();
		controllersTable.defaults().spaceBottom(2f).growX();

		Label addNewLabel = new Label("Add new...", skin.get("header", Label.LabelStyle.class));
		addNewLabel.setAlignment(Align.center);
		add(addNewLabel).growX().row();
		Table addNewTable = new Table();
		addNewTable.defaults().growX().space(4f);
		TextButton addBillboardButton = new TextButton("Billboard", flatStyle);
		addBillboardButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onAddNew(ParticleManager.BILLBOARD_CONTROLLER);
			}
		});
		addNewTable.add(addBillboardButton);
		TextButton addPointSpriteButton = new TextButton("Point Sprite", flatStyle);
		addPointSpriteButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onAddNew(ParticleManager.POINTSPRITE_CONTROLLER);
			}
		});
		addNewTable.add(addPointSpriteButton).row();
		TextButton addModelInstanceButton = new TextButton("Model Instance", flatStyle);
		addModelInstanceButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onAddNew(ParticleManager.MODELINSTANCE_CONTROLLER);
			}
		});
		addNewTable.add(addModelInstanceButton);
		TextButton addParticleControllerButton = new TextButton("Particle Controller", flatStyle);
		addParticleControllerButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onAddNew(ParticleManager.PARTICLECONTROLLER_CONTROLLER);
			}
		});
		addNewTable.add(addParticleControllerButton).row();
		add(addNewTable).row();

		Label textureSectionLabel = new Label("Atlas", skin.get("header", Label.LabelStyle.class));
		textureSectionLabel.setAlignment(Align.center);
		add(textureSectionLabel).growX().row();
		Table textureSectionTable = new Table();
		textureSectionTable.defaults().growX().uniformX().space(8f);
		TextButton loadAtlasButton = new TextButton("Load atlas...", flatStyle);
		loadAtlasButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onLoadAtlasPressed();
			}
		});
		textureSectionTable.add(loadAtlasButton).minHeight(32f);
		atlasPreviewImage = new Image();
		atlasPreviewImage.setScaling(Scaling.fit);
		textureSectionTable.add(atlasPreviewImage).maxHeight(128f);
		add(textureSectionTable);
	}

	public void addNewContollerRow(String name, String typeName, boolean isVisible) {
		Table table = new Table();
		table.defaults().fillY().uniformY().space(4f).minWidth(56f).growX();
		TextField nameField = new TextField(name, getSkin());
		nameField.setTextFieldFilter((textField, c) -> Character.isLetterOrDigit(c) || c == '-' || c == '_');
		nameField.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
					getStage().setKeyboardFocus(null);
					return true;
				}
				return false;
			}
		});
		nameField.addListener(new FocusListener() {

			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					renameOldName = nameField.getText();
				}
				if (!focused) {
					if (renameOldName.equals(nameField.getText())) return;
					for (EventListener listener : getEventListeners()) listener.onRename(renameOldName, nameField.getText());
				}
			}
		});
		table.add(nameField).maxWidth(148f);
		Label typeLabel = new Label(typeName, getSkin());
		typeLabel.setAlignment(Align.center);
		table.add(typeLabel);
		CheckBox visibleBox = new CheckBox("Visible", getSkin());
		visibleBox.setChecked(isVisible);
		visibleBox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (nameField.hasKeyboardFocus()) getStage().setKeyboardFocus(null);
				for (EventListener listener : getEventListeners())
					listener.onVisibilityToggled(nameField.getText(), visibleBox.isChecked());
			}
		});
		table.add(visibleBox);
		TextButton editButton = new TextButton("Edit", getSkin().get("default_flat", TextButton.TextButtonStyle.class));
		editButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (nameField.hasKeyboardFocus()) getStage().setKeyboardFocus(null);
				for (EventListener listener : getEventListeners()) {
					listener.onEdit(nameField.getText());
				}
			}
		});
		table.add(editButton);
		TextButton cloneButton = new TextButton("Clone", getSkin().get("default_flat", TextButton.TextButtonStyle.class));
		cloneButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (nameField.hasKeyboardFocus()) getStage().setKeyboardFocus(null);
				for (EventListener listener : getEventListeners()) {
					listener.onClone(nameField.getText());
				}
			}
		});
		table.add(cloneButton);
		TextButton deleteButton = new TextButton("Delete", getSkin().get("default_flat", TextButton.TextButtonStyle.class));
		deleteButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (nameField.hasKeyboardFocus()) getStage().setKeyboardFocus(null);
				for (EventListener listener : getEventListeners()) {
					listener.onDelete(nameField.getText());
				}
			}
		});
		table.add(deleteButton);
		controllersTable.add(table).row();
	}

	public void setAtlasPreviewImage(TextureRegion region) {
		if (region.getTexture() == null) {
			atlasPreviewImage.setDrawable(null);
			return;
		}
		atlasPreviewImage.setDrawable(new TextureRegionDrawable(region));
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public void addListener(EventListener listener) {
		listeners.add(listener);
	}

	public void updateControllers(Array<ParticleController> controllers) {
		controllersTable.clearChildren();
		for (ParticleController controller : controllers) {
			String typeName = "error";
			if (controller.renderer instanceof BillboardRenderer) typeName = "Billboard";
			if (controller.renderer instanceof PointSpriteRenderer) typeName = "PointSprite";
			if (controller.renderer instanceof ModelInstanceRenderer) typeName = "ModelInstance";
			if (controller.renderer instanceof ParticleControllerControllerRenderer) typeName = "ParticleController";
			boolean isVisible = true;
			if (controller.emitter instanceof RegularEmitter reg)
				isVisible = reg.getEmissionMode() != RegularEmitter.EmissionMode.Disabled;
			addNewContollerRow(controller.name, typeName, isVisible);
		}
	}

	public void removeListener(EventListener listener) {
		listeners.remove(listener);
	}

	private List<EventListener> getEventListeners() {
		return new ArrayList<>(listeners);
	}

	@Override
	public void layout() {
		setHeight(getPrefHeight());
		setWidth(getPrefWidth());
		setX(8f);
		setY(8f);
		super.layout();
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
		super.drawBackground(batch, opacity, x, y);
	}

}
