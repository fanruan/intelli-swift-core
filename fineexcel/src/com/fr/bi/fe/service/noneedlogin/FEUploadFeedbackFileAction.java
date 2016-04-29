package com.fr.bi.fe.service.noneedlogin;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fr.cache.Attachment;
import com.fr.cache.AttachmentSource;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

/**
 * Created by young.
 */
public class FEUploadFeedbackFileAction extends ActionNoSessionCMD {
	@Override
	public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String id = WebUtils.getHTTPRequestParameter(req, "file_id");
        
        long userId = ServiceUtils.getCurrentUserID(req);
        String[] id_array = id.substring(1, id.length()-1).split(",");
        JSONArray ja = new JSONArray();
        for(int i = 0; i < id_array.length; i ++){
        	String attachId = id_array[i].substring(1, id_array[i].length() - 1); 
        	Attachment attach = AttachmentSource.getAttachment(attachId);
        	if (attach != null) {
        		byte[] bytes = attach.getBytes();
        		String fileName = attach.getFilename();
        		fileName = fileName.trim();
        		fileName = fileName.toLowerCase();
        		//生成excel文件到指定目录下
        		
//        		File parentFile = new File( FRContext.getCurrentEnv().getPath() + BIBaseConstant.CUBEDATA.CUBE_DATA_PATH + File.separator + userId + File.separator + "feedback_file" );
        		File parentFile = new File("D:\\FineExcel\\uploadFiles\\"+ userId + File.separator + "feedback_file");
        		if( !parentFile.exists() ) {
        			parentFile.mkdirs();
        		}
        		File file = new File("");
        		if(fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith("jpeg")){
        			file = new File( parentFile, DateUtils.getDate2AllIncludeSSS(new Date()) + "_.png" );
        		}else{
        			String [] temp = fileName.split("\\.");
        			String fileType = temp[temp.length - 1];
        			file = new File( parentFile, DateUtils.getDate2AllIncludeSSS(new Date()) + "_." + fileType );
        		}
        		
        		
        		FileOutputStream fs = new FileOutputStream( file );
        		fs.write( bytes );
        		fs.flush();
        		fs.close();
        		ja.put(file.getName());
//        		ja.put(attachId);
        	}
        }
        WebUtils.printAsJSON( res, ja);
    }

    @Override
    public String getCMD() {
        return "upload_feedback_file";
    }
}