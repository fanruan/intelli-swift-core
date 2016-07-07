package com.fr.bi.web.base.fs;

import com.fr.base.ChartPreStyleServerManager;
import com.fr.bi.conf.fs.FBIConfig;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.chart.base.ChartPreStyle;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

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
//        jo.put("defaultStyle", FBIConfig.getInstance().getChartStyleAttr().getDefaultStyle());
        String defaultColor = ChartPreStyleServerManager.getInstance().getCurrentStyle();
        jo.put("defaultColor", defaultColor);

        JSONArray ja = new JSONArray();
        Iterator<String> it = ChartPreStyleServerManager.getInstance().names();
        while (it.hasNext()) {
            String name = it.next();
            ChartPreStyle style = (ChartPreStyle) ChartPreStyleServerManager.getInstance().getPreStyle(name);
            java.util.List colorList = style.getAttrFillStyle().getColorList();

            Iterator itColor = colorList.iterator();
            List colorArray = new ArrayList();
            while (itColor.hasNext()) {
                Color color = (Color) itColor.next();
                colorArray.add(toHexEncoding(color));
            }
            JSONObject nameJo = new JSONObject();
            nameJo.put("text", name).put("value", name).put("colors", colorArray);
            ja.put(nameJo);
        }
        jo.put("styleList", ja);
        pw.print(jo);
        pw.flush();
        pw.close();
    }

    //Color转换为16进制显示
    private static String toHexEncoding(Color color) {
        String R, G, B;
        StringBuffer sb = new StringBuffer();

        R = Integer.toHexString(color.getRed());
        G = Integer.toHexString(color.getGreen());
        B = Integer.toHexString(color.getBlue());

        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;

        sb.append("#");
        sb.append(R);
        sb.append(G);
        sb.append(B);

        return sb.toString();
    }

}