package com.fr.fs.mapeditor.server.service;

import com.fr.base.Base64;
import com.fr.fs.mapeditor.geojson.JSONMapper;
import com.fr.json.JSONObject;
import com.fr.fs.mapeditor.VanChartAttrHelper;
import com.fr.fs.mapeditor.server.GEOJSONHelper;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.upload.SmartFile;
import com.fr.web.core.upload.SmartFiles;
import com.fr.web.core.upload.SmartUpload;
import com.fr.web.utils.WebUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;

public class MapEditorSaveImageAction extends ActionNoSessionCMD {
    private static String PREFIX = "base64,";
    private static String SUFFIX = "data:image/";

    public String getCMD() {
        return "save_background_image";
    }

    /**
     * 执行Action
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {

        ServletContext servletContext = req.getSession().getServletContext();
        SmartUpload smartUpload = new SmartUpload();
        smartUpload.initialize(servletContext, req, res);
        smartUpload.upload();
        SmartFiles uploadFiles = smartUpload.getFiles();

        if(uploadFiles.getCount() == 0){
            return;
        }

        SmartFile smartFile = uploadFiles.getFile(0);
        String dirPath = smartFile.getFieldName();
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(smartFile.getBytes()));

        String jsonURL = GEOJSONHelper.getInstance().getJsonUrlByPath(dirPath);

        JSONMapper jsonMapper = GEOJSONHelper.getInstance().getGeoJSON(jsonURL);

        jsonMapper.setImageString(Base64.encode(image, smartFile.getFileExt()));
        jsonMapper.setImageSuffix(smartFile.getFileExt());
        jsonMapper.setImageWidth(image.getWidth());
        jsonMapper.setImageHeight(image.getHeight());

        GEOJSONHelper.getInstance().writeGeoJSON(jsonMapper);

        JSONObject json = new JSONObject(jsonMapper.toString());
        json.put("imageUrl", VanChartAttrHelper.getCustomImageURL(jsonURL, req.getSession().getId(), req));

        PrintWriter writer = WebUtils.createPrintWriter(res);
        writer.print(json.toString());
        writer.flush();
        writer.close();
    }
}
