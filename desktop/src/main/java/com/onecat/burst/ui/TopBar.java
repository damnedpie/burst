package com.onecat.burst.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.List;

public class TopBar extends Table implements Disposable {

	private float opacity = 0.9f;
	private final Texture iconTexture;

	public interface EventListener {

		void onNewFilePressed();
		void onOpenFilePressed();
		void onSaveFilePressed();

	}

	private final List<EventListener> listeners = new ArrayList<>();

	public TopBar(Skin skin) {
		super(skin);
		setName("TopBar");
		setTouchable(Touchable.enabled);
		addCaptureListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		defaults().space(4f).minWidth(56f);
		left();
		setBackground("top_bar_default");
		setHeight(38f);
		iconTexture = new Texture(Gdx.files.internal("icon24.png"));
		Image burstIcon = new Image(iconTexture);
		add(burstIcon).size(24f);
		TextButton newButton = new TextButton("New", skin);
		newButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onNewFilePressed();
			}
		});
		newButton.setStyle(skin.get("default_flat", TextButton.TextButtonStyle.class));
		add(newButton);
		TextButton openButton = new TextButton("Open", skin);
		openButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onOpenFilePressed();
			}
		});
		openButton.setStyle(skin.get("default_flat", TextButton.TextButtonStyle.class));
		add(openButton);
		TextButton saveButton = new TextButton("Save", skin);
		saveButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				for (EventListener listener : getEventListeners()) listener.onSaveFilePressed();
			}
		});
		saveButton.setStyle(skin.get("default_flat", TextButton.TextButtonStyle.class));
		add(saveButton);
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
		setY(getStage().getHeight() - getHeight());
		setWidth(getStage().getWidth());
		super.layout();
	}

	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
		super.drawBackground(batch, opacity, x, y);
	}

	@Override
	public void dispose() {
		if (iconTexture != null) iconTexture.dispose();
	}

}
