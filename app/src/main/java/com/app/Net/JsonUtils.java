package com.app.Net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static List<JSONObject> parseJsonArray(String jsonStr) {
        List<JSONObject> objListData = new ArrayList<JSONObject>();
        if (!jsonStr.equals("")) {
            try {
                JSONArray jsonObjs = new JSONArray(jsonStr);
                int count = jsonObjs.length();
                for (int i = 0; i < count; i++) {
                    objListData.add(jsonObjs.getJSONObject(i));
                }
                return objListData;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static List<JSONObject> parseKeyJsonArray(String jsonStr, String key) {
        try {
            JSONArray jsonObjs = new JSONObject(jsonStr).getJSONArray(key);
            return parseJsonArray(jsonObjs.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static JSONObject parseSingle(String jsonStr, String key) {
        try {
            return new JSONObject(jsonStr).getJSONObject(key);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject parseSingle(String jsonStr) {
        try {
            return new JSONObject(jsonStr);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
