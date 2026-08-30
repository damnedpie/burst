package com.onecat.burst.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import java.util.ArrayList;
import java.util.List;

public class ControllersPanel extends Table {

	private float opacity = 0.9f;
	private final ScrollPane controllersScroll;
	private final Table controllersTable;

	public interface EventListener {

	}

	private final List<EventListener> listeners = new ArrayList<>();

	public ControllersPanel(Skin skin) {
		super(skin);
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
		add(controllersScroll);
		controllersTable.defaults().spaceBottom(2f);
		addNewContoller("Billboard_1");
		addNewContoller("PointSprite_1");
		addNewContoller("ModelInstance_1");
		addNewContoller("ParticleController_1");
		row();
	}

	// TODO unified table with header row that gets recreated whenever a new controller is added or removed?
	public void addNewContoller(String name) {
		Table table = new Table();
		table.defaults().fillY().uniformY().space(4f).minWidth(64f);
		TextField nameField = new TextField(name, getSkin());
		table.add(nameField).maxWidth(144f);
		Label typeLabel = new Label("Billboard", getSkin());
		typeLabel.setAlignment(Align.center);
		table.add(typeLabel);
		CheckBox visibleBox = new CheckBox("Visible", getSkin());
		table.add(visibleBox);
		TextButton editButton = new TextButton("Edit", getSkin().get("default_flat", TextButton.TextButtonStyle.class));
		table.add(editButton);
		TextButton cloneButton = new TextButton("Clone", getSkin().get("default_flat", TextButton.TextButtonStyle.class));
		table.add(cloneButton);
		TextButton deleteButton = new TextButton("Delete", getSkin().get("default_flat", TextButton.TextButtonStyle.class));
		table.add(deleteButton);
		controllersTable.add(table).row();
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public void addListener(EventListener listener) {
		listeners.add(listener);
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
