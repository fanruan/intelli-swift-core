package com.finebi.cube.security;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/7/31.
 *
 * @author Connery
 * @since 4.0
 */
public class CubeSecurityCheck {
    private String sumSavePath;
    private Map<String, Long> sumCheck;
    private CubeCRCCalculator cubeCRCCalculator;

    public CubeSecurityCheck(String sumSavePath) {
        this.sumSavePath = sumSavePath;
        sumCheck = new HashMap<String, Long>();
        cubeCRCCalculator = new CubeCRCCalculator();
        if (new File(this.sumSavePath).exists()) {
            initialSumCheck(sumSavePath);
        }
    }

    private void initialSumCheck(String resultPath) {
        String strValue = BIFileUtils.readFile(resultPath);
        try {
            JSONObject jsonObject = new JSONObject(strValue);
            sumCheck = json2map(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Long> json2map(JSONObject jsonObject) {
        Map<String, Long> mapResult = new HashMap<String, Long>();
        try {
            JSONArray content = jsonObject.getJSONArray("content");
            for (int i = 0; i < content.length(); i++) {
                JSONObject item = content.getJSONObject(i);
                mapResult.put(item.getString("path"), item.getLong("sum"));
            }
            return mapResult;
        } catch (JSONException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public Map<String, Long> compute(String cubePath) {
        cubeCRCCalculator.clear();
        cubeCRCCalculator.scanDir(new File(cubePath));
        return cubeCRCCalculator.getResult();
    }

    public boolean check(String cubePath) {
        Map<String, Long> currentResult = compute(cubePath);

        for (Map.Entry<String, Long> entry : sumCheck.entrySet()) {
            if (currentResult.containsKey(entry.getKey())) {
                if (currentResult.get(entry.getKey()).longValue() != entry.getValue().longValue()) {
                    BILogger.getLogger().info(entry.getKey());
                    return false;
                }
            }
        }
        return true;
    }

    public void useLeastSum() {
        sumCheck.clear();
        sumCheck = new HashMap<String, Long>(cubeCRCCalculator.getResult());
    }

    public void saveResult() {
        JSONObject jsonObject = map2json(sumCheck);
        BIFileUtils.writeFile(sumSavePath, jsonObject.toString());
    }

    public boolean removeResult() {
        File file = new File(sumSavePath);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    private JSONObject map2json(Map<String, Long> map) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            for (Map.Entry<String, Long> entity : map.entrySet()) {
                JSONObject item = new JSONObject();
                item.put("path", entity.getKey());
                item.put("sum", entity.getValue());
                array.put(item);
            }
            result.put("content", array);
        } catch (JSONException e) {
            throw BINonValueUtils.beyondControl(e);
        }
        return result;

    }

}
