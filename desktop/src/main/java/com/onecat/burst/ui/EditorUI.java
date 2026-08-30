package com.onecat.burst.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.List;

public class EditorUI implements Disposable {

	private final TopBar topBar;
	private final EditorPanel editorPanel;
	private final ControllersPanel controllersPanel;

	private float uiOpacity = 0.7f;

	public interface EventListener {

		void onNewFilePressed();
		void onOpenFilePressed();
		void onSaveFilePressed();
		void onGridToggled(boolean enabled);

	}

	private final List<EventListener> listeners = new ArrayList<>();

	public EditorUI(Skin skin, Stage stage) {
		stage.addActor(topBar = new TopBar(skin));
		stage.addActor(editorPanel = new EditorPanel(skin, stage));
		stage.addActor(controllersPanel = new ControllersPanel(skin));
		createUICallbacks();
		setUiOpacity(uiOpacity);
	}

	public void setUiOpacity(float opacity) {
		uiOpacity = opacity;
		topBar.setOpacity(uiOpacity);
		editorPanel.setOpacity(uiOpacity);
		controllersPanel.setOpacity(uiOpacity);
	}

	public void addListener(EventListener listener) {
		listeners.add(listener);
	}

	public void removeListener(EventListener listener) {
		listeners.remove(listener);
	}

	private void createUICallbacks() {
		topBar.addListener(new TopBar.EventListener() {

			@Override
			public void onNewFilePressed() {
				for (EventListener listener : getListeners()) listener.onNewFilePressed();
			}

			@Override
			public void onOpenFilePressed() {
				for (EventListener listener : getListeners()) listener.onOpenFilePressed();
			}

			@Override
			public void onSaveFilePressed() {
				for (EventListener listener : getListeners()) listener.onSaveFilePressed();
			}
		});
	}

	private List<EventListener> getListeners() {
		return new ArrayList<>(listeners);
	}

	@Override
	public void dispose() {
		topBar.dispose();
	}

}
