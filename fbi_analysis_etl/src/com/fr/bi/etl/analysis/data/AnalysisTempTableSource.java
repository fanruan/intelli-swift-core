package com.fr.bi.etl.analysis.data;

import com.fr.bi.common.inter.Traversal;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.AbstractCubeTableSource;
import com.finebi.cube.api.ICubeDataLoader;

import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/4/12.
 */
public class AnalysisTempTableSource extends AbstractCubeTableSource implements AnalysisTableSource {

    private static final String UNSUPPORT = "Temp Source do not support";

    private List<AnalysisTableSource> sourceList;

    public AnalysisTempTableSource(List<AnalysisTableSource> sourceList) {
        this.sourceList = sourceList;
    }

    @Override
    public UserTableSource createUserTableSource(long userId) {
        throw new RuntimeException(UNSUPPORT);
    }

    @Override
    public DBTable getDbTable() {
        throw new RuntimeException(UNSUPPORT);
    }

    @Override
    public Set<Table> createTableKeys() {
        throw new RuntimeException(UNSUPPORT);
    }

    @Override
    public int getType() {
        return Constants.TABLE_TYPE.TEMP;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, DBField[] field, ICubeDataLoader loader) {
        throw new RuntimeException(UNSUPPORT);
    }
}
