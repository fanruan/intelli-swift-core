package com.fr.bi.web.conf.services;

import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.data.impl.DBTableData;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.file.DatasourceManager;
import com.fr.fs.control.UserControl;
import com.fr.general.DateUtils;
import com.fr.general.data.DataModel;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sheldon on 14-6-9.
 */
public class BIGetTableUpdateSqlAction extends AbstractBIConfigureAction {
    @Override
    public String getCMD() {
        return "get_preview_table_update";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String stringSql = WebUtils.getHTTPRequestParameter(req, "sql");
        String tableString = WebUtils.getHTTPRequestParameter(req, "table");
        JSONObject table = new JSONObject(tableString);
        Date lastUpdateDate = new Date();
        long threeDaysAgo = lastUpdateDate.getTime() - 24 * 3600 * 1000 * 3;
        lastUpdateDate.setTime(threeDaysAgo);
        String tableId = (String) table.get("id");
        ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getConf(String.valueOf(UserControl.getInstance().getSuperManagerID())));
        CubeTableSource tableSource = null;
        Cube cube = new BICube(resourceRetrievalService, discovery);
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(UserControl.getInstance().getSuperManagerID())) {
            if (businessTable.getID().getIdentity().equals(tableId)) {
                tableSource = businessTable.getTableSource();
            }
        }
        if (null != tableSource) {
            CubeTableEntityService tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
            if (tableEntityService.isCubeLastTimeAvailable()) {
                lastUpdateDate = tableEntityService.getCubeLastTime();
            }
        }
        String sql = parseSQL(stringSql, lastUpdateDate);
        JSONObject jo = new JSONObject();
        jo.put("sql", sql);
        jo.put("last_update_time", lastUpdateDate);

        if (StringUtils.isNotEmpty(sql)) {
            com.fr.data.impl.Connection dbc = DatasourceManager.getInstance().getConnection(table.getString("connection_name"));
            DBTableData dbTableData = new DBTableData(dbc, sql);
            try {
                DataModel dm;
                //转换成内置数据集
                EmbeddedTableData emTableData = EmbeddedTableData.embedify(dbTableData, null, BIBaseConstant.PREVIEW_COUNT);
                dm = emTableData.createDataModel(null);
                int cols = dm.getColumnCount();
                int rows = dm.getRowCount();
                JSONArray fieldNameArray = new JSONArray();
                JSONArray dataArray = new JSONArray();
                for (int i = 0; i < cols; i++) {
                    fieldNameArray.put(dm.getColumnName(i));
                }
                for (int i = 0; i < rows; i++) {
                    JSONArray oneRow = new JSONArray();
                    for (int j = 0; j < cols; j++) {
                        oneRow.put(dm.getValueAt(i, j));
                    }
                    dataArray.put(oneRow);
                }

                jo.put("field_names", fieldNameArray);
                jo.put("data", dataArray);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage());
                jo.put("error", e.getMessage());
            }
        }
        WebUtils.printAsJSON(res, jo);
    }

    private String parseSQL(String sql, Date date) {
        Pattern pat = Pattern.compile("\\$[\\{][^\\}]*[\\}]");
        Matcher matcher = pat.matcher(sql);
        String matchStr = matcher.group(0);
        String dateStr = DateUtils.DATETIMEFORMAT2.format(date);
        while (matcher.find()) {
            sql = sql.replace(matchStr, dateStr);
        }
        return sql;
    }
}