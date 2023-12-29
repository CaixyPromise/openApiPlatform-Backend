package com.caixy.project.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Json操作类
 *
 * @name: com.caixy.project.utils.JsonUtils
 * @author: CAIXYPROMISE
 * @since: 2023-12-29 19:49
 **/
public class JsonUtils
{
    private static final Gson gson = new Gson();

    public static HashMap<String, Object> jsonToMap(String json)
    {
        Type mapType = new TypeToken<HashMap<String, String>>() {}.getType();
        return gson.fromJson(json, mapType);
    }


    public static String mapToString(Map<?, ?> map)
    {
        return gson.toJson(map);
    }

    public static String objectToString(Object object)
    {
        return gson.toJson(object);
    }
}
