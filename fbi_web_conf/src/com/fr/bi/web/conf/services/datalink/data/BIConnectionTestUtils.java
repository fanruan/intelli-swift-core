package com.fr.bi.web.conf.services.datalink.data;

import com.fr.base.FRContext;
import com.fr.bi.stable.data.db.DataLinkInformation;
import com.fr.bi.stable.utils.BIDBUtils;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.json.JSONObject;

/**
 * Created by Connery on 2014/11/24.
 */
public class BIConnectionTestUtils {

    /**
     * 测试连接
     *
     * @param configData 连接参数
     * @return 是否连接成功
     * @throws Exception
     */
    public boolean processConnectionTestNoDetail(String configData) throws Exception {
        JSONObject jsonObject = processConnectionTest(configData);
        return jsonObject.getBoolean("success");
    }

    /**
     * 测试连接
     *
     * @return 是否连接成功和如果失败，失败的详细信息
     * @throws Exception
     */
    public JSONObject processConnectionTest(String configData) throws Exception {
        JSONObject jo = new JSONObject();
        boolean succession = false;
        try {
            succession = isTestLinkSuccessful(fetchConnection(configData));
        } catch (Exception ex) {
            jo.put("failureDetail", ex);
        }
        jo.put("success", succession);
        return jo;
    }

    protected JDBCDatabaseConnection fetchConnection(String configData) throws Exception {
        JSONObject linkDataJo = new JSONObject(configData);
        DataLinkInformation dl = new DataLinkInformation();
        dl.parseJSON(linkDataJo);
        JDBCDatabaseConnection c = dl.createJDBCDatabaseConnection();
        BIDBUtils.dealWithJDBCConnection(c);
        return c;
    }

    /**
     * 测试连接是否成功
     *
     * @return 是否
     * @throws Exception
     */
    private boolean isTestLinkSuccessful(JDBCDatabaseConnection jdbcDatabaseConnection) throws Exception {
        try {
            jdbcDatabaseConnection.testConnection();
            return true;
        } catch (Exception e) {
            FRContext.getLogger().errorWithServerLevel(e.getMessage(), e);
            throw e;
        }
    }
}