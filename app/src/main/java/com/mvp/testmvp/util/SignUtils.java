package com.mvp.testmvp.util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SignUtils {

    private static final String SLOT = "secret@sun.com";

    public static <T> List<T> copyIterator(Iterator<T> iter) {
        List<T> copy = new ArrayList<>();
        while (iter.hasNext())
            copy.add(iter.next());
        return copy;
    }

    public static JSONObject signJsonNotContainList(JSONObject jsonObject) {
        List<String> keyList = getSortedKeyList(jsonObject);
        String paramString = "";
        try {
            for (int i = 0; i < keyList.size(); i++) {
                String key = keyList.get(i);
                paramString += key + "=" + jsonObject.getString(key);
                if (i != keyList.size() - 1) {
                    paramString += "&";
                }
            }
            String secretKey = md5(paramString + SLOT);
            jsonObject.put("sign", secretKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static List<String> getSortedKeyList(JSONObject jsonObject) {
        List<String> keyList = copyIterator(jsonObject.keys());
        Collections.sort(keyList);
        return keyList;
    }

    public static JSONObject signJsonContainTwoLevelList(JSONObject jsonObject, String firstLevelList, String secondLevelList) {
        JSONArray jsonArray;
        try {
            JSONObject newSubJson = new JSONObject();
            jsonArray = jsonObject.getJSONArray(firstLevelList);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = (JSONObject) (jsonArray.get(i));
                String md5 = signListJsonMd5(json, secondLevelList);
                newSubJson.put(i + "", md5);
            }

            String newJsonMd5String = getJsonObjectSignForList(newSubJson);  // 数字大小排
            JSONObject newJson = jsonObject;
            newJson.remove(firstLevelList);
            newJson.put(firstLevelList, newJsonMd5String);
            String signString = getJsonObjectSign(newJson);
            jsonObject.put(firstLevelList, jsonArray);
            jsonObject.put("sign", signString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static String signListJsonMd5(JSONObject jsonObject, String listType) {
        JSONArray jsonArray;
        try {
            JSONObject newSubJson = new JSONObject();
            jsonArray = jsonObject.getJSONArray(listType);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = (JSONObject) (jsonArray.get(i));
                String md5Str = getJsonObjectSign(json);
                newSubJson.put(i + "", md5Str);
            }

            String newJsonMd5String = getJsonObjectSignForList(newSubJson);  // 数字大小排
            JSONObject newJson = jsonObject;
            newJson.remove(listType);
            newJson.put(listType, newJsonMd5String);
            String md5 = getJsonObjectSign(newJson);
            jsonObject.put(listType, jsonArray);

            return md5;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static JSONObject signJsonContainList(JSONObject jsonObject, String... listType) {
        try {
            String jsons = jsonObject.toString();
            JSONObject newJson = new JSONObject(jsons);
            JSONArray jsonArray;
            for (int i = 0; i < listType.length; i++) {
                jsonArray = jsonObject.getJSONArray(listType[i]);
                JSONObject newSubJson = new JSONObject();
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject json = (JSONObject) (jsonArray.get(j));
                    String md5Str = getJsonObjectSign(json);
                    newSubJson.put(j + "", md5Str);
                }
                String newJsonMd5String = getJsonObjectSignForList(newSubJson);  // 数字大小排
                newJson.remove(listType[i]);
                newJson.put(listType[i], newJsonMd5String);
            }
            String signString = getJsonObjectSign(newJson);
            jsonObject.put("sign", signString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    private static String getJsonObjectSign(JSONObject jsonObject) {
        List<String> keyList = SignUtils.getSortedKeyList(jsonObject);
        String secretKey = "";
        String paramString = "";
        try {
            for (int i = 0; i < keyList.size(); i++) {
                String key = keyList.get(i);
                paramString += key + "=" + jsonObject.getString(key);
                if (i != keyList.size() - 1) {
                    paramString += "&";
                }
            }
            secretKey = md5(paramString + SLOT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return secretKey;
    }


    private static String getJsonObjectSignForList(JSONObject jsonObject) {
        List<String> keyList = SignUtils.getSortedKeyListForList(jsonObject);
        String secretKey = "";
        String paramString = "";
        try {
            for (int i = 0; i < keyList.size(); i++) {
                String key = keyList.get(i);
                paramString += key + "=" + jsonObject.getString(key);
                if (i != keyList.size() - 1) {
                    paramString += "&";
                }
            }
            secretKey = md5(paramString + SLOT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return secretKey;
    }

    public static List<String> getSortedKeyListForList(JSONObject jsonObject) {
        List<String> keyList = copyIterator(jsonObject.keys());
        List<Integer> keyListInt = new ArrayList<>();
        for (int i = 0; i < keyList.size(); i++) {
            keyListInt.add(Integer.valueOf(keyList.get(i)));
        }
        Collections.sort(keyListInt);
        List<String> keyListResult = new ArrayList<>();
        for (int i = 0; i < keyList.size(); i++) {
            keyListResult.add(keyListInt.get(i) + "");
        }
        return keyListResult;
    }

    public static String md5(String paramString) {
        String returnStr;
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramString.getBytes());
            returnStr = byteToHexString(localMessageDigest.digest());
            return returnStr;
        } catch (Exception e) {
            return paramString;
        }
    }

    /**
     * 将指定byte数组转换成16进制字符串
     *
     * @param b
     * @return
     */
    public static String byteToHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
