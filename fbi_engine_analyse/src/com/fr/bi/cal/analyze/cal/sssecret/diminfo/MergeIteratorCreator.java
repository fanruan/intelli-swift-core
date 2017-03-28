package com.fr.bi.cal.analyze.cal.sssecret.diminfo;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.stable.engine.cal.DimensionIterator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by 小灰灰 on 2017/1/11.
 * 创建生成子节点的迭代器
 */
public interface MergeIteratorCreator {
    Iterator<MetricMergeResult> createIterator(DimensionIterator[] iterators, GroupValueIndex[] gvis, Comparator c, ICubeTableService[] tis, ICubeDataLoader loader);

    //三无迭代器才必须有个child，为了跟之前样子一样
    boolean isSimple();
}
