package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by zihai on 2016/5/11.
 */
public class TableMergeOperator extends AbstractCreateTableETLOperator{
    private static final String XML_TAG = "AbstractCreateTableETLOperator";
    private static final int ETL_UNION_STYLE = 5;
    private int mergeType;

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    @Override
    public DBTable getBITable(DBTable[] tables) {
        return createOperatorByMergeType().getBITable(tables);
    }

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<ITableSource> parents, ICubeDataLoader loader) {
        return createOperatorByMergeType().writeSimpleIndex(travel, parents, loader);
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        return createOperatorByMergeType().writePartIndex(travel, parents, loader, startCol, start, end);
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("mergeType", this.mergeType);
        return jo;
    }

    private IETLOperator createOperatorByMergeType(){
        return new TableJoinOperator();
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        this.mergeType = jo.getInt("mergeType");
    }
}
