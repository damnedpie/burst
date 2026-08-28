package com.onecat.burst;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		if (NativeFileDialog.NFD_Init() != NativeFileDialog.NFD_OKAY) {
			Log.e("NFD initialization failed");
		}
		stage = new Stage(new ScreenViewport(new OrthographicCamera()));
		((OrthographicCamera) stage.getCamera()).setToOrtho(false);
		Gdx.input.setInputProcessor(stage);
		editorUI = new EditorUI(new Skin(Gdx.files.internal("skin/skin.json")), stage);
		sceneManager = new SceneManager(new DefaultShaderProvider(), new DepthShaderProvider());
		sceneCamera = new WorldCamera();
		sceneCamera.position.set(7f, 9f, 7f);
		sceneCamera.lookAt(0f, 0f, 0f);
		sceneManager.setCamera(sceneCamera);
		grid = new WorldGrid(10, 1);
		gizmo = new WorldGizmo(new GLBLoader().load(Gdx.files.internal("meshes/gizmo.glb")).scene);
		MaterialConverter.makeCompatible(gizmo);
		sceneManager.addScene(gizmo);
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
		sceneManager.update(delta);
		Gdx.gl.glClearColor(0.22f, 0.22f, 0.3f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		grid.render(sceneCamera, sceneManager.getBatch());
		sceneManager.render();
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
	}

}