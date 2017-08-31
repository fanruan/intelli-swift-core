package com.fr.bi.web.service.action;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cluster.utils.BIUserAuthUtils;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.conf.AnalysisBusiTable;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.util.BIReadReportUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/5/13.
 * 当前螺旋分析表是否被其他螺旋分析表使用或者被其它分析使用
 */
public class BIEditAnalysisETLTableAction extends AbstractAnalysisETLAction {
    private static BILogger LOGGER = BILoggerFactory.getLogger(BIEditAnalysisETLTableAction.class);

    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = BIUserAuthUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        JSONObject jo = new JSONObject();
        jo.put("id", tableId);
        jo.put("name", BIAnalysisETLManagerCenter.getAliasManagerProvider().getAliasName(tableId, userId));
        AnalysisBusiTable busiTable = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
        jo.put("describe", busiTable.getDescribe());
        JSONObject source = busiTable.getTableSource().createJSON();
        JSONObject table;
        JSONArray items;
        if (source.has(Constants.ITEMS)) {
            table = source;
        } else {
            table = new JSONObject();
            items = new JSONArray();
            items.put(source);
            table.put(Constants.ITEMS, items);
        }
        jo.put("table", table);

        // 被自己和其他的螺旋分析使用过
        JSONArray allUsedTables = new JSONArray();
        allUsedTables.put(tableId);
        for (BusinessTable anaTable : BIAnalysisETLManagerCenter.getDataSourceManager().getAllBusinessTable()) {
            if (!ComparatorUtils.equals(tableId, anaTable.getID().getIdentity())) {
                Set<BusinessTable> usedTables = ((AnalysisBusiTable) anaTable).getUsedTables();
                if (usedTables.contains(new BIBusinessTable(new BITableID(tableId)))) {
                    jo.put("used", true);
                    allUsedTables.put(anaTable.getID().getIdentityValue());
                }
            }
        }
        jo.put("usedTables", allUsedTables);

        jo.put("usedTemplate", getUsedTempLateList(tableId, BIUserAuthUtils.getCurrentUserID(req)));

        WebUtils.printAsJSON(res, jo);
    }

    private JSONObject getUsedTempLateList(String tableId, long userId) throws Exception {
        JSONObject detailUsedList = new JSONObject();

        //暂时先检查管理员的模板了
        List<BIReportNode> nodeList = BIDAOUtils.getBIDAOManager().findByUserID(userId);
        boolean isInUse = false;
        for (BIReportNode reportNode : nodeList) {
            try {
                JSONObject reportSetting = BIReadReportUtils.getBIReadReportManager().getBIReportNodeJSON(reportNode);
                if (reportSetting.has("widgets")) {
                    JSONObject widgets = reportSetting.getJSONObject("widgets");
                    Iterator<String> widgetIds = widgets.keys();
                    while (widgetIds.hasNext()) {
                        JSONObject widget = widgets.getJSONObject(widgetIds.next());
                        if (isWidgetUseTable(tableId, widget)) {
                            String templateName = reportNode.getDisplayName();
                            JSONArray arr = detailUsedList.optJSONArray(templateName);
                            if (arr == null) {
                                arr = new JSONArray();
                                detailUsedList.put(templateName, arr);
                            }
                            arr.put(widget.optString("name"));
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("The report file was not found when check table used in all reports, skip it");
                LOGGER.warn(e.getMessage(), e);
            }

        }

        return detailUsedList;
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

    @Override
    public String getCMD() {
        return "edit_table";
    }
}
