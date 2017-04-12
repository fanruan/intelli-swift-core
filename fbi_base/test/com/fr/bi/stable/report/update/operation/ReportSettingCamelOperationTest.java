package com.fr.bi.stable.report.update.operation;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import junit.framework.TestCase;

/**
 * Created by kary on 2017/1/23.
 */
public class ReportSettingCamelOperationTest extends TestCase {
    public ReportCamelOperation operation;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        operation = new ReportCamelOperation();
    }

    public void testConvert() throws Exception {
        convertToCamel("table_ori_id", "tableOriId");
        convertToCamel("_table_id", "_tableId");
        convertToCamel("_src", "_src");
    }

    public void testRecursionListUpdate() throws Exception {
        String str = operation.recursionListUpdate(getJsonArray()).toString();
        assertFalse(str.contains("_"));
    }

    public void testRecursionMapUpdate() throws Exception {
        String str = operation.recursionMapUpdate(getJsonObject().toString()).toString();
        assertFalse(str.contains("_"));
    }


    public void convertToCamel(String oriKey, String expectedResult) {
        String newKey = operation.updateKey(oriKey);
        assertTrue(ComparatorUtils.equals(expectedResult, newKey));
    }

    private JSONObject getJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("primary_a", "11");
        JSONObject subJson = new JSONObject();
        subJson.put("sub_a", "21");
        subJson.put("sub_b", "22");
        jsonObject.put("primary_b", subJson);
        return jsonObject;
    }

    private JSONObject getJsonArray() throws JSONException {
        JSONObject subJson = new JSONObject();
        subJson.put("first_list", "00");
        JSONArray array = new JSONArray().put(subJson);
        JSONArray priList = new JSONArray().put(array);
        JSONObject finalArray = new JSONObject().put("pri_array_list", priList);
        return finalArray;
    }
}
