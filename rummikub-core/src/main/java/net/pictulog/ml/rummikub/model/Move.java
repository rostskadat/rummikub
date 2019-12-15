package net.pictulog.ml.rummikub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NonNull;

/**
 * A {@code Move} capture the "after-state" of a specific move by a
 * {@link Player}. There are different kind of {@code Move} that can be
 * represented. A {@code Move} can originated from a specific {@link TileSet}
 * and result in one or more {@link TileSet}. For instance by splitting a
 * {@link TileRun}. A {@code Move} can also have no originating {@link TileSet}.
 * As for instance when creating a new {@link TileRun} or {@link TileGroup} from
 * scratch. Note that some situation require the use of a specific LIFO
 * 
 * @author rostskadat
 *
 */
@Data
public class Move implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * XXX: what is the HEAP!!???!
	 */
	private List<Tile> heap = new ArrayList<Tile>();

	/**
	 * The {@code fromTileSet} represents the {@link TileSet} from which the
	 * {@code Move} was played.
	 */
	private TileSet fromTileSet;

	/**
	 * The {@code tiles} represents the list of {@link Tile} involved in that
	 * {@code Move}
	 */
	@NonNull
	private List<Tile> tiles = new ArrayList<Tile>();

	/**
	 * The {@code toTileSets} represents the list of {@link TileSet} resulting from
	 * that {@code Move}
	 */
	@NonNull
	private List<TileSet> toTileSets = new ArrayList<TileSet>();
	
	@Override
	public boolean equals(Object o) {
		return (o != null && o instanceof Move && sameMove(this, (Move) o));
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	private static final boolean sameMove(Move m1, Move m2) {
		if (m1.getFromTileSet() != m2.getFromTileSet() || (m1.getFromTileSet() != null && ! m1.getFromTileSet().equals(m2.getFromTileSet()))) {
			return false;
		}
		if (! m1.getTiles().containsAll(m2.getTiles())) {
			return false;
		}
		for (TileSet tileSet : m1.getToTileSets()) {
			if (! m2.getToTileSets().contains(tileSet)) {
				return false;
			}
		}
		return true;
	}
}
