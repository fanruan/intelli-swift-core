package com.fr.fs.mapeditor.server.service;

import com.fr.fs.mapeditor.server.GEOJSONHelper;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MapEditorEditEntryAction extends ActionNoSessionCMD {
    public String getCMD() {
        return "edit_entry";
    }

    /**
     * 执行Action
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {

        String oldPath = WebUtils.getHTTPRequestParameter(req, "oldPath");

        String newName = WebUtils.getHTTPRequestParameter(req, "newPath");

        GEOJSONHelper.getInstance().editEntry(newName, oldPath);

    }
}
