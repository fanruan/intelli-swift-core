package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 14-9-18.
 * 通过表名，获取到表格的详细信息
 */
public class BIGetTableDetailInforAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

//        String packageName = WebUtils.getHTTPRequestParameter(req, "package_name");
//        String connectionName = WebUtils.getHTTPRequestParameter(req, "connection_name");
//        String tableName = WebUtils.getHTTPRequestParameter(req, "table_name");
//        String schema = WebUtils.getHTTPRequestParameter(req, "schema_name");
//        String md5TableName = WebUtils.getHTTPRequestParameter(req, "md5_table_name");
//        long userId = ServiceUtils.getCurrentUserID(req);
//        if (StringUtils.isEmpty(md5TableName)){
//            md5TableName = tableName;
//        }
//        String dbLink  = WebUtils.getHTTPRequestParameter(req, "dbLink");
//
//        BIAbstractBusiTable table = BIConfUtils.getTableByPackageNameAndConnectionNameAndTableName(packageName, connectionName, schema, md5TableName, userId);
//        if (table == null) {
//            Iterator<String> iter = BIInterfaceAdapter.getBIBusiPackAdapter().getPackageNameIterator(new BITableKey(connectionName, schema, md5TableName, dbLink), userId);
//            while(iter.hasNext()){
//                packageName = iter.next();
//                table = BIConfUtils.getTableByPackageNameAndConnectionNameAndTableName(packageName, connectionName, schema, md5TableName, userId);
//                if (table != null){
//                    break;
//                }
//            }
//            if(table == null){
//                table = new BIDBBusiTable(connectionName, schema, tableName, dbLink);
//            }
//        }
//
//        JSONObject tableJo = table.createJSON();
//
//        WebUtils.printAsJSON(res, tableJo);
    }

    /**
     * cmd参数值，例如op=write&cmd=sort
     *
     * @return 返回该请求的cmd参数的值
     */
    @Override
    public String getCMD() {
        return "bi_get_table_detail_infor";
    }
}