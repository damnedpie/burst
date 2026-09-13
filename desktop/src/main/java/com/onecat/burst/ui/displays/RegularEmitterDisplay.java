package com.onecat.burst.ui.displays;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class RegularEmitterDisplay extends ControllerComponentDisplay {

	private RegularEmitter emitter;

	public RegularEmitterDisplay(ParticleController controller, Skin skin) {
		super(controller, "Regular Emitter", skin);
	}

	@Override
	protected void initialize() {
		this.emitter = (RegularEmitter) controller.emitter;
	}

	@Override
	protected void onUnfolded() {
		CheckBox continuousCheckbox = new CheckBox("Continuous", getSkin());
		continuousCheckbox.setChecked(emitter.isContinuous());
		continuousCheckbox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				emitter.setContinuous(continuousCheckbox.isChecked());
			}
		});
		addContent(continuousCheckbox);
		addContent(new DisplayComponent.CountComponent(emitter, getSkin()));
		addContent(
				new DisplayComponent.RangedNumericComponent(
						"Delay",
						emitter.delayValue,
						getSkin(),
						true,
						true,
						true
				)
		);
		addContent(
				new DisplayComponent.RangedNumericComponent(
						"Duration",
						emitter.durationValue,
						getSkin(),
						true,
						true,
						true
				)
		);
		addContent(
				new DisplayComponent.ScaledNumericComponent(
						"Emission",
						emitter.emissionValue,
						getSkin(),
						true,
						true,
						true
				)
		);
		addContent(
				new DisplayComponent.ScaledNumericComponent(
						"Life",
						emitter.lifeValue,
						getSkin(),
						true,
						true,
						true
				)
		);
		addContent(
				new DisplayComponent.ScaledNumericComponent(
						"Life Offset",
						emitter.lifeOffsetValue,
						getSkin(),
						true,
						true,
						true
				)
		);
	}

}
