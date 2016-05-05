package com.fr.bi.cal.analyze.cal.sssecret;

import java.util.ArrayList;
import java.util.List;

import com.fr.base.FRContext;
import com.fr.bi.cal.analyze.cal.Executor.ExecutorPartner;
import com.fr.bi.cal.analyze.cal.result.NewRootNodeChild;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.store.GroupKey;
import com.fr.bi.cal.analyze.exception.TooManySummaryException;
import com.fr.bi.field.dimension.calculator.CombinationDateDimensionCalculator;
import com.fr.bi.field.dimension.calculator.CombinationDimensionCalculator;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.SummaryCalculator;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.common.inter.Release;


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
    protected Table tableKey;

    protected ICubeDataLoader loader;

    protected volatile boolean isPageFinished = false;


    protected NoneDimensionGroup() {
    }


    /**
     * Group计算的构造函数
     */
    protected NoneDimensionGroup(Table tableKey, GroupValueIndex gvi, ICubeDataLoader loader) {
        this.loader = loader;
        this.tableKey = tableKey;
        initRoot(gvi);
    }

    public static NoneDimensionGroup createDimensionGroup(final Table tableKey, final GroupValueIndex gvi, final ICubeDataLoader loader) {


        return new NoneDimensionGroup(tableKey, gvi, loader);
    }

    protected void initRoot(GroupValueIndex gvi) {
        node = new Node(null, null);
        node.setGroupValueIndex(gvi);
        isPageFinished = true;
    }

    public List<SummaryCalculator> createCalculatorList(TargetCalculator key) {
        List<SummaryCalculator> calList = new ArrayList<SummaryCalculator>();
        try {
            if (key == null) {
                return calList;
            }
            ICubeTableService summaryIndex = getLoader().getTableIndex(key.createTableKey());
            if (node.getSummaryValue(key) == null) {
                calList.add(key.createSummaryCalculator(summaryIndex, node));
            }
        } catch (TooManySummaryException e) {
        } catch (Exception e) {
            FRContext.getLogger().error(e.getMessage(), e);
        } finally {
        }
        return calList;
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


    public ISingleDimensionGroup createSingleDimensionGroup(DimensionCalculator[] pck, int[] pckindex, DimensionCalculator ck, Object[] data, int ckIndex, boolean useRealData) {
        if (ck instanceof CombinationDimensionCalculator || ck instanceof CombinationDateDimensionCalculator) {
            return ReverseSingleDimensionGroup.createDimensionGroup(tableKey, pck, pckindex, ck, data, ckIndex, node.getGroupValueIndex(), loader, useRealData);
        }

        return SingleDimensionGroup.createDimensionGroup(tableKey, pck, pckindex, ck, data, ckIndex, node.getGroupValueIndex(), loader, useRealData);
    }

    public GroupKey createSingleDimensionGroupKey(DimensionCalculator ck, boolean useRealData) {
        return SingleDimensionGroup.createGroupKey(tableKey, ck, node.getGroupValueIndex(), useRealData);
    }

    public SingleDimensionGroup createNoneTargetSingleDimensionGroup(DimensionCalculator[] pck, int[] pckindex, DimensionCalculator ck, Object[] data, int ckIndex, GroupValueIndex gvi, boolean useRealData) {
        if (ck instanceof CombinationDimensionCalculator || ck instanceof CombinationDateDimensionCalculator) {
            return ReverseSingleDimensionGroup.createDimensionGroup(tableKey, pck, pckindex, ck, data, ckIndex, gvi, loader, useRealData);
        }
        return SingleDimensionGroup.createDimensionGroup(tableKey, pck, pckindex, ck, data, ckIndex, gvi, loader, useRealData);
    }


    /**
     * 计算根节点 第一个维度 用于分页
     *
     * @param key 指标
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
    public void releaseResource() {
//        root.release();
    }

    public int getCurrentTotalRow() {
        return 1;
    }

    public boolean isPageFinished() {
        return isPageFinished;
    }

    public Table getTableKey() {
        return tableKey;
    }

    public ICubeDataLoader getLoader() {
        return loader;
    }
}