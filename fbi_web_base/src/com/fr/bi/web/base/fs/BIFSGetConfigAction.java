package com.fr.bi.web.base.fs;

import com.fr.base.ChartPreStyleServerManager;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Created by 小灰灰 on 2015/8/7.
 */
public class BIFSGetConfigAction extends AbstractBIBaseAction {


    @Override
    public String getCMD() {
        return "get_config_setting";
    }

    /**
     * 注释
     *
     * @param req 参数1
     * @param res 参数2
     * @throws Exception
     */
    @Override
    public void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res)
            throws Exception {
        PrintWriter pw = WebUtils.createPrintWriter(res);
        JSONObject jo = new JSONObject();

        jo.put("chartStyle", FBIConfig.getInstance().getChartStyleAttr().getChartStyle());
        jo.put("defaultStyle", FBIConfig.getInstance().getChartStyleAttr().getDefaultStyle());
        String defaultColor = ChartPreStyleServerManager.getInstance().getCurrentStyle();
        jo.put("defaultColor", StringUtils.isEmpty(defaultColor) ? "--" : defaultColor);

        JSONArray ja = new JSONArray();
        ja.put(new JSONObject().put("text", "--").put("value", "--"));
        Iterator<String> it = ChartPreStyleServerManager.getInstance().names();
        while (it.hasNext()) {
            String name = it.next();
            JSONObject nameJo = new JSONObject();
            nameJo.put("text", name).put("value", name);
            ja.put(nameJo);
        }
        jo.put("styleList", ja);
        pw.print(jo);
        pw.flush();
        pw.close();
    }
}