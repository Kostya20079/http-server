package com.httpserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;

/**
 * Utility class for working with JSON using Jackson library.
 * Provides methods to parse JSON strings, convert Java objects to JSON, and vice versa.
 */

public class JSON {
    // Static ObjectMapper instance with custom configuration
    private static ObjectMapper myObjectMapper = defaultObjectMapper();


    /**
     * Creates a default ObjectMapper instance.
     * Configured to ignore unknown JSON properties during deserialization.
     */
    private static ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        // Prevent errors when JSON contains fields that are not in the Java class
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return om;
    }

    /**
     * Parses a JSON string into a JsonNode tree.
     */
    public static JsonNode parse(String jsonSrs) throws IOException {
        return myObjectMapper.readTree(jsonSrs);
    }

    /**
     * Converts a JsonNode into a Java object of the specified class.
     */
    public static <T> T fromJson(JsonNode jsonNode, Class<T> clazz) throws JsonProcessingException {
        return myObjectMapper.treeToValue(jsonNode, clazz);
    }

    /**
     * Converts a Java object into a JsonNode.
     */
    public static JsonNode toJson(Object obj) {
        return myObjectMapper.valueToTree(obj);
    }

    /**
     * Converts a JsonNode to a compact JSON string.
     */
    public static String stringify(JsonNode node) throws JsonProcessingException {
        return generateJson(node, false);
    }

    /**
     * Converts a JsonNode to a pretty-printed JSON string.
     */
    public static String stringifyPretty(JsonNode node) throws JsonProcessingException {
        return generateJson(node, true);
    }

    /**
     * Internal helper method to generate a JSON string from an object.
     * Can optionally produce pretty-printed output.
     */
    private static String generateJson(Object obj, boolean pretty) throws JsonProcessingException {
        ObjectWriter objectWriter = myObjectMapper.writer();
        if(pretty) {
            objectWriter = objectWriter.with(SerializationFeature.INDENT_OUTPUT);
        }
        return objectWriter.writeValueAsString(obj);
    }
}