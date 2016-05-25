package com.fr.bi.field.dimension.filter.general;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.field.dimension.filter.AbstractDimensionFilter;
import com.fr.bi.field.dimension.filter.DimensionFilterFactory;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GeneralDimensionFilter extends AbstractDimensionFilter {
    @BICoreField
    protected DimensionFilter[] childs;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        if (jo.has("filter_value")) {
            JSONArray ja = jo.getJSONArray("filter_value");
            childs = new DimensionFilter[ja.length()];
            for (int i = 0, len = childs.length; i < len; i++) {
                childs[i] = DimensionFilterFactory.parseFilter(ja.getJSONObject(i), userId);
            }
        }
    }

    @Override
    public List<String> getUsedTargets() {
        List<String> usedList = new ArrayList<String>();
        if (childs != null) {
            for (int i = 0, len = childs.length; i < len; i++) {
                if (childs[i] != null) {
                    usedList.addAll(childs[i].getUsedTargets());
                }
            }
        }
        return usedList;
    }

    /**
     * 创建json
     *
     * @return json对象
     * @throws Exception 报错
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();

        JSONArray childJa = new JSONArray();
        for (int i = 0, len = childs.length; i < len; i++) {
            childJa.put(childs[i].createJSON());
        }
        jo.put("filter_value", childJa);
        return jo;
    }


    @Override
    public boolean needParentRelation() {
        for (DimensionFilter filter : childs) {
            if (filter.needParentRelation()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GeneralDimensionFilter)) {
            return false;
        }

        GeneralDimensionFilter that = (GeneralDimensionFilter) o;

        if (!ComparatorUtils.equals(childs, that.childs)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return childs != null ? Arrays.hashCode(childs) : 0;
    }
}