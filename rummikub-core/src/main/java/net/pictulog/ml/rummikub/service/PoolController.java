package net.pictulog.ml.rummikub.service;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import lombok.Getter;
import net.pictulog.ml.rummikub.model.Pool;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.model.TileColor;

/**
 * The {@code PoolController} is in charge of the {@link Pool} of {@link Tile}s
 * 
 * @author rostskadat
 *
 */
@Controller
public class PoolController {

	private static final Log LOG = LogFactory.getLog(PoolController.class);

	@Value("${numberOfTilesPerColor:13}")
	private int numberOfTilesPerColor;

	@Getter
	private Pool pool;

	@PostConstruct
	private void postConstruct() {
		resetPool();
	}

	public int getPoolSize() {
		return pool.size();
	}

	public void resetPool() {
		pool = new Pool();
		LOG.debug("Creating tiles...");
		for (TileColor color : TileColor.values()) {
			for (int i = 1; i <= numberOfTilesPerColor; i++) {
				// I add 2 tiles of each number / color
				pool.add(new Tile(i, color));
				pool.add(new Tile(i, color));
			}
		}
		LOG.debug("Adding Jockers...");
		pool.add(new Tile(TileColor.RED));
		pool.add(new Tile(TileColor.BLACK));
	}
}
