package com.fr.fs.mapeditor.server.service;

import com.fr.json.JSONObject;
import com.fr.fs.mapeditor.server.GEOJSONHelper;
import com.fr.fs.mapeditor.server.MapLayerConfigManager;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by eason on 2017/6/1.
 */
public class MapEditorGetFolderAction extends ActionNoSessionCMD {

    public String getCMD() {
        return "getfolder";
    }

    /**
     * 执行Action
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
        PrintWriter writer = WebUtils.createPrintWriter(res);

        JSONObject folder = JSONObject.create();


        folder.put("geographic", GEOJSONHelper.getInstance().createGeographicFolderEntries());

        folder.put("image", GEOJSONHelper.getInstance().createImageFolderEntries());

        folder.put("layer", MapLayerConfigManager.getInstance().createMapLayerFolderEntries());

        writer.print(folder.toString());
        writer.flush();
        writer.close();
    }
}
