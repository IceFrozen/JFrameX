package cn.ximuli.jframex.common.utils;

import cn.ximuli.jframex.common.exception.JSONException;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * A JSON serialization and deserialization utility class based on the Jackson library, providing rich JSON
 * processing capabilities including conversion between objects and JSON strings, JSON field extraction,
 * JSON format validation, and JSON content modification.
 *
 * @author lizhipeng
 * @email taozi031@163.com
 */
public class JSONUtil {

    private JSONUtil() {
    }

    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    private static ObjectMapper mapper = new JsonMapper();

    // From JsonPath, used for methods like property value lookup and property name replacement, this class is thread-safe
    private static ParseContext jsonParseContext = null;

    private static Set<JsonReadFeature> JSON_READ_FEATURES_ENABLED;

    static {
        try {
            JSON_READ_FEATURES_ENABLED = new HashSet<>();
            // Allow Java comments in JSON
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_JAVA_COMMENTS);
            // Allow fields in JSON without double quotes
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES);
            // Allow fields in JSON enclosed in single quotes
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_SINGLE_QUOTES);
            // Allow unquoted ASCII control characters in JSON
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS);
            // Allow leading zeros in JSON numbers (e.g., 0001)
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS);
            // Allow NaN, INF, -INF as number types in JSON
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS);
            // Allow cases where only keys exist without values
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_MISSING_VALUES);
            // Allow trailing commas in array JSON
            JSON_READ_FEATURES_ENABLED.add(JsonReadFeature.ALLOW_TRAILING_COMMA);
            // Initialization
            initMapper(mapper, JSON_READ_FEATURES_ENABLED);
            // Initialize JsonPath
            initJsonPath();
        } catch (Exception e) {
            logger.error("jackson config error", e);
        }
    }

    /**
     * Create an ObjectMapper instance
     *
     * @return ObjectMapper instance with configuration
     */
    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        initMapper(objectMapper, JSON_READ_FEATURES_ENABLED);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }


    /**
     * Initialize ObjectMapper with specified features
     *
     * @param mapper           ObjectMapper instance to configure
     * @param jsonReadFeatures Set of JsonReadFeatures to enable
     * @return Configured ObjectMapper
     */
    private static ObjectMapper initMapper(ObjectMapper mapper, Set<JsonReadFeature> jsonReadFeatures) {
        if (jsonReadFeatures == null || jsonReadFeatures.isEmpty()) {
            jsonReadFeatures = JSON_READ_FEATURES_ENABLED;
        }
        if (mapper == null) {
            JsonMapper.Builder builder = JsonMapper.builder().enable(jsonReadFeatures.toArray(new JsonReadFeature[0]));
            mapper = builder.build();
        }
        for (JsonReadFeature f : jsonReadFeatures) {
            mapper.enable(f.mappedFeature());
        }
        return initMapperConfig(mapper);
    }


    /**
     * Configure additional ObjectMapper settings
     *
     * @param objectMapper ObjectMapper to configure
     * @return Configured ObjectMapper
     */
    public static ObjectMapper initMapperConfig(ObjectMapper objectMapper) {
        String dateTimeFormat = DateUtil.DEFAULT_PATTERN;
        objectMapper.setDateFormat(new SimpleDateFormat(dateTimeFormat));
        // Configure JSON indentation support
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        // Allow single values to be treated as arrays
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        // Disallow duplicate keys, throw exception
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        // Disallow using int to represent Enum order() for deserialization, throw exception
        objectMapper.enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        // Do not throw error when unknown properties are present
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // Do not throw exception when objects are empty
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // Date format configuration
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Allow unknown fields
        objectMapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        // Serialize BigDecimal as plain string instead of scientific notation
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        // Support Java 8 date/time types
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new Jdk8Module());
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    /**
     * Initialize json path with Jackson Provider
     */
    private static void initJsonPath() {
        jsonParseContext = JsonPath.using(Configuration.builder() //
                .mappingProvider(new JacksonMappingProvider()) //
                .jsonProvider(new JacksonJsonNodeJsonProvider()).build());
    }

    /**
     * Get the default ObjectMapper instance
     *
     * @return ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    /**
     * JSON deserialization from URL
     *
     * @param url  URL to read from
     * @param type Target class type
     * @return Deserialized object
     */
    public static <V> V from(URL url, Class<V> type) {
        try {
            return mapper.readValue(url, type);
        } catch (IOException e) {

            throw new JSONException("jackson from error, url: {}, type: {}", url.getPath(), type, e);
        }
    }

    /**
     * JSON deserialization from URL
     *
     * @param url  URL to read from
     * @param type TypeReference for generic types
     * @return Deserialized object
     */
    public static <V> V from(URL url, TypeReference<V> type) {
        try {
            return mapper.readValue(url, type);
        } catch (IOException e) {
            throw new JSONException("jackson from error, url: {}, type: {}", url.getPath(), type, e);
        }
    }

    /**
     * JSON deserialization to List from URL
     *
     * @param url  URL to read from
     * @param type Element class type
     * @return List of deserialized objects
     */
    public static <V> List<V> fromList(URL url, Class<V> type) {
        try {
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return mapper.readValue(url, collectionType);
        } catch (IOException e) {
            throw new JSONException("jackson from error, url: {}, type: {}", url.getPath(), type, e);
        }
    }

    /**
     * JSON deserialization from InputStream
     *
     * @param inputStream InputStream to read from
     * @param type        Target class type
     * @return Deserialized object
     */
    public static <V> V from(InputStream inputStream, Class<V> type) {
        try {
            return mapper.readValue(inputStream, type);
        } catch (IOException e) {
            throw new JSONException("jackson from error, type: {}", type, e);
        }
    }

    /**
     * JSON deserialization from InputStream
     *
     * @param inputStream InputStream to read from
     * @param type        TypeReference for generic types
     * @return Deserialized object
     */
    public static <V> V from(InputStream inputStream, TypeReference<V> type) {
        try {
            return mapper.readValue(inputStream, type);
        } catch (IOException e) {
            throw new JSONException("jackson from error, type: {}", type, e);
        }
    }

    /**
     * JSON deserialization to List from InputStream
     *
     * @param inputStream InputStream to read from
     * @param type        Element class type
     * @return List of deserialized objects
     */
    public static <V> List<V> fromList(InputStream inputStream, Class<V> type) {
        try {
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return mapper.readValue(inputStream, collectionType);
        } catch (IOException e) {
            throw new JSONException("jackson from error, type: {}", type, e);
        }
    }

    /**
     * JSON deserialization from File
     *
     * @param file File to read from
     * @param type Target class type
     * @return Deserialized object
     */
    public static <V> V from(File file, Class<V> type) {
        try {
            return mapper.readValue(file, type);
        } catch (IOException e) {
            throw new JSONException("jackson from error, file path: {}, type: {}", file.getPath(), type, e);
        }
    }

    /**
     * JSON deserialization from File
     *
     * @param file File to read from
     * @param type TypeReference for generic types
     * @return Deserialized object
     */
    public static <V> V from(File file, TypeReference<V> type) {
        try {
            return mapper.readValue(file, type);
        } catch (IOException e) {
            throw new JSONException("jackson from error, file path: {}, type: {}", file.getPath(), type, e);
        }
    }

    /**
     * JSON deserialization to List from File
     *
     * @param file File to read from
     * @param type Element class type
     * @return List of deserialized objects
     */
    public static <V> List<V> fromList(File file, Class<V> type) {
        try {
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return mapper.readValue(file, collectionType);
        } catch (IOException e) {
            throw new JSONException("jackson from error, file path: {}, type: {}", file.getPath(), type, e);
        }
    }

    /**
     * JSON deserialization from String
     *
     * @param json JSON string
     * @param type Target class type
     * @return Deserialized object
     */
    public static <V> V from(String json, Class<V> type) {
        return from(json, (Type) type);
    }

    /**
     * JSON deserialization from String
     *
     * @param json JSON string
     * @param type TypeReference for generic types
     * @return Deserialized object
     */
    public static <V> V from(String json, TypeReference<V> type) {
        return from(json, type.getType());
    }

    /**
     * JSON deserialization from String
     *
     * @param json JSON string
     * @param type Target type
     * @return Deserialized object
     */
    public static <V> V from(String json, Type type) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new JSONException("jackson from error, json: {}, type: {}", json, type, e);
        }
    }

    /**
     * JSON deserialization to List from String
     *
     * @param json JSON string
     * @param type Element class type
     * @return List of deserialized objects
     */
    public static <V> List<V> fromList(String json, Class<V> type) {
        if (StringUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        try {
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return mapper.readValue(json, collectionType);
        } catch (IOException e) {
            throw new JSONException("jackson from error, json: {}, type: {}", json, type, e);
        }
    }

    /**
     * JSON deserialization to Map from String
     *
     * @param json JSON string
     * @return Map of String to Object
     */
    public static Map<String, Object> fromMap(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            MapType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
            return mapper.readValue(json, mapType);
        } catch (IOException e) {
            throw new JSONException("jackson from error, json: {}, type: {}", json, e);
        }
    }

    /**
     * Serialize List to JSON string
     *
     * @param list List to serialize
     * @return JSON string
     */
    public static <V> String to(List<V> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new JSONException("jackson to error, data: {}", list, e);
        }
    }

    /**
     * Serialize object to JSON string
     *
     * @param v Object to serialize
     * @return JSON string
     */
    public static <V> String to(V v) {
        try {
            return mapper.writeValueAsString(v);
        } catch (JsonProcessingException e) {
            throw new JSONException("jackson to error, data: {}", v, e);
        }
    }

    /**
     * Serialize List to JSON file
     *
     * @param path File path
     * @param list List to serialize
     */
    public static <V> void toFile(String path, List<V> list) {
        try (Writer writer = new FileWriter(new File(path), true)) {
            mapper.writer().writeValues(writer).writeAll(list);
        } catch (Exception e) {
            throw new JSONException("jackson to file error, path: {}, list: {}", path, list, e);
        }
    }

    /**
     * Serialize object to JSON file
     *
     * @param path File path
     * @param v    Object to serialize
     */
    public static <V> void toFile(String path, V v) {
        try (Writer writer = new FileWriter(new File(path), true)) {
            mapper.writer().writeValues(writer).write(v);
        } catch (Exception e) {
            throw new JSONException("jackson to file error, path: {}, data: {}", path, v, e);
        }
    }

    /**
     * Get a field value as String from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @return Field value as String, null by default
     */
    public static String getAsString(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return null;
            }
            return getAsString(jsonNode);
        } catch (Exception e) {
            throw new JSONException("jackson get string error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Convert JsonNode to String
     *
     * @param jsonNode JsonNode to convert
     * @return String value
     */
    private static String getAsString(JsonNode jsonNode) {
        return jsonNode.isTextual() ? jsonNode.textValue() : jsonNode.toString();
    }

    /**
     * Get text value from JsonNode by key
     *
     * @param jsonNode JsonNode
     * @param key      Field key
     * @return Text value
     */
    public static String getAsText(JsonNode jsonNode, String key) {
        if (jsonNode == null) {
            return null;
        }
        try {
            JsonNode valueNode = jsonNode.get(key);
            if (null == valueNode) {
                return null;
            }
            return valueNode.asText();
        } catch (Exception e) {
            throw new JSONException("jackson get string error, json: {}, key: {}", jsonNode.toString(), key, e);
        }
    }

    /**
     * Get property text value by Json Path (returns null if property is array or object)
     *
     * @param jsonNode -- json object
     * @param jsonPath -- property path, format see: https://github.com/json-path/JsonPath
     * @return Text value
     */
    public static String getAsStringByJsonPath(JsonNode jsonNode, String jsonPath) {
        JsonNode targetNode = jsonParseContext.parse(jsonNode).read(jsonPath);
        if (targetNode == null || targetNode.isContainerNode()) {
            return null;
        }
        return targetNode.asText();
    }

    /**
     * Get property text value by Json Path (returns null if property is array or object)
     *
     * @param json     -- json string
     * @param jsonPath -- property path, format see: https://github.com/json-path/JsonPath
     * @return Text value
     */
    public static String getAsStringByJsonPath(String json, String jsonPath) {
        return getAsStringByJsonPath(from(json, JsonNode.class), jsonPath);
    }

    /**
     * Check if field is empty
     *
     * @param jsonNode JsonNode
     * @param key      Field key
     * @return True if empty, false otherwise
     */
    public static Boolean isEmpty(JsonNode jsonNode, String key) {
        if (jsonNode == null) {
            return true;
        }
        try {
            JsonNode valueNode = jsonNode.get(key);
            if (null == valueNode) {
                return true;
            }
            return valueNode.isEmpty();
        } catch (Exception e) {
            throw new JSONException("jackson isEmpty error, json: {}, key: {}", jsonNode.toString(), key, e);
        }
    }

    /**
     * Get a field value as int from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @return Field value as int, 0 by default
     */
    public static int getAsInt(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return 0;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return 0;
            }
            return jsonNode.isInt() ? jsonNode.intValue() : Integer.parseInt(getAsString(jsonNode));
        } catch (Exception e) {
            throw new JSONException("jackson get int error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a field value as long from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @return Field value as long, 0L by default
     */
    public static long getAsLong(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return 0L;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return 0L;
            }
            return jsonNode.isLong() ? jsonNode.longValue() : Long.parseLong(getAsString(jsonNode));
        } catch (Exception e) {
            throw new JSONException("jackson get long error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a field value as double from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @return Field value as double, 0.0 by default
     */
    public static double getAsDouble(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return 0.0;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return 0.0;
            }
            return jsonNode.isDouble() ? jsonNode.doubleValue() : Double.parseDouble(getAsString(jsonNode));
        } catch (Exception e) {
            throw new JSONException("jackson get double error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a field value as BigInteger from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @return Field value as BigInteger, 0.00 by default
     */
    public static BigInteger getAsBigInteger(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return new BigInteger(String.valueOf(0.00));
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return new BigInteger(String.valueOf(0.00));
            }
            return jsonNode.isBigInteger() ? jsonNode.bigIntegerValue() : new BigInteger(getAsString(jsonNode));
        } catch (Exception e) {
            throw new JSONException("jackson get big integer error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a field value as BigDecimal from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @return Field value as BigDecimal, 0.00 by default
     */
    public static BigDecimal getAsBigDecimal(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return new BigDecimal("0.00");
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return new BigDecimal("0.00");
            }
            return jsonNode.isBigDecimal() ? jsonNode.decimalValue() : new BigDecimal(getAsString(jsonNode));
        } catch (Exception e) {
            throw new JSONException("jackson get big decimal error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a field value as boolean from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @return Field value as boolean, false by default
     */
    public static boolean getAsBoolean(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return false;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return false;
            }
            if (jsonNode.isBoolean()) {
                return jsonNode.booleanValue();
            } else {
                if (jsonNode.isTextual()) {
                    String textValue = jsonNode.textValue();
                    if ("1".equals(textValue)) {
                        return true;
                    } else {
                        return BooleanUtils.toBoolean(textValue);
                    }
                } else {//number
                    return BooleanUtils.toBoolean(jsonNode.intValue());
                }
            }
        } catch (Exception e) {
            throw new JSONException("jackson get boolean error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a field value as byte array from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @return Field value as byte array, null by default
     */
    public static byte[] getAsBytes(String json, String key) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return null;
            }
            return jsonNode.isBinary() ? jsonNode.binaryValue() : getAsString(jsonNode).getBytes();
        } catch (Exception e) {
            throw new JSONException("jackson get byte error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a field value as object from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @param type Target object type
     * @return Field value as object, null by default
     */
    public static <V> V getAsObject(String json, String key, Class<V> type) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return null;
            }
            if (type instanceof Class && (((Class<?>) type).isPrimitive() || ((Class<?>) type) == String.class)) {
                return type.cast(jsonNode.textValue());
            }
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            return from(getAsString(jsonNode), javaType);
        } catch (Exception e) {
            throw new JSONException("jackson get list error, json: {}, key: {}, type: {}", json, key, type, e);
        }
    }


    /**
     * Get a field value as List from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @param type List element type
     * @return Field value as List, empty list by default
     */
    public static <V> List<V> getAsList(String json, String key, Class<V> type) {
        if (StringUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        try {
            JsonNode jsonNode = getAsJsonObject(json, key);
            if (null == jsonNode) {
                return new ArrayList<>();
            }
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            return from(getAsString(jsonNode), collectionType);
        } catch (Exception e) {
            throw new JSONException("jackson get list error, json: {}, key: {}, type: {}", json, key, type, e);
        }
    }

    /**
     * Get a field as JsonNode from JSON
     *
     * @param json JSON string
     * @param key  Field key
     * @return Field value as JsonNode, null by default
     */
    public static JsonNode getAsJsonObject(String json, String key) {
        try {
            JsonNode node = mapper.readTree(json);
            if (null == node) {
                return null;
            }
            return node.get(key);
        } catch (IOException e) {
            throw new JSONException("jackson get object from json error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Add property to JSON
     *
     * @param json  JSON string
     * @param key   Property key
     * @param value Property value
     * @return Modified JSON string
     */
    public static <V> String add(String json, String key, V value) {
        try {
            JsonNode node = mapper.readTree(json);
            add(node, key, value);
            return node.toString();
        } catch (IOException e) {
            throw new JSONException("jackson add error, json: {}, key: {}, value: {}", json, key, value, e);
        }
    }

    /**
     * Add property to JsonNode
     *
     * @param jsonNode JsonNode to modify
     * @param key      Property key
     * @param value    Property value
     */
    private static <V> void add(JsonNode jsonNode, String key, V value) {
        if (value instanceof String) {
            ((ObjectNode) jsonNode).put(key, (String) value);
        } else if (value instanceof Short) {
            ((ObjectNode) jsonNode).put(key, (Short) value);
        } else if (value instanceof Integer) {
            ((ObjectNode) jsonNode).put(key, (Integer) value);
        } else if (value instanceof Long) {
            ((ObjectNode) jsonNode).put(key, (Long) value);
        } else if (value instanceof Float) {
            ((ObjectNode) jsonNode).put(key, (Float) value);
        } else if (value instanceof Double) {
            ((ObjectNode) jsonNode).put(key, (Double) value);
        } else if (value instanceof BigDecimal) {
            ((ObjectNode) jsonNode).put(key, (BigDecimal) value);
        } else if (value instanceof BigInteger) {
            ((ObjectNode) jsonNode).put(key, (BigInteger) value);
        } else if (value instanceof Boolean) {
            ((ObjectNode) jsonNode).put(key, (Boolean) value);
        } else if (value instanceof byte[]) {
            ((ObjectNode) jsonNode).put(key, (byte[]) value);
        } else {
            ((ObjectNode) jsonNode).put(key, to(value));
        }
    }

    /**
     * Remove property from JSON
     *
     * @param json JSON string
     * @param key  Property key to remove
     * @return Modified JSON string
     */
    public static String remove(String json, String key) {
        try {
            JsonNode node = mapper.readTree(json);
            ((ObjectNode) node).remove(key);
            return node.toString();
        } catch (IOException e) {
            throw new JSONException("jackson remove error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Update property in JSON
     *
     * @param json  JSON string
     * @param key   Property key to update
     * @param value New property value
     * @return Modified JSON string
     */
    public static <V> String update(String json, String key, V value) {
        try {
            JsonNode node = mapper.readTree(json);
            ((ObjectNode) node).remove(key);
            add(node, key, value);
            return node.toString();
        } catch (IOException e) {
            throw new JSONException("jackson update error, json: {}, key: {}, value: {}", json, key, value, e);
        }
    }

    /**
     * Format JSON (pretty print)
     *
     * @param json JSON string
     * @return Formatted JSON string
     */
    public static String format(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (IOException e) {
            throw new JSONException("jackson format json error, json: {}", json, e);
        }
    }


    /**
     * Check if string is valid JSON
     *
     * @param json String to check
     * @return True if valid JSON, false otherwise
     */
    public static boolean isJson(String json) {
        if (StringUtils.isBlank(json)) {
            return false;
        }
        try {
            mapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}