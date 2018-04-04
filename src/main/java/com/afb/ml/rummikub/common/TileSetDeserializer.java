package com.afb.ml.rummikub.common;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.afb.ml.rummikub.model.TileSet;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * 
 * @deprecated non necessary, replaced by ObjectMapper.enableDefaultTyping(DefaultTyping.NON_CONCRETE_AND_ARRAYS)
 * @author rostskadat
 *
 */
@Deprecated
public class TileSetDeserializer extends StdDeserializer<TileSet> { 

    private static final long serialVersionUID = 1L;

    private static final Log LOG = LogFactory.getLog(TileSetDeserializer.class);

    public TileSetDeserializer() { 
        this(null); 
    }
 
    public TileSetDeserializer(Class<TileSet> t) {
        super(t);
    }
 
    @Override
    public TileSet deserialize(JsonParser jp, DeserializationContext ctxt) 
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String className = node.get("className").asText();
        TileSet set = null;
        try {
            set = (TileSet) Class.forName(className).newInstance();
            // TODO: what about the actual tiles
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LOG.error(e);
        }
        return set;
    }
}
