package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.result.MemNode;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.SummaryContainer;
import com.fr.bi.stable.report.result.TargetCalculator;


/**
 * TODO 需要改成可以半路计算，提升即时计算性能
 * 即可以用之前已经计算好的结果继续计算
 *
 * @author Daniel
 *         分页机制，使用另外一个线程来判断计算当前已经计算了多少结果了 并取数
 */
public class NoneDimensionGroup implements Release {

    public final static NoneDimensionGroup NULL = new NoneDimensionGroup();
    protected ICubeDataLoader loader;

    protected NoneDimensionGroup() {
    }


    /**
     * Group计算的构造函数
     */
    protected NoneDimensionGroup(BusinessTable tableKey, GroupValueIndex gvi, ICubeDataLoader loader) {
        this.loader = loader;
    }


    public static NoneDimensionGroup createDimensionGroup(final BusinessTable tableKey, final GroupValueIndex gvi, final ICubeDataLoader loader) {


        return new NoneDimensionGroup(tableKey, gvi, loader);
    }
    /**
     * 暂时去掉多线程
     *
     * @param key
     * @return
     */
    public Number getSummaryValue(TargetCalculator key) {
        return new NodeSummaryCalculator(getLoader()).getNodeSummary(node, key);
    }


    public ISingleDimensionGroup createSingleDimensionGroup(DimensionCalculator[] pck, DimensionCalculator ck, Object[] data, int ckIndex, ICubeValueEntryGetter getter, boolean useRealData) {
        if (ckIndex == 0) {
            pck = null;
        }
        return SingleDimensionGroup.createDimensionGroup(tableKey, pck, ck, data, ckIndex, getter, node.getGroupValueIndex(), loader, useRealData);
    }

    /**
     * 计算根节点 第一个维度 用于分页
     *
     * @return 分页的node
     */
    public Node getRoot() {
        return node;
    }

    public LightNode getLightNode() {
        return node;
    }

    /**
     * 释放资源，之前需要释放的，现在暂时没有什么需要释放的
     */
    @Override
    public void clear() {
//        root.release();
    }

    public BusinessTable getTableKey() {
        return tableKey;
    }

    public ICubeDataLoader getLoader() {
        return loader;
    }

    public void releaseMemNode() {
        if (this.tempNode != null) {
            tempNode.release();
            tempNode = null;
        }
    }
}