package cn.hahoo.boilerplate.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr.isEmpty()) return null;

        try {
            return mapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> toObjectList(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr.isEmpty()) return null;

        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, clazz);

        try {
            // return mapper.readValue(jsonStr, new TypeReference<List<T>>(){});
            return mapper.readValue(jsonStr, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
