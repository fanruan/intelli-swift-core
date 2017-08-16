package com.fr.bi.field.target.target;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.BIStyleTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.json.JSONObject;

public abstract class BIAbstractTarget extends BIStyleTarget implements BITarget {

    /**
     *
     */
    private static final long serialVersionUID = 2627125367817748430L;

    @BICoreField
    private int summaryType;

    private int chartType;

    private int summaryIndex;


    public int getSummaryType() {

        return summaryType;
    }

    public void setSummaryIndex(int summaryIndex) {

        this.summaryIndex = summaryIndex;
    }

    public int getSummaryIndex() {

        return summaryIndex;
    }

    @Override
    public int getChartType() {

        return chartType;
    }

    @Override
    public TargetGettingKey createTargetGettingKey() {

        return new TargetGettingKey(getSummaryIndex(), getName());
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {

        super.parseJSON(jo, userId);
        if (jo.has("group")) {
            JSONObject groupJo = jo.getJSONObject("group");
            if (groupJo.has("type")) {
                summaryType = groupJo.getInt("type");
            }
        }
        if (jo.has("styleOfChart")) {
            JSONObject chartJo = jo.getJSONObject("styleOfChart");
            if (chartJo.has("type")) {
                chartType = chartJo.getInt("type");
            }
        }
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof BIAbstractTarget)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BIAbstractTarget that = (BIAbstractTarget) o;

        if (chartType != that.chartType) {
            return false;
        }
        if (summaryType != that.chartType) {
            return false;
        }

        return true;
    }

    /**
     * hash值
     *
     * @return hash值
     */
    @Override
    public int hashCode() {

        int result = super.hashCode();
        result = 31 * result + summaryType;
        result = 31 * result + chartType;
        return result;
    }

}