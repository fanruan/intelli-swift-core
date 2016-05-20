package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/5/12.
 */
public class UsePartOperator extends AbstractCreateTableETLOperator {

    public static final String XML_TAG = "UsePartOperator";
    private static final int NOT_CONTAINS = 2;
    @BICoreField
    private List<String> uselessFields = new ArrayList<String>();


    public UsePartOperator(long userId) {
        super(userId);
    }

    public UsePartOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();

        for (String s : uselessFields) {
            ja.put(s);
        }
        jo.put("value", ja);
        jo.put("type", NOT_CONTAINS);
        return jo;
    }


    /**
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        JSONArray ja = jo.getJSONArray("value");
        for (int i = 0; i < ja.length(); i++) {
            uselessFields.add(ja.getString(i));
        }
    }


    @Override
    public IPersistentTable getBITable(IPersistentTable[] tables) {
        IPersistentTable persistentTable = getBITable();
        for (IPersistentTable t : tables) {
            for (PersistentField c : t.getFieldList()) {
                if (!uselessFields.contains(c.getFieldName())) {
                    persistentTable.addColumn(new PersistentField(c.getFieldName(), c.getType()));
                }
            }
        }
        return persistentTable;
    }

    @Override
    public int writeSimpleIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader) {
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents));
        return ti.getRowCount();
    }

    @Override
    public int writePartIndex(Traversal<BIDataValue> travel, List<? extends ITableSource> parents, ICubeDataLoader loader, int startCol, int start, int end) {
        ICubeTableService ti = loader.getTableIndex(getSingleParentMD5(parents), start, end);
        return ti.getRowCount();
    }

}
