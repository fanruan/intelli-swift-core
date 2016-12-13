package com.fr.bi.web.conf.services;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.cache.Attachment;
import com.fr.cache.AttachmentSource;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zcf on 2016/12/8.
 */
public class BISaveUploadExcelFileAction extends AbstractBIConfigureAction {
    @Override
    public String getCMD() {
        return "save_upload_excel";
    }

    @Override
    public void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
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
                JSONObject status = new JSONObject();
                status.put("success", true);
                WebUtils.printAsJSON(res, status);
            }
        }
    }
}
