package com.fr.bi.web.dezi.services.image;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by fay on 2016/10/26.
 */
public class BIGetImageSizeAction extends AbstractBIDeziAction {
    @Override
    public String getCMD() {
        return "get_image_size";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String src = WebUtils.getHTTPRequestParameter(req, "src");
        if (src != null) {
            JSONObject size = new JSONObject();
            try {
                File parentFile = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.UPLOAD_IMAGE.IMAGE_PATH + File.separator + src);
                if (parentFile.exists()) {
                    BufferedImage sourceImg = ImageIO.read(new FileInputStream(parentFile));
                    size.put("width", sourceImg.getWidth());
                    size.put("height", sourceImg.getHeight());
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
            WebUtils.printAsJSON(res, size);
        }
    }
}
