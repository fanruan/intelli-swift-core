package com.fr.bi.field.target.target;

import com.fr.bi.field.BIStyleTarget;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.json.JSONObject;

public abstract class BIAbstractTarget extends BIStyleTarget implements BITarget {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2627125367817748430L;
	private int summaryType;
    private int chartType;


    public void setSummaryType(int summaryType) {
        this.summaryType = summaryType;
    }

    public int getSuamryType() {
        return summaryType;
    }

    @Override
    public int getChartType() {
        return chartType;
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
            summaryType = groupJo.getInt("type");
        }
        if (jo.has("style_of_chart")) {
            JSONObject chartJo = jo.getJSONObject("style_of_chart");
            chartType = chartJo.getInt("type");
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