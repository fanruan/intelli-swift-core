package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: shendon
 * Date: 13-12-2
 * Time: 下午5:19
 * 删除表格配置的excel
 */
public class BIRemoveTableSettedExcelAction extends AbstractBIConfigureAction {

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
//        String tableString = WebUtils.getHTTPRequestParameter(req, "table");
//        JSONObject jo = new JSONObject( tableString );
//        long userId = ServiceUtils.getCurrentUserID(req);
//        BIImportExcelForTable biImportExcelForTable = new BIImportExcelForTable( jo , userId);
//
//        File f = biImportExcelForTable.getFile();
//        if( f.exists() ) {
//            f.delete();
//        }
//
//        biImportExcelForTable.deleteTableExcel(userId);
    }

    @Override
    public String getCMD() {
        return "remove_excel_file";  //To change body of implemented methods use File | Settings | File Templates.
    }
}