package com.onecat.burst.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class WorldGrid implements Disposable {

	private final int gridSize;
	private final float tileSize;

	private Model gridModel;
	private ModelInstance gridInstance;

	public boolean visible = true;

	public WorldGrid(int gridSize, float tileSize) {
		this.gridSize = gridSize;
		this.tileSize = tileSize;
		buildGrid();
	}

	private void buildGrid() {
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		float offset = (gridSize * tileSize) / 2f;
		// Create grid lines using MeshBuilder
		MeshPartBuilder builder = modelBuilder.part(
				"grid",
				GL20.GL_LINES,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
				new Material()
		);
		// Draw all lines in a single mesh part
		for (int i = 0; i <= gridSize; i++) {
			float pos = i * tileSize - offset;
			// Horizontal lines
			builder.line(
					new Vector3(-offset, 0f, pos),
					Color.GRAY,
					new Vector3(offset, 0f, pos),
					Color.GRAY
			);
			// Vertical lines
			builder.line(
					new Vector3(pos, 0f, -offset),
					Color.GRAY,
					new Vector3(pos, 0f, offset),
					Color.GRAY
			);
		}
		gridModel = modelBuilder.end();
		gridInstance = new ModelInstance(gridModel);
	}

	public void render(Camera camera, ModelBatch modelBatch) {
		Gdx.gl.glLineWidth(2f);
		modelBatch.begin(camera);
		modelBatch.render(gridInstance);
		modelBatch.end();
		Gdx.gl.glLineWidth(1f);
	}

	@Override
	public void dispose() {
		gridModel.dispose();
	}

}
