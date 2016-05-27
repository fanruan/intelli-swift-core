/**
 *
 */
package com.fr.bi.cal.generate;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.source.CubeTableSource;
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

    private BusinessTable table;

    public SingleTableTask(BusinessTable table, long userId) {
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
    protected Map<Integer, Set<CubeTableSource>> getGenerateTables() {
        Map<Integer, Set<CubeTableSource>> generateTable = new HashMap<Integer, Set<CubeTableSource>>();
        try {
            BICollectionUtils.mergeSetValueMap(generateTable, BICubeConfigureCenter.getDataSourceManager().getTableSource(table).createGenerateTablesMap());
        } catch (BIKeyAbsentException e) {
            e.printStackTrace();
        }
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


}