package com.onecat.burst;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.List;

public class EditorUI implements Disposable {

	private final Skin skin;
	private final Stage stage;

	private final Table topBar;
	private Texture iconTexture;

	private float uiOpacity = 0.9f;

	public interface EventListener {

		void onNewFilePressed();
		void onOpenFilePressed();
		void onSaveFilePressed();
		void onGridToggled(boolean enabled);

	}

	private final List<EventListener> listeners = new ArrayList<>();

	public EditorUI(Skin skin, Stage stage) {
		this.skin = skin;
		this.stage = stage;
		stage.addActor(topBar = createTopBar());
	}

	public void addListener(EventListener listener) {
		listeners.add(listener);
	}

	public void removeListener(EventListener listener) {
		listeners.remove(listener);
	}

	private List<EventListener> getListeners() {
		return new ArrayList<>(listeners);
	}

	private Table createTopBar() {
		Table table = new Table(skin) {

			@Override
			public void layout() {
				setY(stage.getHeight() - getHeight());
				setWidth(stage.getWidth());
				super.layout();
			}

			@Override
			protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
				super.drawBackground(batch, uiOpacity, x, y);
			}
		};
		table.defaults().space(4f).minWidth(56f);
		table.left();
		table.setBackground("top_bar_default");
		table.setHeight(38f);
		iconTexture = new Texture(Gdx.files.internal("icon24.png"));
		Image burstIcon = new Image(iconTexture);
		table.add(burstIcon).size(24f);
		TextButton newButton = new TextButton("New", skin);
		newButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getListeners()) listener.onNewFilePressed();
			}
		});
		newButton.setStyle(skin.get("default_flat", TextButton.TextButtonStyle.class));
		table.add(newButton);
		TextButton openButton = new TextButton("Open", skin);
		openButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getListeners()) listener.onOpenFilePressed();
			}
		});
		openButton.setStyle(skin.get("default_flat", TextButton.TextButtonStyle.class));
		table.add(openButton);
		TextButton saveButton = new TextButton("Save", skin);
		saveButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getListeners()) listener.onSaveFilePressed();
			}
		});
		saveButton.setStyle(skin.get("default_flat", TextButton.TextButtonStyle.class));
		table.add(saveButton);
		CheckBox gridCheckbox = new CheckBox("Grid", skin);
		gridCheckbox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getListeners()) listener.onGridToggled(gridCheckbox.isChecked());
			}
		});
		table.add(gridCheckbox);
		return table;
	}

	@Override
	public void dispose() {
		if (iconTexture != null) iconTexture.dispose();
	}

}
