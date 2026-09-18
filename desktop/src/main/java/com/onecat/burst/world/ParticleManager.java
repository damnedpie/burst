package com.onecat.burst.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.particles.*;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader.ParticleEffectLoadParameter;
import com.badlogic.gdx.graphics.g3d.particles.batches.*;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.graphics.g3d.particles.influencers.*;
import com.badlogic.gdx.graphics.g3d.particles.renderers.*;
import com.badlogic.gdx.graphics.g3d.particles.values.RectangleSpawnShapeValue;
import com.badlogic.gdx.utils.*;
import com.onecat.burst.utils.Log;
import com.onecat.burst.utils.Settings;
import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import java.io.IOException;
import java.util.*;

public class ParticleManager implements Disposable {

	public static final String BILLBOARD_CONTROLLER = "billboard";
	public static final String POINTSPRITE_CONTROLLER = "point_sprite";
	public static final String MODELINSTANCE_CONTROLLER = "model_instance";
	public static final String PARTICLECONTROLLER_CONTROLLER = "particle_controller";

	private final ParticleSystem particleSystem;
	private final BillboardParticleBatch billboardParticleBatch;
	private final PointSpriteParticleBatch pointSpriteParticleBatch;
	private final ModelInstanceParticleBatch modelInstanceParticleBatch;
	private final AssetManager am;
	private ParticleEffect effect;

	private TextureAtlas textureAtlas;
	private String atlasName;
	private String effectName;

	public interface EventListener {

		void onTextureChanged(String name, TextureRegion region);

	}

	private final List<EventListener> listeners = new ArrayList<>();

	public ParticleManager(Camera camera) {
		am = new AssetManager();
		GLBAssetLoader glbLoader = new GLBAssetLoader();
		am.setLoader(SceneAsset.class, glbLoader);
		am.load("burst_test_particles.atlas", TextureAtlas.class);
		am.load("burst_test_particles.png", Texture.class);
		am.load("burst_test_mesh.glb", SceneAsset.class);
		am.finishLoading();
		ParticleEffectLoader loader = new ParticleEffectLoader(new AbsoluteFileHandleResolver());
		am.setLoader(ParticleEffect.class, loader);

		particleSystem = new ParticleSystem();
		billboardParticleBatch = new BillboardParticleBatch();
		billboardParticleBatch.getBlendingAttribute().sourceFunction = GL20.GL_SRC_ALPHA;
		billboardParticleBatch.getBlendingAttribute().destFunction = GL20.GL_ONE_MINUS_SRC_ALPHA;
		billboardParticleBatch.setCamera(camera);
		billboardParticleBatch.setTexture(am.get("burst_test_particles.png", Texture.class));
		particleSystem.add(billboardParticleBatch);
		pointSpriteParticleBatch = new PointSpriteParticleBatch();
		pointSpriteParticleBatch.getBlendingAttribute().sourceFunction = GL20.GL_SRC_ALPHA;
		pointSpriteParticleBatch.getBlendingAttribute().destFunction = GL20.GL_ONE_MINUS_SRC_ALPHA;
		pointSpriteParticleBatch.setCamera(camera);
		pointSpriteParticleBatch.setTexture(am.get("burst_test_particles.png", Texture.class));
		particleSystem.add(pointSpriteParticleBatch);
		modelInstanceParticleBatch = new ModelInstanceParticleBatch();
		particleSystem.add(modelInstanceParticleBatch);
	}

	public void addListener(EventListener listener) {
		listeners.add(listener);
	}

	public void removeListener(EventListener listener) {
		listeners.remove(listener);
	}

	public void createCleanSession() {
		cleanupEverything();
		setAtlas("burst_test_particles.atlas");
		effect = new ParticleEffect();
		addDefaultBillboardController();
		effect.init();
		effect.start();
		particleSystem.add(effect);
	}

	public void loadPfx(FileHandle fileHandle) {
		cleanupEverything();
		Log.l("Loading %s", fileHandle.path());
		ParticleEffectLoadParameter param = new ParticleEffectLoadParameter(particleSystem.getBatches());
		setAtlas(null);
		effectName = fileHandle.path();
		am.load(fileHandle.path(), ParticleEffect.class, param);
		am.finishLoading();
		effect = new ParticleEffect(am.get(fileHandle.path(), ParticleEffect.class));
		// In order to prevent ResourceData losing the atlas upon re-saving the effect, we need to provide the atlas path into
		// RegionInfluencers of the VFX (if any)
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(fileHandle);
		JsonValue assetsSection = root.get("assets");
		if (assetsSection != null) {
			for (JsonValue asset : assetsSection) {
				if (asset.getString("type").equals("com.badlogic.gdx.graphics.g2d.TextureAtlas")) {
					setAtlas(asset.getString("filename").replaceAll("\\\\", "/"), false);
					break;
				}
			}
		}
		if (atlasName != null) {
			for (ParticleController controller : effect.getControllers()) {
				RegionInfluencer regionInfluencer = controller.findInfluencer(RegionInfluencer.class);
				if (regionInfluencer != null) {
					regionInfluencer.atlasName = atlasName;
				}
			}
		}
		effect.init();
		effect.start();
		particleSystem.add(effect);
	}

