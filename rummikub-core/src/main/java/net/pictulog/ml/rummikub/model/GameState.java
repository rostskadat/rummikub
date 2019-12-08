package net.pictulog.ml.rummikub.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * This class gather the after state of a {@link Moves}.
 * 
 * @author rostskadat
 *
 */
@Data
public class GameState implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Integer> drawnTileIndexes;

	private Table finalTable;

	private Pool finalPool;

	private List<Player> finalPlayers;
}
