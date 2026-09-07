package com.onecat.burst.ui.displays;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;

public class ListPicker extends Table {

	private final List<String> availableList;
	private final List<String> selectedList;

	public interface EventListener {

		void changed();

	}

	private final ArrayList<EventListener> listeners = new ArrayList<>();

	// This is required to preserve the original order in which regions come in the atlas
	Array<String> originalItems;

	public ListPicker(Skin skin) {
		super(skin);
		defaults().space(4f);
		availableList = new List<>(skin);
		add(availableList).growX().minHeight(availableList.getItemHeight() * 4).uniformX();
		Table listControlsTable = new Table();
		listControlsTable.defaults().space(4f);
		ImageButton removeButton = new ImageButton(skin.get("left", ImageButton.ImageButtonStyle.class));
		removeButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (makeItemUnselected(selectedList.getSelected()))
					for (EventListener listener : getEventListeners()) listener.changed();
			}
		});
		listControlsTable.add(removeButton).row();
		ImageButton addButton = new ImageButton(skin.get("right", ImageButton.ImageButtonStyle.class));
		addButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (makeItemSelected(availableList.getSelected()))
					for (EventListener listener : getEventListeners()) listener.changed();
			}
		});
		listControlsTable.add(addButton);
		add(listControlsTable).top();
		selectedList = new List<>(skin);
		add(selectedList).growX().minHeight(selectedList.getItemHeight() * 4).uniformX();
		Table orderControlsTable = new Table();
		orderControlsTable.defaults().space(4f);
		ImageButton upButton = new ImageButton(skin.get("increase", ImageButton.ImageButtonStyle.class));
		upButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (moveUp())
					for (EventListener listener : getEventListeners()) listener.changed();
			}
		});
		orderControlsTable.add(upButton).row();
		ImageButton downButton = new ImageButton(skin.get("decrease", ImageButton.ImageButtonStyle.class));
		downButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (moveDown())
					for (EventListener listener : getEventListeners()) listener.changed();
			}
		});
		orderControlsTable.add(downButton);
		add(orderControlsTable).top();
	}

	public void addListener(EventListener listener) {
		listeners.add(listener);
	}

	private ArrayList<EventListener> getEventListeners() {
		return new ArrayList<>(listeners);
	}

	/**
	 * Use on initialization only.
	 * @param items all items, both selected and available
	 */
	public void setItems(String... items) {
		originalItems = new Array<>(items);
		availableList.setItems(items);
	}

	public String[] getSelectedItems() {
		String[] selectedItems = new String[selectedList.getItems().size];
		for (int i = 0; i < selectedList.getItems().size; i++) {
			selectedItems[i] = selectedList.getItems().get(i);
		}
		return selectedItems;
	}

	/**
	 * Moves an item from available to selected.
	 * @param item item to move
	 * @return true if anything changed, false on no changes
	 */
	public boolean makeItemSelected(String item) {
		if (item == null) return false;
		Array<String> availableItems = new Array<>(originalItems);
		availableItems.removeAll(selectedList.getItems(), false);
		availableItems.removeValue(item, false);
		availableList.setItems(availableItems);
		Array<String> selectedItems = selectedList.getItems();
		selectedItems.add(item);
		selectedList.setItems(selectedItems);
		return true;
	}

	/**
	 * Moves an item from selected to available
	 * @param item item to move
	 * @return true if anything changed, false on no changes
	 */
	public boolean makeItemUnselected(String item) {
		if (item == null) return false;
		if (selectedList.getItems().size == 1) return false;
		Array<String> availableItems = new Array<>(originalItems);
		Array<String> selectedItems = selectedList.getItems();
		selectedItems.removeValue(item, false);
		availableItems.removeAll(selectedItems, false);
		availableList.setItems(availableItems);
		selectedList.setItems(selectedItems);
		return true;
	}

	/**
	 * Sets all items as selected in the original order.
	 */
	public void selectAll() {
		availableList.setItems();
		selectedList.setItems(originalItems);
		for (EventListener listener : getEventListeners()) listener.changed();
	}

	/**
	 * Sets all items as unselected except the first.
	 */
	public void clearSelection() {
		Array<String> allItems = new Array<>(originalItems);
		String first = allItems.first();
		allItems.removeValue(first, false);
		availableList.setItems(allItems);
		selectedList.setItems(first);
		for (EventListener listener : getEventListeners()) listener.changed();
	}

	/**
	 * Moves currently selected item in the right list up
	 * @return true if anything changed
	 */
	private boolean moveUp() {
		int itemIndex = selectedList.getSelectedIndex();
		if (itemIndex == 0) return false;
		Array<String> selectedItems = selectedList.getItems();
		selectedItems.swap(itemIndex, itemIndex - 1);
		selectedList.setItems(selectedItems);
		return true;
	}

	/**
	 * Moves currently selected item in the right list down
	 * @return true if anything changed
	 */
	private boolean moveDown() {
		int itemIndex = selectedList.getSelectedIndex();
		if (itemIndex + 1 == selectedList.getItems().size) return false;
		Array<String> selectedItems = selectedList.getItems();
		selectedItems.swap(itemIndex, itemIndex + 1);
		selectedList.setItems(selectedItems);
		return true;
	}

	@Override
	protected void setStage(Stage stage) {
		super.setStage(stage);
		if (stage == null) {
			listeners.clear();
		}
	}

}