	public void savePfx(FileHandle fileHandle) {
		// If project uses default Burst atlas and gets saved, make a copy of the atlas in target destination and link the atlas
		if (atlasName.equals("burst_test_particles.atlas")) {
			FileHandle defaultAtlas = Gdx.files.internal("burst_test_particles.atlas");
			defaultAtlas.copyTo(fileHandle.parent());
			FileHandle defaultTexture = Gdx.files.internal("burst_test_particles.png");
			defaultTexture.copyTo(fileHandle.parent());
			setAtlas(fileHandle.parent().path() + "/burst_test_particles.atlas", false);
			for (ParticleController controller : effect.getControllers()) {
				RegionInfluencer regionInfluencer = controller.findInfluencer(RegionInfluencer.class);
				if (regionInfluencer == null) continue;
				regionInfluencer.atlasName = atlasName;
			}
		}
		boolean prettyPrint = Settings.getBoolean(Settings.SET_PRETTY_PRINT_ENABLED);
		JsonWriter.OutputType outputType = switch (Settings.getString(Settings.SET_OUTPUT_MODE)) {
			case "json" -> JsonWriter.OutputType.json;
			case "js" -> JsonWriter.OutputType.javascript;
			case "minimal" -> JsonWriter.OutputType.minimal;
			default -> throw new IllegalStateException("Unexpected value: " + Settings.getString(Settings.SET_OUTPUT_MODE));
		};
		ParticleEffectLoader.ParticleEffectSaveParameter param = new ParticleEffectLoader.ParticleEffectSaveParameter(
				fileHandle,
				am,
				particleSystem.getBatches(),
				outputType,
				prettyPrint
		);
		ParticleEffectLoader loader = new ParticleEffectLoader(new AbsoluteFileHandleResolver());
		try {
			loader.save(effect, param);
		} catch (IOException e) {
			Log.e("Couldn't save PFX!", e);
		}
	}

	/**
	 * @param name the suggested name
	 * @return same name if it's not in use or a unique enumerated variant
	 */
	public String getUnusedName(String name) {
		if (effect.findController(name) == null) return name;
		String baseName = name.replaceFirst("_\\d+$", "");
		int counter = 1;
		String newName;
		do {
			newName = baseName + "_" + counter;
			counter++;
		} while (effect.findController(newName) != null);

		return newName;
	}

	public void createNewController(String typeName) {
		if (typeName.equals(BILLBOARD_CONTROLLER)) {
			addDefaultBillboardController();
		}
		else if (typeName.equals(POINTSPRITE_CONTROLLER)) {
			addDefaultPointSpriteController();
		}
		else if (typeName.equals(MODELINSTANCE_CONTROLLER)) {
			addDefaultModelInstanceController();
		}
		else if (typeName.equals(PARTICLECONTROLLER_CONTROLLER)) {
			addDefaultParticleControllerController();
		}
	}

	public ParticleEffect getEffect() {
		return effect;
	}

	public void update(float delta) {
		particleSystem.update(delta);
	}

	public void render(Camera camera, ModelBatch batch) {
		particleSystem.begin();
		particleSystem.draw();
		particleSystem.end();
		batch.begin(camera);
		batch.render(particleSystem);
		batch.end();
	}

	public void setAtlas(@Null String atlasName) {
		setAtlas(atlasName, true);
	}

	public void setAtlas(@Null String atlasName, boolean forceUpdate) {
		cleanupAtlas();
		this.atlasName = atlasName;
		if (atlasName != null) {
			am.load(atlasName, TextureAtlas.class);
			am.finishLoading();
			textureAtlas = am.get(atlasName, TextureAtlas.class);
			billboardParticleBatch.setTexture(textureAtlas.getTextures().first());
			pointSpriteParticleBatch.setTexture(textureAtlas.getTextures().first());
			if (forceUpdate) forceUpdateAtlasRegions();
			for (EventListener listener : getListeners())
				listener.onTextureChanged(atlasName, new TextureRegion(textureAtlas.getTextures().first()));
		}
		else {
			for (EventListener listener : getListeners())
				listener.onTextureChanged(null, new TextureRegion());
		}
	}

