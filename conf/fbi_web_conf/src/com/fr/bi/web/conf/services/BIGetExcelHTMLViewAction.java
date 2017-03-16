package com.fr.bi.web.conf.services;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIGetImportedExcelHTML;
import com.fr.bi.web.conf.utils.BIGetImportedExcelViewData;
import com.fr.cache.Attachment;
import com.fr.cache.AttachmentSource;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.html.Tag;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by zcf on 2016/12/2.
 */
public class BIGetExcelHTMLViewAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String fileId = WebUtils.getHTTPRequestParameter(req, "fileId");

        BIGetImportedExcelHTML excelHTML = new BIGetImportedExcelHTML(fileId);
        Tag tag = excelHTML.getHtmlFromExcel(req);
        String html = tag.toHtml();
        JSONObject jo = new JSONObject();
        jo.put("excelHTML", html);
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_excel_html_view";
    }
}
