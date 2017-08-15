package com.fr.bi.report.result;

/**
 * Created by andrew_asa on 2017/8/13.
 */
public interface GroupNodeSummaryContainer {

    /**
     * 设置汇总值
     *
     * @param index
     * @param value
     */
    void setSummaryValue(int index, Number value);


    Number getSummaryValue(int index);

    Number[] getSummaryValue();

    void setSummaryValue(Number[] summaryValue);
}