	public TextureAtlas getAtlas() {
		return textureAtlas;
	}

	public String getAtlasName() {
		return atlasName;
	}

	public void replaceAtlas(String atlasName) {
		if (Objects.equals(atlasName, this.atlasName)) return;
		setAtlas(atlasName);
	}

	public void replaceRegionInfluencerWithSingle(String controllerName, String regionName) {
		ParticleController controller = effect.findController(controllerName);
		RegionInfluencer.Single influencer = new RegionInfluencer.Single(textureAtlas.findRegion(regionName));
		influencer.setAtlasName(atlasName);
		controller.replaceInfluencer(RegionInfluencer.class, influencer);
		controller.init();
	}

	public void replaceRegionInfluencerWithRandom(String controllerName, String... regionNames) {
		ParticleController controller = effect.findController(controllerName);
		RegionInfluencer.Random influencer = new RegionInfluencer.Random();
		TextureRegion[] textureRegions = new TextureRegion[regionNames.length];
		for (int i = 0; i < regionNames.length; i++) {
			textureRegions[i] = textureAtlas.findRegion(regionNames[i]);
		}
		influencer.clear();
		influencer.add(textureRegions);
		influencer.setAtlasName(atlasName);
		controller.replaceInfluencer(RegionInfluencer.class, influencer);
		controller.init();
	}

	public void replaceRegionInfluencerWithAnimated(String controllerName, String... regionNames) {
		ParticleController controller = effect.findController(controllerName);
		RegionInfluencer.Animated influencer = new RegionInfluencer.Animated();
		TextureRegion[] textureRegions = new TextureRegion[regionNames.length];
		for (int i = 0; i < regionNames.length; i++) {
			textureRegions[i] = textureAtlas.findRegion(regionNames[i]);
		}
		influencer.clear();
		influencer.add(textureRegions);
		influencer.setAtlasName(atlasName);
		controller.replaceInfluencer(RegionInfluencer.class, influencer);
		controller.init();
	}

	public Array<String> getLoadedModelPaths() {
		Array<SceneAsset> allModels = new Array<>();
		am.getAll(SceneAsset.class, allModels);
		Array<String> allModelPaths = new Array<>();
		for (SceneAsset asset : allModels) {
			allModelPaths.add(am.getAssetFileName(asset));
		}
		return allModelPaths;
	}

	public Array<Model> getLoadedModels() {
		Array<Model> models = new Array<>();
		Array<SceneAsset> allModels = new Array<>();
		am.getAll(SceneAsset.class, allModels);
		for (SceneAsset asset : allModels) {
			models.add(asset.scene.model);
		}
		return models;
	}

	/**
	 * Recreates all RegionInfluencers in every ParticleController and sets it to first region of current atlas. Use when the atlas
	 * is replaced completely.
	 */
	private void forceUpdateAtlasRegions() {
		if (effect == null) return;
		for (ParticleController controller : effect.getControllers()) {
			if (controller.renderer instanceof ModelInstanceRenderer) continue;
			if (controller.renderer instanceof ParticleControllerControllerRenderer) continue; // TODO more specific case

			String[] regionNames = new String[textureAtlas.getRegions().size];
			for (int i = 0; i < textureAtlas.getRegions().size; i++) {
				regionNames[i] = textureAtlas.getRegions().get(i).name;
			}
			RegionInfluencer oldInfluencer = controller.findInfluencer(RegionInfluencer.class);
			if (oldInfluencer instanceof RegionInfluencer.Single) {
				replaceRegionInfluencerWithSingle(controller.name, regionNames[0]);
			}
			else if (oldInfluencer instanceof RegionInfluencer.Random) {
				replaceRegionInfluencerWithRandom(controller.name, regionNames);
			}
			else {
				replaceRegionInfluencerWithAnimated(controller.name, regionNames);
			}
		}
	}

	private void cleanupAtlas() {
		if (atlasName != null) {
			textureAtlas = null;
			am.unload(atlasName);
			atlasName = null;
		}
	}

	private void cleanupEverything() {
		if (effect != null) {
			particleSystem.remove(effect);
			effect.dispose();
			effect = null;
		}
		if (effectName != null) {
			am.unload(effectName);
			effectName = null;
		}
		cleanupAtlas();
	}

