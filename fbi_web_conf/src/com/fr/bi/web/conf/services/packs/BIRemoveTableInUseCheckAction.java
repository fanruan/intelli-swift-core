package com.fr.bi.web.conf.services.packs;

import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

/**
 * 检查所有模板是否存在正在使用当前表字段的
 * Created by Young's on 2016/9/23.
 */
public class BIRemoveTableInUseCheckAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String id = WebUtils.getHTTPRequestParameter(req, "id");

        //暂时先检查管理员的模板了
        List<BIReportNode> nodeList = BIDAOUtils.findByUserID(ServiceUtils.getCurrentUserID(req));
        boolean isInUse = false;
        for (int i = 0; i < nodeList.size(); i++) {
            BIReportNode reportNode = nodeList.get(i);
            JSONObject reportSetting = BIReadReportUtils.getBIReportNodeJSON(reportNode);
            if (reportSetting.has("widgets")) {
                JSONObject widgets = reportSetting.getJSONObject("widgets");
                Iterator<String> widgetIds = widgets.keys();
                while (widgetIds.hasNext()) {
                    JSONObject widget = widgets.getJSONObject(widgetIds.next());
                    isInUse = isWidgetUseTable(id, widget);
                    if (isInUse) {
                        break;
                    }
                }
            }
        }
        WebUtils.printAsJSON(res, new JSONObject().put("inUse", isInUse));
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
                    if (src.has("table_id")) {
                        String tId = src.getString("table_id");
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

    @Override
    public String getCMD() {
        return "remove_table_in_use_check";
    }
}
