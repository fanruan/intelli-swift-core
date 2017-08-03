package com.fr.bi.web.service.action;

import com.fr.bi.cluster.utils.BIUserAuthUtils;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.util.BIReadReportUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

/**
 * Created by windy on 2017/2/22.
 */
public class BIAnalysisETLCheckTableInUseAction extends AbstractAnalysisETLAction {
    @Override
    public String getCMD() {
        return "etl_table_in_use_check";
    }

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        String id = WebUtils.getHTTPRequestParameter(req, "id");
        JSONArray usedList = new JSONArray();

        //暂时先检查管理员的模板了
        List<BIReportNode> nodeList = BIDAOUtils.getBIDAOManager().findByUserID(BIUserAuthUtils.getCurrentUserID(req));
        boolean isInUse = false;
        for (BIReportNode reportNode : nodeList) {
            JSONObject reportSetting = BIReadReportUtils.getBIReadReportManager().getBIReportNodeJSON(reportNode);
            if (reportSetting.has("widgets")) {
                JSONObject widgets = reportSetting.getJSONObject("widgets");
                Iterator<String> widgetIds = widgets.keys();
                while (widgetIds.hasNext()) {
                    JSONObject widget = widgets.getJSONObject(widgetIds.next());
                    if(isWidgetUseTable(id, widget)){
                        usedList.put(reportNode.getDisplayName());
                        break;
                    }
                }
            }
        }
        WebUtils.printAsJSON(res, new JSONObject().put("usedTemplate", usedList));
    }

    private boolean isWidgetUseTable(String tableId, JSONObject widget) throws Exception {
        boolean isInUse = false;
        if (widget.has("dimensions")) {
            JSONObject dimensions = widget.getJSONObject("dimensions");
            Iterator<String> dIds = dimensions.keys();
            while (dIds.hasNext()) {
                JSONObject dim = dimensions.getJSONObject(dIds.next());
                if (dim.has("_src")) {
                    JSONObject src = dim.getJSONObject("_src");
                    if (src.has("tableId")) {
                        String tId = src.getString("tableId");
                        isInUse = ComparatorUtils.equals(tId, tableId);
                        if (isInUse) {
                            break;
                        }
                    }
                }
            }
        }
        return isInUse;
    }
}
