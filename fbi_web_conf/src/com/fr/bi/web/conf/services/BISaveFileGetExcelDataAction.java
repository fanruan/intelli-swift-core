package com.fr.bi.web.conf.services;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIGetImportedExcelData;
import com.fr.cache.Attachment;
import com.fr.cache.AttachmentSource;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by sheldon on 14-8-6.
 */
public class BISaveFileGetExcelDataAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String fileId = WebUtils.getHTTPRequestParameter(req, "fileId");
        Attachment attach = AttachmentSource.getAttachment(fileId);
        if (attach != null) {
            byte[] bytes = attach.getBytes();
            String fileName = attach.getFilename();
            fileName = fileName.trim();
            fileName = fileName.toLowerCase();
            boolean isExcel = fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".csv");
            if (isExcel) {
                File parentFile = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.EXCELDATA.EXCEL_DATA_PATH);
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                //以attach_id+fileName作为标识
                File file = new File(parentFile, fileId + fileName);
                FileOutputStream fs = new FileOutputStream(file);
                fs.write(bytes);
                fs.flush();
                fs.close();
                BIGetImportedExcelData excelTableData = new BIGetImportedExcelData(file.getName());
                JSONObject jo = excelTableData.getFieldsAndPreviewData();
                jo.put("full_file_name" , file.getName());
                WebUtils.printAsJSON(res, jo);
            }
        }
    }

    @Override
    public String getCMD() {
        return "save_file_get_excel_data";
    }
}