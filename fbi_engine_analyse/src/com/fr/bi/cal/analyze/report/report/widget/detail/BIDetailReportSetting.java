package com.fr.bi.cal.analyze.report.report.widget.detail;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;


/**
 * Created by GUY on 2015/4/9.
 */
public class BIDetailReportSetting implements BIDetailSetting {
    @BICoreField
    protected String[] view;

    private boolean freeze;

    private int number = 0;

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("view")) {
            JSONObject views = jo.getJSONObject("view");
            JSONObject dims = jo.getJSONObject("dimensions");
            try {
                JSONArray dimIds = views.getJSONArray(BIReportConstant.REGION.DIMENSION1);
                view = new String[dimIds.length()];
                for(int i = 0; i < dimIds.length(); i++){
                    view[i] = dimIds.getString(i);
                }
            } catch (Exception e){
                BILogger.getLogger().info(e.getMessage());
            }
            if (views.has("style")) {
                JSONObject style = views.getJSONObject("style");
                if (style.has("freeze")) {
                    freeze = style.optBoolean("freeze", false);
                }
                if (style.has("number")) {
                    number = style.optInt("number", 0);
                }
            }
        }
    }

    /**
     * 获取行内容
     *
     * @return
     */
    @Override
    public String[] getView() {
        if (view == null) {
            view = new String[0];
        }
        return view;
    }

    @Override
    public boolean isFreeze() {
        return freeze;
    }

    /**
     * 有无编号
     *
     * @return 编号
     */
    @Override
    public int isOrder() {
        return number;
    }

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }
}