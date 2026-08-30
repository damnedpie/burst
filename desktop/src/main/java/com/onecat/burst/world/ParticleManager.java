package com.onecat.burst.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.particles.*;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader.ParticleEffectLoadParameter;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.utils.Disposable;
import com.onecat.burst.utils.Log;

public class ParticleManager implements Disposable {

	ParticleSystem particleSystem;
	BillboardParticleBatch billboardParticleBatch;
	ParticleEffect effect;
	AssetManager am;

	public ParticleManager(Camera camera) {
		particleSystem = new ParticleSystem();
		billboardParticleBatch = new BillboardParticleBatch();
		billboardParticleBatch.getBlendingAttribute().sourceFunction = GL20.GL_SRC_ALPHA;
		billboardParticleBatch.getBlendingAttribute().destFunction = GL20.GL_ONE_MINUS_SRC_ALPHA;
		billboardParticleBatch.setCamera(camera);
		particleSystem.add(billboardParticleBatch);
		// TODO other batch types

		am = new AssetManager();
		ParticleEffectLoader loader = new ParticleEffectLoader(new AbsoluteFileHandleResolver());
		am.setLoader(ParticleEffect.class, loader);
	}

	public void loadPfx(FileHandle fileHandle) {
		Log.l("Loading %s", fileHandle.path());
		ParticleEffectLoadParameter param = new ParticleEffectLoadParameter(particleSystem.getBatches());
		if (effect != null) {
			particleSystem.remove(effect);
			effect.dispose();
		}
		am.load(fileHandle.path(), ParticleEffect.class, param);
		am.finishLoading();
		effect = new ParticleEffect(am.get(fileHandle.path(), ParticleEffect.class));
		effect.init();
		effect.start();
		particleSystem.add(effect);
	}

	public void update(float delta) {
		particleSystem.update(delta);
	}

	public void draw() {
		particleSystem.begin();
		particleSystem.draw();
		particleSystem.end();
	}

	public void render(Camera camera, ModelBatch batch) {
		batch.begin(camera);
		batch.render(particleSystem);
		batch.end();
	}

	@Override
	public void dispose() {
		if (effect != null) effect.dispose();
		am.dispose();
	}

}
