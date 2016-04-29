package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: shendon
 * Date: 13-12-4
 * Time: 下午5:24
 * To save the table's field's position
 */
public class BISetTableFieldAction extends AbstractBIConfigureAction {

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
//        String tableString = WebUtils.getHTTPRequestParameter(req, "table");
//        String fieldString = WebUtils.getHTTPRequestParameter(req, "fieldPositions");
//        long userId = ServiceUtils.getCurrentUserID(req);
//        JSONObject table = new JSONObject( tableString );
//        JSONArray fieldPositions = new JSONArray( fieldString );
//
//        BITableConf tf = new BITableConf();
//        tf.parseJSON( table );
//        BITableKey biTableKey = tf.createKey();
//
//        BIConnectionManagerInterface connectionManager = BIInterfaceAdapter.getBIConnectionAdapter();
//        String excelName = connectionManager.getExcelName( biTableKey , userId);
//
//        FieldExcelColumn fieldExcelColumn = new FieldExcelColumn(excelName ,biTableKey, userId);
//
//        JSONObject jo = new JSONObject();
//        jo.put("fieldPositions", fieldPositions);
//        fieldExcelColumn.parseJSON(jo);
//
//        connectionManager.setLasetSettedExcel(fieldExcelColumn, userId);
    }

    @Override
    public String getCMD() {
        return "set_table_field";
    }
}