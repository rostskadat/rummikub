package net.pictulog.ml.rummikub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * This class gather the after state of a {@link MoveSet}.
 * 
 * @author N090536
 *
 */
@Data
public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> drawnTileIndexes = new ArrayList<>();

    private Table finalTable;

    private Pool finalPool;

    private List<Player> finalPlayers = new ArrayList<>();
}
