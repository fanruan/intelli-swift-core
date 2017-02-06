package com.fr.bi.resource;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.base.TemplateUtils;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.report.map.BIMapInfoManager;
import com.fr.bi.conf.report.map.BIWMSManager;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.stable.bridge.Transmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by Wang on 2017/1/18.
 */
public class BaseResouceHelper {
    public static Transmitter DataTransmitter = new Transmitter() {

        @Override
        public String transmit(HttpServletRequest req, HttpServletResponse res, String[] files) {
            return getDataJs(req, files);
        }
    };

    public static class FormulaTransmitter implements Transmitter {
        private String formula = null;

        @Override
        public String transmit(HttpServletRequest req, HttpServletResponse res, String[] files) {
            return transmit(files);
        }

        public String transmit(String[] files) {
            if (formula != null) {
                return formula;
            }
            synchronized (this) {
                String res = formula;
                if (res == null) {
                    res = getFormulaJS(files);
                    if (!StableUtils.isDebug()) {
                        formula = res;
                    }
                }
                return res;
            }
        }
    }

    public static class MapConstantTransmitter implements Transmitter {

        private JSONObject innerMapInfo;
        private JSONObject customMapInfo;
        private JSONObject wmsInfo;

        @Override
        public String transmit(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String[] files) {
            Map<String, JSONObject> map = new HashMap<String, JSONObject>();
            StringBuilder buffer = new StringBuilder();
            try {
                innerMapInfo = new JSONObject();
                customMapInfo = new JSONObject();
                wmsInfo = new JSONObject();
                map.put("wmsInfo", wmsInfo);
                map.put("custom_map_info", customMapInfo);
                map.put("inner_map_info", innerMapInfo);
                customMapInfo.put("MAP_NAME", new JSONObject());
                customMapInfo.put("MAP_TYPE_NAME", new JSONObject());
                customMapInfo.put("MAP_PATH", new JSONObject());
                customMapInfo.put("MAP_LAYER", new JSONObject());
                customMapInfo.put("MAP_PARENT_CHILDREN", new JSONObject());
                innerMapInfo.put("MAP_NAME", new JSONObject());
                innerMapInfo.put("MAP_TYPE_NAME", new JSONObject());
                innerMapInfo.put("MAP_PATH", new JSONObject());
                innerMapInfo.put("MAP_LAYER", new JSONObject());
                innerMapInfo.put("MAP_PARENT_CHILDREN", new JSONObject());

                formatWMSData();
                formatMapData();
                for (String file : files) {
                    buffer.append(TemplateUtils.renderTemplate(file, map));
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage());
            }
            return buffer.toString();
        }

        private void formatMapData() throws JSONException {
            BIMapInfoManager manager = BIMapInfoManager.getInstance();
            for (Map.Entry<String, Integer> innerLayerEntry : manager.getinnerMapLayer().entrySet()) {
                innerMapInfo.getJSONObject("MAP_LAYER").put(innerLayerEntry.getKey(), innerLayerEntry.getValue());
            }
            for (Map.Entry<String, String> innerNameEntry : manager.getinnerMapName().entrySet()) {
                innerMapInfo.getJSONObject("MAP_NAME").put(innerNameEntry.getKey(), innerNameEntry.getValue());
            }
            for (Map.Entry<String, String> innerTypeNameEntry : manager.getinnerMapTypeName().entrySet()) {
                innerMapInfo.getJSONObject("MAP_TYPE_NAME").put(innerTypeNameEntry.getKey(), innerTypeNameEntry.getValue());
            }
            for (Map.Entry<String, String> innerPathEntry : manager.getinnerMapPath().entrySet()) {
                innerMapInfo.getJSONObject("MAP_PATH").put(innerPathEntry.getKey(), innerPathEntry.getValue());
            }
            for (Map.Entry<String, List<String>> innerParentChildrenEntry : manager.getinnerMapParentChildrenRelation().entrySet()) {
                innerMapInfo.getJSONObject("MAP_PARENT_CHILDREN").put(innerParentChildrenEntry.getKey(), innerParentChildrenEntry.getValue());
            }
            for (Map.Entry<String, Integer> customLayerEntry : manager.getCustomMapLayer().entrySet()) {
                customMapInfo.getJSONObject("MAP_LAYER").put(customLayerEntry.getKey(), customLayerEntry.getValue());
            }
            for (Map.Entry<String, String> customNameEntry : manager.getCustomMapName().entrySet()) {
                customMapInfo.getJSONObject("MAP_NAME").put(customNameEntry.getKey(), customNameEntry.getValue());
            }
            for (Map.Entry<String, String> customTypeNameEntry : manager.getCustomMapTypeName().entrySet()) {
                customMapInfo.getJSONObject("MAP_TYPE_NAME").put(customTypeNameEntry.getKey(), customTypeNameEntry.getValue());
            }
            for (Map.Entry<String, String> customPathEntry : manager.getCustomMapPath().entrySet()) {
                customMapInfo.getJSONObject("MAP_PATH").put(customPathEntry.getKey(), customPathEntry.getValue());
            }
            for (Map.Entry<String, List<String>> customParentChildrenEntry : manager.getCustomMapParentChildrenRelation().entrySet()) {
                customMapInfo.getJSONObject("MAP_PARENT_CHILDREN").put(customParentChildrenEntry.getKey(), customParentChildrenEntry.getValue());
            }
        }

        private void formatWMSData() throws JSONException {
            BIWMSManager manager = BIWMSManager.getInstance();
            Map<String, JSONObject> map = manager.getWMSInfo();
            for (Map.Entry<String, JSONObject> entry : map.entrySet()) {
                String key = entry.getKey();
                JSONObject value = entry.getValue();
                wmsInfo.put(key, value);
            }
        }
    }

    public static BaseResouceHelper.MapConstantTransmitter MapTransmitter = new BaseResouceHelper.MapConstantTransmitter();

    public static BaseResouceHelper.FormulaTransmitter FormulaTransmitter = new BaseResouceHelper.FormulaTransmitter();

    public static String[] getDataJS() {
        return new String[]{"/com/fr/bi/web/js/template/pool.data.js"};
    }

    public static String[] getMapJS() {
        return new String[]{"/com/fr/bi/web/js/template/map.js"};
    }

    public static String[] getFormulaCollectionJS() {
        return new String[]{"/com/fr/bi/web/js/template/formula.collection.js"};
    }


