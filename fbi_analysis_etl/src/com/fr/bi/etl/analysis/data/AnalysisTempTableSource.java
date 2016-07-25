package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.etl.analysis.Constants;
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
    public void getSourceUsedAnalysisETLSource(Set<AnalysisCubeTableSource> set) {
    }

    @Override
    public void refreshWidget() {

    }

    @Override
    public IPersistentTable getPersistentTable() {
        return  new PersistentTable(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
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
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        throw new RuntimeException(UNSUPPORT);
    }

    
}
