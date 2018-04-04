package com.afb.ml.rummikub.common;

import java.io.IOException;

import com.afb.ml.rummikub.model.TileSet;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * 
 * @deprecated non necessary, replaced by ObjectMapper.enableDefaultTyping(DefaultTyping.NON_CONCRETE_AND_ARRAYS)
 * @author rostskadat
 *
 */
@Deprecated
public class TileSetSerializer extends StdSerializer<TileSet> {

    private static final long serialVersionUID = 1L;

    public TileSetSerializer() {
        this(null);
    }

    public TileSetSerializer(Class<TileSet> t) {
        super(t);
    }

    @Override
    public void serialize(TileSet tileSet, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("className", tileSet.getClass().getName());
        jgen.writeArrayFieldStart("tiles");
        tileSet.forEach(tile -> {
            try {
                jgen.writeObject(tile);
            } catch (IOException e) {
                // NA
            }
        });
        jgen.writeEndArray();
        jgen.writeEndObject();
    }

}
