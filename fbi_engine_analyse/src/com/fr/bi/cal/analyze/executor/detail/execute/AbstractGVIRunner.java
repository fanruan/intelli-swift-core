package com.fr.bi.cal.analyze.executor.detail.execute;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.analyze.executor.GVIRunner;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.stable.data.db.BIRowValue;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.bi.stable.utils.algorithem.BIComparatorUtils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * 根据gvi得出一张表的数据
 * Created by GUY on 2015/4/21.
 */
public abstract class AbstractGVIRunner implements GVIRunner {

    protected GroupValueIndex gvi;

    protected BIDetailWidget widget;
    protected ICubeDataLoader loader;
    protected transient BIDetailTarget[] viewDimension;
    protected transient String[] sortTargets;
    protected transient BusinessTable target;
    protected transient Map<String, TargetFilter> filterMap;
    protected BIUser biUser;
    public AbstractGVIRunner(GroupValueIndex gvi, BIDetailWidget widget, ICubeDataLoader loader, long userId) {
        biUser = new BIUser(userId);
        this.gvi = gvi;
        this.widget = widget;
        this.loader = loader;
        this.viewDimension = widget.getViewDimensions();
        this.sortTargets = widget.getSortTargets();
        this.filterMap = widget.getTargetFilterMap();
        this.target = widget.getTargetDimension();
    }

    protected class DetailSortCompactor implements Comparator<BIRowValue> {

        @Override
        public int compare(BIRowValue o1, BIRowValue o2) {
            for (int i = 0; i < sortTargets.length; i++) {
                BIDetailTarget target = BITravalUtils.getTargetByName(sortTargets[i], viewDimension);
                if (target == null) {
                    continue;
                }
                int sortIndex = getIndexById(sortTargets[i]);
                int c = target.getSort().getComparator().compare(o1.getValues()[sortIndex], o2.getValues()[sortIndex]);
                if (c != 0) {
                    return c;
                }
            }
            return ((Long) o1.getRow()).intValue() - ((Long) (o2.getRow())).intValue();
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }

    private int getIndexById(String dId) {
        int index = -1;
        for (int i = 0; i < viewDimension.length; i++) {
            if(BIComparatorUtils.isExactlyEquals(dId, viewDimension[i].getValue())) {
                index = i;
            }
        }
        return index;
    }

    protected void executeUntilCallOver(DetailParas paras, Map<String, Object> values, HashSet<String> caledTargets) {
        while (true) {
            Iterator<BIDetailTarget> it = paras.getCalculateList().iterator();
            boolean called = false;
            while (it.hasNext()) {
                BIDetailTarget calTarget = it.next();
                if (!caledTargets.contains(calTarget.getValue()) && calTarget.isReady4Calculate(values)) {
                    values.put(calTarget.getValue(), calTarget.createDetailValue(null, values, loader, biUser.getUserId()));
                    caledTargets.add(calTarget.getValue());
                    called = true;
                }
            }
            if (!called) {
                break;
            }
        }
    }
}