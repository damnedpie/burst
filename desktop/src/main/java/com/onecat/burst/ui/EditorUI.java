package com.onecat.burst.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import java.util.ArrayList;
import java.util.List;

public class EditorUI implements Disposable {

	private final TopBar topBar;
	private final EditorPanel editorPanel;
	private final ProjectPanel projectPanel;
	private ControllerPanel controllerPanel;

	private float uiOpacity = 0.7f;

	public interface EventListener {

		void onNewFilePressed();
		void onOpenFilePressed();
		void onSaveFilePressed();

		void onFovChanged(int value);
		void onDeltaMultiplierChanged(float value);
		void onBackgroundColorPicked(String hex);
		void onBackgroundColorUpdated(String hex);
		void onGridToggled(boolean enabled);
		void onGizmoToggled(boolean enabled);
		void onPrettyPrintToggled(boolean enabled);
		void onOutputStyleChanged(String outputStyle);

		void onControllerAddNew(String typeName);
		void onControllerEdit(String name);
		void onControllerClone(String name);
		void onControllerDelete(String name);
		void onControllerVisibilityToggled(String name, boolean enabled);
		void onControllerRename(String oldName, String newName);
		void onLoadAtlasPressed();

	}

	private final List<EventListener> listeners = new ArrayList<>();

	public EditorUI(Skin skin, Stage stage) {
		stage.addActor(topBar = new TopBar(skin));
		stage.addActor(editorPanel = new EditorPanel(skin));
		stage.addActor(projectPanel = new ProjectPanel(skin));
		createUICallbacks();
		setUiOpacity(uiOpacity);
	}

	public void setUiOpacity(float opacity) {
		uiOpacity = opacity;
		topBar.setOpacity(uiOpacity);
		editorPanel.setOpacity(uiOpacity);
		projectPanel.setOpacity(uiOpacity);
		if (controllerPanel != null) controllerPanel.setOpacity(uiOpacity);
	}

	public void addListener(EventListener listener) {
		listeners.add(listener);
	}

	public void removeListener(EventListener listener) {
		listeners.remove(listener);
	}

	public void updateControllers(Array<ParticleController> controllers) {
		projectPanel.updateControllers(controllers);
	}

	public void setAtlasPreviewImage(TextureRegion region) {
		projectPanel.setAtlasPreviewImage(region);
	}

	public void setEditedController(ParticleController controller) {
		if (controllerPanel != null) {
			controllerPanel.remove();
		}
		if (controller != null) {
			topBar.getStage().addActor(controllerPanel = new ControllerPanel(controller, topBar.getSkin()));
			controllerPanel.setOpacity(uiOpacity);
		}
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
		editorPanel.addListener(new EditorPanel.EventListener() {

			@Override
			public void onFovChanged(int value) {
				for (EventListener listener : getListeners()) listener.onFovChanged(value);
			}

			@Override
			public void onDeltaMultiplierChanged(float value) {
				for (EventListener listener : getListeners()) listener.onDeltaMultiplierChanged(value);
			}

			@Override
			public void onBackgroundColorPicked(String hex) {
				for (EventListener listener : getListeners()) listener.onBackgroundColorPicked(hex);
			}

			@Override
			public void onBackgroundColorUpdated(String hex) {
				for (EventListener listener : getListeners()) listener.onBackgroundColorUpdated(hex);
			}

			@Override
			public void onGridToggled(boolean enabled) {
				for (EventListener listener : getListeners()) listener.onGridToggled(enabled);
			}

			@Override
			public void onGizmoToggled(boolean enabled) {
				for (EventListener listener : getListeners()) listener.onGizmoToggled(enabled);
			}

			@Override
			public void onPrettyPrintToggled(boolean enabled) {
				for (EventListener listener : getListeners()) listener.onPrettyPrintToggled(enabled);
			}

			@Override
			public void onOutputStyleChanged(String outputStyle) {
				for (EventListener listener : getListeners()) listener.onOutputStyleChanged(outputStyle);
			}
		});
		projectPanel.addListener(new ProjectPanel.EventListener() {

			@Override
			public void onAddNew(String typeName) {
				for (EventListener listener : getListeners()) listener.onControllerAddNew(typeName);
			}

			@Override
			public void onEdit(String name) {
				for (EventListener listener : getListeners()) listener.onControllerEdit(name);
			}

			@Override
			public void onClone(String name) {
				for (EventListener listener : getListeners()) listener.onControllerClone(name);
			}

			@Override
			public void onDelete(String name) {
				for (EventListener listener : getListeners()) listener.onControllerDelete(name);
			}

			@Override
			public void onVisibilityToggled(String name, boolean enabled) {
				for (EventListener listener : getListeners()) listener.onControllerVisibilityToggled(name, enabled);
			}

			@Override
			public void onRename(String oldName, String newName) {
				for (EventListener listener : getListeners()) listener.onControllerRename(oldName, newName);
			}

			@Override
			public void onLoadAtlasPressed() {
				for (EventListener listener : getListeners()) listener.onLoadAtlasPressed();
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
