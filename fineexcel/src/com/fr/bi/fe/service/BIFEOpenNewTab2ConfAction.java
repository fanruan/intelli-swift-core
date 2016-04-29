package com.fr.bi.fe.service;

import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by young.
 */
public class BIFEOpenNewTab2ConfAction extends ActionNoSessionCMD {
    /**
     * 执行
     *
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {
    	String tableName = WebUtils.getHTTPRequestParameter(req, "full_file_name");
    	Map map = new HashMap();
        map.put("fullname", tableName);
        PrintWriter writer = WebUtils.createPrintWriter(res);
        WebUtils.writeOutTemplate("/com/fr/bi/web/html/bi_conf_excel.html", res, map);
    }

    /**
     * cmd参数值，例如op=write&cmd=sort
     *
     * @return 返回该请求的cmd参数的值
     */
    @Override
    public String getCMD() {
        return "fe_open_tab_to_conf";
    }
}