package com.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Joo00der
 * @desc FastJson 加强版工具
 * @date 2021/3/31
 */
public class JSONUtils {
    public static final String NUMBER = "Number";
    public static final String ARRAY = "Array";
    public static final String STRING = "String";


    public static String getNumberStrWithPath(JSONObject target,String path){
        return getNumberStrWithPath(target, path, "");
    }

    public static String getNumberStrWithPath(JSONObject target,String path,String defaultVal){
       BigDecimal data =  getNumberWithPath(target,path,null);
       return Objects.isNull(data) ?defaultVal:data.toPlainString();
    }

    public static BigDecimal getNumberWithPath(JSONObject target,String path,BigDecimal defaultVal){
        String obj = getStringWithPath(target, path, null);
        return Objects.isNull(obj) ? defaultVal : new BigDecimal(obj);
    }

    public static JSONArray getJSONArrayWithPath(JSONObject target, String path) {
        return getJSONArrayWithPath(target, path, new JSONArray());
    }

    public static JSONArray getJSONArrayWithPath(JSONObject target, String path, JSONArray defaultVal) {
        Object obj = getObjectWithPath(target, path, null);
        return Objects.isNull(obj) ? defaultVal : JSON.parseArray(JSON.toJSONString(obj));
    }

    public static JSONObject getJSONObjectWithPath(JSONObject target, String path) {
        return getJSONObjectWithPath(target, path, new JSONObject());
    }

    public static JSONObject getJSONObjectWithPath(JSONObject target, String path, JSONObject defaultVal) {
        Object obj = getObjectWithPath(target, path, null);
        return Objects.isNull(obj) ? defaultVal : JSON.parseObject(JSON.toJSONString(obj));
    }

    public static String getStringWithPath(JSONObject target, String path) {
        return getStringWithPath(target, path, "");
    }

    public static String getStringWithPath(JSONObject target, String path, String defaultVal) {
        Object obj = getObjectWithPath(target, path, null);
        return Objects.isNull(obj) ? defaultVal : obj.toString();
    }

    public static Object getObjectWithPath(Object obj, String path, Object defVal) {
        if (Objects.isNull(obj)) return defVal;
        Object res = JSONPath.eval(obj, path);
        if (Objects.isNull(res)) return defVal;
        return res;
    }
}
