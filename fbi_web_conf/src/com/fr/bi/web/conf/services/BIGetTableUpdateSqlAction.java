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
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIWebSQLPreviewUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.DateUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sheldon on 14-6-9.
 * 增量更新SQL语句预览展示
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
        //预览时不一定已经生成table，所以无法使用table类型来判断
        String connectionName;
        if (!table.toMap().get("connection_name").equals(DBConstant.CONNECTION.SQL_CONNECTION)) {
            connectionName = table.getString("connection_name");
        } else {
            connectionName = table.getString("dataLinkName");
        }
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
        JSONObject jo ;
        String sql = parseSQL(stringSql, lastUpdateDate);
        BILoggerFactory.getLogger().info("preview SQL：" + sql);
        String subQuery = BIWebSQLPreviewUtils.getTableQuery(sql);
        jo = BIWebSQLPreviewUtils.getPreviewData(subQuery, connectionName);
        jo.put("sql", sql).put("last_update_time", lastUpdateDate);
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