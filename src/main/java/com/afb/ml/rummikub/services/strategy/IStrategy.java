package com.afb.ml.rummikub.services.strategy;

import com.afb.ml.rummikub.model.Player;

/**
 * The interface every strategy must implement.
 * 
 * @author rostskadat
 *
 */
public interface IStrategy {

    /**
     * This method play a move for a specific player. It returns whether it played or not.
     * 
     * @param player
     *            The player whose turn has come up
     * @return whether it played or not
     */
    boolean play(Player player);

}
