package com.fr.bi.web.report.services;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Young's on 2016/6/30.
 */
public class BIGetUploadedImageAction extends ActionNoSessionCMD {
    @Override
    public String getCMD() {
        return "get_uploaded_image";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String imageId = WebUtils.getHTTPRequestParameter(req, "image_id");
        res.setContentType("image/png");
        OutputStream os = res.getOutputStream();
        try {
            File parentFile = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.UPLOAD_IMAGE.IMAGE_PATH);
            File file = new File(parentFile, imageId);
            byte[] bytes;
            InputStream is = new FileInputStream(file);
            int length = (int) file.length();
            bytes = new byte[length];
            int offset = 0;
            int numRead;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            is.close();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}
