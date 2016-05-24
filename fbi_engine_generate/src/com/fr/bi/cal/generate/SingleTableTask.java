/**
 *
 */
package com.fr.bi.cal.generate;

import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Daniel
 *         *
 */
public class SingleTableTask extends AllTask {

    private BITable table;

    public SingleTableTask(BITable table, long userId) {
        super(userId);
        this.table = table;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SingleTableTask other = (SingleTableTask) obj;
        if (table == null) {
            if (other.table != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(table, other.table)) {
            return false;
        }
        return true;
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("table", table.createJSON());
        return jo;
    }

    @Override
    protected Map<Integer, Set<ITableSource>> getGenerateTables() {
        Map<Integer, Set<ITableSource>> generateTable = new HashMap<Integer, Set<ITableSource>>();
        BICollectionUtils.mergeSetValueMap(generateTable, BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(table.getID(), biUser).createGenerateTablesMap());
        return generateTable;
    }

    @Override
    protected boolean checkCubeVersion(TableCubeFile cube) {
        return false;
    }


    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.SINGLE;
    }

    public Table getTableKey() {
        return table;
    }

}