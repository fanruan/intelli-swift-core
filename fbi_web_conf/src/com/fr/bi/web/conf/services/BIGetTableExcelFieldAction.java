package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: shendon
 * Date: 13-12-5
 * Time: 上午9:47
 * To get the field'position from table
 */
public class BIGetTableExcelFieldAction extends AbstractBIConfigureAction {

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
//        String tableString = WebUtils.getHTTPRequestParameter(req, "table");
//        long userId = ServiceUtils.getCurrentUserID(req);
//        JSONObject tableJo = new JSONObject( tableString );
//        BITableConf tf = new BITableConf();
//        tf.parseJSON( tableJo );
//
//        BITableKey tk = tf.createKey();
//
//        //先获取到最后一次的excel是不是当前的excel
//        FieldExcelColumn fieldExcelColumn = BIInterfaceAdapter.getBIConnectionAdapter().getLastFieldExcelColumn( tk, userId );
//        if( fieldExcelColumn == null ) {
//            fieldExcelColumn = BIInterfaceAdapter.getBIConnectionAdapter().getFieldExcelColumn( tk , userId);
//        }
//
//
//        //建立所需要的json对象
//        JSONObject jo = new JSONObject();
//        jo.put( "fieldPositions", fieldExcelColumn.createJSON() );
//
//        WebUtils.printAsJSON( res, jo);
    }

    @Override
    public String getCMD() {
        return "get_table_excel_field";
    }
}