package com.fr.bi.stable.report.update.operation;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kary on 2017/3/27.
 */
public class ReportKeyChangeOperation extends ReportCamelOperation {
    private static final String DEFAULT_FILE_NAME = "keys.json";
    private JSONObject keys;

    public ReportKeyChangeOperation(JSONObject keys) {
        this.keys = keys;
    }

    public ReportKeyChangeOperation() {
        try {
            if (null == keys) {
                keys = readKeyJson();
                formatValues();
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
    }

    protected String updateKey(String str) {
        try {
            String updatedKeys = null != keys ? keys.optString(str, str) : str;
            if (!ComparatorUtils.equals(updatedKeys, str)) {
                BILoggerFactory.getLogger(this.getClass()).info(BIStringUtils.append("compatibility warning! the parameter whose name is ", str, " should be transfered"));
            }

            return updatedKeys;
        } catch (Exception e) {
            BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
        }
        return str;
    }

    private void formatValues() throws JSONException {
        JSONObject finalJson = new JSONObject();
        Iterator iterator = keys.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String[] originalKeys = patterValues(keys.getString(key)).split("/");
            for (String oriKey : originalKeys) {
                finalJson.put(oriKey, key);
            }
        }
        keys = finalJson;
    }

    private String patterValues(String originalKey) {
        Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))");
        Matcher matcher = pattern.matcher(originalKey);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return originalKey;
    }

    private JSONObject readKeyJson() throws JSONException, ClassNotFoundException, IOException {
        StringBuffer keysStr = new StringBuffer();
        InputStream is = this.getClass().getResourceAsStream(DEFAULT_FILE_NAME);
        if (is == null) {
            BILoggerFactory.getLogger(this.getClass()).error("keys.json not existed in this path" + this.getClass().getResource("").toString());
            return new JSONObject();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s;
        while ((s = br.readLine()) != null) {
            keysStr.append(s);
        }

        BILoggerFactory.getLogger(this.getClass()).error(keysStr.toString());
        return new JSONObject(keysStr.toString());
    }
}