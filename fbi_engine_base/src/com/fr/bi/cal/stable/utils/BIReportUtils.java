package com.fr.bi.cal.stable.utils;

import com.fr.base.BaseUtils;
import com.fr.base.BaseXMLUtils;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.report.widget.BIDataColumnFactory;
import com.fr.bi.fs.BIDesignSetting;
import com.fr.bi.fs.BIFileRepository;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.CodeUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BIReportUtils {

    public static JSONObject getBIReportNodeJSON(BIReportNode node) throws Exception {
        String nodePath = CodeUtils.decodeText(node.getPath());
        /**
         * 兼容以前的绝对路径
         */
        String dirString = BIFileRepository.getInstance().getBIDirName(node.getUserId());
        //屏蔽系统文件路径问题
        int nodeLength = nodePath.lastIndexOf("\\" + dirString + "\\");
        nodeLength = nodeLength == -1 ? nodePath.lastIndexOf("/" + dirString + "/") : nodeLength;
        if (nodeLength > -1) {
            // +2 左右两个路径分隔符
            nodePath = nodePath.substring(nodeLength + dirString.length() + 2);
        }
        if (nodePath.startsWith("\\") || nodePath.startsWith("/")) {
            nodePath = nodePath.substring(1);
        }
        File parent = BIFileRepository.getInstance().getBIDirFile(node.getUserId());
        File file = new File(parent, nodePath);
        if (!file.exists()) {
            throw new RuntimeException("can't find file:" + node.getPath() + "! might be delete or move!");
        }
        BIDesignSetting setting = (BIDesignSetting) BaseXMLUtils.readXMLFile(
                BaseUtils.readResource(file.getAbsolutePath()),
                new BIDesignSetting());
        return setting.getReportSetting();
    }

    /**
     * daniel
     * FIXME 还需要考虑 控件和指标过滤
     *
     * @param widget
     * @return
     * @throws JSONException
     */
    private static JSONArray getAllFields(JSONObject widget) throws JSONException {
        JSONArray res = new JSONArray();
        if (widget.has("dimensions")) {
            JSONObject ja = widget.getJSONObject("dimensions");
            Iterator it = ja.keys();
            while (it.hasNext()){
                res.put(ja.get(it.next().toString()));
            }
        }
        return res;
    }

    public static Set<BIField> getUsedFieldByReportNode(BIReportNode node, long userId) throws Exception {
        JSONObject reportSetting = getBIReportNodeJSON(node);
        JSONObject widgets = reportSetting.getJSONObject("widgets");
        Set<BIField> fields = new HashSet<BIField>();
        Iterator it = widgets.keys();
        while (it.hasNext()){
            JSONObject widget = widgets.getJSONObject(it.next().toString());
            JSONArray targets = getAllFields(widget);
            for (int j = 0; j < targets.length(); j++) {
                JSONObject target = targets.getJSONObject(j);
                if (target.has(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT)) {
                    JSONObject fieldJo = target.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
                    BIField field = BIDataColumnFactory.createBIDataColumnByFieldID(fieldJo.getString("field_id"), new BIUser(userId));
                    fields.add(field);
                }
            }
        }
        return fields;
    }
}