package net.pictulog.ml.rummikub.model;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A {@code Moves} represents a list of {@link Move} played by a
 * {@link Player} during its turn.
 * 
 * @author rostskadat
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Moves extends ArrayList<Move> {

	private static final long serialVersionUID = 1L;

	private Player player;
}
