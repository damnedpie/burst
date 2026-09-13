package com.onecat.burst;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.emitters.Emitter;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.onecat.burst.ui.EditorUI;
import com.onecat.burst.ui.displays.EditableGraph;
import com.onecat.burst.utils.*;
import com.onecat.burst.world.*;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.utils.MaterialConverter;
import org.lwjgl.util.nfd.NativeFileDialog;
import java.util.HashSet;
import java.util.Set;

public class Main implements ApplicationListener {

	// Properties
	private final Color bgColor = new Color();
	float deltaMultiplier = 1.0f;
	// UI
	private EditorUI editorUI;
	private Stage stage;
	// Rendering
	private SceneManager sceneManager;
	private WorldCamera sceneCamera;
	private WorldGrid grid;
	private WorldGizmo gizmo;
	private ParticleManager particleManager;

	private final HashSet<String> unfoldedInfluencers = new HashSet<>();

	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		if (NativeFileDialog.NFD_Init() != NativeFileDialog.NFD_OKAY) {
			Log.e("NFD initialization failed");
		}
		stage = new Stage(new ScreenViewport(new OrthographicCamera()));
		((OrthographicCamera) stage.getCamera()).setToOrtho(false);
		editorUI = new EditorUI(new Skin(Gdx.files.internal("skin.json")), stage);
		editorUI.addListener(createUICallbacks());
		sceneManager = new SceneManager(new DefaultShaderProvider(), new DepthShaderProvider());
		sceneCamera = new WorldCamera();
		sceneCamera.fieldOfView = Settings.getInteger(Settings.SET_FOV);
		sceneCamera.setTarget(0, 0, 0);
		sceneCamera.setDistance(15f);
		sceneManager.setCamera(sceneCamera);
		grid = new WorldGrid(10, 1);
		grid.visible = Settings.getBoolean(Settings.SET_GRID_ENABLED);
		gizmo = new WorldGizmo(new GLBLoader().load(Gdx.files.internal("gizmo.glb")).scene);
		gizmo.visible = Settings.getBoolean(Settings.SET_GIZMO_ENABLED);
		MaterialConverter.makeCompatible(gizmo);
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(sceneCamera.getInputProcessor());
		Gdx.input.setInputProcessor(multiplexer);
		particleManager = new ParticleManager(sceneCamera);
		particleManager.addListener(new ParticleManager.EventListener() {

			@Override
			public void onTextureChanged(String name, TextureRegion region) {
				Main.this.onTextureChanged(name, region);
			}
		});
		particleManager.createCleanSession();
		editorUI.updateControllers(particleManager.getEffect().getControllers());
		bgColor.set(Color.valueOf(Settings.getString(Settings.SET_BG_COLOR)));
		EditableGraph.shapeRenderer = new ShapeRenderer();
		Gdx.graphics.setTitle("Burst | unsaved project");
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
		particleManager.update(delta * deltaMultiplier);
		Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		if (grid.visible) grid.render(sceneCamera, sceneManager.getBatch());
		sceneManager.getRenderableProviders().clear();
		if (gizmo.visible) sceneManager.addScene(gizmo);
		sceneManager.render();
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
		if (EditableGraph.shapeRenderer != null) {
			EditableGraph.shapeRenderer.dispose();
			EditableGraph.shapeRenderer = null;
		}
	}

	public static ParticleManager getParticleManager() {
		return ((Main) Gdx.app.getApplicationListener()).particleManager;
	}

	public static void markAsUnfolded(String componentName) {
		((Main) Gdx.app.getApplicationListener()).unfoldedInfluencers.add(componentName);
	}

	public static void markAsFolded(String componentName) {
		((Main) Gdx.app.getApplicationListener()).unfoldedInfluencers.remove(componentName);
	}

	public static Set<String> getUnfolded() {
		return ((Main) Gdx.app.getApplicationListener()).unfoldedInfluencers;
	}

	private void onTextureChanged(String name, TextureRegion region) {
		editorUI.setAtlasPreviewImage(region);
		editorUI.updateAtlas(name, particleManager.getAtlas());
	}

	private void onNewFilePressed() {
		particleManager.createCleanSession();
		editorUI.updateControllers(particleManager.getEffect().getControllers());
		editorUI.setEditedController(null);
		Gdx.graphics.setTitle("Burst | unsaved project");
	}

	private void onOpenFilePressed() {
		String filePath = FilePicker.pick(FilePicker.Mode.OPEN, "Particle effect (.pfx)", "pfx");
		if (filePath == null) return;
		Gdx.graphics.setTitle("Burst | " + filePath);
		particleManager.loadPfx(Gdx.files.absolute(filePath));
		editorUI.updateControllers(particleManager.getEffect().getControllers());
		editorUI.setEditedController(null);
	}

	private void onSaveFilePressed() {
		String filePath = FilePicker.pick(FilePicker.Mode.SAVE, "Particle effect (.pfx)", "pfx");
		if (filePath == null) return;
		particleManager.savePfx(Gdx.files.absolute(filePath));
	}

	private void onGridToggled(boolean enabled) {
		Settings.putBoolean(Settings.SET_GRID_ENABLED, enabled);
		grid.visible = enabled;
	}

	private void onGizmoToggled(boolean enabled) {
		Settings.putBoolean(Settings.SET_GIZMO_ENABLED, enabled);
		gizmo.visible = enabled;
	}

	private void onPrettyPrintToggled(boolean enabled) {
		Settings.putBoolean(Settings.SET_PRETTY_PRINT_ENABLED, enabled);
	}

	private void onOutputStyleChanged(String outputStyle) {
		Settings.putString(Settings.SET_OUTPUT_MODE, outputStyle);
	}

	private void onControllerAddNew(String typeName) {
		particleManager.createNewController(typeName);
		editorUI.updateControllers(particleManager.getEffect().getControllers());
	}

	private void onControllerEdit(String name) {
		editorUI.setEditedController(particleManager.getEffect().findController(name));
	}

	private void onControllerClone(String name) {
		ParticleController copy = particleManager.getEffect().findController(name).copy();
		copy.name = particleManager.getUnusedName(name);
		particleManager.getEffect().getControllers().add(copy);
		copy.init();
		editorUI.updateControllers(particleManager.getEffect().getControllers());
	}

	private void onControllerDelete(String name) {
		ParticleController controller = particleManager.getEffect().findController(name);
		particleManager.getEffect().getControllers().removeValue(controller, true);
		editorUI.updateControllers(particleManager.getEffect().getControllers());
	}

	private void onControllerVisibilityToggled(String name, boolean enabled) {
		Emitter emitter = particleManager.getEffect().findController(name).emitter;
		if (emitter instanceof RegularEmitter reg) {
			if (enabled)
				reg.setEmissionMode(RegularEmitter.EmissionMode.Enabled);
			else
				reg.setEmissionMode(RegularEmitter.EmissionMode.Disabled);
		}
	}

	private void onControllerRename(String oldName, String newName) {
		if (particleManager.getEffect().findController(newName) != null) {
			editorUI.updateControllers(particleManager.getEffect().getControllers());
			return;
		}
		particleManager.getEffect().findController(oldName).name = newName;
		editorUI.updateControllers(particleManager.getEffect().getControllers());
		Log.l("Renamed \"%s\" to \"%s\"", oldName, newName);
	}

	private void onLoadAtlasPressed() {
		String filePath = FilePicker.pick(FilePicker.Mode.OPEN, "Texture atlas (.atlas)", "atlas");
		if (filePath == null) return;
		particleManager.replaceAtlas(filePath);
	}

	private void onFovChanged(int value) {
		Settings.putInteger(Settings.SET_FOV, value);
		sceneCamera.fieldOfView = value;
	}

	private void onDeltaMultiplierChanged(float value) {
		deltaMultiplier = value;
	}

	private void onBackgroundColorPicked(String hex) {
		bgColor.set(Color.valueOf(hex));
		Settings.putString(Settings.SET_BG_COLOR, hex);
	}

	private void onBackgroundColorUpdated(String hex) {
		bgColor.set(Color.valueOf(hex));
	}

	private void onUiOpacityChanged(float value) {
		Settings.putFloat(Settings.SET_UI_OPACITY, value);
		editorUI.setUiOpacity(value);
	}

	private EditorUI.EventListener createUICallbacks() {
		return new EditorUI.EventListener() {

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

			@Override
			public void onGizmoToggled(boolean enabled) {
				Main.this.onGizmoToggled(enabled);
			}

			@Override
			public void onPrettyPrintToggled(boolean enabled) {
				Main.this.onPrettyPrintToggled(enabled);
			}

			@Override
			public void onOutputStyleChanged(String outputStyle) {
				Main.this.onOutputStyleChanged(outputStyle);
			}

			@Override
			public void onControllerAddNew(String typeName) {
				Main.this.onControllerAddNew(typeName);
			}

			@Override
			public void onControllerEdit(String name) {
				Main.this.onControllerEdit(name);
			}

			@Override
			public void onControllerClone(String name) {
				Main.this.onControllerClone(name);
			}

			@Override
			public void onControllerDelete(String name) {
				Main.this.onControllerDelete(name);
			}

			@Override
			public void onControllerVisibilityToggled(String name, boolean enabled) {
				Main.this.onControllerVisibilityToggled(name, enabled);
			}

			@Override
			public void onControllerRename(String oldName, String newName) {
				Main.this.onControllerRename(oldName, newName);
			}

			@Override
			public void onLoadAtlasPressed() {
				Main.this.onLoadAtlasPressed();
			}

			@Override
			public void onFovChanged(int value) {
				Main.this.onFovChanged(value);
			}

			@Override
			public void onDeltaMultiplierChanged(float value) {
				Main.this.onDeltaMultiplierChanged(value);
			}

			@Override
			public void onBackgroundColorPicked(String hex) {
				Main.this.onBackgroundColorPicked(hex);
			}

			@Override
			public void onBackgroundColorUpdated(String hex) {
				Main.this.onBackgroundColorUpdated(hex);
			}

			@Override
			public void onUiOpacityChanged(float value) {
				Main.this.onUiOpacityChanged(value);
			}
		};
	}

}