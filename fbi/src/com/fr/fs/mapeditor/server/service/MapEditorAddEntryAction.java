package com.fr.fs.mapeditor.server.service;

import com.fr.json.JSONObject;
import com.fr.fs.mapeditor.server.GEOJSONHelper;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class MapEditorAddEntryAction extends ActionNoSessionCMD {
    public String getCMD() {
        return "add_entry";
    }

    /**
     * 执行Action
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String name = WebUtils.getHTTPRequestParameter(req, "name");
        String parentPath = WebUtils.getHTTPRequestParameter(req, "parentPath");
        boolean isImageMap = WebUtils.getHTTPRequestBoolParameter(req, "isImageMap");

        JSONObject folder = isImageMap ? GEOJSONHelper.getInstance().createImageEntry(name, parentPath) : GEOJSONHelper.getInstance().createGeographicEntry(name, parentPath);

        PrintWriter writer = WebUtils.createPrintWriter(res);
        writer.print(folder.toString());
        writer.flush();
        writer.close();
    }
}
