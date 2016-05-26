package com.fr.bi.cal.analyze.cal.result;

import com.fr.base.FRContext;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.utils.CubeBaseUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by 小灰灰 on 14-3-4.
 */
public class RootNodeChild extends Node {

    /**
     *
     */
    private static final long serialVersionUID = 2195477256564513817L;

    private volatile boolean calculated = false;

    private HashSet<TargetCalculator> keys = new HashSet<TargetCalculator>();

    public RootNodeChild(DimensionCalculator key, Object data) {
        super(key, data);
    }

    public RootNodeChild(DimensionCalculator key, Object data, GroupValueIndex gvi) {
        super(key, data);
        setGroupValueIndex(gvi);
    }

    public void finishCalculate() {
        calculated = true;
    }

    public void addCalculatedKeys(TargetCalculator key) {
        keys.add(key);
    }

    public boolean isKeyCalculated(TargetCalculator key) {
        return keys.contains(key);
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void prepareReCalculate() {
        calculated = false;
    }

    @Override
    public int getSummarySize() {
        return getParent().getSummarySize();
    }


    @Override
    public Number getSummaryValue(Object key) {
        return getParent().getSummaryValue(key);
    }

    public void addBaseSummaryData(TargetCalculator key, NodeExpander expander, ICubeDataLoader loader) {

        if (key == null) {
            return;
        }
        ICubeTableService si = loader.getTableIndex(key.createTableKey().getTableSource());
        key.calculateFilterIndex(loader);

        List threadList = new ArrayList();
        if (getParent().getSummaryValue(key) == null) {
            threadList.add(key.createSummaryCalculator(si, getParent()));
        }
        addBaseSummaryData(key, expander, this, threadList, si);

        try {
            CubeBaseUtils.invokeCalculatorThreads(threadList);
        } catch (InterruptedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }

        addCalculatedKeys(key);

    }


    public void addBaseSummaryData(TargetCalculator key, NodeExpander pexpander, Node node, List threadList, ICubeTableService si) {
        if (pexpander == null) {
            return;
        }
        for (int i = 0; i < node.getChildLength(); i++) {
            if (node.getChild(i).getSummaryValue(key) == null) {
                threadList.add(key.createSummaryCalculator(si, node.getChild(i)));
            }
            addBaseSummaryData(key, pexpander.getChildExpander(node.getChild(i).getShowValue()), node.getChild(i), threadList, si);
        }
    }

}