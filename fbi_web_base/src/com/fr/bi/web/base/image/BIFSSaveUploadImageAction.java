package com.fr.bi.web.base.image;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.cache.Attachment;
import com.fr.cache.AttachmentSource;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by windy on 2016/10/12.
 */
public class BIFSSaveUploadImageAction extends AbstractBIBaseAction {
    @Override
    public String getCMD() {
        return "save_upload_image";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String attachId = WebUtils.getHTTPRequestParameter(req, "attach_id");
        Attachment attach = AttachmentSource.getAttachment(attachId);
        if (attach != null) {
            byte[] bytes = attach.getBytes();
            String imageName = attach.getFilename();
            imageName = imageName.trim();
            imageName = imageName.toLowerCase();
            JSONObject status = new JSONObject();
            try {
                File parentFile = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.UPLOAD_IMAGE.IMAGE_PATH);
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                //以attach_id+fileName作为标识
                File file = new File(parentFile, attachId + "_" + imageName);
                FileOutputStream fs = new FileOutputStream(file);
                fs.write(bytes);
                fs.flush();
                fs.close();
                status.put("success", true);
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
            WebUtils.printAsJSON(res, status);
        }
    }
}
