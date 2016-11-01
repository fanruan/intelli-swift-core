package com.fr.bi.web.conf.services.datalink;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.DecryptBi;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIWebSQLPreviewUtils;
import com.fr.data.core.db.DBUtils;
import com.fr.general.Decrypt;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;


/**
 * Created with IntelliJ IDEA.
 * User: Sheldon
 * Date: 13-11-1
 * Time: 下午3:55
 * describe: preview table
 * SQL语句预览
 */
public class BIPreviewServerLinkAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String linkName = WebUtils.getHTTPRequestParameter(req, "data_link");

        //sql语句是加密过的，否则可以通过url直接操作数据库*/
        String query = WebUtils.getHTTPRequestParameter(req, "sql");
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
            BILoggerFactory.getLogger().info("preview sql:" + query);
        }
        Connection conn = null;
        try {
            String subQuery = BIWebSQLPreviewUtils.getTableQuery(query);
            JSONObject data = BIWebSQLPreviewUtils.getPreviewData(subQuery, linkName);
            return data;
        } catch (Exception ignore) {
            BILoggerFactory.getLogger().info(ignore.getMessage());
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