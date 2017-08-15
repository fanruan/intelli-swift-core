package com.fr.bi.report.result;

import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by andrew_asa on 2017/8/8.
 * Gvi容器
 */
public interface GviContainer {

    /**
     * 设置某个计算指标的gvi
     *
     * @param key
     * @param gvi
     */
    void setTargetIndex(TargetGettingKey key, GroupValueIndex gvi);

    /**
     * 获取某个计算指标的gvi
     *
     * @param key
     * @return
     */
    GroupValueIndex getTargetIndex(TargetGettingKey key);

}
