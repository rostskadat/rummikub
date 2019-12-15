package net.pictulog.ml.rummikub.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * A {@code Moves} represents a list of {@link Move} played by a {@link Player}
 * during its turn.
 * 
 * @author rostskadat
 *
 */
@Getter
@Setter
public class Moves extends ArrayList<Move> {

	private static final long serialVersionUID = 1L;

	private Player player;

	@Override
	public boolean equals(Object o) {
		return (o != null && o instanceof Moves && (sameMoves(this, (Moves) o)));
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	private static final boolean sameMoves(Moves m1, Moves m2) {
		return m1.size() == m2.size() && sameMove(m1, m2);
	}

	private static final boolean sameMove(Moves m1, Moves m2) {
		for (Move m : m1) {
			if (! m2.contains(m)) {
				return false;
			}
		}
		return true;
	}

}
