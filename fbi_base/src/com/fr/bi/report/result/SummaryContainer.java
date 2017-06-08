package com.fr.bi.report.result;


import com.fr.bi.report.key.TargetGettingKey;

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
    void setSummaryValue(TargetGettingKey key, Number value);


    Number getSummaryValue(TargetGettingKey key);

    Number[] getSummaryValue();

    void setSummaryValue(Number[] summaryValue);
}