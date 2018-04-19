package net.pictulog.ml.rummikub.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;

import net.pictulog.ml.rummikub.model.MoveSet;
import net.pictulog.ml.rummikub.model.MoveSet.Move;
import net.pictulog.ml.rummikub.model.Player;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.model.TileSet;

@Controller
public class MoveController {

    public MoveSet openMoveSet(Player player) {
        MoveSet turn = new MoveSet();
        turn.setPlayer(player);
        return turn;
    }

    /**
     * This method will add a {@link Move} to the {@link MoveSet}.<br/>
     * <b>UC1</b>: T(BA(12345), BL(12345), RE(12345)) + RA(JB,JR, RE(4)):
     * <ol>
     * <li>BA(12345) + J => BA(123J5) + BA(4)</li>
     * <li>BL(12345) + J => BL(123J5) + BL(4)</li>
     * <li>RE(4) + BA(4) + BL(4) => 4(BA,BL,RE)</li>
     * <li><b>BA(12345) + BL(12345) + JB + JR + RE(4) => BA(123J5) + BL(123J5) + 4(BA,BL,RE)</b></li>
     * </ol>
     *
     * @param moveSet
     *            The {@link MoveSet} to add the {@link Move} to.
     * @param from
     *            The optional {@link TileSet} from which the move
     * @param tiles
     * @param to
     */
    @SuppressWarnings("unchecked")
    public void addMove(MoveSet moveSet, TileSet from, List<Tile> userTiles, List<TileSet> to) {
        assert (to != null);
        assert (!to.isEmpty());

        Move move = new Move();
        move.setToTileSets(to);
        move.setFromTileSet(from);

        List<Tile> safeFrom = (from == null) ? Collections.emptyList() : from;
        List<Tile> safeUserTiles = (userTiles == null) ? Collections.emptyList() : userTiles;

        List<Tile> inputTiles = new ArrayList<>();
        inputTiles.addAll(safeFrom);
        inputTiles.addAll(safeUserTiles);

        List<Tile> outputTiles = new ArrayList<>();
        to.stream().forEach(outputTiles::addAll);

        List<Tile> heap = moveSet.getHeap();
        Collection<Tile> toHeap = CollectionUtils.subtract(inputTiles, outputTiles);
        // I should make sure that the state of the heap is consistent, and that I do not remove many times the same
        // tile.
        if (!toHeap.isEmpty()) {
            heap.addAll(toHeap);
        } else {
            Collection<Tile> fromHeap = CollectionUtils.subtract(outputTiles, inputTiles);
            if (!fromHeap.isEmpty()) {
                heap.removeAll(fromHeap);
            }
        }
        moveSet.add(move);
    }

    public void closeMoveSet(MoveSet moveSet) {
        assert (!moveSet.isEmpty());
        assert (moveSet.getHeap().isEmpty());
        assert (moveSet.getPlayer() != null);
    }
}
