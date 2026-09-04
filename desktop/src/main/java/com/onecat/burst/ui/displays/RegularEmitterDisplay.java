package com.onecat.burst.ui.displays;

import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class RegularEmitterDisplay extends Table {

	public RegularEmitterDisplay(RegularEmitter emitter, Skin skin) {
		super(skin);
		setBackground(skin.getDrawable("panel_thin_default"));
		defaults().growX().minHeight(26f).space(4f);
		Label header = new Label("Regular Emitter", skin);
		header.setAlignment(Align.center);
		add(header).row();
		CheckBox continuousCheckbox = new CheckBox("Continuous", skin);
		continuousCheckbox.setChecked(emitter.isContinuous());
		continuousCheckbox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				emitter.setContinuous(continuousCheckbox.isChecked());
			}
		});
		add(continuousCheckbox).row();
		add(new DisplayComponent.CountComponent(emitter, skin)).row();
		add(new DisplayComponent.RangedNumericComponent("Delay", emitter.delayValue, skin)).row();
		add(new DisplayComponent.RangedNumericComponent("Duration", emitter.durationValue, skin)).row();
		add(new DisplayComponent.ScaledNumericComponent("Emission", emitter.emissionValue, skin)).row();
		add(new DisplayComponent.ScaledNumericComponent("Life", emitter.lifeValue, skin)).row();
		add(new DisplayComponent.ScaledNumericComponent("Life Offset", emitter.lifeOffsetValue, skin)).row();
	}

}