	private void addDefaultBillboardController() {
		ParticleController newController = new ParticleController(
				getUnusedName("Billboard_1"),
				createDefaultRegularEmitter(),
				new BillboardRenderer(),
				createDefaultRegionInfluencer(),
				createDefaultSpawnInfluencer(),
				createDefaultDynamicsInfluencer()
		);
		newController.renderer.setBatch(billboardParticleBatch);
		effect.getControllers().add(newController);
		newController.init();
		newController.start();
	}

	private void addDefaultPointSpriteController() {
		ParticleController newController = new ParticleController(
				getUnusedName("PointSprite_1"),
				createDefaultRegularEmitter(),
				new PointSpriteRenderer(),
				createDefaultRegionInfluencer(),
				createDefaultSpawnInfluencer(),
				createDefaultDynamicsInfluencer()
		);
		newController.renderer.setBatch(pointSpriteParticleBatch);
		effect.getControllers().add(newController);
		newController.init();
		newController.start();
	}

	private void addDefaultModelInstanceController() {
		/* TODO come back later once decided what to do with non G3DB models
		 *  The problem here is that libGDX's ParticleSystems require a Model class asset to work with, and saving logic in
		 *  ModelInfluencer asks the AssetManager about asset filename of the model. With GLTF assets, which are loaded as
		 *  SceneAssets, this doesn't really work out of the box because we extract a Model from the SceneAsset, so only the
		 *  SceneAsset reference is held in the AssetManager and querying the Model filepath from AssetManager returns null.
		 *  Loading logic breaks too, because loader will expect a GLTF file to be loaded as a Model upon parsing the particle
		 *  effect JSON, and it's not a simple model but a SceneAsset.
		 */
		if (true) return;
		ParticleController newController = new ParticleController(
				getUnusedName("ModelInstance_1"),
				createDefaultRegularEmitter(),
				new ModelInstanceRenderer(),
				createDefaultModelInfluencer(),
				createDefaultSpawnInfluencer(),
				createDefaultDynamicsInfluencer()
		);
		newController.renderer.setBatch(modelInstanceParticleBatch);
		effect.getControllers().add(newController);
		newController.init();
		newController.start();
	}

	private void addDefaultParticleControllerController() {
		// TODO implement
	}

	private RegularEmitter createDefaultRegularEmitter() {
		RegularEmitter regularEmitter = new RegularEmitter();
		regularEmitter.setContinuous(true);
		regularEmitter.maxParticleCount = 100;
		regularEmitter.durationValue.setLow(3000, 3000);
		regularEmitter.emissionValue.setHigh(100);
		regularEmitter.lifeValue.setHigh(500, 1000);
		return regularEmitter;
	}

	private RegionInfluencer createDefaultRegionInfluencer() {
		RegionInfluencer regionInfluencer;
		Texture texture = null;
		if (textureAtlas != null) texture = textureAtlas.getTextures().first();
		if (texture == null) {
			texture = am.get("burst_test_particles.png", Texture.class);
			regionInfluencer = new RegionInfluencer.Single(texture);
			for (EventListener listener : getListeners())
				listener.onTextureChanged(null, new TextureRegion(texture));
		}
		else {
			regionInfluencer = new RegionInfluencer.Single(textureAtlas.getRegions().first());
		}
		return regionInfluencer;
	}

	private ModelInfluencer createDefaultModelInfluencer() {
		ModelInfluencer modelInfluencer;
		modelInfluencer = new ModelInfluencer.Single(getLoadedModels().first());
		return modelInfluencer;
	}

	private SpawnInfluencer createDefaultSpawnInfluencer() {
		RectangleSpawnShapeValue spawnShapeValue = new RectangleSpawnShapeValue();
		spawnShapeValue.spawnDepthValue.setHigh(1f);
		spawnShapeValue.spawnWidthValue.setHigh(1f);
		return new SpawnInfluencer(spawnShapeValue);
	}

	private DynamicsInfluencer createDefaultDynamicsInfluencer() {
		DynamicsInfluencer dynamicsInfluencer = new DynamicsInfluencer();
		DynamicsModifier.PolarAcceleration modifier = new DynamicsModifier.PolarAcceleration();
		modifier.strengthValue.setHigh(5f, 10f);
		modifier.thetaValue.setHigh(0f, 360f);
		modifier.phiValue.setHigh(-35f, 35f);
		dynamicsInfluencer.velocities.add(modifier);
		return dynamicsInfluencer;
	}

	private List<EventListener> getListeners() {
		return new ArrayList<>(listeners);
	}

	@Override
	public void dispose() {
		if (effect != null) effect.dispose();
		am.dispose();
	}

}
