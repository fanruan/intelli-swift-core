package com.fr.bi.etl.analysis.data;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.AbstractCubeTableSource;
import com.finebi.cube.api.ICubeDataLoader;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by 小灰灰 on 2015/12/21.
 */
public class AnalysisBaseTableSource extends AbstractCubeTableSource implements AnalysisTableSource {
    private transient Map<Long, UserTableSource> userBaseTableMap = new ConcurrentHashMap<Long, UserTableSource>();
    @BICoreField
    protected BIWidget widget;

    private int etlType;


    public AnalysisBaseTableSource(BIWidget widget, int etlType) {
        this.widget = widget;
        this.etlType = etlType;
    }


    @Override
    public DBTable getDbTable() {
        if (dbTable == null) {
            dbTable = new DBTable(null, fetchObjectCore().getID().getIdentityValue(), null);
            for (BITargetAndDimension column : widget.getViewDimensions()){
                BIDataColumn c = column.getStatisticElement();
                dbTable.addColumn(new BIColumn(c.getFieldName(), c.getFieldType()));
            }
            for (BITargetAndDimension column : widget.getViewTargets()){
                BIDataColumn c = column.getStatisticElement();
                dbTable.addColumn(new BIColumn(c.getFieldName(), c.getFieldType()));
            }
        }
        return dbTable;
    }

    @Override
    public Set<Table> createTableKeys() {
        Set set = new HashSet<Table>();
        set.add(new BITable(fetchObjectCore().getIDValue()));
        return set;
    }

    @Override
    public int getType() {
        return Constants.TABLE_TYPE.BASE;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, DBField[] field, ICubeDataLoader loader) {
        return 0;
    }

    @Override
    public UserTableSource createUserTableSource(long userId) {
        UserTableSource source = userBaseTableMap.get(userId);
        if (source == null){
            synchronized (userBaseTableMap){
                UserTableSource tmp = userBaseTableMap.get(userId);
                if (tmp == null){
                    source = new UserBaseTableSource(widget, etlType, userId);
                    userBaseTableMap.put(userId, source);
                } else {
                    source = tmp;
                }
            }
        }
        return source;
    }
}