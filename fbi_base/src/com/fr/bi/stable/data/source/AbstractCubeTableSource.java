package com.fr.bi.stable.data.source;

import com.fr.base.TableData;
import com.fr.bi.base.key.BIKey;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/24.
 */
public abstract class AbstractCubeTableSource extends AbstractTableSource {
    /**
     * 获取某个字段的distinct值
     *
     * @param fieldName
     * @param loader
     * @param userId
     */
    @Override
    public Set getFieldDistinctNewestValues(String fieldName, ICubeDataLoader loader, long userId) {
        return getFieldDistinctValuesFromCube(fieldName, loader, userId);
    }

    @Override
    public JSONObject createPreviewJSON(ArrayList<String> fields, ICubeDataLoader loader, long userId) throws Exception {
        return createPreviewJSONFromCube(fields, loader);
    }

    @Override
    public TableData createTableData(List<String> fields, ICubeDataLoader loader, long userId) throws Exception {
        EmbeddedTableData td = new EmbeddedTableData();
        ICubeTableService tableIndex = loader.getTableIndex(this);
        if (tableIndex == null || tableIndex.getRowCount() == 0) {
            return null;
        }
        Set<BIKey> columns = new LinkedHashSet<BIKey>();
        if (fields == null || fields.isEmpty()) {
            columns = tableIndex.getColumns().keySet();
        } else {
            for (String s : fields) {
                BIKey key = new IndexKey(s);
                if (tableIndex.getColumns().get(key) != null) {
                    columns.add(key);
                } else {
                    return null;
                }
            }
        }
        for (BIKey col : columns) {
            Object value = tableIndex.getColumnDetailReader(col).getValue(0);
            td.addColumn(col.getKey(), value == null ? String.class : value.getClass());
        }
        for (int row = 0; row < tableIndex.getRowCount(); row++) {
            List<Object> rowList = new ArrayList<Object>();
            for (BIKey col : columns) {
                rowList.add(tableIndex.getColumnDetailReader(col).getValue(row));
            }
            td.addRow(rowList);
        }
        return td;
    }

}