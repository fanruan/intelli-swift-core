package com.fr.bi.sql.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.sql.analysis.Constants;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.data.source.AbstractCubeTableSource;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/4/12.
 */
public class AnalysisSQLTempTableSource extends AbstractCubeTableSource implements AnalysisSQLTableSource {

    private static final String UNSUPPORT = "Temp Source do not support";

    private List<AnalysisSQLTableSource> sourceList;

    public AnalysisSQLTempTableSource(List<AnalysisSQLTableSource> sourceList) {
        this.sourceList = sourceList;
    }

    @Override
    public List<AnalysisSQLSourceField> getFieldsList() {
        return new ArrayList<AnalysisSQLSourceField>();
    }

    @Override
    public String toSql() {
        throw new RuntimeException(UNSUPPORT);
    }

    @Override
    public void getSourceUsedAnalysisSQLSource(Set<AnalysisSQLTableSource> sources, Set<AnalysisSQLTableSource> helper) {

    }

    @Override
    public IPersistentTable getPersistentTable() {
        return  new PersistentTable(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
    }


    @Override
    public int getType() {
        return Constants.TABLE_TYPE.SQL_TEMP;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONArray ja = new JSONArray();
        for (AnalysisSQLTableSource source : this.sourceList){
            ja.put(source.createJSON());
        }
        JSONObject table = new JSONObject();
        table.put(Constants.ITEMS, ja);
        return table;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        throw new RuntimeException(UNSUPPORT);
    }
}
