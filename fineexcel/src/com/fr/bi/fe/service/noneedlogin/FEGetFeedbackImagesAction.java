package com.fr.bi.fe.service.noneedlogin;

import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

/**
 * Created by young.
 */
public class FEGetFeedbackImagesAction extends ActionNoSessionCMD {
    @Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String filePaths = WebUtils.getHTTPRequestParameter(req, "filePath");
        
//    	JSONArray filePath = FineExcelUserService.getInstance().getPicturePath(filePaths);
    	File file = new File(filePaths);
    	if(file.exists()) {
    		RenderedImage image = ImageIO.read(file);
//    		BufferedImage image = ImageIO.read(file);
    		res.setContentType("image/jpg");
    		res.setHeader("Pragma", "no-cache");
    		res.setHeader("Cache-Control", "no-cache");
    		HttpSession session=req.getSession();
    		session.setAttribute("strbuffer", image);
    		ServletOutputStream sos = res.getOutputStream();
    		if(image != null) {
    			ImageIO.write(image, "png", sos);
    			sos.close();
    		}
    	}
    }

    @Override
    public String getCMD() {
        return "get_feedback_picture";
    }
}