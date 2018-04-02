package com.afb.ml.rummikub.services;

import com.afb.ml.rummikub.model.Player;

public interface IStrategy {

    boolean play(Player play);

}
