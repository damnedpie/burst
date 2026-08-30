package com.onecat.burst.world;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class WorldCamera extends PerspectiveCamera {

	// Orbit parameters
	private final Vector3 target = new Vector3(Vector3.Zero);
	private float distance = 15f;
	private final float minDistance = 1.5f;
	private final float maxDistance = 25f;

	// Rotation angles (in radians)
	private float azimuth = 0f;
	private float elevation = 0.5f; // Start with a positive elevation (above)

	// Sensitivity
	private final float rotationSpeed = 0.004f;
	private final float zoomSpeed = 2.5f;

	private InputProcessor inputProcessor;

	public WorldCamera() {
		super(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		near = 0.1f;
		reset();
	}

	private void updateCamera() {
		// azimuth: rotation around Y axis
		// elevation: angle from Y axis (0 = top, PI = bottom)
		float x = distance * MathUtils.sin(elevation) * MathUtils.sin(azimuth);
		float y = distance * MathUtils.cos(elevation);
		float z = distance * MathUtils.sin(elevation) * MathUtils.cos(azimuth);
		// Position is target + offset
		position.set(target.x + x, target.y + y, target.z + z);
		// Look at target
		lookAt(target);
		// Up vector - always world up to prevent rolling
		up.set(Vector3.Y);
		normalizeUp();
		update();
	}

	public void zoom(float amount) {
		distance = MathUtils.clamp(distance - amount, minDistance, maxDistance);
		updateCamera();
	}

	public void setTarget(float x, float y, float z) {
		target.set(x, y, z);
		updateCamera();
	}

	public void setDistance(float distance) {
		this.distance = MathUtils.clamp(distance, minDistance, maxDistance);
		updateCamera();
	}

	public void reset() {
		target.set(Vector3.Zero);
		distance = 15f;
		azimuth = 45f * MathUtils.degRad;
		elevation = 0.5f;
		updateCamera();
	}

	public InputProcessor getInputProcessor() {
		if (inputProcessor == null) {
			inputProcessor = new InputProcessor() {

				private boolean isDragging = false;
				private float lastMouseX, lastMouseY;
				private final int dragButton = Input.Buttons.LEFT;

				@Override
				public boolean keyDown(int keycode) {
					return false;
				}

				@Override
				public boolean keyUp(int keycode) {
					return false;
				}

				@Override
				public boolean keyTyped(char character) {
					return false;
				}

				@Override
				public boolean touchDown(int screenX, int screenY, int pointer, int button) {
					if (button == dragButton) {
						isDragging = true;
						lastMouseX = screenX;
						lastMouseY = screenY;
						return true;
					}
					return false;
				}

				@Override
				public boolean touchUp(int screenX, int screenY, int pointer, int button) {
					if (button == dragButton) {
						isDragging = false;
						return true;
					}
					return false;
				}

				@Override
				public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
					return false;
				}

				@Override
				public boolean touchDragged(int screenX, int screenY, int pointer) {
					if (!isDragging) return false;
					float deltaX = screenX - lastMouseX;
					float deltaY = screenY - lastMouseY;
					// Update rotation
					azimuth -= deltaX * rotationSpeed;
					elevation -= deltaY * rotationSpeed;
					// Clamp elevation to prevent flipping
					elevation = MathUtils.clamp(elevation, 0.01f, MathUtils.PI - 0.01f);
					lastMouseX = screenX;
					lastMouseY = screenY;
					updateCamera();
					return true;
				}

				@Override
				public boolean mouseMoved(int screenX, int screenY) {
					return false;
				}

				@Override
				public boolean scrolled(float amountX, float amountY) {
					zoom(-amountY * zoomSpeed);
					return true;
				}
			};
		}
		return inputProcessor;
	}

}