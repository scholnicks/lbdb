package net.scholnick.lbdb.util;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * JSONUtilities is a collection of utility methods for working with JSON.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public final class JSONUtilities {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Converts the given object to a JSON string. */
    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        }
        catch (JacksonException e) {
            throw new ApplicationException(e);
        }
    }

    private JSONUtilities() {}
}
