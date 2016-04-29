package com.fr.bi.stable.report.result;


import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;

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

    GroupValueIndex getIndex4Cal();


    GroupValueIndex getIndex4CalByTargetKey(TargetGettingKey key);

    Number getSummaryValue(Object key);
}