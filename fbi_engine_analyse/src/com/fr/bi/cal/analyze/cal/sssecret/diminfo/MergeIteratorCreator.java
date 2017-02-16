package com.fr.bi.cal.analyze.cal.sssecret.diminfo;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.sssecret.MetricMergeResult;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/1/11.
 */
public interface MergeIteratorCreator {
    Iterator<MetricMergeResult> createIterator(Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators, GroupValueIndex[] gvis, Comparator c, ICubeTableService[] tis, ICubeDataLoader loader);

    boolean isSimple();
}
