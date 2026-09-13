package com.onecat.burst.ui.displays;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ScaleInfluencer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ScaleInfluencerDisplay extends ControllerComponentDisplay {

	ScaleInfluencer influencer;

	public ScaleInfluencerDisplay(ParticleController controller, Skin skin, boolean isActive) {
		super(controller, "Scale Influencer", skin, true, isActive);
	}

	@Override
	protected void initialize() {
		influencer = controller.findInfluencer(ScaleInfluencer.class);
	}

	@Override
	protected void onUnfolded() {
		addContent(
				new DisplayComponent.ScaledNumericComponent(
						"Scale",
						influencer.value,
						getSkin(),
						true,
						false,
						true
				)
		);
	}

	@Override
	protected void onAdded() {
		influencer = new ScaleInfluencer();
		if (!controller.replaceInfluencer(ScaleInfluencer.class, influencer)) {
			controller.influencers.add(influencer);
			controller.init();
		}
	}

	@Override
	protected void onRemoved() {
		controller.removeInfluencer(ScaleInfluencer.class);
		influencer = null;
	}

}
