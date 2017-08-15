package com.fr.fs.mapeditor.server.service;

import com.fr.json.JSONArray;
import com.fr.fs.mapeditor.server.GEOJSONHelper;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class MapEditorSaveJSONDataAction extends ActionNoSessionCMD{
    public String getCMD() {
        return "save_json_data";
    }

    /**
     * 执行Action
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String dirPath = WebUtils.getHTTPRequestParameter(req, "dirPath");

        JSONArray jsonData = new JSONArray(WebUtils.getHTTPRequestParameter(req, "jsonData"));

        PrintWriter writer = WebUtils.createPrintWriter(res);
        writer.print(GEOJSONHelper.getInstance().saveJSONData(dirPath, jsonData));
        writer.flush();
        writer.close();

    }
}
