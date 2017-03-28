package com.fr.bi.stable.report.update.operation;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kary on 2017/3/27.
 */
public class ReportKeyChangeOperation extends ReportCamelOperation {
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
            return keys.has(str) ? keys.getString(str) : str;
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

    private JSONObject readKeyJson() throws JSONException, ClassNotFoundException, FileNotFoundException {
        String path = null;
        try {
            path = FRContext.getCurrentEnv().getPath();
        } catch (Exception e) {
            throw new FileNotFoundException();
        }
        if (null!=path) {
            List<String> files = BIFileUtils.getListFiles(path, "json", true);
            for (String filePath : files) {
                if (filePath.endsWith("keys.json")) {
                    String s = BIFileUtils.readFile(filePath);
                    return new JSONObject(s);
                }
            }
        }
        throw new ClassNotFoundException();
    }
}
