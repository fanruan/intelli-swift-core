package com.fr.bi.base.provider;

import com.fr.json.JSONObject;


public interface ParseJSONWithUID {
    /**
     * 将JSON对象转换成java对象
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception
     */
    public void parseJSON(JSONObject jo, long userId) throws Exception;
}