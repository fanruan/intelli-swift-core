package com.fr.bi.web.conf.services.datalink;

import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.utils.DecryptBi;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.data.core.db.DBUtils;
import com.fr.data.impl.DBTableData;
import com.fr.data.impl.EmbeddedTableData;
import com.fr.file.DatasourceManager;
import com.fr.general.Decrypt;
import com.fr.general.data.DataModel;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: Sheldon
 * Date: 13-11-1
 * Time: 下午3:55
 * describe: preview table
 */
public class BIPreviewServerLinkAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String linkName = WebUtils.getHTTPRequestParameter(req, "data_link");

        //sql语句是加密过的，否则可以通过url直接操作数据库*/
        String query = WebUtils.getHTTPRequestParameter(req, "sql");
        PrintWriter writer = WebUtils.createPrintWriter(res);
        JSONObject jo = getFlexgridPreviewJo(query, linkName);
        WebUtils.printAsJSON(res, jo);
    }

    public JSONObject getFlexgridPreviewJo(String query, String linkName) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(query) || StringUtils.isEmpty(linkName)) {
            return new JSONObject();
        }
        Decrypt pt;
        //加密处理过
        if (StringUtils.isNotEmpty(query)) {
            query = DecryptBi.decrypt(query, "sh");
        }
//        query = java.net.URLDecoder.decode(query , "utf-8");
        Connection conn = null;
        try {
            com.fr.data.impl.Connection dbc = DatasourceManager.getInstance().getConnection(linkName);

            DBTableData dbTableData = new DBTableData(dbc, query);

            //转换成内置数据集
            EmbeddedTableData emTableData = EmbeddedTableData.embedify(dbTableData, null, BIBaseConstant.PREVIEW_COUNT);
            DataModel dm = emTableData.createDataModel(null);

            int cols = dm.getColumnCount();
            int rows = Math.min(dm.getRowCount(), BIBaseConstant.PREVIEW_COUNT);

            JSONArray fieldJa = new JSONArray();
            for (int i = 0; i < cols; i++) {
                fieldJa.put(dm.getColumnName(i));
            }

            JSONArray dataJa = new JSONArray();
            for (int i = 0; i < rows; i++) {

                JSONArray oneRowJa = new JSONArray();
                for (int j = 0; j < cols; j++) {
                    oneRowJa.put(dm.getValueAt(i, j).toString());
                }

                dataJa.put(oneRowJa);
            }

            return new JSONObject().put("field_names", fieldJa).put("data", dataJa);

        } catch (Exception ignore) {
            BILogger.getLogger().info(ignore.getMessage());
        } finally {
            DBUtils.closeConnection(conn);
        }

        return new JSONObject();
    }

    @Override
    public String getCMD() {
        return "preview_server_link";
    }
}