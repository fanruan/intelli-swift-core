package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.Executor.ExecutorPartner;
import com.fr.bi.cal.analyze.cal.result.MemNode;
import com.fr.bi.cal.analyze.cal.result.NewRootNodeChild;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.common.inter.Release;
import com.fr.bi.field.dimension.calculator.CombinationDateDimensionCalculator;
import com.fr.bi.field.dimension.calculator.CombinationDimensionCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.AllShowRoaringGroupValueIndex;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.TargetCalculator;


/**
 * TODO 需要改成可以半路计算，提升即时计算性能
 * 即可以用之前已经计算好的结果继续计算
 *
 * @author Daniel
 *         分页机制，使用另外一个线程来判断计算当前已经计算了多少结果了 并取数
 */
public class NoneDimensionGroup extends ExecutorPartner<NewRootNodeChild> implements Release {

    public final static NoneDimensionGroup NULL = new NoneDimensionGroup();
    protected volatile Node node;

    //当前计算的那个表的指标
    protected BusinessTable tableKey;

    protected ICubeDataLoader loader;

    protected volatile boolean isPageFinished = false;

    private boolean needAllCalculate = false;

    private  MemNode tempNode;


    protected NoneDimensionGroup() {
    }


    /**
     * Group计算的构造函数
     */
    protected NoneDimensionGroup(BusinessTable tableKey, GroupValueIndex gvi, ICubeDataLoader loader) {
        this.loader = loader;
        this.tableKey = tableKey;
        initRoot(gvi);
    }

    protected NoneDimensionGroup(BusinessTable tableKey, MemNode node, ICubeDataLoader loader) {
        this.loader = loader;
        this.tableKey = tableKey;
        this.tempNode = node;
        initRoot(node.getGroupValueIndex());
    }

    protected NoneDimensionGroup(BusinessTable tableKey, GroupValueIndex gvi, ICubeDataLoader loader, boolean needAllCalculate) {
        this.tableKey = tableKey;
        this.loader = loader;
        this.needAllCalculate = needAllCalculate;
        initRoot(gvi);
    }

    public static NoneDimensionGroup createDimensionGroup(final BusinessTable tableKey, final GroupValueIndex gvi, final ICubeDataLoader loader) {


        return new NoneDimensionGroup(tableKey, gvi, loader);
    }

    public static NoneDimensionGroup createDimensionGroup(final BusinessTable tableKey, MemNode node, final ICubeDataLoader loader) {


        return new NoneDimensionGroup(tableKey, node, loader);
    }


    protected void initRoot(GroupValueIndex gvi) {
        node = new Node(null, null);
        node.setGroupValueIndex(gvi);
        isPageFinished = true;
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


    public ISingleDimensionGroup createSingleDimensionGroup(DimensionCalculator[] pck, DimensionCalculator ck, Object[] data, int ckIndex, boolean useRealData) {
        if(ckIndex == 0){
            pck = null;
        }
        if (ck instanceof CombinationDimensionCalculator || ck instanceof CombinationDateDimensionCalculator) {
            return ReverseSingleDimensionGroup.createDimensionGroup(tableKey, pck, ck, data, ckIndex, node.getGroupValueIndex(), loader, useRealData);
        }
        return SingleDimensionGroup.createDimensionGroup(tableKey, pck, ck, data, ckIndex, node.getGroupValueIndex(), loader, useRealData);
    }

    public ISingleDimensionGroup createNoneTargetSingleDimensionGroup(DimensionCalculator[] pck, DimensionCalculator ck, Object[] data, int ckIndex, GroupValueIndex gvi, boolean useRealData) {
        if(ckIndex == 0){
            pck = null;
        }
        if (ck instanceof CombinationDimensionCalculator || ck instanceof CombinationDateDimensionCalculator) {
            return ReverseSingleDimensionGroup.createDimensionGroup(tableKey, pck, ck, data, ckIndex, gvi, loader, useRealData);
        }
        return SingleDimensionGroup.createDimensionGroup(tableKey, pck, ck, data, ckIndex, gvi, loader, useRealData);
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

    public int getCurrentTotalRow() {
        return 1;
    }

    public boolean isPageFinished() {
        return isPageFinished;
    }

    public BusinessTable getTableKey() {
        return tableKey;
    }

    public ICubeDataLoader getLoader() {
        return loader;
    }

    public boolean isNeedAllCalculate() {
        return needAllCalculate;
    }

    public void setNeedAllCalculate(boolean needAllCalculate) {
        this.needAllCalculate = needAllCalculate;
    }

    /**
     * 过滤掉自定义分组,自定义排序
     * @param needAllCal
     * @param dcs
     * @return
     */
    private boolean judgeNeedAllCal(boolean needAllCal, DimensionCalculator[] dcs){
        if (getRoot().getGroupValueIndex() instanceof AllShowRoaringGroupValueIndex){
            return false;
        }
        if(!needAllCal){
            return false;
        }
        for (DimensionCalculator d : dcs){
            if(d.getSortType() == BIReportConstant.SORT.CUSTOM
                    || d.getGroup().getType() != BIReportConstant.GROUP.ID_GROUP
                    || d.getGroup().getType() != BIReportConstant.GROUP.NO_GROUP){
                return false;
            }
        }
        return true;
    }

    public void releaseMemNode(){
        if (this.tempNode != null){
            tempNode.release();
            tempNode = null;
        }
    }
}