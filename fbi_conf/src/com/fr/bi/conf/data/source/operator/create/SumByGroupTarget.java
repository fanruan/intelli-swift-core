package com.fr.bi.conf.data.source.operator.create;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.TraversalAction;
import com.fr.bi.stable.utils.BIIDUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * Created by 小灰灰 on 2015/7/9.
 */
public class SumByGroupTarget implements JSONTransform {
    private int sumType;
    private String name;

    @Override
    public String toString() {
        return "SumByGroupTarget{" +
                "sumType=" + sumType +
                ", name='" + name + '\'' +
                ", nameText='" + nameText + '\'' +
                '}';
    }

    private String nameText;

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        if (jsonObject.has("name")) {
            this.nameText = jsonObject.optString("name");
        }
        if(jsonObject.has("group")){
            JSONObject jo = jsonObject.getJSONObject("group");
            this.sumType = jo.optInt("type");
        }
        if(jsonObject.has("_src")){
            JSONObject jo = jsonObject.optJSONObject("_src");
            if (jo.has("field_name")){
                this.name = jo.getString("field_name");
            }
        }
    }

    public Object getSumValue(ICubeTableService ti, GroupValueIndex gvi) {
        switch (sumType) {
            case BIReportConstant.SUMMARY_TYPE.AVG:
                return ti.getSUMValue(gvi, new IndexKey(name)) / gvi.getRowsCountWithData();
            case BIReportConstant.SUMMARY_TYPE.MAX:
                return ti.getMAXValue(gvi, new IndexKey(name));
            case BIReportConstant.SUMMARY_TYPE.MIN:
                return ti.getMINValue(gvi, new IndexKey(name));
            case BIReportConstant.SUMMARY_TYPE.COUNT:
                return ti.getDistinctCountValue(gvi, new IndexKey(name));
            case BIReportConstant.SUMMARY_TYPE.SUM:
                return ti.getSUMValue(gvi, new IndexKey(name));
            case BIReportConstant.SUMMARY_TYPE.APPEND:
                return getAppendString(ti, gvi);
            case BIReportConstant.SUMMARY_TYPE.RECORD_COUNT:
                return (double)gvi.getRowsCountWithData();
        }
        return null;
    }

    private String getAppendString(final ICubeTableService ti, GroupValueIndex gvi) {
        final StringBuffer sb = new StringBuffer();
        gvi.Traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int[] rowIndices) {
                for (int i = 0; i < rowIndices.length; i++) {
                    if (i != 0) {
                        sb.append("/");
                    }
                    sb.append(ti.getRow(new IndexKey(name), rowIndices[i]));
                }
            }
        });
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public String getNameText() {
        return nameText;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSumType() {
        return sumType;
    }

    public void setSumType(int sumType) {
        this.sumType = sumType;
    }

    public int getColumnType(){
        return getSumType() == BIReportConstant.SUMMARY_TYPE.APPEND ? DBConstant.COLUMN.STRING :DBConstant.COLUMN.NUMBER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SumByGroupTarget)) {
            return false;
        }

        SumByGroupTarget that = (SumByGroupTarget) o;

        if (sumType != that.sumType) {
            return false;
        }
        if (name != null ? !ComparatorUtils.equals(name, that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = sumType;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}