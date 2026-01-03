package net.scholnick.lbdb.util;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public final class JSONUtilities {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        }
        catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONUtilities() {}
}
