package com.fr.bi.test;

import com.fr.cache.Attachment;
import com.fr.data.cache.AttachmentCacheManager;
import com.fr.general.web.ParameterConsts;
import com.fr.stable.CodeUtils;
import com.fr.stable.StringUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.upload.SmartFile;
import com.fr.web.core.upload.SmartFiles;
import com.fr.web.core.upload.SmartUpload;
import com.fr.web.utils.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author kunsnat E-mail:kunsnat@gmail.com
 * @version 创建时间：2011-12-12 下午04:18:35
 *          类说明: 上传附件
 */
public class AttachmentUploadAction extends ActionNoSessionCMD {

    /**
     * 上传文件
     * @param req http请求
     * @param res http应答
     * @throws Exception
     */
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
		ServletContext servletContext = req.getSession().getServletContext();
		// peter: 开始从客户端获得文件.
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.initialize(servletContext, req, res);
		smartUpload.upload();
		SmartFiles uploadFiles = smartUpload.getFiles();


		//没有上传文件,直接return
		if (uploadFiles.getCount() == 0) {
			return;
		}

		//只取第一个文件,多个文件的情况,先不考虑
		SmartFile smartFile = uploadFiles.getFile(0);
		String fileName = WebUtils.getHTTPRequestParameter(req, "filename");

		String swidth = WebUtils.getHTTPRequestParameter(req, "width");
		String sheight = WebUtils.getHTTPRequestParameter(req, "height");
		int width = (swidth != null) ? Integer.parseInt(swidth) : 1;
		int height = (sheight != null) ? Integer.parseInt(sheight) : 1;

		if (StringUtils.isEmpty(fileName)) {
			fileName = smartFile.getFileName();
		}
		Attachment attach = AttachmentCacheManager.addAttachment(
				smartFile.getContentType().indexOf("image") > 0 ? ParameterConsts.IMAGE : ParameterConsts.OTHER,
				CodeUtils.cjkDecode(fileName), smartFile.getBytes(), width, height
		);

		PrintWriter writer = WebUtils.createPrintWriter(res);
		writer.print(attach.toConfig());
		writer.flush();
		writer.close();

	}

    public String getCMD() {
		return "ah_upload";
	}

}
