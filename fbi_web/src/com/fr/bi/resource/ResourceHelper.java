package com.fr.bi.resource;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.base.TemplateUtils;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.ArrayUtils;
import com.fr.stable.StableUtils;
import com.fr.stable.bridge.Transmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 读取各种资源的帮助类
 */
public class ResourceHelper {
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

    public static FormulaTransmitter FormulaTransmitter = new FormulaTransmitter();


    public static String[] getDataJS() {
        return new String[]{"/com/fr/bi/web/js/template/pool.data.js"};
    }

    public static String[] getFormulaCollectionJS() {
        return new String[]{"/com/fr/bi/web/js/template/formula.collection.js"};
    }


    private static String getDataJs(HttpServletRequest req, String[] files) {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject groups = new JSONObject();
        JSONObject packages = new JSONObject();
        JSONObject relations = new JSONObject();
        JSONObject connections = new JSONObject();
        JSONObject tables = new JSONObject();
        JSONObject source = new JSONObject();
        JSONObject fields = new JSONObject();
        JSONObject translations = new JSONObject();
        JSONObject excelViews = new JSONObject();
        List<BIPackageID> authPacks = BIModuleUtils.getAvailablePackID(userId);
        try {
            groups = BICubeConfigureCenter.getPackageManager().createGroupJSON(userId);
            JSONObject allPacks = BIModuleUtils.createPackJSON(userId, req.getLocale());
            //管理员
            if (UserControl.getInstance().getSuperManagerID() == userId) {
                packages = allPacks;
            }
            //前台能看到的业务包
            for (BIPackageID pId : authPacks) {
                if (allPacks.has(pId.getIdentityValue())) {
                    packages.put(pId.getIdentityValue(), allPacks.getJSONObject(pId.getIdentityValue()));
                }
            }

            translations = BIModuleUtils.createAliasJSON(userId);
            relations = BICubeConfigureCenter.getTableRelationManager().createRelationsPathJSON(userId);
            excelViews = BIConfigureManagerCenter.getExcelViewManager().createJSON(userId);
            Set<IBusinessPackageGetterService> packs = BIModuleUtils.getAllPacks(userId);
            for (IBusinessPackageGetterService p : packs) {
                if (UserControl.getInstance().getSuperManagerID() != userId && !authPacks.contains(p.getID())) {
                    continue;
                }
                for (BIBusinessTable t : (Set<BIBusinessTable>) p.getBusinessTables()) {
                    JSONObject jo = t.createJSONWithFieldsInfo(BICubeManager.getInstance().fetchCubeLoader(userId));
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
            BILogger.getLogger().error(e.getMessage(), e);
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
            BILogger.getLogger().error(e.getMessage(), e);
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
                BILogger.getLogger().error(e.getMessage(), e);
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
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return buffer.toString();
    }

    public static String[] getShowCss() {
        return new String[]{

                "com/fr/bi/web/css/show/show.view.css",
                "com/fr/bi/web/css/show/pane/show.pane.css",
                "com/fr/bi/web/css/show/pane/widgets/show.widgets.css",
                "com/fr/bi/web/css/show/pane/widgets/detail/show.detail.css",
                "com/fr/bi/web/css/show/pane/widgets/detail/region/show.region.css",
                "com/fr/bi/web/css/show/pane/widgets/detail/region/field/show.dimension.css",
                "com/fr/bi/web/css/show/pane/widgets/detail/region/field/show.target.css"

        };
    }

    public static String[] getShowJs() {
        return new String[]{
                "com/fr/bi/web/js/show/show.start.js",
                "com/fr/bi/web/js/show/model.js",
                "com/fr/bi/web/js/show/view.js",
                "com/fr/bi/web/js/show/modules/show.floatbox.manage.js",
                "com/fr/bi/web/js/show/modules/show.model.manage.js",
                "com/fr/bi/web/js/show/modules/show.view.manage.js",
                "com/fr/bi/web/js/show/modules/model/show.model.js",
                "com/fr/bi/web/js/show/modules/model/pane/model.pane.js",

                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.widget.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.detailtable.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.string.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.query.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.reset.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.date.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.daterange.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.number.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.tree.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.year.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.yearmonth.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.yearquarter.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.content.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.image.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/model.web.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/model.detail.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/model.detailtable.detail.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/model.dimension.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/model.detail.dimension.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/model.target.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/filter/model.dimensionfilter.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/filter/model.targetfilter.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/customgroup/model.customgroup.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/customsort/model.customsort.js",
                "com/fr/bi/web/js/show/modules/model/pane/widgets/detail/field/numbercustomgroup/model.number.custom.group.js",
                "com/fr/bi/web/js/show/modules/view/show.view.js",
                "com/fr/bi/web/js/show/modules/view/pane/show.pane.js",

                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.widget.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.detailtable.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.string.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.tree.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.date.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.year.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.yearmonth.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.yearquarter.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.daterange.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.number.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.widgets.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.content.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.image.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.generalquery.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.reset.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.query.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/show.web.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/show.detail.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/show.detailtable.detail.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/show.dimension.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/show.detail.dimension.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/show.target.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/customgroup/show.customgroup.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/customsort/show.customsort.js",
                "com/fr/bi/web/js/show/modules/view/pane/widgets/detail/field/numbercustomgroup/show.number.custom.group.js"

        };
    }

    public static String[] getDeziCss() {
        return new String[]{
                "com/fr/bi/web/css/dezi/dezi.view.css",
                "com/fr/bi/web/css/dezi/pane/dezi.pane.css",
                "com/fr/bi/web/css/dezi/pane/widgets/dezi.widgets.css",
                "com/fr/bi/web/css/dezi/pane/widgets/detail/dezi.detail.css",
                "com/fr/bi/web/css/dezi/pane/widgets/detail/region/dezi.region.css",
                "com/fr/bi/web/css/dezi/pane/widgets/detail/region/field/dezi.dimension.css",
                "com/fr/bi/web/css/dezi/pane/widgets/detail/region/field/dezi.target.css"
        };
    }

    public static String[] getDeziJs() {
        return new String[]{
                "com/fr/bi/web/js/dezi/dezi.start.js",
                "com/fr/bi/web/js/dezi/model.js",
                "com/fr/bi/web/js/dezi/view.js",
                "com/fr/bi/web/js/dezi/modules/dezi.floatbox.manage.js",
                "com/fr/bi/web/js/dezi/modules/dezi.model.manage.js",
                "com/fr/bi/web/js/dezi/modules/dezi.view.manage.js",
                "com/fr/bi/web/js/dezi/modules/model/dezi.model.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/model.pane.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.widget.js",

                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.detailtable.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.string.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.query.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.reset.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.date.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.daterange.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.number.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.tree.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.year.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.yearmonth.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.yearquarter.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.generalquery.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.string.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.tree.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.year.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.yearmonth.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.yearquarter.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.number.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.date.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.detailtable.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.daterange.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/model.tree.detail.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.content.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.image.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/model.web.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.detail.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.string.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.tree.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.number.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.date.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.tree.dimension.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/model.target.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/filter/model.dimensionfilter.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/filter/model.targetfilter.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/customgroup/model.customgroup.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/customsort/model.customsort.js",
                "com/fr/bi/web/js/dezi/modules/model/pane/widgets/detail/field/numbercustomgroup/model.number.custom.group.js",
                "com/fr/bi/web/js/dezi/modules/view/dezi.view.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/dezi.pane.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.widget.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.detailtable.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.string.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.query.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.reset.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.date.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.year.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.yearmonth.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.yearquarter.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.daterange.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.number.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.tree.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.generalquery.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.widgets.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.string.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.date.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.number.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.tree.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.year.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.yearmonth.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.yearquarter.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.detailtable.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/dezi.daterange.detail.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.content.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.image.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/dezi.web.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.detail.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.string.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.tree.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.number.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.date.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.tree.dimension.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/dezi.target.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/customgroup/dezi.customgroup.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/customsort/dezi.customsort.js",
                "com/fr/bi/web/js/dezi/modules/view/pane/widgets/detail/field/numbercustomgroup/dezi.number.custom.group.js"
        };
    }

    public static String[] getConfCss() {
        return new String[]{
                "com/fr/bi/web/css/conf/conf.view.css",
                "com/fr/bi/web/css/conf/businesspackages/conf.package.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/conf.onePack.css",
                "com/fr/bi/web/css/conf/cube/conf.updatecube.css",
                "com/fr/bi/web/css/conf/datalink/conf.datalink.css",
                "com/fr/bi/web/css/conf/datalink/conf.datalink.add.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/conf.etl.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/addfield/conf.addfield.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/addfield/conf.addformulafieldfloatbox.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/addgroupfield/conf.addgroupfield.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/addgroupfield/conf.addgroupfieldfloatbox.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/partfield/conf.selectpartfield.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/filter/conf.filter.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/statistic/conf.groupandstatistic.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/union/conf.union.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/convert/conf.convert.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/join/conf.join.css",
                "com/fr/bi/web/css/conf/businesspackages/onepackage/etl/circle/conf.circle.css",
                "com/fr/bi/web/css/conf/multirelation/conf.multirelation.css",
                "com/fr/bi/web/css/conf/businesspackages/group/conf.packages.group.css",
                "com/fr/bi/web/css/conf/permissionmanage/conf.permission.manage.css",


        };
    }

    public static String[] getConfJs() {
        return new String[]{
                "com/fr/bi/web/js/conf/conf.start.js",
                "com/fr/bi/web/js/conf/model.js",
                "com/fr/bi/web/js/conf/view.js",
                "com/fr/bi/web/js/conf/modules/conf.floatbox.manage.js",
                "com/fr/bi/web/js/conf/modules/conf.model.manage.js",
                "com/fr/bi/web/js/conf/modules/conf.view.manage.js",

                "com/fr/bi/web/js/conf/modules/model/conf.model.js",
                "com/fr/bi/web/js/conf/modules/model/businesspackage/manage/model.packages.group.js",
                "com/fr/bi/web/js/conf/modules/model/businesspackage/model.packages.manage.js",
                "com/fr/bi/web/js/conf/modules/model/permissionmanage/model.permission.manage.js",
                "com/fr/bi/web/js/conf/modules/model/cube/model.updatecube.js",
                "com/fr/bi/web/js/conf/modules/model/cube/model.cubepath.js",
                "com/fr/bi/web/js/conf/modules/model/cube/model.cubelog.js",
                "com/fr/bi/web/js/conf/modules/model/dataLink/model.datalink.js",
                "com/fr/bi/web/js/conf/modules/model/multirelation/model.multirelation.js",
                "com/fr/bi/web/js/conf/modules/model/etl/model.tableset.js",
                "com/fr/bi/web/js/conf/modules/model/dataLink/model.datalink.add.js",
                "com/fr/bi/web/js/conf/modules/view/conf.view.js",
                "com/fr/bi/web/js/conf/modules/view/businesspackage/conf.packages.manage.js",
                "com/fr/bi/web/js/conf/modules/view/businesspackage/group/conf.packages.group.js",
                "com/fr/bi/web/js/conf/modules/view/cube/conf.updatecube.js",
                "com/fr/bi/web/js/conf/modules/view/cube/conf.cubepath.js",
                "com/fr/bi/web/js/conf/modules/view/cube/conf.cubelog.js",
                "com/fr/bi/web/js/conf/modules/view/dataLink/conf.datalink.js",
                "com/fr/bi/web/js/conf/modules/view/multirelation/conf.multirelation.js",
                "com/fr/bi/web/js/conf/modules/view/dataLink/conf.datalink.add.js",
                "com/fr/bi/web/js/conf/modules/view/permissionmanage/conf.permission.manage.js"
        };
    }

    public static String[] getCommonCss() {
        return new String[]{
                //驾驶舱相关模块
                "com/fr/bi/web/css/modules/base/combos/widget.combo.css",

                "com/fr/bi/web/css/modules/tablefield/widget.tablefield.css",
                "com/fr/bi/web/css/modules/tablefield/widget.tablefield.searchresult.pane.css",

                //过滤
                "com/fr/bi/web/css/modules/filter/filteritems/dimension/item.notypefield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/dimension/item.stringfield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/dimension/item.numberfield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/dimension/item.datefield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/target/item.notypefield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/target/item.stringfield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/target/item.numberfield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/target/item.datefield.css",
                "com/fr/bi/web/css/modules/filter/filteritems/formula/item.emptyformula.css",
                "com/fr/bi/web/css/modules/filter/filteritems/formula/item.formula.css",

                //明细表字段关联设置
                "com/fr/bi/web/css/modules/fieldrelationsetting/item.commontable.set.detailtable.css",
                "com/fr/bi/web/css/modules/fieldrelationsetting/tab.path.setting.detailtable.css",
                "com/fr/bi/web/css/modules/fieldrelationsetting/fieldrelationsettingpopup.css",

                //下拉树控件字段关联设置
                "com/fr/bi/web/css/modules/fieldrelationsettingwithpreviewpopup/fieldrelationsettingwithpreviewpopup.css",

                "com/fr/bi/web/css/modules/relation/button.relationtable.field.css",
                "com/fr/bi/web/css/modules/relation/widget.relationset.group.css",

                //选择字段
                "com/fr/bi/web/css/modules/selectdata/tab.selectdata.css",
                "com/fr/bi/web/css/modules/selectdata/preview/pane.preview.selectdata.css",
                "com/fr/bi/web/css/modules/selectdata/treeitem4reusedimension/calctarget.item.level0.css",

                "com/fr/bi/web/css/modules/selectdata4filter/node/node.level0.dimension.css",

                "com/fr/bi/web/css/modules/selectdatamask/widget.selectdata.mask.css",

                //通用查询选字段
                "com/fr/bi/web/css/modules/selectdata4generalquery/widget.generalquery.usedfields.pane.css",
                //选择文本
                "com/fr/bi/web/css/modules/selectstring/tab.selectstring.css",

                //选择日期
                "com/fr/bi/web/css/modules/selectdate/tab.selectdate.css",

                //详细设置相关模块
                "com/fr/bi/web/css/modules/dimensionsmanager/charttype/combo/combo.tabletype.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/charttype/charttype.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regions/region.dimension.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regions/region.target.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/regionsmanager.css",
                "com/fr/bi/web/css/modules/dimensionsmanager/dimensionsmanager.css",


                //详细设置相关模块(预览)
                "com/fr/bi/web/css/modules/dimensionsmanager4show/charttype/combo/combo.tabletype.css",
                "com/fr/bi/web/css/modules/dimensionsmanager4show/charttype/charttype.css",
                "com/fr/bi/web/css/modules/dimensionsmanager4show/regions/region.dimension.css",
                "com/fr/bi/web/css/modules/dimensionsmanager4show/regions/region.target.css",
                "com/fr/bi/web/css/modules/dimensionsmanager4show/regionsmanager.css",
                "com/fr/bi/web/css/modules/dimensionsmanager4show/dimensionsmanager.css",

                //数值类型自定义分组
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.group.css",
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.other.css",
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.tab.css",
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.combo.css",
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.item.css",
                "com/fr/bi/web/css/modules/numberintervalcustomgroup/widget.customgroup.number.panel.css",

                //自定义分组
                "com/fr/bi/web/css/modules/customgroup/widget.ungroupedpane.css",
                "com/fr/bi/web/css/modules/customgroup/widget.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.fieldPane.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.expander.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.bottom.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.view.searcher.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.pane.searcher.customgroup.css",
                "com/fr/bi/web/css/modules/customgroup/widget.button.field.customgroup.css",

                //自定义排序
                "com/fr/bi/web/css/modules/customsort/widget.pane.customsort.css",

                //union
                "com/fr/bi/web/css/modules/onepackage/etl/union/widget.addunion.table.css",
                "com/fr/bi/web/css/modules/onepackage/etl/union/widget.unionresult.header.css",

                //etl预览
                "com/fr/bi/web/css/modules/onepackage/etl/preview/widget.etltable.preview.css",

                //etl增加公式列
                "com/fr/bi/web/css/modules/onepackage/etl/addfield/widget.button.formulafield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/addfield/widget.formulalist.css",

                "com/fr/bi/web/css/modules/onepackage/etl/flowchart/widget.etltable.operator.css",
                "com/fr/bi/web/css/modules/onepackage/etl/flowchart/widget.etltable.combo.css",

                //etl行列转化
                "com/fr/bi/web/css/modules/onepackage/etl/convert/convert.listItem.css",
                "com/fr/bi/web/css/modules/onepackage/etl/convert/convert.displaylabel.css",
                "com/fr/bi/web/css/modules/onepackage/etl/convert/convert.genFields.css",
                "com/fr/bi/web/css/modules/onepackage/etl/convert/convert.selectFieldsDataPane.css",

                //etl新增分组列
                "com/fr/bi/web/css/modules/onepackage/etl/addgroupfield/widget.addgroupfield.css",

                //etl新增公式列
                "com/fr/bi/web/css/modules/onepackage/etl/addfield/widget.addformulafield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/addfield/widget.addformulafield.popover.css",

                //etl自循环列
                "com/fr/bi/web/css/modules/onepackage/etl/circle/circle.operator.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/operatorPane/tab/circle.operator.pane.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/operatorPane/tab/circle.select.field.button.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/operatorPane/tab/circle.tab.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/operatorPane/tab/circle.two.condition.switch.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/operatorPane/tab/circle.tab.region.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/levelPane/circle.display.editor.css",
                "com/fr/bi/web/css/modules/onepackage/etl/circle/circle.result.pane.css",

                //etl分组统计
                "com/fr/bi/web/css/modules/onepackage/etl/group/group.select.fields.css",
                "com/fr/bi/web/css/modules/onepackage/etl/group/group.select.fields.item.css",
                "com/fr/bi/web/css/modules/onepackage/etl/group/group.dimension.css",
                "com/fr/bi/web/css/modules/onepackage/etl/group/region.string.css",

                //etl选择部分字段
                "com/fr/bi/web/css/modules/onepackage/etl/partfield/widget.selectpartfieldlist.css",

                //etl过滤
                "com/fr/bi/web/css/modules/onepackage/etl/filter/filteritem/item.stringfield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/filter/filteritem/item.numberfield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/filter/filteritem/item.datefield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/filter/filteritem/item.notypefield.css",
                "com/fr/bi/web/css/modules/onepackage/etl/filter/formula/item.formula.css",
                "com/fr/bi/web/css/modules/onepackage/etl/filter/formula/item.emptyformula.css",

                //FineBI Service
                "com/fr/bi/web/css/modules/finebiservice/expander.finebiservice.css",
                "com/fr/bi/web/css/modules/finebiservice/finebiservice.css",

                //业务包分组
                "com/fr/bi/web/css/modules/businesspackagegroup/buttons/button.businesspackage.css",
                //维度与指标的匹配关系
                "com/fr/bi/web/css/modules/matchingrelationship/infopane/settargetregion.infopane.css",
                "com/fr/bi/web/css/modules/matchingrelationship/infopane/unsettingtargetregion.infopane.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/dimensiontreecombo/dimensiontree.trigger.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/dimensiontreecombo/dimensiontree.combo.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/targetlabelcontrol.settingpane.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/settingpane.css",
                "com/fr/bi/web/css/modules/matchingrelationship/settingpane/multipathchooser.settingpane.css",

                //多路径设置
                "com/fr/bi/web/css/fragments/multirelation/item.tablefield.multirelation.css",
                "com/fr/bi/web/css/fragments/multirelation/item.multirelation.css",
                "com/fr/bi/web/css/fragments/multirelation/expander.multirelation.css",
                "com/fr/bi/web/css/fragments/multirelation/item.tablefield.multirelation.css",
                "com/fr/bi/web/css/fragments/multirelation/view.searcher.multirelation.css",

                //查看真实数据
                "com/fr/bi/web/css/fragments/base/items/widget.realdatacheckbox.css",

                "com/fr/bi/web/css/fragments/datalink/widget.testlink.loading.css",

                //计算指标
                "com/fr/bi/web/css/modules/calculatetarget/calculatetargetpopup.css",
                "com/fr/bi/web/css/modules/calculatetarget/pane.calculate.target.rank.css",

                //去数据库选表
                "com/fr/bi/web/css/modules/selecttable/widget.selecttable.pane.css",
                "com/fr/bi/web/css/modules/selecttable/widget.datalinktab.css",
                "com/fr/bi/web/css/modules/selecttable/widget.databasetables.pane.css",
                "com/fr/bi/web/css/modules/selecttable/widget.packagetables.pane.css",
                "com/fr/bi/web/css/modules/selecttable/widget.etltables.pane.css",

                //表格样式
                "com/fr/bi/web/css/modules/chartsetting/grouptable/widget.grouptable.setting.css",
                "com/fr/bi/web/css/modules/chartsetting/crosstable/widget.crosstable.setting.css",

                //图样式
                "com/fr/bi/web/css/modules/chartsetting/charts/selectcolorcombo/item.selectcolor.css",
                "com/fr/bi/web/css/modules/chartsetting/charts/charts.setting.css",

                //警戒线
                "com/fr/bi/web/css/modules/cordon/pane/item.cordon.css",
                "com/fr/bi/web/css/modules/cordon/pane/pane.cordon.css",

                //带参数的日期控件
                "com/fr/bi/web/css/modules/multidatecombowithparam/multidate.parampopup.css",
                "com/fr/bi/web/css/modules/multidatecombowithparam/multidate.parampane.css",
                "com/fr/bi/web/css/modules/multidatecombowithparam/multidate.paramtrigger.css",
                "com/fr/bi/web/css/modules/paramtimeinterval/timeinterval.param.css",

                //表格上的单元格
                "com/fr/bi/web/css/modules/tablechartmanager/datatable/tablecell/normal/headercell.normal.css",
                "com/fr/bi/web/css/modules/tablechartmanager/datatable/tablecell/normal/cell.tarbody.normal.css",
                "com/fr/bi/web/css/modules/tablechartmanager/datatable/tablecell/normal/expandercell.normal.css",
                "com/fr/bi/web/css/modules/tablechartmanager/datatable/widget.summarytable.css",
                "com/fr/bi/web/css/modules/tablechartmanager/datatable/combo/sortfilter.combo.css",
                "com/fr/bi/web/css/modules/tablechartmanager/tablechartmanager.css",
                "com/fr/bi/web/css/modules/tablechartmanager/errorpane/tablechart.errorpane.css",

                //自适应布局
                "com/fr/bi/web/css/modules/fit/widgetchooser/widget.dragicongroup.css",
                "com/fr/bi/web/css/modules/fit/fit.widget.css",
                "com/fr/bi/web/css/modules/fit/fit.css",

                "com/fr/bi/web/css/modules/globalupdate/widget.globalupdate.setting.css",

                //明细表超级链接
                "com/fr/bi/web/css/modules/hyperlink/hyperlink.insert.css",

                //明细表表格
                "com/fr/bi/web/css/modules/detailtable/widget.detailtable.css",
                "com/fr/bi/web/css/modules/detailtable/cell/header.detailtable.css",


                //联动
                "com/fr/bi/web/css/modules/linkage/linkage.target.css",
                "com/fr/bi/web/css/modules/linkage/linkage.css",

                //cube日志
                "com/fr/bi/web/css/modules/cubelog/items/title.item.wronginfo.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/items/item.wronginfo.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/items/item.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/nodes/node.wronginfo.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/nodes/node.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/tree.cubelog.css",
                "com/fr/bi/web/css/modules/cubelog/cubelog.css",

                //excel
                "com/fr/bi/web/css/extend/excel/excel.upload.css",
                "com/fr/bi/web/css/extend/excel/fieldset/excel.fieldset.css",
                "com/fr/bi/web/css/extend/excel/fieldset/excel.fieldset.table.css",
                "com/fr/bi/web/css/extend/excel/fieldset/combo/combo.fieldset.css",
                "com/fr/bi/web/css/extend/excel/fieldset/combo/item.excelfieldtype.css",
                "com/fr/bi/web/css/extend/excel/tipcombo/excel.tipcombo.css",

                //sql
                "com/fr/bi/web/css/extend/sql/sql.edit.css",

                "com/fr/bi/web/css/modules/datalink/widget.datalink.add.css/",
                "com/fr/bi/web/css/modules/datalink/widget.datalink.schema.add.css/",

                "com/fr/bi/web/css/modules/cubepath/widget.cubepath.css",
                "com/fr/bi/web/css/modules/cubepath/widget.cubepath.confirm.css",

                "com/fr/bi/web/css/modules/targetstyle/widget.targetstylesetting.css",
                "com/fr/bi/web/css/modules/targetstyle/widget.targetcondition.stylesetting.css",
                "com/fr/bi/web/css/modules/targetstyle/widget.stylesetting.iconmark.css",

                "com/fr/bi/web/css/modules/widgetfilter/widget.widgetfilter.css",
                "com/fr/bi/web/css/modules/widgetfilter/item.linkagefilter.css",
                "com/fr/bi/web/css/modules/widgetfilter/item.targetfilter.css",
                "com/fr/bi/web/css/modules/widgetfilter/item.dimensionfilter.css",
                "com/fr/bi/web/css/modules/widgetfilter/item.controlfilter.css",
                "com/fr/bi/web/css/modules/widgetfilter/item.drillfilter.css",

                "com/fr/bi/web/css/modules/globalupdate/widget.globalupdate.setting.css",

                //权限相关
                "com/fr/bi/web/css/modules/permissionmanage/authoritypackagestree.css",
                "com/fr/bi/web/css/modules/permissionmanage/logininfo/widget.selectfieldmask.logininfo.css",
                "com/fr/bi/web/css/modules/permissionmanage/authorityset/authority.batchset.pane.css",
                "com/fr/bi/web/css/modules/permissionmanage/authorityset/authority.singleset.pane.css",
                "com/fr/bi/web/css/modules/permissionmanage/addrole/searcher.singleaddrole.css",
                "com/fr/bi/web/css/modules/permissionmanage/addrole/searcher.batchaddrole.css",
                "com/fr/bi/web/css/modules/permissionmanage/addrole/authority.singleaddrole.pane.css",
                "com/fr/bi/web/css/modules/permissionmanage/addrole/authority.batchaddrole.pane.css",

                //另存为
                "com/fr/bi/web/css/modules/saveas/report.saveas.floatbox.css",

                //图表钻取
                "com/fr/bi/web/css/modules/chartdrill/widget.chartdrill.css",
                "com/fr/bi/web/css/modules/chartdrill/cell.chartdrill.css",

                //excelview
                "com/fr/bi/web/css/extend/excelview/excelview.cell.css",
                "com/fr/bi/web/css/extend/excelview/excelview.css",

                //excelview设置
                "com/fr/bi/web/css/extend/excelviewsetting/tree/items/header.excelviewsetting.css",
                "com/fr/bi/web/css/extend/excelviewsetting/tree/items/item.excelviewsetting.css",
                "com/fr/bi/web/css/extend/excelviewsetting/tree/table.excelviewsetting.css",
                "com/fr/bi/web/css/extend/excelviewsetting/tree/expander.excelviewsetting.css",
                "com/fr/bi/web/css/extend/excelviewsetting/excel/excelviewsetting.cell.css",
                "com/fr/bi/web/css/extend/excelviewsetting/excel/excel.excelviewsetting.css",
                "com/fr/bi/web/css/extend/excelviewsetting/excelviewsetting.css",


                //详细设置相关模块(预览)
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/charttype/combo/combo.tabletype.css",
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/charttype/charttype.css",
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/regions/region.dimension.css",
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/regions/region.target.css",
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/regionsmanager.css",
                "com/fr/bi/web/css/modules4show/dimensionsmanager4show/dimensionsmanager.css",

                //更新设置
                "com/fr/bi/web/css/extend/update/update.tabledata.css",
                "com/fr/bi/web/css/extend/update/singletable/update.singletable.setting.css",
                "com/fr/bi/web/css/extend/update/singletable/preview/update.previewpane.css",

                //指标弹出明细表
                "com/fr/bi/web/css/modules/detailtablepopup/module/selectdata/treeitem/item.level0.css",
                "com/fr/bi/web/css/modules/detailtablepopup/module/selectdata/treeitem/item.level1.css",
                "com/fr/bi/web/css/modules/detailtablepopup/module/detailtable/cell/cell.detailtable.detailtablepopup.css",
                "com/fr/bi/web/css/modules/detailtablepopup/module/detailtable/cell/header.detailtable.detailtablepopup.css",
                "com/fr/bi/web/css/modules/detailtablepopup/module/detailtable/detailtable.detailtablepopup.css",
                "com/fr/bi/web/css/modules/detailtablepopup/module/dimensionsmanager/dimensionsmanager.detailtablepopup.css",
                "com/fr/bi/web/css/modules/detailtablepopup/module/view/view.detailtablepopup.css",
                "com/fr/bi/web/css/modules/detailtablepopup/detailtablepopup.css",

                //选择字段服务
                "com/fr/bi/web/css/services/packageselectdataservice/relationtable/node.relationtables.css"
        };
    }

    public static String[] getCommonJs() {
        return new String[]{
                "com/fr/bi/web/js/modules/utils.js",
                "com/fr/bi/web/js/modules/utils4conf.js",
                "com/fr/bi/web/js/modules/config.js",
                "com/fr/bi/web/js/modules/constant.js",
                "com/fr/bi/web/js/modules/cache.js",
                "com/fr/bi/web/js/modules/broadcast.js",

                //下面是各个功能模块
                "com/fr/bi/web/js/fragments/cube/button.hoursetting.js",
                "com/fr/bi/web/js/modules/tablefield/button.relationtables.js",
                "com/fr/bi/web/js/fragments/base/tabs/widget.datastyletab.js",
                "com/fr/bi/web/js/fragments/base/items/widget.realdatacheckbox.js",
                "com/fr/bi/web/js/modules/base/buttons/button.databasetable.js",
                "com/fr/bi/web/js/modules/base/combos/widget.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/abstract.dimensiontarget.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/dimension/abstract.dimension.combo.js",
                "com/fr/bi/web/js/modules/base/combos/widget.controldimension.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/dimension/widget.stringdimension.combo.js",

                "com/fr/bi/web/js/modules/base/combos/dimension/dimension/widget.numberdimension.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/dimension/widget.datedimension.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/target/widget.target.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/target/widget.count.target.combo.js",
                "com/fr/bi/web/js/modules/base/combos/dimension/target/widget.calculate.target.combo.js",
                "com/fr/bi/web/js/modules/base/combos/detail/widget.detailstring.combo.js",
                "com/fr/bi/web/js/modules/base/combos/detail/widget.detailnumber.combo.js",
                "com/fr/bi/web/js/modules/base/combos/detail/widget.detaildate.combo.js",
                "com/fr/bi/web/js/modules/base/combos/detail/widget.detailformula.combo.js",
                "com/fr/bi/web/js/modules/base/buttons/button.databasetable.js",

                //statistic
                "com/fr/bi/web/js/modules/base/combos/statistic/widget.statisticnumber.combo.js",
                "com/fr/bi/web/js/modules/base/combos/statistic/widget.statisticdate.combo.js",
                "com/fr/bi/web/js/modules/base/combos/statistic/widget.statisticstring.combo.js",

                //group
                "com/fr/bi/web/js/modules/base/combos/group/widget.groupnumber.combo.js",
                "com/fr/bi/web/js/modules/base/combos/group/widget.groupdate.combo.js",
                "com/fr/bi/web/js/modules/base/combos/group/widget.groupstring.combo.js",

                //数值区间自定义分组forDezi
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.combo.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.group.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.item.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.other.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.tab.js",
                "com/fr/bi/web/js/modules/numberintervalcustomgroup/widget.customgroup.number.panel.js",

                //详细设置相关模块
                "com/fr/bi/web/js/modules/dimensionsmanager/charttype/combo/combo.tablecharttype.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/charttype/combo/maptypecombo/combo.maptype.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/charttype/combo/maptypecombo/popup.maptype.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/charttype/charttype.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regions/abstract.region.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regions/region.dimension.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regions/region.target.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regions/region.detail.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regions/region.tree.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/regionsmanager.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/model.dimensionsmanager.js",
                "com/fr/bi/web/js/modules/dimensionsmanager/dimensionsmanager.js",

                "com/fr/bi/web/js/modules/tablechartmanager/tablechartmanager.js",
                "com/fr/bi/web/js/modules/tablechartmanager/errorpane/tablechart.errorpane.js",

                "com/fr/bi/web/js/modules/dimensionsmanagers4show/model.dimensionsmanagershow.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/regionsmanagershow.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/dimensionsmanagershow.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/charttype/combo/combo.tabletype.show.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/charttype/charttype.show.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/regions/region.dimensionshow.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/regions/region.targetshow.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/regions/region.detailshow.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/regions/abstract.regionshow.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/widget/widget.count.target.combo.show.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/widget/widget.datedimension.combo.show.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/widget/widget.numberdimension.combo.show.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/widget/widget.stringdimension.combo.show.js",
                "com/fr/bi/web/js/modules/dimensionsmanagers4show/widget/widget.target.combo.show.js",

                //自定义分组
                "com/fr/bi/web/js/modules/customgroup/widget.group2other.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.combo.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.search.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.node.arrow.delete.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/view.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.expander.group.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.button.field.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.pane.group.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.pane.allfileds.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.pane.searcher.customgroup.js",
                "com/fr/bi/web/js/modules/customgroup/widget.view.searcher.customgroup.js",

                //取数
                "com/fr/bi/web/js/modules/selectdatacombo/widget.selectdatacombo.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/widget.selectdata4dimensioncombo.js",

                //自定义排序
                "com/fr/bi/web/js/modules/customsort/widget.pane.customsort.js",

                //详细设置中的选择字段
                "com/fr/bi/web/js/modules/selectdata/preview/pane.preview.selectdata.js",
                "com/fr/bi/web/js/modules/selectdata/preview/section.preview.selectdata.js",
                "com/fr/bi/web/js/modules/selectdata/treeitem/item.level0.js",
                "com/fr/bi/web/js/modules/selectdata/treeitem/item.level1.js",
                "com/fr/bi/web/js/modules/selectdata/treeitem/item.level2.js",

                "com/fr/bi/web/js/modules/selectdata/treeitem4reusedimension/calctarget.item.level0.js",
                "com/fr/bi/web/js/modules/selectdata/treeitem4reusedimension/calctarget.button.level0.js",

                "com/fr/bi/web/js/modules/selectdata/treeitem4reusedimension/item.level0.js",
                "com/fr/bi/web/js/modules/selectdata/widget.selectdatapane.js",
                "com/fr/bi/web/js/modules/selectdata/widget.selectdimensionpane.js",
                "com/fr/bi/web/js/modules/selectdata/tab.selectdata.js",

                //明细表选字段
                "com/fr/bi/web/js/modules/selectdata4detail/treenode/abstract.node.level.js",
                "com/fr/bi/web/js/modules/selectdata4detail/treenode/node.level0.js",
                "com/fr/bi/web/js/modules/selectdata4detail/treenode/node.level1.js",
                "com/fr/bi/web/js/modules/selectdata4detail/widget.selectdatapane.detail.js",

                //树控件选字段
                "com/fr/bi/web/js/modules/selectdata4tree/treenode/abstract.node.level.js",
                "com/fr/bi/web/js/modules/selectdata4tree/treenode/node.level0.js",
                "com/fr/bi/web/js/modules/selectdata4tree/treenode/node.level1.js",
                "com/fr/bi/web/js/modules/selectdata4tree/widget.selectdatapane.tree.js",


                //过滤界面的选择字段
                "com/fr/bi/web/js/modules/selectdata4filter/widget.dimensionselectfield.js",
                "com/fr/bi/web/js/modules/selectdata4filter/widget.targetselectfield.js",
                "com/fr/bi/web/js/modules/selectdata4filter/node/node.level0.dimension.js",

                //通用查询选字段
                "com/fr/bi/web/js/modules/selectdata4generalquery/widget.generalquery.selectdata.tab.js",
                "com/fr/bi/web/js/modules/selectdata4generalquery/widget.generalquery.selectdata.pane.js",
                "com/fr/bi/web/js/modules/selectdata4generalquery/widget.generalquery.usedfields.pane.js",
                "com/fr/bi/web/js/modules/selectdata4generalquery/item/item.generalquery.selectdata.js",

                //分组统计选字段
                "com/fr/bi/web/js/modules/selectdata4statistics/widget.selectsingletablefield.js",

                //文本控件选字段
                "com/fr/bi/web/js/modules/selectstring/treeitem/item.level0.js",
                "com/fr/bi/web/js/modules/selectstring/pane.selectstring.js",

                //数值控件选字段
                "com/fr/bi/web/js/modules/selectnumber/treeitem/item.level0.js",
                "com/fr/bi/web/js/modules/selectnumber/pane.selectnumber.js",

                //日期控件选字段
                "com/fr/bi/web/js/modules/selectdate/treeitem/item.level0.js",
                "com/fr/bi/web/js/modules/selectdate/pane.selectdate.js",


                //文本控件
                "com/fr/bi/web/js/modules/selectdatacombo/widget.selectdatacombo.js",

                //树控件
                "com/fr/bi/web/js/modules/selecttreedatacombo/selecttreedatacombo.js",


                "com/fr/bi/web/js/extend/excel/upload/excel.upload.js",
                "com/fr/bi/web/js/extend/excel/upload/excel.upload.model.js",
                "com/fr/bi/web/js/extend/excel/upload/button.uploadexcel.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/excel.fieldset.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/excel.fieldset.table.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/combo/combo.fieldset.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/combo/trigger.fieldset.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/combo/item.excelfieldtype.js",
                "com/fr/bi/web/js/extend/excel/upload/fieldset/combo/popup.fieldset.js",
                "com/fr/bi/web/js/extend/excel/upload/tipcombo/excel.tipcombo.js",

                "com/fr/bi/web/js/extend/sql/editsql/sql.edit.js",
                "com/fr/bi/web/js/extend/sql/editsql/sql.edit.model.js",

                "com/fr/bi/web/js/modules/date/interval.date.js",
                "com/fr/bi/web/js/modules/date/interval.date.param.js",
                "com/fr/bi/web/js/modules/date/multi.date.js",

                "com/fr/bi/web/js/modules/tablefield/widget.tablefield.js",
                "com/fr/bi/web/js/modules/tablefield/widget.tablefield.searchresult.pane.js",
                "com/fr/bi/web/js/modules/tablefield/widget.tablefieldwithsearch.pane.js",

                //下拉树控件字段关联设置
                "com/fr/bi/web/js/modules/fieldrelationsettingwithpreview/fieldrelationsettingwithpreviewpopup.js",
                "com/fr/bi/web/js/modules/fieldrelationsettingwithpreview/model.fieldrelationsettingwithpreviewpopup.js",

                //明细表字段关联设置
                "com/fr/bi/web/js/modules/fieldrelationsetting/item.path.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/item.commontable.set.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/switch.path.setting.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/tab.path.setting.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/popup.path.setting.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/combo.path.setting.detailtable.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/fieldrelationsettingpopup.js",
                "com/fr/bi/web/js/modules/fieldrelationsetting/model.fieldrelationsettingpopup.js",

                //下拉树控件字段关联设置
                "com/fr/bi/web/js/modules/fieldrelationsettingwithpreview/fieldrelationsettingwithpreviewpopup.js",
                "com/fr/bi/web/js/modules/fieldrelationsettingwithpreview/model.fieldrelationsettingwithpreviewpopup.js",

                //过滤条件
                "com/fr/bi/web/js/modules/filter/filterpopup/targetfilterpopup.js",
                "com/fr/bi/web/js/modules/filter/filterpopup/dimensionfilterpopup.js",
                "com/fr/bi/web/js/modules/filter/filterpopup/targetsummaryfilter.popup.js",
                "com/fr/bi/web/js/modules/filter/filterpopup/detailtablefilter.popup.js",
                "com/fr/bi/web/js/modules/filter/filterpopup/authorityfilter.popup.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.stringfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.numberfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.datefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/target.date.tab.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.notypefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/factory.filteritem.target.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/factory.filteritem.dimension.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.stringfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.datefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/widget.selectdata4dimensioncombo.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/widget.selectdata4targetcombo.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.numberfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.notypefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.formula.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.formula.js",
                "com/fr/bi/web/js/modules/filter/filteritems/dimension/item.emptyformula.js",
                "com/fr/bi/web/js/modules/filter/filteritems/target/item.emptyformula.js",

                "com/fr/bi/web/js/modules/filter/filteritems/auth/item.notypefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/item.stringfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/item.numberfield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/item.datefield.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/widget.selectdata4authority.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/factory.filteritem.auth.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/widget.authority.stringcombo.js",
                "com/fr/bi/web/js/modules/filter/filteritems/auth/widget.logininfo.combo.js",

                "com/fr/bi/web/js/modules/filter/filteritems/generalquery/item.generalquery.notypefilter.js",

                //过滤
                "com/fr/bi/web/js/modules/filter/expander/filter.expander.js",
                "com/fr/bi/web/js/modules/filter/filter.target.js",
                "com/fr/bi/web/js/modules/filter/filter.dimension.js",
                "com/fr/bi/web/js/modules/filter/targetsummary/filter.target.summary.js",
                "com/fr/bi/web/js/modules/filter/generalquery/filter.generalquery.js",
                "com/fr/bi/web/js/modules/filter/detailtable/filter.detailtable.js",
                "com/fr/bi/web/js/modules/filter/auth/filter.authority.js",

                //计算指标
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/calculatetargetpopup.summary.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/model.calculatetargetpopup.summary.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4detail/calculatetargetpopup.detail.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4detail/model.calculatetargetpopup.detail.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.abstract.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.group.abstract.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.period.rate.abstract.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.period.value.abstract.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.month.on.month.value.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.month.on.month.rate.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.year.on.year.rate.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.year.on.year.value.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.formula.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.rank.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.rank.group.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.sum.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.sum.group.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.sum.above.group.js",
                "com/fr/bi/web/js/modules/calculatetarget/calculatetarget4summary/pane/pane.calculate.target.sum.above.js",


                //带参数的复杂日期模块
                "com/fr/bi/web/js/modules/multidatecombowithparam/item/item.level0.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/item/item.level1.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/multidate.parampane.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/multidate.parampopup.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/multidate.paramtrigger.js",
                "com/fr/bi/web/js/modules/multidatecombowithparam/multidate.paramcombo.js",
                "com/fr/bi/web/js/modules/paramtimeinterval/timeinterval.param.js",

                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/widget.relationpane.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/widget.relationpane.model.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/tools/widget.relation.settingtable.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/tools/widget.relationset.group.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/tools/button.relationtable.field.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/selectdatamask/widget.selectdata.mask.js",
                "com/fr/bi/web/js/modules/onepackage/etl/relation/pane/selectdata4relation/widget.selectsinglefield.relation.js",

                "com/fr/bi/web/js/modules/selectonetable/widget.selectonetable.pane.js",
                "com/fr/bi/web/js/modules/selecttable/widget.selecttable.pane.js",
                "com/fr/bi/web/js/modules/selecttable/linknames/widget.datalinkgroup.js",
                "com/fr/bi/web/js/modules/selecttable/widget.datalinktab.js",
                "com/fr/bi/web/js/modules/selecttable/sourcetable/widget.databasetables.mainpane.js",
                "com/fr/bi/web/js/modules/selecttable/sourcetable/widget.databasetables.pane.js",
                "com/fr/bi/web/js/modules/selecttable/sourcetable/widget.databasetables.pager.js",
                "com/fr/bi/web/js/modules/selecttable/sourcetable/widget.databasetables.searchresult.pane.js",
                "com/fr/bi/web/js/modules/selecttable/packagetable/widget.packagetables.pane.js",
                "com/fr/bi/web/js/modules/selecttable/etltable/widget.etltables.pane.js",
                "com/fr/bi/web/js/modules/selecttable/etltable/widget.etlflowchart.button.js",

                //表格的单元格
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tablecell/normal/headercell.normal.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tablecell/normal/cell.tarbody.normal.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/tablecell/normal/expandercell.normal.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/combo/sortfilter.dimension.combo.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/combo/sortfilter.target.combo.js",

                "com/fr/bi/web/js/modules/tablechartmanager/datatable/summarytable/widget.summarytable.js",
                "com/fr/bi/web/js/modules/tablechartmanager/datatable/summarytable/summarytable.model.js",

                //明细表
                "com/fr/bi/web/js/modules/detailtable/widget.detailtable.js",
                "com/fr/bi/web/js/modules/detailtable/cell/header.detailtable.js",
                "com/fr/bi/web/js/modules/detailtable/cell/cell.detailtable.js",

                //业务包分组
                "com/fr/bi/web/js/modules/businesspackagegroup/buttons/button.businesspackage.mange.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/buttons/button.businesspackage.add.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/expander.businesspackage.group.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/pane.businesspackage.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/pane.ungroup.and.group.businesspackage.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/widget.group.businesspackage.js",
                "com/fr/bi/web/js/modules/businesspackagegroup/widget.manage.businesspackage.js",


                //etl
                //etl新增分组列
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.button.field.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.combo.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.expander.group.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.group2other.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.node.arrow.delete.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.pane.allfields.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.pane.group.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.search.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.pane.searcher.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/addgroupfield/widget.view.searcher.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/widget.addgroupfield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/widget.addgroupfield.model.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/widget.addgroupfield.popover.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addgroupfield/widget.addgroupfield.popover.model.js",

                //etl新增公式列widget
                "com/fr/bi/web/js/modules/onepackage/etl/addformulafield/widget.addformulafield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addformulafield/widget.addformulafield.model.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addformulafield/widget.addformulafield.popover.js",
                "com/fr/bi/web/js/modules/onepackage/etl/addformulafield/widget.addformulafield.popover.model.js",

                //etl流程图
                "com/fr/bi/web/js/modules/onepackage/etl/flowchart/widget.etltable.combo.js",
                "com/fr/bi/web/js/modules/onepackage/etl/flowchart/widget.etltable.operator.js",
                "com/fr/bi/web/js/modules/onepackage/etl/flowchart/widget.etltables.pane.js",

                //union
                "com/fr/bi/web/js/modules/onepackage/etl/union/union/widget.addunion.table.js",
                "com/fr/bi/web/js/modules/onepackage/etl/union/union/widget.unionresult.header.js",
                "com/fr/bi/web/js/modules/onepackage/etl/union/widget.union.js",
                "com/fr/bi/web/js/modules/onepackage/etl/union/widget.union.model.js",

                //join
                "com/fr/bi/web/js/modules/onepackage/etl/join/join/widget.jointype.group.js",
                "com/fr/bi/web/js/modules/onepackage/etl/join/join/widget.jointype.button.js",
                "com/fr/bi/web/js/modules/onepackage/etl/join/join/widget.joinresult.header.js",
                "com/fr/bi/web/js/modules/onepackage/etl/join/widget.join.js",
                "com/fr/bi/web/js/modules/onepackage/etl/join/widget.join.model.js",

                //etl预览
                "com/fr/bi/web/js/modules/onepackage/etl/preview/widget.etltable.preview.js",
                "com/fr/bi/web/js/modules/onepackage/etl/preview/widget.etltable.preview.center.js",

                //etl关联
                "com/fr/bi/web/js/modules/onepackage/etl/relation/widget.setrelation.pane.js",

                //etl过滤
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/field/factory.filteritem.field.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/field/item.datefield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/field/item.notypefield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/field/item.numberfield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/field/item.stringfield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/formula/item.emptyformula.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filteritem/formula/item.formula.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filter.expander.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filter.fieldandformula.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filter.selectsinglefield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/filter.multiselect.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/widget.filter.js",
                "com/fr/bi/web/js/modules/onepackage/etl/filter/widget.filter.model.js",

                //增加公式列
                "com/fr/bi/web/js/modules/onepackage/etl/confaddfield/widget.button.formulafield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/confaddfield/widget.formulalist.js",

                //行列转化
                "com/fr/bi/web/js/modules/onepackage/etl/convert/convert/convert.selectFieldsDataPane.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/convert/convert.initialFields.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/convert/convert.genFields.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/convert/convert.displaylabel.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/convert/convert.listItem.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/widget.convert.js",
                "com/fr/bi/web/js/modules/onepackage/etl/convert/widget.convert.model.js",

                //自循环列
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.tab.region.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.select.field.button.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.tab.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.two.condition.switch.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.one.region.popup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/tab/circle.two.region.popup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/circle.operator.pane.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/levelpane/circle.level.pane.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/circle.self.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/operatorpane/circle.showtextcombo.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/levelpane/circle.display.editor.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/circle/circle.result.pane.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/widget.circle.js",
                "com/fr/bi/web/js/modules/onepackage/etl/circle/widget.circle.model.js",


                //分组统计
                "com/fr/bi/web/js/modules/onepackage/etl/group/group.select.fields.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/group.select.fields.item.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/group.select.fields.item.group.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.combo.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.group.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.item.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.other.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.panel.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/number/widget.customgroup.number.tab.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.group2other.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.combo.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.search.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.node.arrow.delete.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.expander.group.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.button.field.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.pane.group.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.pane.allfields.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.pane.searcher.customgroup.js",
                "com/fr/bi/web/js/modules/onepackage/etl/group/customgroup/widget.view.searcher.customgroup.js",


                //部分字段
                "com/fr/bi/web/js/modules/onepackage/etl/partfield/partfield/widget.selectpartfieldlist.js",
                "com/fr/bi/web/js/modules/onepackage/etl/partfield/widget.partfield.js",
                "com/fr/bi/web/js/modules/onepackage/etl/partfield/widget.partfield.model.js",

                //分组统计
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/customgroup/customgroup.statistic.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/abstract.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/date.group.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/date.statistic.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/number.group.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/number.statistic.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/string.statistic.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/field/string.group.dimension.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/numbercustomgroup/numbercustomgroup.statistic.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/widget.statistic.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/region/region.string.js",
                "com/fr/bi/web/js/modules/onepackage/etl/statistic/widget.statistic.model.js",

                //etl
                "com/fr/bi/web/js/modules/onepackage/etl/widget.etl.model.js",
                "com/fr/bi/web/js/modules/onepackage/etl/widget.etl.js",


                //FineBI Service
                "com/fr/bi/web/js/modules/finebiservice/expander.finebiservice.js",
                "com/fr/bi/web/js/modules/finebiservice/finebiservice.js",

                //指标和维度的匹配关系
                "com/fr/bi/web/js/modules/matchingrelationship/popup.matchingrelationship.js",
                "com/fr/bi/web/js/modules/matchingrelationship/tab.matchingrelationship.js",
                "com/fr/bi/web/js/modules/matchingrelationship/infopane/unsettingtargetregion.infopane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/infopane/settargetregion.infopane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/infopane/infopane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/settingpane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/multipathchooser.settingpane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/multimatchmulti.settingpane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/targetlabelcontrol.settingpane.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/dimensiontreecombo/dimensiontree.combo.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/dimensiontreecombo/dimensiontree.popup.js",
                "com/fr/bi/web/js/modules/matchingrelationship/settingpane/dimensiontreecombo/dimensiontree.trigger.js",

                //表格属性设置
                "com/fr/bi/web/js/modules/chartsetting/charts/selectcolorcombo/combo.selectcolor.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/selectcolorcombo/wrap.item.selectcolor.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/selectcolorcombo/item.selectcolor.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/selectcolorcombo/popup.selectcolor.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/selectcolorcombo/trigger.selectcolor.js",
                "com/fr/bi/web/js/modules/chartsetting/widget.chartsetting.js",
                "com/fr/bi/web/js/modules/chartsetting/grouptable/widget.grouptable.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/charts.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/multiaxischart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/percentchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/lineareachart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/barchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/scatterchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/comparecolumnchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/compareareachart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/dashboardchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/donutchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/fallaxischart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/piechartsetting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/radarchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/rangeareachart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/forcebubble.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/mapchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/charts/settings/gismapchart.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/crosstable/widget.crosstable.setting.js",
                "com/fr/bi/web/js/modules/chartsetting/detailtable/widget.detailtable.setting.js",

                //展示表格
                "com/fr/bi/web/js/modules/tablechartmanager/chartdisplay/chartdisplay.js",
                "com/fr/bi/web/js/modules/tablechartmanager/chartdisplay/chartdisplay.model.js",

                //警戒线
                "com/fr/bi/web/js/modules/cordon/pane/item.cordon.js",
                "com/fr/bi/web/js/modules/cordon/pane/pane.cordon.js",
                "com/fr/bi/web/js/modules/cordon/popup.cordon.js",

                "com/fr/bi/web/js/fragments/multirelation/expander.multirelation.js",
                "com/fr/bi/web/js/fragments/multirelation/multirelation.js",
                "com/fr/bi/web/js/fragments/multirelation/item.multirelation.js",
                "com/fr/bi/web/js/fragments/multirelation/item.tablefield.multirelation.js",
                "com/fr/bi/web/js/fragments/multirelation/view.searcher.multirelation.js",

                //业务包面板
                "com/fr/bi/web/js/modules/onepackage/onepackagetablespane/widget.packagetables.pane.js",
                "com/fr/bi/web/js/modules/onepackage/onepackagetablespane/widget.packagesearcher.resultpane.js",

                //表关联视图
                "com/fr/bi/web/js/modules/onepackage/onepackagerelationspane/tablerelations.pane.js",
                "com/fr/bi/web/js/modules/onepackage/onepackagerelationspane/tablerelations.pane.model.js",

                //自适应布局
                //选组件
                "com/fr/bi/web/js/modules/fit/widgetchooser/reuse/pane.reuse.js",
                "com/fr/bi/web/js/modules/fit/widgetchooser/dragiconbutton.js",
                "com/fr/bi/web/js/modules/fit/widgetchooser/dragiconcombo.js",
                "com/fr/bi/web/js/modules/fit/widgetchooser/reuse/dragwidgetitem.js",
                "com/fr/bi/web/js/modules/fit/widgetchooser/widget.dragicongroup.js",
                "com/fr/bi/web/js/modules/fit/fit.widget.js",
                "com/fr/bi/web/js/modules/fit/fit.js",

                //联动
                "com/fr/bi/web/js/modules/linkage/model.linkage.js",
                "com/fr/bi/web/js/modules/linkage/linkage.target.js",
                "com/fr/bi/web/js/modules/linkage/linkage.targets.js",
                "com/fr/bi/web/js/modules/linkage/linkage.js",

                //onewidget
                "com/fr/bi/web/js/modules/onewidget/onewidget.js",
                "com/fr/bi/web/js/modules/onewidget/detailtable.js",

                //one package
                "com/fr/bi/web/js/modules/onepackage/widget.onepackage.js",
                "com/fr/bi/web/js/modules/onepackage/widget.onepackage.model.js",

                //数据连接
                "com/fr/bi/web/js/modules/datalink/widget.testlink.loading.js",
                "com/fr/bi/web/js/modules/datalink/combo.adddatalink.js",
                "com/fr/bi/web/js/modules/datalink/normal/widget.datalink.add.js",
                "com/fr/bi/web/js/modules/datalink/normal/widget.datalink.add.model.js",
                "com/fr/bi/web/js/modules/datalink/schema/widget.datalink.schema.add.js",
                "com/fr/bi/web/js/modules/datalink/schema/widget.datalink.schema.add.model.js",

                "com/fr/bi/web/js/modules/cubepath/widget.cubepath.js",
                "com/fr/bi/web/js/modules/cubepath/widget.cubepath.confirm.js",

                //指标样式设置
                "com/fr/bi/web/js/modules/targetstyle/widget.targetstylesetting.js",
                "com/fr/bi/web/js/modules/targetstyle/widget.targetcondition.stylesetting.js",
                "com/fr/bi/web/js/modules/targetstyle/widget.stylesetting.iconmark.js",
                "com/fr/bi/web/js/modules/targetstyle/combo/combo.iconmark.js",
                "com/fr/bi/web/js/modules/targetstyle/combo/popup.iconmark.js",
                "com/fr/bi/web/js/modules/targetstyle/combo/trigger.iconmark.js",
                "com/fr/bi/web/js/modules/targetstyle/combo/item.iconmark.js",
                "com/fr/bi/web/js/modules/targetstyle/conditionitem/item.targetstylecondition.js",


                //cube日志
                "com/fr/bi/web/js/modules/cubelog/items/item.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/items/title.item.wronginfo.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/items/item.wronginfo.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/nodes/node.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/nodes/node.wronginfo.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/popup/popup.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/expander/expander.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/tree.cubelog.js",
                "com/fr/bi/web/js/modules/cubelog/cubelog.js",

                "com/fr/bi/web/js/modules/widgetfilter/widget.widgetfilter.js",
                "com/fr/bi/web/js/modules/widgetfilter/widget.widgetfilter.model.js",
                "com/fr/bi/web/js/modules/widgetfilter/item.linkagefilter.js",
                "com/fr/bi/web/js/modules/widgetfilter/item.targetfilter.js",
                "com/fr/bi/web/js/modules/widgetfilter/item.dimensionfilter.js",
                "com/fr/bi/web/js/modules/widgetfilter/item.controlfilter.js",
                "com/fr/bi/web/js/modules/widgetfilter/item.drillfilter.js",

                //全局更新
                "com/fr/bi/web/js/modules/globalupdate/widget.globalupdate.setting.js",
                "com/fr/bi/web/js/modules/globalupdate/item/item.timesetting.js",

                //明细表超级链接
                "com/fr/bi/web/js/modules/hyperlink/hyperlink.insert.js",
                "com/fr/bi/web/js/modules/hyperlink/hyperlink.popup.js",

                //另存为
                "com/fr/bi/web/js/modules/saveas/report.saveas.floatbox.js",

                //图表钻取
                "com/fr/bi/web/js/modules/chartdrill/widget.chartdrill.js",
                "com/fr/bi/web/js/modules/chartdrill/cell.chartdrill.js",
                "com/fr/bi/web/js/modules/chartdrill/button.pushdrill.js",

                //etl plugin
                "com/fr/bi/web/js/extend/excel/etl.excel.plugin.js",
                //sql plugin
                "com/fr/bi/web/js/extend/sql/etl.sql.plugin.js",

                //excelview
                "com/fr/bi/web/js/extend/excelview/excelview.cell.js",
                "com/fr/bi/web/js/extend/excelview/excelview.js",

                //excelview设置
                "com/fr/bi/web/js/extend/excelviewsetting/tree/items/header.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/tree/items/item.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/tree/table.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/tree/expander.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/tree/tree.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/excel/excelviewsetting.cell.js",
                "com/fr/bi/web/js/extend/excelviewsetting/excel/excel.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/model.excelviewsetting.js",
                "com/fr/bi/web/js/extend/excelviewsetting/excelviewsetting.js",

                //权限控制
                "com/fr/bi/web/js/modules/permissionmanage/authoritypackagestree.js",
                "com/fr/bi/web/js/modules/permissionmanage/authorityset/authority.singleset.pane.js",
                "com/fr/bi/web/js/modules/permissionmanage/authorityset/authority.batchset.pane.js",
                "com/fr/bi/web/js/modules/permissionmanage/addrole/authority.singleaddrole.pane.js",
                "com/fr/bi/web/js/modules/permissionmanage/addrole/authority.batchaddrole.pane.js",
                "com/fr/bi/web/js/modules/permissionmanage/addrole/searcher.batchaddrole.js",
                "com/fr/bi/web/js/modules/permissionmanage/addrole/searcher.singleaddrole.js",
                "com/fr/bi/web/js/modules/permissionmanage/logininfo/widget.selectsinglefield.logininfo.js",
                "com/fr/bi/web/js/modules/permissionmanage/logininfo/widget.selectfieldmask.logininfo.js",

                //dimensionmanager4show
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/model.dimensionsmanagershow.js",
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/regionsmanagershow.js",
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/dimensionsmanagershow.js",
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/charttype/combo/combo.tabletype.show.js",
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/charttype/combo/maptypescombo/combo.maptype.show.js",
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/charttype/combo/maptypescombo/popup.maptype.show.js",
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/charttype/charttype.show.js",
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/regions/region.dimensionshow.js",
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/regions/region.targetshow.js",
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/regions/region.detailshow.js",
                "com/fr/bi/web/js/modules4show/dimensionsmanager4show/regions/abstract.regionshow.js",

                //dimension show
                "com/fr/bi/web/js/modules4show/dimension4show/abstract.dimensiontarget.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/dimension/abstract.dimension.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/dimension/widget.numberdimension.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/dimension/widget.datedimension.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/dimension/widget.stringdimension.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/target/widget.target.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/target/widget.count.target.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/target/widget.calculate.target.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/detail/widget.detaildate.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/detail/widget.detailformula.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/detail/widget.detailnumber.combo.show.js",
                "com/fr/bi/web/js/modules4show/dimension4show/detail/widget.detailstring.combo.show.js",

                //实时报表进度条
                "com/fr/bi/web/js/modules4show/cubeprogressbar/cubeprogressbar.js",
                //实时报表指示器
                "com/fr/bi/web/js/modules4show/cubeprogressindicator/cubeprogressindicator.js",

                //实时报表选择字段
                "com/fr/bi/web/js/modules4realtime/selectdata/treenode/abstract.node.level.js",
                "com/fr/bi/web/js/modules4realtime/selectdata/treenode/node.level0.js",
                "com/fr/bi/web/js/modules4realtime/selectdata/treenode/node.level1.js",
                "com/fr/bi/web/js/modules4realtime/selectdata/widget.selectdata.js",
                //实时报表文本选择字段
                "com/fr/bi/web/js/modules4realtime/selectstring/widget.selectstring.js",
                //实时报表数值选择字段
                "com/fr/bi/web/js/modules4realtime/selectnumber/widget.selectnumber.js",
                //实时报表日期选择字段
                "com/fr/bi/web/js/modules4realtime/selectdate/widget.selectdate.js",

                //表更新
                "com/fr/bi/web/js/extend/update/update.tabledata.js",
                "com/fr/bi/web/js/extend/update/update.tabledata.model.js",
                "com/fr/bi/web/js/extend/update/singletable/update.singletable.setting.js",
                "com/fr/bi/web/js/extend/update/singletable/update.singletable.setting.model.js",
                "com/fr/bi/web/js/extend/update/singletable/preview/update.previewpane.js",
                "com/fr/bi/web/js/extend/update/singletable/preview/update.previewpane.model.js",

                //指标弹出明细表
                "com/fr/bi/web/js/modules/detailtablepopup/module/selectdata/treenode/abstract.node.level.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/selectdata/treeitem/item.level0.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/selectdata/treeitem/item.level1.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/selectdata/treeitem/item.level2.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/selectdata/treenode/node.level0.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/selectdata/treenode/node.level1.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/selectdata/selectdata.detailtablepopup.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/dimensionsmanager/region.detailtablepopup.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/dimensionsmanager/dimensionsmanager.detailtablepopup.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/detailtable/cell/cell.detailtable.detailtablepopup.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/detailtable/cell/header.detailtable.detailtablepopup.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/detailtable/model.detailtable.detailtablepopup.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/detailtable/detailtable.detailtablepopup.js",

                "com/fr/bi/web/js/modules/detailtablepopup/module/model/field/model.detailtablepopup.detail.dimension.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/model/model.detailtablepopup.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/view/field/view.detailtablepopup.detail.dimension.js",
                "com/fr/bi/web/js/modules/detailtablepopup/module/view/view.detailtablepopup.js",
                "com/fr/bi/web/js/modules/detailtablepopup/detailtablepopup.js",


                //业务包选择字段服务
                "com/fr/bi/web/js/services/packageselectdataservice/treenode/node.level0.js",
                "com/fr/bi/web/js/services/packageselectdataservice/treenode/node.level1.js",
                "com/fr/bi/web/js/services/packageselectdataservice/treenode/node.level1.date.js",
                "com/fr/bi/web/js/services/packageselectdataservice/relationtable/node.relationtables.js",
                "com/fr/bi/web/js/services/packageselectdataservice/relationtable/node.level2.date.js",
                "com/fr/bi/web/js/services/packageselectdataservice/relationtable/expander.relationtables.js",
                "com/fr/bi/web/js/services/packageselectdataservice/packageselectdataservice.js",

                //简单字段选择服务
                "com/fr/bi/web/js/services/simpleselectdataservice/simpleselectdataservice.js",


                /**
                 * 切片
                 */

                //tablechartmanager
                "com/fr/bi/web/js/aspects/tablechartmanager/aspect.tablechartmanager.js",

                //detailtable
                "com/fr/bi/web/js/aspects/detailtable/aspect.detailtable.js",

                "com/fr/bi/web/js/aspects/config.js",
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
                "com/fr/bi/web/css/base/third/jquery-ui.custom.css",
                "com/fr/bi/web/css/base/third/jquery.mCustomScrollbar.css",
                "com/fr/bi/web/css/base/third/leaflet.css",
                "com/fr/bi/web/css/base/view/floatboxview.css",
                "com/fr/bi/web/css/base/view/popupview.css",
                "com/fr/bi/web/css/base/view/scrollview.css",
                "com/fr/bi/web/css/base/combination/combo.css",
                "com/fr/bi/web/css/base/combination/searcher.css",
                "com/fr/bi/web/css/base/combination/expander/condition.expander.css",
                "com/fr/bi/web/css/base/colorpicker/button.colorpicker.css",
                "com/fr/bi/web/css/base/colorpicker/colorpicker.css",
                "com/fr/bi/web/css/base/colorpicker/editor.colorpicker.css",
                "com/fr/bi/web/css/base/pager/pager.css",
                "com/fr/bi/web/css/base/pager/pager.number.css",
                "com/fr/bi/web/css/base/pager/pager.skip.css",
                "com/fr/bi/web/css/base/pager/pager.all.css",
                "com/fr/bi/web/css/base/pager/pager.updownprenext.css",
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
                "com/fr/bi/web/css/base/layer/panel.css",
                "com/fr/bi/web/css/base/layer/layer.searcher.css",
                "com/fr/bi/web/css/base/layer/layer.multiselect.css",
                "com/fr/bi/web/css/base/layer/layer.panel.css",


                "com/fr/bi/web/css/utils/widget.css",
                "com/fr/bi/web/css/utils/color.css",
                "com/fr/bi/web/css/utils/common.css",
                "com/fr/bi/web/css/utils/cursor.css",
                "com/fr/bi/web/css/utils/font.css",
                "com/fr/bi/web/css/utils/icon.css",
                "com/fr/bi/web/css/utils/animate.css",
                "com/fr/bi/web/css/utils/background.css",
                "com/fr/bi/web/css/utils/opacity.css",
                "com/fr/bi/web/css/utils/overflow.css",
                "com/fr/bi/web/css/utils/pos.css",
                "com/fr/bi/web/css/utils/size.css",
                "com/fr/bi/web/css/utils/sizing.css",
                "com/fr/bi/web/css/utils/triangle.css",
                "com/fr/bi/web/css/utils/xtable.css",
                "com/fr/bi/web/css/utils/zindex.css",
                "com/fr/bi/web/css/utils/special.css",


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


                "com/fr/bi/web/css/widget/selecttable/single/button.databasetable.css",

                //移动到分组
                "com/fr/bi/web/css/widget/move2group/button.add.move2group.css",
                "com/fr/bi/web/css/widget/move2group/combo.move2group.css",

                //复制到分组
                "com/fr/bi/web/css/widget/copy2group/button.add.copy2group.css",
                "com/fr/bi/web/css/widget/copy2group/combo.copy2group.css",


                //单选下拉树
                "com/fr/bi/web/css/widget/singletree/combo.single.tree.css",

                //文本工具栏
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

                //自适应表
                "com/fr/bi/web/css/widget/adaptivetable/adaptivetable.css",

                //Excel表
                "com/fr/bi/web/css/widget/exceltable/exceltable.cell.css",
                "com/fr/bi/web/css/widget/exceltable/exceltable.header.cell.css",
                "com/fr/bi/web/css/widget/exceltable/exceltable.css",

                //自定义滚动条表
                "com/fr/bi/web/css/widget/customscrolltable/customscrolltable.scrollbar.css",
                "com/fr/bi/web/css/widget/customscrolltable/customscrolltable.css",

                //分页表格
                "com/fr/bi/web/css/widget/pagetable/pagetable.cell.css",
                "com/fr/bi/web/css/widget/pagetable/pagetable.css",

                //带序号表格
                "com/fr/bi/web/css/widget/sequencetable/listnumber.sequencetable.css",
                "com/fr/bi/web/css/widget/sequencetable/treenumber.sequencetable.css",
                "com/fr/bi/web/css/widget/sequencetable/sequencetable.css",


                //布局
                "com/fr/bi/web/css/widget/arrangement/arrangement.droppable.css",
                "com/fr/bi/web/css/widget/arrangement/arrangement.css",

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
                "com/fr/bi/web/js/data/constant/constant.js",
                "com/fr/bi/web/js/data/constant/strings.js",
                "com/fr/bi/web/js/data/constant/enums.js",
                "com/fr/bi/web/js/data/constant/colors.js",
                "com/fr/bi/web/js/data/constant/attrs.js",
                "com/fr/bi/web/js/data/constant/biconst.js",
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
                "com/fr/bi/web/js/base/utils/lru.js",
                "com/fr/bi/web/js/base/utils/aspect.js",

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
                "com/fr/bi/web/js/base/module/combination/map.button.js",
                "com/fr/bi/web/js/base/module/combination/navigation.js",
                "com/fr/bi/web/js/base/module/combination/loader.js",
                "com/fr/bi/web/js/base/module/combination/searcher.js",

                "com/fr/bi/web/js/base/module/farbtastic/farbtastic.js",

                "com/fr/bi/web/js/base/module/chart/chart.js",

                "com/fr/bi/web/js/base/module/canvas/canvas.js",

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
                "com/fr/bi/web/js/base/module/table/table.tree.js",

                "com/fr/bi/web/js/base/module/layer/layer.floatbox.js",
                "com/fr/bi/web/js/base/module/layer/layer.popup.js",
                "com/fr/bi/web/js/base/module/layer/layer.scroll.js",
                "com/fr/bi/web/js/base/module/layer/layer.searcher.js",

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
                "com/fr/bi/web/js/base/wrapper/layout/layout.flexible.js",
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
                "com/fr/bi/web/js/base/wrapper/layout/adapt/flexbox.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.leftrightvertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/adapt.vertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/auto.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/float.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/absolute.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/adapt/absolute.vertical.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.float.center.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.horizontal.js",
                "com/fr/bi/web/js/base/wrapper/layout/middle/middle.vertical.js",

                "com/fr/bi/web/js/third/jquery.mousewheel.js",
                "com/fr/bi/web/js/third/jquery.mCustomScrollbar.js",
                "com/fr/bi/web/js/third/jquery.ui.core.js",
                "com/fr/bi/web/js/third/jquery.ui.widget.js",
                "com/fr/bi/web/js/third/jquery.ui.mouse.js",
                "com/fr/bi/web/js/third/jquery.ui.position.js",
                "com/fr/bi/web/js/third/jquery.ui.button.js",
                "com/fr/bi/web/js/third/jquery.ui.resizable.js",
                "com/fr/bi/web/js/third/jquery.ui.draggable.js",
                "com/fr/bi/web/js/third/jquery.ui.droppable.js",
                "com/fr/bi/web/js/third/jquery.ui.sortable.js",
                "com/fr/bi/web/js/third/jquery.ui.effect.js",
                "com/fr/bi/web/js/third/d3.js",
                "com/fr/bi/web/js/third/vancharts-all.js",
                "com/fr/bi/web/js/third/leaflet.js",


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

                "com/fr/bi/web/js/case/button/icon/icon.half.js",

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

                "com/fr/bi/web/js/case/canvas/canvas.complex.js",

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

                "com/fr/bi/web/js/case/pager/pager.number.js",
                "com/fr/bi/web/js/case/pager/pager.skip.js",
                "com/fr/bi/web/js/case/pager/pager.all.js",
                "com/fr/bi/web/js/case/pager/pager.updownprenext.js",

                "com/fr/bi/web/js/case/table/table.layertree.cell.js",
                "com/fr/bi/web/js/case/table/table.layertree.js",
                "com/fr/bi/web/js/case/table/tabler.js",

                //chart
                "com/fr/bi/web/js/case/chart/chart.combine.js",
                "com/fr/bi/web/js/case/chart/factory.charts.js",

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
                "com/fr/bi/web/js/widget/detailchart/chart.accumulatearea.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulateaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulatebar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.accumulateradar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.area.js",
                "com/fr/bi/web/js/widget/detailchart/chart.axis.js",
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
                "com/fr/bi/web/js/widget/detailchart/chart.radar.js",
                "com/fr/bi/web/js/widget/detailchart/chart.scatter.js",
                "com/fr/bi/web/js/widget/detailchart/chart.fallaxis.js",
                "com/fr/bi/web/js/widget/detailchart/chart.rangearea.js",
                "com/fr/bi/web/js/widget/detailchart/chart.map.js",
                "com/fr/bi/web/js/widget/detailchart/chart.gismap.js",

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
                "com/fr/bi/web/js/widget/selectdata/tree/node/node.level1.date.js",
                "com/fr/bi/web/js/widget/selectdata/tree/treeitem/item.level0.js",
                "com/fr/bi/web/js/widget/selectdata/tree/treeitem/item.level1.js",
                "com/fr/bi/web/js/widget/selectdata/tree/loader.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/tree/expander.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/tree/tree.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/switcher/switcher.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/searchpane/segment.search.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/searchpane/result.search.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/searcher/searcher.selectdata.js",
                "com/fr/bi/web/js/widget/selectdata/tab/tab.selectdata.js",

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

                //可以设置参数的下拉框
                "com/fr/bi/web/js/widget/paramsettingcombo/param1.date.item.js",
                "com/fr/bi/web/js/widget/paramsettingcombo/param0.date.item.js",
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
                "com/fr/bi/web/js/widget/paramsettingcombo/param2.date.item.js",

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


                //移动到分组
                "com/fr/bi/web/js/widget/move2group/button.add.move2group.js",
                "com/fr/bi/web/js/widget/move2group/toolbar.move2group.js",
                "com/fr/bi/web/js/widget/move2group/combo.move2group.js",

                //复制到分组
                "com/fr/bi/web/js/widget/copy2group/button.add.copy2group.js",
                "com/fr/bi/web/js/widget/copy2group/toolbar.copy2group.js",
                "com/fr/bi/web/js/widget/copy2group/combo.copy2group.js",

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

                //自适应表
                "com/fr/bi/web/js/widget/adaptivetable/adaptivetable.js",

                //Excel表
                "com/fr/bi/web/js/widget/exceltable/exceltable.cell.js",
                "com/fr/bi/web/js/widget/exceltable/exceltable.header.cell.js",
                "com/fr/bi/web/js/widget/exceltable/exceltable.js",

                //自定义滚动条表
                "com/fr/bi/web/js/widget/customscrolltable/customscrolltable.scrollbar.js",
                "com/fr/bi/web/js/widget/customscrolltable/customscrolltable.js",

                //分页表格
                "com/fr/bi/web/js/widget/pagetable/pagetable.cell.js",
                "com/fr/bi/web/js/widget/pagetable/pagetable.js",

                //带序号表格
                "com/fr/bi/web/js/widget/sequencetable/listnumber.sequencetable.js",
                "com/fr/bi/web/js/widget/sequencetable/treenumber.sequencetable.js",
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
                "com/fr/bi/web/js/widget/arrangement/arrangement.js",

                //自适应布局
                "com/fr/bi/web/js/widget/adaptivearrangement/adaptivearrangement.js",

                "com/fr/bi/web/js/widget/uploadfile/progress.uploadfile.js",

                /**
                 * 以下是部件
                 */
                //loading面板
                "com/fr/bi/web/js/components/pane.loading.js",

                //选值
                "com/fr/bi/web/js/components/valuechooser/combo.valuechooser.js",
                "com/fr/bi/web/js/components/bigvaluechooser/combo.bigvaluechooser.js",


                //树选值
                "com/fr/bi/web/js/components/treevaluechooser/combo.treevaluechooser.js",

                //带样式表格
                "com/fr/bi/web/js/components/styletable/styletable.js",

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

                //工程配置
                "com/fr/bi/web/js/config.js"
        };
    }

    public static String[] getFoundationCss() {
        String[] base = getBaseCss();
        String[] widget = getCommonCss();
        return (String[]) ArrayUtils.addAll(base, widget);
    }

    /**
     * 可以用于管理平台中的布局和控件JS
     *
     * @return JS文件数组
     */
    public static String[] getFoundationJs() {
        String[] base = getBaseJs();
        String[] widget = getCommonJs();
        return (String[]) ArrayUtils.addAll(base, widget);
    }
}
