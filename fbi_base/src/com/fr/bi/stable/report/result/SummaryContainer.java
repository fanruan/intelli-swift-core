package com.fr.bi.stable.report.result;


import java.util.Map;

/**
 * Created by 小灰灰 on 2014/4/2.
 * 管理汇总的接口
 */
public interface SummaryContainer {
    /**
     * 设置汇总值
     *
     * @param key
     * @param value
     */
    void setSummaryValue(Object key, Object value);


    public Number getSummaryValue(Object key);

    Map getSummaryValue();

    void setSummaryValue(Map summaryValue);
}