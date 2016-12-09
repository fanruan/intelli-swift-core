package com.fr.bi.cal.analyze.report.report.widget.table;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by GUY on 2015/4/9.
 */
public abstract class BIAbstractTableSetting implements BITableSetting {
    private static final long serialVersionUID = 4556313379761261578L;
    protected HashMap<String, String[]> groups_of_dimensions = new HashMap<String, String[]>();

    protected HashMap<String, String[]> groups_of_targets = new HashMap<String, String[]>();

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        JSONObject view = jo.getJSONObject("view");
        Iterator<String> it = view.keys();
        while (it.hasNext()) {
            addDimTar(it.next(), view);
        }
    }

    private void addDimTar(String type, JSONObject view) throws Exception {
        JSONArray ja = view.getJSONArray(type);
        if (Integer.parseInt(type) >= Integer.parseInt(BIReportConstant.REGION.DIMENSION1)
                && Integer.parseInt(type) < Integer.parseInt(BIReportConstant.REGION.TARGET1)) {
            groups_of_dimensions.put(type, BIJsonUtils.jsonArray2StringArray(ja));
            return;
        }
        groups_of_targets.put(type, BIJsonUtils.jsonArray2StringArray(ja));
    }

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }
}