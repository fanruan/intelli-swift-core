package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 14-8-5.
 * 导入设置表的excel
 */
public class BIImportExcel4SetField extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {

//        String id = WebUtils.getHTTPRequestParameter(req, "id");
//
//        String joString = WebUtils.getHTTPRequestParameter(req, "table");
//
//        JSONObject jo = new JSONObject( joString );
//        long userId = ServiceUtils.getCurrentUserID(req);
//
//        String[] id_array = id.split("\\.");
//        if(id_array.length == 1) {
//            Attachment attach = AttachmentSource.getAttachment(id);
//
//            if (attach != null) {
//                byte[] bytes = attach.getBytes();
//                String reportName = attach.getFilename();
//                reportName = reportName.trim();
//
//                reportName = reportName.toLowerCase();
//                if ( reportName.endsWith(".xls") || reportName.endsWith(".xlsx") ) {
//
//                    //生成excel文件到指定目录下
//                    BIImportExcelForTable biImportExcel = new BIImportExcelForTable(reportName, jo);
//                    File file = biImportExcel.getFile();
//
//                    FileOutputStream fs = new FileOutputStream( file );
//                    fs.write( bytes );
//                    fs.flush();
//                    fs.close();
//
//                    //将关系写入Connection
//                    biImportExcel.pushTableExcelToConnection(userId);
//                }
//            }
//        }
    }

    @Override
    public String getCMD() {
        return "import_excel_4_set_field";
    }
}