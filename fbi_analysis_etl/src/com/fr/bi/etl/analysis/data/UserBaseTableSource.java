package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.cal.analyze.report.report.widget.AbstractBIWidget;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.general.ComparatorUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/24.
 */
public class UserBaseTableSource extends AnalysisBaseTableSource implements UserCubeTableSource {
    private static final long serialVersionUID = 1151614375203260317L;
    private UserWidget userWidget;
    private long userId;
    @BIIgnoreField
    @BICoreField
    private List<TargetFilter> authorFilter;
    private AnalysisBaseTableSource parent;

    public UserBaseTableSource(AnalysisBaseTableSource parent, long userId) {
        super(parent.widget, parent.etlType, parent.fieldList, parent.name, parent.widgetTableId);
        this.userId = userId;
        this.parent = parent;
        this.userWidget = new UserWidget(parent.widget, userId);
    }


    /**
     * @return
     */
    @Override
    public long getUserId() {
        return userId;
    }


    @Override
    public boolean containsIDParentsWithMD5(String md5, long userId) {
        Set<AnalysisCubeTableSource> set = new HashSet<AnalysisCubeTableSource>();
        getSourceUsedAnalysisETLSource(set);
        for (AnalysisCubeTableSource source : set) {
            if (ComparatorUtils.equals(source.createUserTableSource(userId).fetchObjectCore().getIDValue(), md5)) {
                return true;
            }
        }
        return ComparatorUtils.equals(this.fetchObjectCore().getIDValue(), md5);
    }

    @Override
    public BICore fetchObjectCore() {
        authorFilter = ((AbstractBIWidget)widget).getAuthFilter(userId);
        return super.fetchObjectCore();
    }

    @Override
    public AnalysisCubeTableSource getAnalysisCubeTableSource() {
        return parent;
    }

    @Override
    public boolean isParentAvailable() {
        for (BITargetAndDimension dim : widget.getViewDimensions()) {
            if (dim != null && dim.getStatisticElement() != null && dim.createTableKey() != null) {
                if (!isAvailable(dim.createTableKey().getTableSource())) {
                    return false;
                }
                ;
            }
        }
        for (BITargetAndDimension target : widget.getViewTargets()) {
            if (target != null && target.getStatisticElement() != null && target.createTableKey() != null) {
                if (!isAvailable(target.createTableKey().getTableSource())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Set<CubeTableSource> getParentSource() {
        Set<CubeTableSource> set = new HashSet<CubeTableSource>();
        for (BITargetAndDimension dim : widget.getViewDimensions()) {
            if (dim.getStatisticElement() != null && dim.createTableKey() != null) {
                set.add(dim.createTableKey().getTableSource());
            }
        }
        for (BITargetAndDimension target : widget.getViewTargets()) {
            if (target.getStatisticElement() != null && target.createTableKey() != null) {
                set.add(target.createTableKey().getTableSource());
            }
        }
        return set;
    }


    private boolean isAvailable(CubeTableSource source) {
        if (source instanceof AnalysisCubeTableSource) {
            if (!BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().isAvailable((AnalysisCubeTableSource) source, new BIUser(userId))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getType() {
        return BIBaseConstant.TABLE_TYPE.USER_BASE;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        this.userWidget.clear();
        int total = 0;
        Iterator<List<List>> valueIterator = userWidget.getDataIterator();
        while (valueIterator.hasNext()) {
            List<List> values = valueIterator.next();
            for (int i = 0; i < values.size(); i++) {
                List value = values.get(i);
                for (int j = 0; j < value.size(); j++) {
                    travel.actionPerformed(new BIDataValue(i + total, j, value.get(j)));
                }
            }
            total += values.size();
        }
        this.userWidget.clear();
        return total;
    }

    /**
     * @return
     */
    @Override
    public Set<CubeTableSource> getSourceUsedBaseSource(Set<CubeTableSource> set, Set<CubeTableSource> helper) {
        if (helper.contains(parent)) {
            return set;
        }
        helper.add(parent);
        set.add(parent);
        for (BITargetAndDimension dim : widget.getViewDimensions()) {
            if (dim.getStatisticElement() != null && dim.createTableKey() != null && dim.createTableKey().getTableSource() != null) {
                dim.createTableKey().getTableSource().getSourceUsedBaseSource(set, helper);
            }
        }
        for (BITargetAndDimension target : widget.getViewTargets()) {
            if (target.getStatisticElement() != null && target.createTableKey() != null && target.createTableKey().getTableSource() != null) {
                target.createTableKey().getTableSource().getSourceUsedBaseSource(set, helper);
            }
        }
        return set;
    }

    @Override
    public UserCubeTableSource createUserTableSource(long userId) {
        return this;
    }

    @Override
    public long read4Part(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader, int start, int end) {
        List<List> values = userWidget.createData(start, end);
        for (int i = 0; i < values.size(); i++) {
            List value = values.get(i);
            for (int j = 0; j < value.size(); j++) {
                travel.actionPerformed(new BIDataValue(i, j, value.get(j)));
            }
        }
        return values.size();
    }

    @Override
    public void clearUserBaseTableMap() {
        super.clearUserBaseTableMap();
        userWidget.clear();
    }
}