    private static String getDataJs(HttpServletRequest req, String[] files) {
        long userId = ServiceUtils.getCurrentUserID(req);
        long manageId = UserControl.getInstance().getSuperManagerID();
        JSONObject groups = new JSONObject();
        JSONObject packages = new JSONObject();
        JSONObject relations = new JSONObject();
        JSONObject connections = new JSONObject();
        JSONObject tables = new JSONObject();
        JSONObject source = new JSONObject();
        JSONObject fields = new JSONObject();
        JSONObject translations = new JSONObject();
        JSONObject excelViews = new JSONObject();
        try {
            JSONObject allGroups = BICubeConfigureCenter.getPackageManager().createGroupJSON(userId);
            JSONObject allPacks = BIModuleUtils.createAnalysisPackJSON(userId, req.getLocale());
            List<BIPackageID> authPacks = new ArrayList<BIPackageID>();
            //从分组中去掉allPacks没有的业务包
            Iterator<String> gIds = allGroups.keys();
            while (gIds.hasNext()) {
                String gId = gIds.next();
                JSONObject oneGroup = allGroups.getJSONObject(gId);
                JSONArray nChildren = new JSONArray();
                if (oneGroup.has("children")) {
                    JSONArray children = oneGroup.getJSONArray("children");
                    for (int i = 0; i < children.length(); i++) {
                        JSONObject child = children.getJSONObject(i);
                        if (allPacks.has(child.getString("id"))) {
                            nChildren.put(child);
                        }
                    }
                    oneGroup.put("children", nChildren);
                }
                allGroups.put(gId, oneGroup);
            }
            //管理员
            if (manageId == userId) {
                packages = allPacks;
                groups = allGroups;
            } else {
                //前台能看到的业务包
                authPacks = BIModuleUtils.getAvailablePackID(userId);
                for (BIPackageID pId : authPacks) {
                    if (allPacks.has(pId.getIdentityValue())) {
                        packages.put(pId.getIdentityValue(), allPacks.getJSONObject(pId.getIdentityValue()));
                    }
                }

                //分组
                Iterator<String> groupIds = allGroups.keys();
                while (groupIds.hasNext()) {
                    String groupId = groupIds.next();
                    JSONObject group = allGroups.getJSONObject(groupId);
                    JSONArray nChildren = new JSONArray();
                    if (group.has("children")) {
                        JSONArray children = group.getJSONArray("children");
                        for (int i = 0; i < children.length(); i++) {
                            JSONObject child = children.getJSONObject(i);
                            String childId = child.getString("id");
                            if (packages.has(childId)) {
                                nChildren.put(child);
                            }
                        }
                        group.put("children", nChildren);
                    }
                    if (nChildren.length() > 0) {
                        groups.put(groupId, group);
                    }
                }
            }

            translations = BIModuleUtils.createAliasJSON(userId);
            relations = BICubeConfigureCenter.getTableRelationManager().createRelationsPathJSON(manageId);
            excelViews = BIConfigureManagerCenter.getExcelViewManager().createJSON(manageId);
            Set<IBusinessPackageGetterService> packs = BIModuleUtils.getAllPacks(userId);
            for (IBusinessPackageGetterService p : packs) {
                if (manageId != userId && !authPacks.contains(p.getID())) {
                    continue;
                }
                for (BIBusinessTable t : (Set<BIBusinessTable>) p.getBusinessTables()) {
                    JSONObject jo = t.createJSONWithFieldsInfo(userId);
                    JSONObject tableFields = jo.getJSONObject("tableFields");
                    tables.put(t.getID().getIdentityValue(), tableFields);
                    JSONObject fieldsInfo = jo.getJSONObject("fieldsInfo");
                    fields.join(fieldsInfo);
                }
            }
            Set<BITableRelation> connectionSet = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);
            JSONArray connectionJA = new JSONArray();
            for (BITableRelation connection : connectionSet) {
                connectionJA.put(connection.createJSON());
            }
            connections.put("connectionSet", connectionJA);

        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

        Map<String, JSONObject> map = new HashMap<String, JSONObject>();
        map.put("source", source);
        map.put("groups", groups);
        map.put("packages", packages);
        map.put("relations", relations == null ? new JSONObject() : relations);
        map.put("connections", connections);
        map.put("translations", translations);
        map.put("tables", tables);
        map.put("fields", fields);
        map.put("excel_views", excelViews);
        StringBuilder buffer = new StringBuilder();
        try {
            for (String file : files) {
                buffer.append(TemplateUtils.renderTemplate(file, map));
            }
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return buffer.toString();
    }


    private static String getFormulaJS(String[] files) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONArray array = new JSONArray();
        JSONArray formulaJos = FormulaCollections.getAllFormulaObject();
        for (int i = 0; i < formulaJos.length(); i++) {
            try {
                JSONObject formulaJo = formulaJos.getJSONObject(i);
                String name = formulaJo.getString("name");
                array.put(name.toLowerCase());
                array.put(name.toUpperCase());
            } catch (JSONException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }

        map.put("formula", array);
        map.put("formulaJSONs", FormulaCollections.getAllFormulaObject());
        StringBuilder buffer = new StringBuilder();
        try {
            for (String file : files) {
                buffer.append(TemplateUtils.renderTemplate(file, map));
            }
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return buffer.toString();
    }

    public static String[] getThirdCss() {
        return new String[]{
                "com/fr/bi/web/css/base/third/jquery-ui.custom.css",
                "com/fr/bi/web/css/base/third/leaflet.css",
        };
    }

    public static String[] getThirdJs() {
        return new String[]{
                "com/fr/bi/web/js/third/jquery.mousewheel.js",
                "com/fr/bi/web/js/third/jquery.ui.core.js",
                "com/fr/bi/web/js/third/jquery.ui.widget.js",
                "com/fr/bi/web/js/third/jquery.ui.mouse.js",
                "com/fr/bi/web/js/third/jquery.ui.position.js",
                "com/fr/bi/web/js/third/jquery.ui.resizable.js",
                "com/fr/bi/web/js/third/jquery.ui.draggable.js",
                "com/fr/bi/web/js/third/jquery.ui.droppable.js",
                "com/fr/bi/web/js/third/jquery.ui.sortable.js",
                "com/fr/bi/web/js/third/es5-sham.js",
                "com/fr/bi/web/js/third/d3.js",
                "com/fr/bi/web/js/third/raphael.js",
                "com/fr/bi/web/js/third/vancharts-all.js",
                "com/fr/bi/web/js/third/leaflet.js"
        };
    }

    public static String[] getBaseCss() {
        return new String[]{
                "com/fr/bi/web/css/normalize.css",
                "com/fr/bi/web/css/base/base.css",
                "com/fr/bi/web/css/base/dom.css",
                "com/fr/bi/web/css/base/single/single.css",
                "com/fr/bi/web/css/base/single/text.css",
                "com/fr/bi/web/css/base/single/input/input.css",
                "com/fr/bi/web/css/base/single/input/file.css",
                "com/fr/bi/web/css/base/single/button/button.css",
                "com/fr/bi/web/css/base/single/button/listitem.css",
                "com/fr/bi/web/css/base/single/button/item.multiselect.css",
                "com/fr/bi/web/css/base/single/button/item.texticon.css",
                "com/fr/bi/web/css/base/single/button/item.singleselect.icontext.css",
                "com/fr/bi/web/css/base/single/button/item.singleselectradio.css",
                "com/fr/bi/web/css/base/single/bar/bar.css",
                "com/fr/bi/web/css/base/single/editor/editor.css",
                "com/fr/bi/web/css/base/single/editor/editor.state.css",
                "com/fr/bi/web/css/base/single/editor/editor.state.simple.css",
                "com/fr/bi/web/css/base/single/editor/editor.record.css",
                "com/fr/bi/web/css/base/single/editor/editor.sign.css",
                "com/fr/bi/web/css/base/single/editor/editor.shelter.css",
                "com/fr/bi/web/css/base/single/editor/editor.textarea.css",
                "com/fr/bi/web/css/base/single/editor/editor.multifile.css",
                "com/fr/bi/web/css/base/single/tip/tip.css",
                "com/fr/bi/web/css/base/single/tip/tip.bubble.css",
                "com/fr/bi/web/css/base/single/tip/tip.toast.css",
                "com/fr/bi/web/css/base/single/tip/tip.tooltip.css",

                "com/fr/bi/web/css/base/wrapper/inline.center.css",
                "com/fr/bi/web/css/base/wrapper/inline.vertical.css",
                "com/fr/bi/web/css/base/wrapper/flex.center.css",
                "com/fr/bi/web/css/base/wrapper/flex.horizontal.css",
                "com/fr/bi/web/css/base/wrapper/flex.vertical.center.css",
                "com/fr/bi/web/css/base/wrapper/flex.wrapper.center.css",
                "com/fr/bi/web/css/base/wrapper/flex.wrapper.horizontal.css",
                "com/fr/bi/web/css/base/wrapper/flex.wrapper.vertical.center.css",

                "com/fr/bi/web/css/base/view/floatboxview.css",
                "com/fr/bi/web/css/base/view/popupview.css",
                "com/fr/bi/web/css/base/view/scrollview.css",
                "com/fr/bi/web/css/base/combination/combo.css",
                "com/fr/bi/web/css/base/combination/searcher.css",
                "com/fr/bi/web/css/base/combination/expander/condition.expander.css",
                "com/fr/bi/web/css/base/colorpicker/button.colorpicker.css",
                "com/fr/bi/web/css/base/colorpicker/colorpicker.css",
                "com/fr/bi/web/css/base/chart/chart.combine.css",
                "com/fr/bi/web/css/base/colorpicker/editor.colorpicker.css",
                "com/fr/bi/web/css/base/pager/pager.css",
                "com/fr/bi/web/css/base/pager/pager.direction.css",
                "com/fr/bi/web/css/base/pager/pager.all.count.css",
                "com/fr/bi/web/css/base/segment/button.segment.css",
                "com/fr/bi/web/css/base/segment/segment.css",
                "com/fr/bi/web/css/base/trigger/trigger.css",
                "com/fr/bi/web/css/base/trigger/trigger.icon.css",
                "com/fr/bi/web/css/base/trigger/trigger.editor.css",
                "com/fr/bi/web/css/base/trigger/trigger.selecttext.css",
                "com/fr/bi/web/css/base/trigger/trigger.selecttextsmall.css",
                "com/fr/bi/web/css/base/tree/display.tree.css",
                "com/fr/bi/web/css/base/tree/branch.tree.css",
                "com/fr/bi/web/css/base/foundation/bi.message.css",
                "com/fr/bi/web/css/base/formula/codemirror.css",
                "com/fr/bi/web/css/base/table/table.cell.css",
                "com/fr/bi/web/css/base/table/table.header.cell.css",
                "com/fr/bi/web/css/base/table/table.footer.cell.css",
                "com/fr/bi/web/css/base/table/table.header.css",
                "com/fr/bi/web/css/base/table/table.footer.css",
                "com/fr/bi/web/css/base/table/table.css",
                "com/fr/bi/web/css/base/table/table.tree.css",
                "com/fr/bi/web/css/base/table/table.grid.cell.css",
                "com/fr/bi/web/css/base/table/table.grid.scrollbar.css",
                "com/fr/bi/web/css/base/table/table.collection.cell.css",
                "com/fr/bi/web/css/base/table/table.resizable.cell.css",
                "com/fr/bi/web/css/base/table/table.resizable.css",
                "com/fr/bi/web/css/base/layer/panel.css",
                "com/fr/bi/web/css/base/layer/layer.searcher.css",
                "com/fr/bi/web/css/base/layer/layer.multiselect.css",
                "com/fr/bi/web/css/base/layer/layer.panel.css",
                "com/fr/bi/web/css/base/reqloading/loading.request.css",
                "com/fr/bi/web/css/base/logintimeout/login.timeout.css",

                "com/fr/bi/web/css/utils/widget.css",
                "com/fr/bi/web/css/utils/common.css",
                "com/fr/bi/web/css/utils/cursor.css",
                "com/fr/bi/web/css/utils/font.css",
                "com/fr/bi/web/css/utils/icon.css",
                "com/fr/bi/web/css/utils/animate.css",
                "com/fr/bi/web/css/utils/background.css",
                "com/fr/bi/web/css/utils/overflow.css",
                "com/fr/bi/web/css/utils/pos.css",
                "com/fr/bi/web/css/utils/sizing.css",
                "com/fr/bi/web/css/utils/special.css",

                //默认主题配色
                "com/fr/bi/web/css/themes/default.css",
                "com/fr/bi/web/css/themes/light.css",
                "com/fr/bi/web/css/themes/dark.css",


                //基础控件---start

                "com/fr/bi/web/css/widget/base/tip/tip.helper.css",

                //文本框控件
                "com/fr/bi/web/css/widget/base/editor/editor.text.css",
                "com/fr/bi/web/css/widget/base/editor/editor.text.small.css",
                "com/fr/bi/web/css/widget/base/editor/editor.search.css",
                "com/fr/bi/web/css/widget/base/editor/editor.href.css",
                "com/fr/bi/web/css/widget/base/editor/editor.sign.style.css",
                "com/fr/bi/web/css/widget/base/editor/editor.search.small.css",
                "com/fr/bi/web/css/widget/base/editor/editor.href.css",

                //segment控件
                "com/fr/bi/web/css/widget/base/segment/button.line.segment.css",
                "com/fr/bi/web/css/widget/base/segment/segment.line.css",

                //masker
                "com/fr/bi/web/css/widget/base/mask/loading.mask.css",

                //toolbar
                "com/fr/bi/web/css/widget/base/toolbar/toolbar.progress.processor.css",
                "com/fr/bi/web/css/widget/base/toolbar/toolbar.progress.bar.css",
                "com/fr/bi/web/css/widget/base/toolbar/toolbar.progress.css",


                //iconcombo
                "com/fr/bi/web/css/widget/base/combo/iconcombo/combo.icon.css",

                ///--end


                //日期控件
                "com/fr/bi/web/css/widget/date/trigger.date.css",
                "com/fr/bi/web/css/widget/date/calendar/picker.date.css",


                //年份控件
                "com/fr/bi/web/css/widget/year/trigger.year.css",
                "com/fr/bi/web/css/widget/year/popup.year.css",
                "com/fr/bi/web/css/widget/year/combo.year.css",

                //web控件
                "com/fr/bi/web/css/widget/web/web.css",

                //月份控件
                "com/fr/bi/web/css/widget/month/trigger.month.css",
                "com/fr/bi/web/css/widget/month/popup.month.css",
                "com/fr/bi/web/css/widget/month/combo.month.css",

                //季度控件
                "com/fr/bi/web/css/widget/quarter/trigger.quarter.css",
                "com/fr/bi/web/css/widget/quarter/popup.quarter.css",
                "com/fr/bi/web/css/widget/quarter/combo.quarter.css",


                //下拉列表
                "com/fr/bi/web/css/widget/downlist/popup.downlist.css",
                "com/fr/bi/web/css/widget/downlist/combo.downlist.css",

                //过滤
                "com/fr/bi/web/css/widget/filterpane/abstract.item.filter.css",
                "com/fr/bi/web/css/widget/filterpane/expander.filter.css",
                "com/fr/bi/web/css/widget/filterpane/pane.filter.css",
                "com/fr/bi/web/css/widget/filterpane/operation.filter.css",

                //公式输入框
                "com/fr/bi/web/css/widget/formula/group.symbol.css",
                "com/fr/bi/web/css/widget/formula/pane.function.css",
                "com/fr/bi/web/css/widget/formula/editor.search.css",
                "com/fr/bi/web/css/widget/formula/insert.formula.css",
                "com/fr/bi/web/css/widget/formula/item.button.text.treeleaf.css",
                "com/fr/bi/web/css/widget/formula/tree.function.css",
                "com/fr/bi/web/css/widget/formula/tree.fieldname.css",

                //日期
                "com/fr/bi/web/css/widget/multidate/multidate.combo.css",
                "com/fr/bi/web/css/widget/multidate/multidate.popup.css",
                "com/fr/bi/web/css/widget/multidate/multidate.year.css",
                "com/fr/bi/web/css/widget/multidate/multidate.quarter.css",
                "com/fr/bi/web/css/widget/multidate/multidate.month.css",
                "com/fr/bi/web/css/widget/multidate/multidate.week.css",
                "com/fr/bi/web/css/widget/multidate/multidate.day.css",
                "com/fr/bi/web/css/widget/multidate/multidate.segment.css",

                //时间区间
                "com/fr/bi/web/css/widget/timeinterval/timeinterval.css",

                //单值滑块
                "com/fr/bi/web/css/widget/singleslider/track/widget.track.css",
                "com/fr/bi/web/css/widget/singleslider/slider/widget.slider.css",
                "com/fr/bi/web/css/widget/singleslider/singleslider.css",

                //选择字段树
                "com/fr/bi/web/css/widget/selectdata/tree/node/node.level0.css",
                "com/fr/bi/web/css/widget/selectdata/tree/node/node.level1.css",
                "com/fr/bi/web/css/widget/selectdata/tree/treeitem/item.level0.css",
                "com/fr/bi/web/css/widget/selectdata/tree/treeitem/item.level1.css",
                "com/fr/bi/web/css/widget/selectdata/tree/expander.selectdata.css",
                "com/fr/bi/web/css/widget/selectdata/tree/tree.selectdata.css",
                "com/fr/bi/web/css/widget/selectdata/searchpane/segment.search.selectdata.css",
                "com/fr/bi/web/css/widget/selectdata/searchpane/result.search.selectdata.css",
                "com/fr/bi/web/css/widget/selectdata/searchpane/searcher.selectdata.css",
                "com/fr/bi/web/css/widget/selectdata/searchpane/tab.search.selectdata.css",

                //数值区间控件
                "com/fr/bi/web/css/widget/numericalinterval/popup.numericalinterval.css",
                "com/fr/bi/web/css/widget/numericalinterval/numericalinterval.css",

                //可以设置参数的下拉框
                "com/fr/bi/web/css/widget/paramsettingcombo/param2.date.item.css",
                "com/fr/bi/web/css/widget/paramsettingcombo/param1.date.item.css",
                "com/fr/bi/web/css/widget/paramsettingcombo/param0.date.item.css",
                "com/fr/bi/web/css/widget/paramsettingcombo/yearcombo/combo.param.year.css",
                "com/fr/bi/web/css/widget/paramsettingcombo/datecombo/combo.param.date.css",
                "com/fr/bi/web/css/widget/paramsettingcombo/datecombo/combo.param.date.css",
                "com/fr/bi/web/css/widget/paramsettingcombo/dateintervalcombo/combo.param.dateinterval.css",
                "com/fr/bi/web/css/widget/paramsettingcombo/yearmonthcombo/combo.param.yearmonth.css",
                "com/fr/bi/web/css/widget/paramsettingcombo/yearseasoncombo/combo.param.yearseason.css",

                //复选下拉框
                "com/fr/bi/web/css/widget/multiselect/trigger/button.checkselected.css",
                "com/fr/bi/web/css/widget/multiselect/multiselect.combo.css",
                "com/fr/bi/web/css/widget/multiselect/multiselect.trigger.css",
                "com/fr/bi/web/css/widget/multiselect/check/multiselect.display.css",
                "com/fr/bi/web/css/widget/multiselect/check/multiselect.check.pane.css",
                "com/fr/bi/web/css/widget/multiselect/search/multiselect.search.pane.css",

                //文本列表
                "com/fr/bi/web/css/widget/multistringlist/multistringlist.css",

                "com/fr/bi/web/css/widget/selecttable/single/button.databasetable.css",

                //移动到分组
                "com/fr/bi/web/css/widget/move2group/button.add.move2group.css",
                "com/fr/bi/web/css/widget/move2group/combo.move2group.css",

                //复制到分组
                "com/fr/bi/web/css/widget/copy2group/button.add.copy2group.css",
                "com/fr/bi/web/css/widget/copy2group/combo.copy2group.css",


                //单选下拉树
                "com/fr/bi/web/css/widget/singletree/combo.single.tree.css",

                //文本标签
                "com/fr/bi/web/css/widget/listlabel/listlabel.css",
                "com/fr/bi/web/css/widget/listlabel/group.item.listlabel.css",

                //树标签
                "com/fr/bi/web/css/widget/treelabel/treelabel.css",
                "com/fr/bi/web/css/widget/treelabel/treelabel.view.css",

                //文本工具栏
                "com/fr/bi/web/css/widget/texttoolbar/alignchooser/texttoolbar.alignchooser.css",
                "com/fr/bi/web/css/widget/texttoolbar/texttoolbar.css",

                //文本编辑区
                "com/fr/bi/web/css/widget/textarea/textarea.css",

                //图片组件
                "com/fr/bi/web/css/widget/image/uploadimage.css",
                "com/fr/bi/web/css/widget/image/button/upload.css",
                "com/fr/bi/web/css/widget/image/button/href/image.button.href.css",
                "com/fr/bi/web/css/widget/image/button/size/image.button.size.css",

                //可选下拉树
                "com/fr/bi/web/css/widget/selecttree/combo.select.tree.css",

                //多层单选下拉树


                //复选下拉树
                "com/fr/bi/web/css/widget/multitree/check/multi.tree.check.pane.css",
                "com/fr/bi/web/css/widget/multitree/trigger/multi.tree.button.checkselected.css",
                "com/fr/bi/web/css/widget/multitree/multi.tree.trigger.css",
                "com/fr/bi/web/css/widget/multitree/display.multi.tree.css",
                "com/fr/bi/web/css/widget/multitree/search.multi.tree.css",
                "com/fr/bi/web/css/widget/multitree/popup.multi.tree.css",
                "com/fr/bi/web/css/widget/multitree/multi.tree.combo.css",

                //树列表
                "com/fr/bi/web/css/widget/multitreelist/multitreelist.popup.css",

                //选色控件
                "com/fr/bi/web/css/widget/colorchooser/colorchooser.trigger.css",
                "com/fr/bi/web/css/widget/colorchooser/colorchooser.popup.css",

                //路径选择
                "com/fr/bi/web/css/widget/pathchooser/pathregion.css",
                "com/fr/bi/web/css/widget/pathchooser/pathchooser.css",

                //文件管理
                "com/fr/bi/web/css/widget/filemanager/items/item.file.filemanager.css",
                "com/fr/bi/web/css/widget/filemanager/items/item.folder.filemanager.css",
                "com/fr/bi/web/css/widget/filemanager/nav/button/button.nav.filemanager.css",
                "com/fr/bi/web/css/widget/filemanager/nav/nav.filemanager.css",
                "com/fr/bi/web/css/widget/filemanager/filemanager.css",


                //关联视图
                "com/fr/bi/web/css/widget/relationview/relationview.item.css",
                "com/fr/bi/web/css/widget/relationview/relationview.region.css",


                //预览表
                "com/fr/bi/web/css/widget/previewtable/previewtable.cell.css",
                "com/fr/bi/web/css/widget/previewtable/previewtable.header.cell.css",
                "com/fr/bi/web/css/widget/previewtable/previewtable.css",

                //列可拖拽排序表
                "com/fr/bi/web/css/widget/sortabletable/sortabletable.css",

                //螺旋分析tab控件
                "com/fr/bi/web/css/widget/dynamicgrouptab/dynamicgroup.tab.buttongroup.css",

                //Excel表
                "com/fr/bi/web/css/widget/exceltable/exceltable.cell.css",
                "com/fr/bi/web/css/widget/exceltable/exceltable.header.cell.css",
                "com/fr/bi/web/css/widget/exceltable/exceltable.css",

                //分页表格
                "com/fr/bi/web/css/widget/pagetable/pagetable.cell.css",
                "com/fr/bi/web/css/widget/pagetable/pagetable.css",

                //带序号表格
                "com/fr/bi/web/css/widget/sequencetable/numbercell.sequencetable.css",
                "com/fr/bi/web/css/widget/sequencetable/listnumber.sequencetable.css",
                "com/fr/bi/web/css/widget/sequencetable/treenumber.sequencetable.css",
                "com/fr/bi/web/css/widget/sequencetable/dynamicnumber.sequencetable.css",
                "com/fr/bi/web/css/widget/sequencetable/sequencetable.css",


                //布局
                "com/fr/bi/web/css/widget/arrangement/arrangement.droppable.css",
                "com/fr/bi/web/css/widget/arrangement/arrangement.block.css",
                "com/fr/bi/web/css/widget/arrangement/arrangement.css",


                "com/fr/bi/web/css/widget/interactivearrangement/interactivearrangement.css",

                "com/fr/bi/web/css/widget/timesetting/timesetting.day.css",
                "com/fr/bi/web/css/widget/timesetting/timesetting.hour.css",

                /**
                 * components
                */
                //模板管理
                "com/fr/bi/web/css/components/templatemanager/liststyleitem/item.file.templatemanager.css",
                "com/fr/bi/web/css/components/templatemanager/liststyleitem/item.folder.templatemanager.css",
                "com/fr/bi/web/css/components/templatemanager/cardstyleitem/report.cardview.item.css",
                "com/fr/bi/web/css/components/templatemanager/cardstyleitem/folder.cardview.item.css",
                "com/fr/bi/web/css/components/templatemanager/items/item.folder.templatemanager.css",
                "com/fr/bi/web/css/components/templatemanager/tools/reportsearchresult.pane.css",
                "com/fr/bi/web/css/components/templatemanager/tools/foldermoveto.pane.css",
                "com/fr/bi/web/css/components/templatemanager/tools/share/sharereport.pane.css",
                "com/fr/bi/web/css/components/templatemanager/tools/share/selecteduser.grouplist.css",
                "com/fr/bi/web/css/components/templatemanager/tools/share/selecteduser.button.css",
                "com/fr/bi/web/css/components/templatemanager/tools/share/usersearchresult.pane.css",
                "com/fr/bi/web/css/components/templatemanager/templatemanager.css",

                //所有模板
                "com/fr/bi/web/css/components/allreports/allreports.css",
                "com/fr/bi/web/css/components/allreports/allreports.filter.css",
                "com/fr/bi/web/css/components/allreports/item/allreports.list.item.css",
                "com/fr/bi/web/css/components/allreports/item/allreports.card.item.css",
                "com/fr/bi/web/css/components/allreports/hangout/reporthangout.css",

                //分享给我的
                "com/fr/bi/web/css/components/share/sharetome.css",
                "com/fr/bi/web/css/components/share/sharetome.singleuser.css",

                //excelview
                "com/fr/bi/web/css/components/excel/excelview/excelviewdisplaymanager.css",
        };
    }

    public static String[] getBaseJs() {
        return new String[]{
                //core
                "com/fr/bi/web/js/core/foundation.js",
                "com/fr/bi/web/js/core/underscore.js",
                "com/fr/bi/web/js/core/mvc/fbi.js",
                "com/fr/bi/web/js/core/mvc/factory/factory.js",
                "com/fr/bi/web/js/core/mvc/router/rooter.js",

                //const
                "com/fr/bi/web/js/data/data.js",
                "com/fr/bi/web/js/data/constant/biconst.js",
                "com/fr/bi/web/js/data/constant/constant.js",
                "com/fr/bi/web/js/data/constant/strings.js",
                "com/fr/bi/web/js/data/constant/enums.js",
                "com/fr/bi/web/js/data/constant/colors.js",
                "com/fr/bi/web/js/data/constant/attrs.js",
                "com/fr/bi/web/js/data/pool/pool.js",
                "com/fr/bi/web/js/data/pool/pool.buffer.js",
                "com/fr/bi/web/js/data/pool/pool.sharing.js",
                "com/fr/bi/web/js/data/req/req.js",
                "com/fr/bi/web/js/data/source/source.js",


                //base
                "com/fr/bi/web/js/base/var.js",
                "com/fr/bi/web/js/base/status.js",
                "com/fr/bi/web/js/base/events.js",
                "com/fr/bi/web/js/base/base.js",
                "com/fr/bi/web/js/base/plugin.js",
                "com/fr/bi/web/js/base/cache.js",
                "com/fr/bi/web/js/base/controller.js",
                "com/fr/bi/web/js/base/dom.js",
                "com/fr/bi/web/js/base/function.js",
                "com/fr/bi/web/js/base/model.js",
                "com/fr/bi/web/js/base/special.js",
                "com/fr/bi/web/js/base/view.js",
                "com/fr/bi/web/js/base/widget.js",

                "com/fr/bi/web/js/base/proto/array.js",
                "com/fr/bi/web/js/base/proto/number.js",
                "com/fr/bi/web/js/base/proto/date.js",
                "com/fr/bi/web/js/base/proto/function.js",
                "com/fr/bi/web/js/base/utils/base64.js",
                "com/fr/bi/web/js/base/utils/md5.js",
                "com/fr/bi/web/js/base/utils/xml.js",
                "com/fr/bi/web/js/base/utils/chinesePY.js",
                "com/fr/bi/web/js/base/utils/queue.js",
                "com/fr/bi/web/js/base/utils/linkedHashMap.js",
                "com/fr/bi/web/js/base/utils/tree.js",
                "com/fr/bi/web/js/base/utils/vector.js",
                "com/fr/bi/web/js/base/utils/set.js",
                "com/fr/bi/web/js/base/utils/lru.js",
                "com/fr/bi/web/js/base/utils/heap.js",
                "com/fr/bi/web/js/base/utils/integerBufferSet.js",
                "com/fr/bi/web/js/base/utils/prefixIntervalTree.js",
                "com/fr/bi/web/js/base/utils/helper.scroll.js",
                "com/fr/bi/web/js/base/utils/tableRowBuffer.js",
                "com/fr/bi/web/js/base/utils/cellSizeAndPositionManager.js",
                "com/fr/bi/web/js/base/utils/sectionManager.js",
                "com/fr/bi/web/js/base/utils/detectElementResize.js",
                "com/fr/bi/web/js/base/utils/aspect.js",
                "com/fr/bi/web/js/base/utils/events/eventlistener.js",
                "com/fr/bi/web/js/base/utils/events/wheelhandler.js",
                "com/fr/bi/web/js/base/utils/events/mousemovetracker.js",

                "com/fr/bi/web/js/base/parsers/expression.js",
                "com/fr/bi/web/js/base/parsers/path.js",
                "com/fr/bi/web/js/base/parsers/watcher.js",

                "com/fr/bi/web/js/base/action/action.js",
                "com/fr/bi/web/js/base/action/action.show.js",
                "com/fr/bi/web/js/base/action/action.show.effect.js",
                "com/fr/bi/web/js/base/action/action.show.scale.js",
                "com/fr/bi/web/js/base/behavior/behavior.js",
                "com/fr/bi/web/js/base/behavior/behavior.redmark.js",
                "com/fr/bi/web/js/base/behavior/behavior.highlight.js",
                "com/fr/bi/web/js/base/controller/controller.broadcast.js",
                "com/fr/bi/web/js/base/controller/controller.floatbox.js",
                "com/fr/bi/web/js/base/controller/controller.layer.js",
                "com/fr/bi/web/js/base/controller/controller.masker.js",
                "com/fr/bi/web/js/base/controller/controller.resizer.js",
                "com/fr/bi/web/js/base/controller/router.floatbox.js",
                "com/fr/bi/web/js/base/controller/controller.bubbles.js",
                "com/fr/bi/web/js/base/controller/controller.tooltips.js",
                "com/fr/bi/web/js/base/loader/loader.style.js",
                "com/fr/bi/web/js/base/event/event.list.js",
                "com/fr/bi/web/js/base/event/off.list.js",
                "com/fr/bi/web/js/base/event/listener.list.js",
                "com/fr/bi/web/js/base/logic/logic.js",
                "com/fr/bi/web/js/base/logic/logic.layout.js",
                "com/fr/bi/web/js/base/listener/listener.show.js",

                "com/fr/bi/web/js/base/module/el.js",
                "com/fr/bi/web/js/base/module/pane.js",
                "com/fr/bi/web/js/base/module/single/single.js",
                "com/fr/bi/web/js/base/module/single/text.js",
                "com/fr/bi/web/js/base/module/single/a/a.js",
                "com/fr/bi/web/js/base/module/single/img/img.js",
                "com/fr/bi/web/js/base/module/single/icon/icon.js",
                "com/fr/bi/web/js/base/module/single/button/button.basic.js",
                "com/fr/bi/web/js/base/module/single/button/button.node.js",
                "com/fr/bi/web/js/base/module/single/button/buttons/button.js",
                "com/fr/bi/web/js/base/module/single/button/buttons/button.icon.js",
                "com/fr/bi/web/js/base/module/single/button/buttons/button.image.js",
                "com/fr/bi/web/js/base/module/single/button/buttons/button.text.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/textitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/texticonitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/icontextitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/blankicontextitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/blankiconicontextitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/blankicontexticonitem.js",
                "com/fr/bi/web/js/base/module/single/button/listitem/icontexticonitem.js",
                "com/fr/bi/web/js/base/module/single/button/node/textnode.js",
                "com/fr/bi/web/js/base/module/single/button/node/texticonnode.js",
                "com/fr/bi/web/js/base/module/single/button/node/icontextnode.js",
                "com/fr/bi/web/js/base/module/single/button/node/icontexticonnode.js",
                "com/fr/bi/web/js/base/module/single/input/input.js",
                "com/fr/bi/web/js/base/module/single/input/file.js",
                "com/fr/bi/web/js/base/module/single/input/checkbox.js",
                "com/fr/bi/web/js/base/module/single/input/radio.js",
                "com/fr/bi/web/js/base/module/single/bar/bar.loading.js",
                "com/fr/bi/web/js/base/module/single/editor/editor.js",
                "com/fr/bi/web/js/base/module/single/editor/editor.code.js",
                "com/fr/bi/web/js/base/module/single/editor/editor.textarea.js",
                "com/fr/bi/web/js/base/module/single/editor/editor.multifile.js",
                "com/fr/bi/web/js/base/module/single/label/label.js",
                "com/fr/bi/web/js/base/module/single/link/link.js",
                "com/fr/bi/web/js/base/module/single/tip/tip.js",
                "com/fr/bi/web/js/base/module/single/trigger/trigger.js",
                "com/fr/bi/web/js/base/module/single/iframe/iframe.js",

                "com/fr/bi/web/js/base/module/combination/combo.js",
                "com/fr/bi/web/js/base/module/combination/switcher.js",
                "com/fr/bi/web/js/base/module/combination/expander.js",
                "com/fr/bi/web/js/base/module/combination/group.combo.js",
                "com/fr/bi/web/js/base/module/combination/group.button.js",
                "com/fr/bi/web/js/base/module/combination/tree.button.js",
                "com/fr/bi/web/js/base/module/combination/map.button.js",
                "com/fr/bi/web/js/base/module/combination/tab.js",
                "com/fr/bi/web/js/base/module/combination/navigation.js",
                "com/fr/bi/web/js/base/module/combination/loader.js",
                "com/fr/bi/web/js/base/module/combination/searcher.js",

                "com/fr/bi/web/js/base/module/farbtastic/farbtastic.js",

                "com/fr/bi/web/js/base/module/chart/chart.js",

//                "com/fr/bi/web/js/base/module/canvas/canvas.js",

                "com/fr/bi/web/js/base/module/svg/svg.js",

                "com/fr/bi/web/js/base/module/pager/pager.js",

                "com/fr/bi/web/js/base/module/tree/treeview.js",
                "com/fr/bi/web/js/base/module/tree/customtree.js",
                "com/fr/bi/web/js/base/module/tree/synctree.js",
                "com/fr/bi/web/js/base/module/tree/parttree.js",

                "com/fr/bi/web/js/base/module/table/table.cell.js",
                "com/fr/bi/web/js/base/module/table/table.header.cell.js",
                "com/fr/bi/web/js/base/module/table/table.footer.cell.js",
                "com/fr/bi/web/js/base/module/table/table.header.js",
                "com/fr/bi/web/js/base/module/table/table.footer.js",
                "com/fr/bi/web/js/base/module/table/table.js",
                "com/fr/bi/web/js/case/table/table.tree.js",
                "com/fr/bi/web/js/base/module/table/table.grid.cell.js",
                "com/fr/bi/web/js/base/module/table/table.grid.scrollbar.js",
                "com/fr/bi/web/js/base/module/table/table.grid.js",
                "com/fr/bi/web/js/base/module/table/table.collection.cell.js",
                "com/fr/bi/web/js/base/module/table/table.collection.js",
                "com/fr/bi/web/js/base/module/table/table.resizable.cell.js",
                "com/fr/bi/web/js/base/module/table/table.resizable.js",

//                "com/fr/bi/web/js/base/module/table/table.fix.js",

                "com/fr/bi/web/js/base/module/grid/grid.js",
                "com/fr/bi/web/js/base/module/collection/collection.js",

                "com/fr/bi/web/js/base/module/layer/layer.floatbox.js",
                "com/fr/bi/web/js/base/module/layer/layer.popup.js",
                "com/fr/bi/web/js/base/module/layer/layer.scroll.js",
                "com/fr/bi/web/js/base/module/layer/layer.searcher.js",

                "com/fr/bi/web/js/base/module/reqloading/loading.request.js",

                /**公式编辑器*/
                "com/fr/bi/web/js/base/module/formula/formulaeditor.js",
                "com/fr/bi/web/js/base/module/formula/codemirror/codemirror.js",
                "com/fr/bi/web/js/base/module/formula/codemirror/addon/show-hint.js",
                "com/fr/bi/web/js/base/module/formula/codemirror/addon/formula-hint.js",
                "com/fr/bi/web/js/base/module/formula/codemirror/addon/formula-mode.js",

                "com/fr/bi/web/js/base/module/foundation/bi.message.js",

                "com/fr/bi/web/js/base/adapter/adapter.floatsection.js",

                "com/fr/bi/web/js/base/wrapper/layout.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.absolute.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.adaptive.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.border.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.card.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.default.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.flow.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.lattice.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.inline.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.tape.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.grid.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.vertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.division.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.window.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.table.js",
                "com/fr/bi/web/js/base/wrapper/layout/layout.td.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/float.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/absolute.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/inline.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.leftrightvertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.vertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/auto.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/float.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/absolute.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/absolute.vertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/inline.vertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.float.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.vertical.js",

                "com/fr/bi/web/js/base/wrapper/layout/flex/flex.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/flex/flex.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/flex/flex.vertical.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/flex/wrapper/flex.wrapper.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/flex/wrapper/flex.wrapper.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/flex/wrapper/flex.wrapper.vertical.center.js",

                "com/fr/bi/web/js/data/utils.js",
                //filter
                "com/fr/bi/web/js/data/filter/filtervalue.factory.js",
                "com/fr/bi/web/js/data/filter/filter.factory.js",
                "com/fr/bi/web/js/data/filter/objectcondition/obj.general.and.filter.js",
                "com/fr/bi/web/js/data/filter/objectcondition/obj.general.or.filter.js",
                "com/fr/bi/web/js/data/filter/objectcondition/obj.single.filter.js",
                "com/fr/bi/web/js/data/filter/objectcondition/filter.obj.factory.js",
                "com/fr/bi/web/js/data/filter/condition/empty.filter.js",
                "com/fr/bi/web/js/data/filter/condition/general.and.filter.js",
                "com/fr/bi/web/js/data/filter/condition/general.or.filter.js",
                "com/fr/bi/web/js/data/filter/condition/single.filter.js",
                "com/fr/bi/web/js/data/filter/number/number.equal.filter.js",
                "com/fr/bi/web/js/data/filter/number/number.inrange.filter.js",
                "com/fr/bi/web/js/data/filter/number/number.kth.filter.js",
                "com/fr/bi/web/js/data/filter/number/number.largethan.or.equal.filter.js",
                "com/fr/bi/web/js/data/filter/number/number.lessthan.filter.js",
                "com/fr/bi/web/js/data/filter/number/number.notequal.filter.js",
                "com/fr/bi/web/js/data/filter/number/number.notinrange.filter.js",
                "com/fr/bi/web/js/data/filter/number/number.notnull.filter.js",
                "com/fr/bi/web/js/data/filter/number/number.null.filter.js",
                "com/fr/bi/web/js/data/filter/string/string.contain.filter.js",
                "com/fr/bi/web/js/data/filter/string/string.endwith.filter.js",
                "com/fr/bi/web/js/data/filter/string/string.in.filter.js",
                "com/fr/bi/web/js/data/filter/string/string.notcontain.filter.js",
                "com/fr/bi/web/js/data/filter/string/string.notendwith.filter.js",
                "com/fr/bi/web/js/data/filter/string/string.notstartwith.filter.js",
                "com/fr/bi/web/js/data/filter/string/string.notin.filter.js",
                "com/fr/bi/web/js/data/filter/string/string.notnull.filter.js",
                "com/fr/bi/web/js/data/filter/string/string.null.filter.js",
                "com/fr/bi/web/js/data/filter/string/string.startwith.filter.js",

                "com/fr/bi/web/js/case/case.js",
                "com/fr/bi/web/js/case/layer/panel.js",
                "com/fr/bi/web/js/case/layer/pane.list.js",
                "com/fr/bi/web/js/case/layer/layer.multiselect.js",
                "com/fr/bi/web/js/case/layer/layer.panel.js",
                "com/fr/bi/web/js/case/list/list.select.js",
                "com/fr/bi/web/js/case/tip/tip.bubble.js",
                "com/fr/bi/web/js/case/tip/tip.toast.js",
                "com/fr/bi/web/js/case/tip/tip.tooltip.js",
                "com/fr/bi/web/js/case/toolbar/toolbar.multiselect.js",

                "com/fr/bi/web/js/case/button/icon/icon.trigger.js",
                "com/fr/bi/web/js/case/button/icon/icon.half.js",
                "com/fr/bi/web/js/case/button/icon/icon.change.js",

                "com/fr/bi/web/js/case/button/item.multiselect.js",
                "com/fr/bi/web/js/case/button/item.singleselect.js",
                "com/fr/bi/web/js/case/button/item.singleselect.icontext.js",
                "com/fr/bi/web/js/case/button/item.singleselect.radio.js",
                "com/fr/bi/web/js/case/button/treeitem/item.icon.treeleaf.js",
                "com/fr/bi/web/js/case/button/treeitem/item.multilayer.icon.treeleaf.js",
                "com/fr/bi/web/js/case/button/treeitem/item.first.treeleaf.js",
                "com/fr/bi/web/js/case/button/treeitem/item.last.treeleaf.js",
                "com/fr/bi/web/js/case/button/treeitem/item.mid.treeleaf.js",
                "com/fr/bi/web/js/case/button/treeitem/item.treetextleaf.js",

                "com/fr/bi/web/js/case/button/node/node.icon.arrow.js",
                "com/fr/bi/web/js/case/button/node/node.multilayer.icon.arrow.js",
                "com/fr/bi/web/js/case/button/node/node.plus.js",
                "com/fr/bi/web/js/case/button/node/node.first.plus.js",
                "com/fr/bi/web/js/case/button/node/node.last.plus.js",
                "com/fr/bi/web/js/case/button/node/node.mid.plus.js",
                "com/fr/bi/web/js/case/button/node/node.arrow.js",
                "com/fr/bi/web/js/case/button/node/node.triangle.js",

                "com/fr/bi/web/js/case/editor/editor.state.js",
                "com/fr/bi/web/js/case/editor/editor.state.simple.js",
                "com/fr/bi/web/js/case/editor/editor.sign.js",
                "com/fr/bi/web/js/case/editor/editor.shelter.js",
                "com/fr/bi/web/js/case/editor/editor.record.js",
                "com/fr/bi/web/js/case/segment/button.segment.js",
                "com/fr/bi/web/js/case/segment/segment.js",
                "com/fr/bi/web/js/case/checkbox/check.treenode.js",
                "com/fr/bi/web/js/case/checkbox/check.first.treenode.js",
                "com/fr/bi/web/js/case/checkbox/check.last.treenode.js",
                "com/fr/bi/web/js/case/checkbox/check.mid.treenode.js",
                "com/fr/bi/web/js/case/checkbox/check.treegroupnode.js",
                "com/fr/bi/web/js/case/checkbox/check.arrowtreegroupnode.js",
                "com/fr/bi/web/js/case/checkbox/check.checkingmarknode.js",

                "com/fr/bi/web/js/case/trigger/trigger.icon.js",
                "com/fr/bi/web/js/case/trigger/trigger.text.js",
                "com/fr/bi/web/js/case/trigger/trigger.text.small.js",
                "com/fr/bi/web/js/case/trigger/trigger.editor.js",
                "com/fr/bi/web/js/case/trigger/trigger.text.select.js",
                "com/fr/bi/web/js/case/trigger/trigger.text.select.small.js",

                "com/fr/bi/web/js/case/calendar/calendar.js",
                "com/fr/bi/web/js/case/calendar/calendar.year.js",

                "com/fr/bi/web/js/case/colorpicker/button/button.colorpicker.js",
                "com/fr/bi/web/js/case/colorpicker/colorpicker.js",
                "com/fr/bi/web/js/case/colorpicker/editor.colorpicker.js",

//                "com/fr/bi/web/js/case/canvas/canvas.complex.js",

                "com/fr/bi/web/js/case/floatbox/floatboxsection.bar.js",

                "com/fr/bi/web/js/case/loader/loader.lazy.js",
                "com/fr/bi/web/js/case/loader/loader.list.js",
                "com/fr/bi/web/js/case/loader/sort.list.js",

                "com/fr/bi/web/js/case/expander/expander.branch.js",
                "com/fr/bi/web/js/case/expander/expander.branch.handstand.js",

                "com/fr/bi/web/js/case/tree/tree.simple.js",
                "com/fr/bi/web/js/case/tree/tree.level.js",
                "com/fr/bi/web/js/case/tree/tree.display.js",
                "com/fr/bi/web/js/case/tree/tree.branch.js",
                "com/fr/bi/web/js/case/tree/tree.branch.handstand.js",

                "com/fr/bi/web/js/case/pager/pager.direction.js",
                "com/fr/bi/web/js/case/pager/pager.all.count.js",

                //各种表格
                "com/fr/bi/web/js/case/table/table.adaptive.js",
                "com/fr/bi/web/js/case/table/table.style.cell.js",
                "com/fr/bi/web/js/case/table/table.tree.js",
                "com/fr/bi/web/js/case/table/table.layertree.js",
                "com/fr/bi/web/js/case/table/table.dynamicsummarytree.js",
                "com/fr/bi/web/js/case/table/table.dynamicsummarylayertree.js",

                //chart
                "com/fr/bi/web/js/case/chart/chart.combine.js",
                "com/fr/bi/web/js/case/chart/factory.charts.js",

                "com/fr/bi/web/js/case/logintimeout/login.timeout.js",

                "com/fr/bi/web/js/case/zclip/zclip.js",


                /**
                 * 基础类控件
                */
                "com/fr/bi/web/js/widget/base/tip/tip.helper.js",

                //text combo
                "com/fr/bi/web/js/widget/base/combo/textvaluecombo/popup.textvalue.js",
                "com/fr/bi/web/js/widget/base/combo/textvaluecombo/combo.textvalue.js",
                "com/fr/bi/web/js/widget/base/combo/textvaluecombo/combo.textvaluesmall.js",

                // text icon check combo
                "com/fr/bi/web/js/widget/base/combo/textvaluecheckcombo/popup.textvaluecheck.js",
                "com/fr/bi/web/js/widget/base/combo/textvaluecheckcombo/combo.textvaluecheck.js",
                "com/fr/bi/web/js/widget/base/combo/textvaluecheckcombo/combo.textvaluechecksmall.js",

                //editor icon check combo
                "com/fr/bi/web/js/widget/base/combo/editoriconcheckcombo/combo.editoriconcheck.js",

                //text icon pane combo
                "com/fr/bi/web/js/widget/base/combo/textvaluedownlistcombo/combo.textvaluedownlist.js",
                "com/fr/bi/web/js/widget/base/combo/textvaluedownlistcombo/trigger.textvaluedownlist.js",

                //单选combo
                "com/fr/bi/web/js/widget/base/combo/staticcombo/combo.static.js",

                //iconcombo
                "com/fr/bi/web/js/widget/base/combo/iconcombo/trigger.iconcombo.js",
                "com/fr/bi/web/js/widget/base/combo/iconcombo/popup.iconcombo.js",
                "com/fr/bi/web/js/widget/base/combo/iconcombo/combo.icon.js",

                //文本框控件
                "com/fr/bi/web/js/widget/base/editor/editor.text.js",
                "com/fr/bi/web/js/widget/base/editor/editor.text.small.js",
                "com/fr/bi/web/js/widget/base/editor/editor.search.js",
                "com/fr/bi/web/js/widget/base/editor/editor.search.small.js",
                "com/fr/bi/web/js/widget/base/editor/editor.adapt.js",
                "com/fr/bi/web/js/widget/base/editor/editor.sign.initial.js",
                "com/fr/bi/web/js/widget/base/editor/editor.sign.style.js",
                "com/fr/bi/web/js/widget/base/editor/editor.clear.js",

                //segment控件
                "com/fr/bi/web/js/widget/base/segment/button.line.segment.js",
                "com/fr/bi/web/js/widget/base/segment/segment.line.js",

                //mask
                "com/fr/bi/web/js/widget/base/mask/loading.mask.js",
                "com/fr/bi/web/js/widget/base/mask/loading.background.js",
                "com/fr/bi/web/js/widget/base/mask/cancel.loading.mask.js",

                //toolbar
                "com/fr/bi/web/js/widget/base/toolbar/toolbar.progress.processor.js",
                "com/fr/bi/web/js/widget/base/toolbar/toolbar.progress.bar.js",
                "com/fr/bi/web/js/widget/base/toolbar/toolbar.progress.js",


                /**
                 * 详细控件实现
                */
                //日期控件
                "com/fr/bi/web/js/widget/date/trigger.date.js",
                "com/fr/bi/web/js/widget/date/calendar/trigger.triangle.date.js",
                "com/fr/bi/web/js/widget/date/calendar/picker.date.js",
                "com/fr/bi/web/js/widget/date/calendar/combo.year.date.js",
                "com/fr/bi/web/js/widget/date/calendar/combo.month.date.js",
                "com/fr/bi/web/js/widget/date/calendar/popup.calendar.date.js",
                "com/fr/bi/web/js/widget/date/combo.date.js",

                //图控件
                "com/fr/bi/web/js/widget/detailchart/chart.abstract.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulatearea.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulateaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulatebar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulateradar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.area.js",
                "com/fr/bi/web/js/widget/detailchart/chart.axis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.multiaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.multiaxiscombine.js",
                "com/fr/bi/web/js/widget/detailchart/chart.bar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.bubble.js",
                "com/fr/bi/web/js/widget/detailchart/chart.dashboard.js",
                "com/fr/bi/web/js/widget/detailchart/chart.donut.js",
                "com/fr/bi/web/js/widget/detailchart/chart.forcebubble.js",
                "com/fr/bi/web/js/widget/detailchart/chart.line.js",
                "com/fr/bi/web/js/widget/detailchart/chart.percentaccumulateaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.percentaccumulatearea.js",
                "com/fr/bi/web/js/widget/detailchart/chart.compareaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.comparebar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.comparearea.js",
                "com/fr/bi/web/js/widget/detailchart/chart.pie.js",
                "com/fr/bi/web/js/widget/detailchart/chart.multipie.js",
                "com/fr/bi/web/js/widget/detailchart/chart.recttree.js",
                "com/fr/bi/web/js/widget/detailchart/chart.radar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.scatter.js",
                "com/fr/bi/web/js/widget/detailchart/chart.fallaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.rangearea.js",
                "com/fr/bi/web/js/widget/detailchart/chart.map.js",
                "com/fr/bi/web/js/widget/detailchart/chart.gismap.js",
                "com/fr/bi/web/js/widget/detailchart/chart.funnel.js",
                "com/fr/bi/web/js/widget/detailchart/chart.heatmap.js",
                "com/fr/bi/web/js/widget/detailchart/chart.pareto.js",

                //年份控件
                "com/fr/bi/web/js/widget/year/trigger.year.js",
                "com/fr/bi/web/js/widget/year/popup.year.js",
                "com/fr/bi/web/js/widget/year/combo.year.js",

                //月份控件
                "com/fr/bi/web/js/widget/month/trigger.month.js",
                "com/fr/bi/web/js/widget/month/popup.month.js",
                "com/fr/bi/web/js/widget/month/combo.month.js",

                //季度控件
                "com/fr/bi/web/js/widget/quarter/trigger.quarter.js",
                "com/fr/bi/web/js/widget/quarter/popup.quarter.js",
                "com/fr/bi/web/js/widget/quarter/combo.quarter.js",

                //年月控件
                "com/fr/bi/web/js/widget/yearmonth/combo.yearmonth.js",
                //年季度控件
                "com/fr/bi/web/js/widget/yearquarter/combo.yearquarter.js",
                //公式编辑器
                "com/fr/bi/web/js/widget/formula/group.symbol.js",
                "com/fr/bi/web/js/widget/formula/insert.formula.js",
                "com/fr/bi/web/js/widget/formula/pane.function.js",
                "com/fr/bi/web/js/widget/formula/tree.fieldname.js",
                "com/fr/bi/web/js/widget/formula/tree.function.js",
                "com/fr/bi/web/js/widget/formula/pane.searcher.function.js",
                "com/fr/bi/web/js/widget/formula/item.button.text.treeleaf.js",

                //公式下拉框
                "com/fr/bi/web/js/widget/base/combo/formulacombo/trigger.formulacombo.js",
                "com/fr/bi/web/js/widget/base/combo/formulacombo/popup.formulacombo.js",
                "com/fr/bi/web/js/widget/base/combo/formulacombo/combo.formula.js",


                //下拉列表
                "com/fr/bi/web/js/widget/downlist/item.downlist.js",
                "com/fr/bi/web/js/widget/downlist/item.downlistgroup.js",
                "com/fr/bi/web/js/widget/downlist/group.downlist.js",
                "com/fr/bi/web/js/widget/downlist/popup.downlist.js",
                "com/fr/bi/web/js/widget/downlist/combo.downlist.js",

                //过滤界面
                "com/fr/bi/web/js/widget/filterpane/abstract.item.filter.js",
                "com/fr/bi/web/js/widget/filterpane/expander.filter.js",
                "com/fr/bi/web/js/widget/filterpane/pane.filter.js",
                "com/fr/bi/web/js/widget/filterpane/operation.filter.js",
                "com/fr/bi/web/js/widget/filterpane/filter.js",

                //日期
                "com/fr/bi/web/js/widget/multidate/abstract.multidate.datepane.js",
                "com/fr/bi/web/js/widget/multidate/multidate.year.js",
                "com/fr/bi/web/js/widget/multidate/multidate.quarter.js",
                "com/fr/bi/web/js/widget/multidate/multidate.month.js",
                "com/fr/bi/web/js/widget/multidate/multidate.day.js",
                "com/fr/bi/web/js/widget/multidate/multidate.week.js",
                "com/fr/bi/web/js/widget/multidate/multidate.combo.js",
                "com/fr/bi/web/js/widget/multidate/multidate.popup.js",
                "com/fr/bi/web/js/widget/multidate/multidate.segment.js",

                //时间区间控件
                "com/fr/bi/web/js/widget/timeinterval/timeinterval.js",

                "com/fr/bi/web/js/widget/timesetting/timesetting.day.js",
                "com/fr/bi/web/js/widget/timesetting/timesetting.hour.js",

                //选择字段树
                "com/fr/bi/web/js/widget/selectdata/tree/node/node.level0.js",
                "com/fr/bi/web/js/widget/selectdata/tree/node/node.level1.js",
                "com/fr/bi/web/js/widget/selectdata/tree/node/node.level2.js",
                "com/fr/bi/web/js/widget/selectdata/tree/node/node.level1.date.js",
                "com/fr/bi/web/js/widget/selectdata/tree/node/node.level2.date.js",
                "com/fr/bi/web/js/widget/selectdata/tree/treeitem/item.level0.js",
                "com/fr/bi/web/js/widget/selectdata/tree/treeitem/item.level1.js",
                "com/fr/bi/web/js/widget/selectdata/tree/loader.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/tree/expander.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/tree/tree.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/switcher/switcher.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/searchpane/segment.search.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/searchpane/result.search.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/searcher/searcher.selectdata.js",

                //简单的一个搜索功能
                "com/fr/bi/web/js/widget/simplesearcher/searcher.simple.js",

                //简单的选择字段树，主要用于过滤、cube配置内的选择字段
                "com/fr/bi/web/js/widget/simpleselectdata/treenode/node.level0.js",
                "com/fr/bi/web/js/widget/simpleselectdata/treenode/node.level1.js",
                "com/fr/bi/web/js/widget/simpleselectdata/searchpane/segment.search.simpleselectdata.js",
                "com/fr/bi/web/js/widget/simpleselectdata/searchpane/result.search.simpleselectdata.js",
                "com/fr/bi/web/js/widget/simpleselectdata/searcher/searcher.simpleselectdata.js",

                //数值区间控件
                "com/fr/bi/web/js/widget/numericalinterval/trigger.numericalinterval.js",
                "com/fr/bi/web/js/widget/numericalinterval/combo.numericalinterval.js",
                "com/fr/bi/web/js/widget/numericalinterval/popup.numericalinterval.js",
                "com/fr/bi/web/js/widget/numericalinterval/numericalinterval.js",

                //单值滑块
                "com/fr/bi/web/js/widget/singleslider/singleslider.js",
                "com/fr/bi/web/js/widget/singleslider/slider/widget.slider.js",
                "com/fr/bi/web/js/widget/singleslider/track/widget.track.js",

                //区间滑块
                "com/fr/bi/web/js/widget/intervalslider/intervalslider.js",

                //可以设置参数的下拉框
                "com/fr/bi/web/js/widget/paramsettingcombo/param0.date.item.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/param1.date.item.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/param2.date.item.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/param3.date.item.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/popup.param.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/rangevaluecombo/rangevaluecombo.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/yearcombo/combo.param.year.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/yearcombo/param.year.item.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/datecombo/combo.param.date.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/datecombo/popup.param.date.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/yearmonthcombo/popup.param.yearmonth.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/yearmonthcombo/combo.param.yearmonth.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/yearseasoncombo/popup.param.yearseason.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/yearseasoncombo/combo.param.yearseason.js",

                "com/fr/bi/web/js/widget/paramsettingcombo/dateintervalcombo/combo.param.dateinterval.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/dateintervalcombo/popup.param.dateinterval.js",

                //下拉复选框
                "com/fr/bi/web/js/widget/multiselect/trigger/button.checkselected.js",
                "com/fr/bi/web/js/widget/multiselect/trigger/switcher.checkselected.js",
                "com/fr/bi/web/js/widget/multiselect/trigger/editor.multiselect.js",
                "com/fr/bi/web/js/widget/multiselect/trigger/searcher.multiselect.js",
                "com/fr/bi/web/js/widget/multiselect/multiselect.trigger.js",
                "com/fr/bi/web/js/widget/multiselect/check/multiselect.display.js",
                "com/fr/bi/web/js/widget/multiselect/check/multiselect.check.pane.js",
                "com/fr/bi/web/js/widget/multiselect/multiselect.loader.js",
                "com/fr/bi/web/js/widget/multiselect/multiselect.popup.view.js",
                "com/fr/bi/web/js/widget/multiselect/search/multiselect.search.loader.js",
                "com/fr/bi/web/js/widget/multiselect/search/multiselect.search.pane.js",
                "com/fr/bi/web/js/widget/multiselect/multiselect.combo.js",

                //文本列表
                "com/fr/bi/web/js/widget/multistringlist/multistringlist.js",

                //移动到分组
                "com/fr/bi/web/js/widget/move2group/button.add.move2group.js",
                "com/fr/bi/web/js/widget/move2group/toolbar.move2group.js",
                "com/fr/bi/web/js/widget/move2group/combo.move2group.js",

                //复制到分组
                "com/fr/bi/web/js/widget/copy2group/button.add.copy2group.js",
                "com/fr/bi/web/js/widget/copy2group/toolbar.copy2group.js",
                "com/fr/bi/web/js/widget/copy2group/combo.copy2group.js",

                //文本标签
                "com/fr/bi/web/js/widget/listlabel/listlabel.js",
                "com/fr/bi/web/js/widget/listlabel/group.item.listlabel.js",

                //树标签
                "com/fr/bi/web/js/widget/treelabel/treelabel.js",
                "com/fr/bi/web/js/widget/treelabel/treelabel.view.js",

                //树列表
                "com/fr/bi/web/js/widget/multitreelist/multitreelist.js",
                "com/fr/bi/web/js/widget/multitreelist/multitreelist.popup.js",

                //单选下拉树
                "com/fr/bi/web/js/widget/singletree/singletree.combo.js",
                "com/fr/bi/web/js/widget/singletree/singletree.popup.js",
                "com/fr/bi/web/js/widget/singletree/singletree.trigger.js",

                //可选下拉树
                "com/fr/bi/web/js/widget/selecttree/nodes/node.first.plus.js",
                "com/fr/bi/web/js/widget/selecttree/nodes/node.mid.plus.js",
                "com/fr/bi/web/js/widget/selecttree/nodes/node.last.plus.js",
                "com/fr/bi/web/js/widget/selecttree/selecttree.combo.js",
                "com/fr/bi/web/js/widget/selecttree/selecttree.expander.js",
                "com/fr/bi/web/js/widget/selecttree/selecttree.popup.js",

                //多层单选下拉树
                "com/fr/bi/web/js/widget/multilayersingletree/treeitem/item.first.treeleaf.js",
                "com/fr/bi/web/js/widget/multilayersingletree/treeitem/item.mid.treeleaf.js",
                "com/fr/bi/web/js/widget/multilayersingletree/treeitem/item.last.treeleaf.js",
                "com/fr/bi/web/js/widget/multilayersingletree/node/node.first.plus.js",
                "com/fr/bi/web/js/widget/multilayersingletree/node/node.mid.plus.js",
                "com/fr/bi/web/js/widget/multilayersingletree/node/node.last.plus.js",
                "com/fr/bi/web/js/widget/multilayersingletree/multilayersingletree.leveltree.js",
                "com/fr/bi/web/js/widget/multilayersingletree/multilayersingletree.popup.js",
                "com/fr/bi/web/js/widget/multilayersingletree/multilayersingletree.combo.js",

                //多层可选下拉树
                "com/fr/bi/web/js/widget/multilayerselecttree/node/node.first.plus.js",
                "com/fr/bi/web/js/widget/multilayerselecttree/node/node.mid.plus.js",
                "com/fr/bi/web/js/widget/multilayerselecttree/node/node.last.plus.js",
                "com/fr/bi/web/js/widget/multilayerselecttree/multilayerselecttree.leveltree.js",
                "com/fr/bi/web/js/widget/multilayerselecttree/multilayerselecttree.popup.js",
                "com/fr/bi/web/js/widget/multilayerselecttree/multilayerselecttree.combo.js",

                //复选下拉树
                "com/fr/bi/web/js/widget/multitree/trigger/multi.tree.button.checkselected.js",
                "com/fr/bi/web/js/widget/multitree/trigger/searcher.multi.tree.js",
                "com/fr/bi/web/js/widget/multitree/multi.tree.popup.js",
                "com/fr/bi/web/js/widget/multitree/multi.tree.search.pane.js",
                "com/fr/bi/web/js/widget/multitree/check/multi.tree.check.pane.js",
                "com/fr/bi/web/js/widget/multitree/multi.tree.combo.js",

                //可切换单选复选的树
                "com/fr/bi/web/js/widget/switchtree/switchtree.js",

                //选色控件
                "com/fr/bi/web/js/widget/colorchooser/colorchooser.trigger.js",
                "com/fr/bi/web/js/widget/colorchooser/colorchooser.popup.js",
                "com/fr/bi/web/js/widget/colorchooser/colorchooser.custom.js",
                "com/fr/bi/web/js/widget/colorchooser/colorchooser.js",

                //路径选择
                "com/fr/bi/web/js/widget/pathchooser/pathregion.js",
                "com/fr/bi/web/js/widget/pathchooser/pathchooser.js",

                //带有方向的路径选择
                "com/fr/bi/web/js/widget/directionpathchooser/directionpathchooser.js",

                //文本编辑区
                "com/fr/bi/web/js/widget/textarea/textarea.js",

                //文本工具条
                "com/fr/bi/web/js/widget/texttoolbar/sizechooser/texttoolbar.sizechooser.js",
                "com/fr/bi/web/js/widget/texttoolbar/alignchooser/texttoolbar.alignchooser.js",
                "com/fr/bi/web/js/widget/texttoolbar/colorchooser/texttoolbar.colorchooser.trigger.js",
                "com/fr/bi/web/js/widget/texttoolbar/backgroundchooser/texttoolbar.backgroundchooser.trigger.js",
                "com/fr/bi/web/js/widget/texttoolbar/texttoolbar.js",

                //文件管理
                "com/fr/bi/web/js/widget/filemanager/items/item.file.filemanager.js",
                "com/fr/bi/web/js/widget/filemanager/items/item.folder.filemanager.js",
                "com/fr/bi/web/js/widget/filemanager/nav/button/button.nav.filemanager.js",
                "com/fr/bi/web/js/widget/filemanager/nav/nav.filemanager.js",
                "com/fr/bi/web/js/widget/filemanager/buttongroup.filemanager.js",
                "com/fr/bi/web/js/widget/filemanager/list.filemanager.js",
                "com/fr/bi/web/js/widget/filemanager/filemanager.js",

                //表关联
                "com/fr/bi/web/js/widget/branchrelation/branchrelation.js",

                //关联视图
                "com/fr/bi/web/js/widget/relationview/relationview.item.js",
                "com/fr/bi/web/js/widget/relationview/relationview.region.js",
                "com/fr/bi/web/js/widget/relationview/relationview.js",

                //预览表
                "com/fr/bi/web/js/widget/previewtable/previewtable.cell.js",
                "com/fr/bi/web/js/widget/previewtable/previewtable.header.cell.js",
                "com/fr/bi/web/js/widget/previewtable/previewtable.js",

                //列可拖拽排序表
                "com/fr/bi/web/js/widget/sortabletable/sortabletable.js",
                "com/fr/bi/web/js/widget/sortabletable/sortabletable.drag.button.js",
                "com/fr/bi/web/js/widget/sortabletable/sortabletable.dashline.vertical.js",
                "com/fr/bi/web/js/widget/sortabletable/sortabletable.dashline.horizontal.js",

                //螺旋分析tab控件
                "com/fr/bi/web/js/widget/dynamicgrouptab/dynamicgroup.tab.buttongroup.js",

                //Excel表
                "com/fr/bi/web/js/widget/exceltable/exceltable.cell.js",
                "com/fr/bi/web/js/widget/exceltable/exceltable.header.cell.js",
                "com/fr/bi/web/js/widget/exceltable/exceltable.js",

                //分页表格
                "com/fr/bi/web/js/widget/pagetable/pagetable.cell.js",
                "com/fr/bi/web/js/widget/pagetable/pagetable.js",

                //带序号表格
                "com/fr/bi/web/js/widget/sequencetable/listnumber.sequencetable.js",
                "com/fr/bi/web/js/widget/sequencetable/treenumber.sequencetable.js",
                "com/fr/bi/web/js/widget/sequencetable/dynamicnumber.sequencetable.js",
                "com/fr/bi/web/js/widget/sequencetable/sequencetable.js",

                //图片组件
                "com/fr/bi/web/js/widget/image/uploadimage.js",
                "com/fr/bi/web/js/widget/image/button/size/image.button.size.js",
                "com/fr/bi/web/js/widget/image/button/size/image.button.size.combo.js",
                "com/fr/bi/web/js/widget/image/button/size/radio/image.button.size.radio.js",
                "com/fr/bi/web/js/widget/image/button/href/image.button.href.js",

                //web组件
                "com/fr/bi/web/js/widget/web/web.js",

                //布局
                "com/fr/bi/web/js/widget/arrangement/arrangement.droppable.js",
                "com/fr/bi/web/js/widget/arrangement/arrangement.block.js",
                "com/fr/bi/web/js/widget/arrangement/arrangement.js",

                //自适应布局
                "com/fr/bi/web/js/widget/adaptivearrangement/adaptivearrangement.js",

                //交互式布局
                "com/fr/bi/web/js/widget/interactivearrangement/interactivearrangement.js",

                "com/fr/bi/web/js/widget/uploadfile/progress.uploadfile.js",

                /**
                 * 以下是部件
                */
                //loading面板
                "com/fr/bi/web/js/components/pane.loading.js",

                //选值
                "com/fr/bi/web/js/components/valuechooser/combo.valuechooser.js",
                "com/fr/bi/web/js/components/allvaluechooser/combo.allvaluechooser.js",
                "com/fr/bi/web/js/components/bigvaluechooser/combo.bigvaluechooser.js",

                //带空节点的层级树
                "com/fr/bi/web/js/components/tree/leveltree.empty.js",
                "com/fr/bi/web/js/components/tree/multilayerleveltree.empty.js",

                //树选值
                "com/fr/bi/web/js/components/treevaluechooser/combo.treevaluechooser.js",

                //excelview
                "com/fr/bi/web/js/components/excel/excelview/excelviewdisplaymanager.js",

                //数据预处理表格
                "com/fr/bi/web/js/components/pretreatedtable/pretreatedtable.js",

                //模板管理
                "com/fr/bi/web/js/components/templatemanager/liststyleitems/report.listview.item.js",
                "com/fr/bi/web/js/components/templatemanager/liststyleitems/folder.listview.item.js",
                "com/fr/bi/web/js/components/templatemanager/cardstyleitems/folder.cardview.item.js",
                "com/fr/bi/web/js/components/templatemanager/cardstyleitems/report.cardview.item.js",
                "com/fr/bi/web/js/components/templatemanager/tools/folderandfile.sortcombo.js",
                "com/fr/bi/web/js/components/templatemanager/tools/reportsearchresult.pane.js",
                "com/fr/bi/web/js/components/templatemanager/tools/foldermoveto.pane.js",
                "com/fr/bi/web/js/components/templatemanager/tools/share/sharereport.pane.js",
                "com/fr/bi/web/js/components/templatemanager/tools/share/selecteduser.grouplist.js",
                "com/fr/bi/web/js/components/templatemanager/tools/share/selecteduser.button.js",
                "com/fr/bi/web/js/components/templatemanager/tools/share/usersearchresult.pane.js",
                "com/fr/bi/web/js/components/templatemanager/tools/editshared/pane.editshared.js",
                "com/fr/bi/web/js/components/templatemanager/buttongroup.templatemanager.js",
                "com/fr/bi/web/js/components/templatemanager/templatemanager.js",
                "com/fr/bi/web/js/components/templatemanager/templatemanager.model.js",

                //查看所有模板（管理员）
                "com/fr/bi/web/js/components/allreports/allreports.js",
                "com/fr/bi/web/js/components/allreports/allreports.filter.js",
                "com/fr/bi/web/js/components/allreports/allreports.group.js",
                "com/fr/bi/web/js/components/allreports/item/allreports.item.model.js",
                "com/fr/bi/web/js/components/allreports/item/allreports.list.item.js",
                "com/fr/bi/web/js/components/allreports/item/allreports.card.item.js",
                "com/fr/bi/web/js/components/allreports/hangout/reporthangout.js",

                //分享给我
                "com/fr/bi/web/js/components/share/sharetome.js",
                "com/fr/bi/web/js/components/share/sharetome.singleuser.js",
        };
    }
}
