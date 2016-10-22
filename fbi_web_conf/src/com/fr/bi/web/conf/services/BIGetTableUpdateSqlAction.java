package com.fr.bi.web.conf.services;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.structure.table.CubeSourceHelper;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
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
        BILoggerFactory.getLogger().info("preview SQL start");
        String stringSql = WebUtils.getHTTPRequestParameter(req, "sql");
        String tableString = WebUtils.getHTTPRequestParameter(req, "table");
        JSONObject table = new JSONObject(tableString);
        Date lastUpdateDate = new Date();
        long threeDaysAgo = lastUpdateDate.getTime() - 24 * 3600 * 1000 * 3;
        lastUpdateDate.setTime(threeDaysAgo);
//        String tableId = (String) table.get("id");
//        ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
//        ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getConf(String.valueOf(UserControl.getInstance().getSuperManagerID())));
//        CubeTableSource tableSource = null;
//        Cube cube = new BICube(resourceRetrievalService, discovery);
//        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(UserControl.getInstance().getSuperManagerID())) {
//            if (businessTable.getID().getIdentity().equals(tableId)) {
//                tableSource = businessTable.getTableSource();
//            }
//        }
            CubeTableSource tableSource = CubeSourceHelper.getSource(new BITableKey((String) table.get("md5")));
        if (null != tableSource) {
                    ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getConf(String.valueOf(UserControl.getInstance().getSuperManagerID())));
                    Cube cube = new BICube(resourceRetrievalService, discovery);
            CubeTableEntityService tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
            if (tableEntityService.isLastExecuteTimeAvailable()) {
                lastUpdateDate = tableEntityService.getLastExecuteTime();
                tableEntityService.clear();
            }
        }
        String sql = parseSQL(stringSql, lastUpdateDate);
        JSONObject jo = new JSONObject();
        jo.put("sql", sql);
        jo.put("last_update_time", lastUpdateDate);
        BILoggerFactory.getLogger().info("preview SQL：" + sql);
        if (StringUtils.isNotEmpty(sql)) {
            //预览时不一定已经生成table，所以无法使用table类型来判断
            String connectionName = null;
            if (!table.toMap().get("connection_name").equals(DBConstant.CONNECTION.SQL_CONNECTION)) {
                connectionName = table.getString("connection_name");
            } else {
                connectionName = table.getString("dataLinkName");
            }
            com.fr.data.impl.Connection dbc = DatasourceManager.getInstance().getConnection(connectionName);
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
                BILoggerFactory.getLogger().error(e.getMessage());
                jo.put("error", e.getMessage());
            }
        }
        WebUtils.printAsJSON(res, jo);
    }

    private String parseSQL(String sql, Date lastDate) {
        //替换上次更新时间
        Pattern lastTimePat = Pattern.compile("\\$[\\{]__last_update_time__[\\}]");
        sql = replacePattern(sql, lastTimePat, lastDate);

        //替换当前时间
        Pattern currentTimePat = Pattern.compile("\\$[\\{]__current_update_time__[\\}]");
        sql = replacePattern(sql, currentTimePat, new Date(System.currentTimeMillis()));
        return sql;
    }

    private String replacePattern(String sql, Pattern pattern, Date date) {
        Matcher matcher = pattern.matcher(sql);
        String dateStr = DateUtils.DATETIMEFORMAT2.format(date);
        while (matcher.find()) {
            String matchStr = matcher.group(0);
            sql = sql.replace(matchStr, dateStr);
        }
        return sql;
    }
}