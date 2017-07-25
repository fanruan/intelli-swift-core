package com.fr.bi.cal.analyze.cal.sssecret;


import com.fr.bi.cal.analyze.cal.index.loader.MetricGroupInfo;
import com.fr.bi.cal.analyze.cal.multithread.BIMultiThreadExecutor;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeCreator;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.cache.list.IntList;

import java.util.List;

/**
 * Created by Hiram on 2015/1/6.
 */
public interface IRootDimensionGroup {
    NoneDimensionGroup getRoot();

    /**
     * @param gv       表示一行的值
     * @param index    上一次游标的位置
     * @param deep     维度的层级
     * @param expander 展开信息
     * @param list     当前的游标
     */
    ReturnStatus getNext(GroupConnectionValue gv,
                         int[] index,
                         int deep,
                         NodeExpander expander,
                         IntList list);

    int[] getValueStartRow(Object[] value);

    Node getConstructedRoot();

    IRootDimensionGroup createClonedRoot();

    void checkStatus(BIMultiThreadExecutor executor);

    void checkMetricGroupInfo(NodeCreator nodeCreator, List<MetricGroupInfo> metricGroupInfos);
}