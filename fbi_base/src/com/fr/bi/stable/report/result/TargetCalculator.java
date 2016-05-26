package com.fr.bi.stable.report.result;

import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.report.key.SummaryCalculator;
import com.fr.bi.stable.report.key.TargetGettingKey;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public interface TargetCalculator extends Serializable {


    /**
     * 计算
     *
     * @param cr   tableindex对象
     * @param node 节点
     */
    public void doCalculator(ICubeTableService cr, SummaryContainer node);

    public void doCalculator(ICubeTableService cr, SummaryContainer node, TargetGettingKey key);

    /**
     * 构建计算指标-
     *
     * @return 计算指标
     */
    public TargetCalculator[] createTargetCalculators();

    /**
     * 计算集合C的汇总值
     *
     * @param key 汇总的key 计算指标有区别
     * @param c   集合
     * @return 结果
     */
    public <T extends BINode> Double calculateChildNodes(TargetGettingKey key, Collection<T> c);


    /**
     * 计算一次集合C的汇总值
     *
     * @param key 汇总的key 计算指标有区别
     * @param c   集合
     * @return 结果
     */
    public <T extends BICrossNode> Double calculateChildNodesOnce(TargetGettingKey key, Collection<T> c);

    /**
     * 计算集合C的汇总值
     * key 为自己 计算指标有区别
     *
     * @param c 集合
     * @return 结果
     */
    public Double calculateChildNodes(Collection<BINode> c);

    /**
     * 先计算好过滤的index
     */
    public void calculateFilterIndex(ICubeDataLoader loader);

    public BITargetKey createTargetKey();

    public BusinessTable createTableKey();

    /**
     * 创建 SummaryCalculator
     *
     * @param cr   tableindex对象
     * @param node 节点
     * @return 创建的SummaryCalculator
     */
    public SummaryCalculator createSummaryCalculator(ICubeTableService cr,
                                                     SummaryContainer node);

    public String getName();

    public TargetGettingKey createTargetGettingKey();
}