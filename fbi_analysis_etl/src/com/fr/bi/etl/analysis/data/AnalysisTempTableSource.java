package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.source.AbstractCubeTableSource;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/4/12.
 */
public class AnalysisTempTableSource extends AbstractCubeTableSource implements AnalysisCubeTableSource {

    private static final String UNSUPPORT = "Temp Source do not support";

    private List<AnalysisCubeTableSource> sourceList;

    public AnalysisTempTableSource(List<AnalysisCubeTableSource> sourceList) {
        this.sourceList = sourceList;
    }

    @Override
    public UserCubeTableSource createUserTableSource(long userId) {
        throw new RuntimeException(UNSUPPORT);
    }

    @Override
    public List<AnalysisETLSourceField> getFieldsList() {
        return new ArrayList<AnalysisETLSourceField>();
    }

    @Override
    public IPersistentTable getPersistentTable() {
        throw new RuntimeException(UNSUPPORT);
    }


    @Override
    public int getType() {
        return Constants.TABLE_TYPE.TEMP;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONArray ja = new JSONArray();
        for (AnalysisCubeTableSource source : this.sourceList){
            ja.put(source.createJSON());
        }
        JSONObject table = new JSONObject();
        table.put(Constants.ITEMS, ja);
        return table;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, CubeFieldSource[] field, ICubeDataLoader loader) {
        throw new RuntimeException(UNSUPPORT);
    }
}
