package com.fr.bi.etl.analysis.data;

import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.AbstractETLTableSource;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.finebi.cube.api.ICubeDataLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2015/12/14.
 */
public class AnalysisETLTableSource extends AbstractETLTableSource<IETLOperator, AnalysisTableSource> implements AnalysisTableSource{

    private transient Map<Long, UserTableSource> userBaseTableMap = new ConcurrentHashMap<Long, UserTableSource>();

    private int invalidIndex;

    public void setInvalidIndex(int invalidIndex) {
        this.invalidIndex = invalidIndex;
    }

    @Override
    public int getType() {
        return Constants.TABLE_TYPE.ETL;
    }

    /**
     * 写简单索引
     *
     * @param travel
     * @param field
     * @param loader
     * @return
     */
    @Override
    public long read(Traversal<BIDataValue> travel, DBField[] field, ICubeDataLoader loader) {
        return 0;
    }

    public AnalysisETLTableSource() {
    }

    @Override
    public UserTableSource createUserTableSource(long userId) {
        UserTableSource source = userBaseTableMap.get(userId);
        if (source == null){
            synchronized (userBaseTableMap){
                UserTableSource tmp = userBaseTableMap.get(userId);
                if (tmp == null){
                    List<UserTableSource> parents = new ArrayList<UserTableSource>();
                    for (AnalysisTableSource parent : getParents()){
                        parents.add(parent.createUserTableSource(userId));
                    }
                    source = new UserETLTableSource(getETLOperators(), parents, userId);
                    userBaseTableMap.put(userId, source);
                } else {
                    source = tmp;
                }
            }
        }
        return source;
    }

}