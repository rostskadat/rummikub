package net.pictulog.ml.rummikub.ui.components;

import com.vaadin.ui.Label;

import net.pictulog.ml.rummikub.model.Tile;

public class UiTile extends Label {

	private static final long serialVersionUID = 1L;

	public UiTile(Tile tile) {
		super();
		if (tile.getNumber() != null) {
			setValue(String.valueOf(tile.getNumber()));
		}
		setStyleName("tile-"+tile.getColor().name());
	}
}
