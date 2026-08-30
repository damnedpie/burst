package com.onecat.burst;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.onecat.burst.ui.EditorUI;
import com.onecat.burst.utils.FilePicker;
import com.onecat.burst.utils.Log;
import com.onecat.burst.world.*;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.utils.MaterialConverter;
import org.lwjgl.util.nfd.NativeFileDialog;

public class Main implements ApplicationListener {

	private EditorUI editorUI;
	private Stage stage;
	private SceneManager sceneManager;
	private WorldCamera sceneCamera;
	private WorldGrid grid;
	private WorldGizmo gizmo;
	private ParticleManager particleManager;

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		if (NativeFileDialog.NFD_Init() != NativeFileDialog.NFD_OKAY) {
			Log.e("NFD initialization failed");
		}
		stage = new Stage(new ScreenViewport(new OrthographicCamera()));
		((OrthographicCamera) stage.getCamera()).setToOrtho(false);
		editorUI = new EditorUI(new Skin(Gdx.files.internal("skin/skin.json")), stage);
		editorUI.addListener(new EditorUI.EventListener() {

			@Override
			public void onNewFilePressed() {
				Main.this.onNewFilePressed();
			}

			@Override
			public void onOpenFilePressed() {
				Main.this.onOpenFilePressed();
			}

			@Override
			public void onSaveFilePressed() {
				Main.this.onSaveFilePressed();
			}

			@Override
			public void onGridToggled(boolean enabled) {
				Main.this.onGridToggled(enabled);
			}
		});
		sceneManager = new SceneManager(new DefaultShaderProvider(), new DepthShaderProvider());
		sceneCamera = new WorldCamera();
		sceneCamera.setTarget(0, 0, 0);
		sceneCamera.setDistance(15f);
		sceneManager.setCamera(sceneCamera);
		grid = new WorldGrid(10, 1);
		gizmo = new WorldGizmo(new GLBLoader().load(Gdx.files.internal("meshes/gizmo.glb")).scene);
		MaterialConverter.makeCompatible(gizmo);
		sceneManager.addScene(gizmo);
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(sceneCamera.getInputProcessor());
		Gdx.input.setInputProcessor(multiplexer);
		particleManager = new ParticleManager(sceneCamera);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		stage.getCamera().update();
		float aspect = (float) width / height;
		float viewportHeight = 40f;
		float viewportWidth = viewportHeight * aspect;
		sceneManager.updateViewport(viewportWidth, viewportHeight);
		//noinspection GDXJavaUnsafeIterator
		for (Actor actor : stage.getActors()) {
			if (actor instanceof Widget) {
				((Widget) actor).invalidate();
			}
			else if (actor instanceof WidgetGroup) {
				((WidgetGroup) actor).invalidate();
				((WidgetGroup) actor).invalidateHierarchy();
			}
		}
	}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		sceneCamera.update();
		sceneManager.update(delta);
		particleManager.update(delta);
		new Color(0.22f, 0.22f, 0.3f, 1.0f);
		Gdx.gl.glClearColor(0.22f, 0.22f, 0.3f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		grid.render(sceneCamera, sceneManager.getBatch());
		sceneManager.render();
		particleManager.draw();
		particleManager.render(sceneCamera, sceneManager.getBatch());
		stage.getViewport().apply();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void pause() { }

	@Override
	public void resume() { }

	@Override
	public void dispose() {
		NativeFileDialog.NFD_Quit();
		grid.dispose();
		gizmo.modelInstance.model.dispose();
		sceneManager.dispose();
		editorUI.dispose();
		particleManager.dispose();
	}

	private void onNewFilePressed() {

	}

	private void onOpenFilePressed() {
		String filePath = FilePicker.pick(FilePicker.Mode.OPEN);
		if (filePath == null) return;
		Gdx.graphics.setTitle("Burst | " + filePath);
		particleManager.loadPfx(Gdx.files.absolute(filePath));
	}

	private void onSaveFilePressed() {

	}

	private void onGridToggled(boolean enabled) {

	}

}