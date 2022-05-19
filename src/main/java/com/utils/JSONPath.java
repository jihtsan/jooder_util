package com.utils;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;

import java.util.List;
import java.util.Optional;

public class JSONPath<T> {
    String json;
    String jsonPath;
    String resultStr;
    Class<T> clazz;

    public JSONPath(Object json, String jsonPath, Class<T> clazz) {
        if (json instanceof String) {
            this.json = (String) json;
        } else {
            this.json = new Gson().toJson(json);
        }

        this.jsonPath = jsonPath;
        this.clazz = clazz;
        parse();
    }

    public static <T> Optional<T> getValue(Object json, String jsonPath, Class<T> clazz) {
        JSONPath<T> res = new JSONPath<T>(json, jsonPath, clazz);
        if (String.class.equals(clazz)) {
            return Optional.ofNullable((T) res.resultStr);
        }
        return Optional.ofNullable((T) new Gson().fromJson(res.resultStr, clazz));
    }

    public static <T> Optional<List<T>> getListValue(String json, String jsonPath, Class<T> clazz) {
        JSONPath<T> res = new JSONPath<T>(json, jsonPath, clazz);
        return Optional.ofNullable((List<T>) new Gson().fromJson(res.resultStr, clazz));
    }

    public void parse() {
        if (jsonPath == null || "".equals(jsonPath)) {
            this.resultStr = this.json;
        } else {
            this.resultStr = JsonPath.read(json, jsonPath).toString();
        }
    }
}


