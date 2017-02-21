package com.fr.bi.util;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


public class BIConfUtils {
    public static boolean isSameRelation(BITableSourceRelation one, BITableSourceRelation other, long userId) {
        if (one == null || other == null) {
            return false;
        }
        if (one.hashCode() == other.hashCode()) {
            return true;
        }
        return (ComparatorUtils.equals(one.getForeignKey().getFieldName(), other.getForeignKey().getFieldName()) && ComparatorUtils.equals(one.getPrimaryKey().getFieldName(), other.getPrimaryKey().getFieldName()));

    }

    public static List<BITableSourceRelation> convert2TableSourceRelation(List<BITableRelation> relations) {
        List<BITableSourceRelation> list = new ArrayList<BITableSourceRelation>();
        for (BITableRelation relation : relations) {
            list.add(convert2TableSourceRelation(relation));
        }
        return list;
    }

    public static BITableSourceRelation convert2TableSourceRelation(BITableRelation relation) {
        BusinessField primaryField = relation.getPrimaryField();
        BusinessField foreignField = relation.getForeignField();
        CubeTableSource primaryTableSource = null;
        CubeTableSource foreignTableSource = null;
        try {
            primaryTableSource = BICubeConfigureCenter.getDataSourceManager().getTableSource(primaryField.getTableBelongTo());
            foreignTableSource = BICubeConfigureCenter.getDataSourceManager().getTableSource(foreignField.getTableBelongTo());
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
        ICubeFieldSource primaryFieldSource = new BICubeFieldSource(primaryTableSource, primaryField.getFieldName(), primaryField.getClassType(), primaryField.getFieldSize());
        ICubeFieldSource foreignFieldSource = new BICubeFieldSource(foreignTableSource, foreignField.getFieldName(), foreignField.getClassType(), foreignField.getFieldSize());
        return new BITableSourceRelation(
                primaryFieldSource,
                foreignFieldSource,
                primaryTableSource,
                foreignTableSource
        );
    }

    public static Map<String, JSONObject> getAvailableData(HttpServletRequest req) {
        long userId = ServiceUtils.getCurrentUserID(req);
        long manageId = UserControl.getInstance().getSuperManagerID();
        JSONObject groups = new JSONObject();
        JSONObject packages = new JSONObject();
        JSONObject relations = new JSONObject();
        JSONObject connections = new JSONObject();
        JSONObject tables = new JSONObject();
        JSONObject source = new JSONObject();
        JSONObject fields = new JSONObject();
        JSONObject noAuthFields = new JSONObject();
        JSONObject translations = new JSONObject();
        JSONObject excelViews = new JSONObject();
        try {
            groups = getAuthGroups(userId, req.getLocale());
            packages = getAuthPackages(userId, req.getLocale());
            translations = BIModuleUtils.createAliasJSON(userId);
            relations = BICubeConfigureCenter.getTableRelationManager().createRelationsPathJSON(manageId);
            excelViews = BIConfigureManagerCenter.getExcelViewManager().createJSON(manageId);
            initTableAndFields(userId, tables, fields, noAuthFields);
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
        map.put("noAuthFields", noAuthFields);
        map.put("excel_views", excelViews);
        return map;
    }

    /**
     * 有权限的分组
     *
     * @param userId
     * @param locale
     * @return
     * @throws Exception
     */
    private static JSONObject getAuthGroups(long userId, Locale locale) throws Exception {
        JSONObject allGroups = getFormatGroups(userId, locale);
        if (ComparatorUtils.equals(UserControl.getInstance().getSuperManagerID(), userId)) {
            return allGroups;
        }
        Iterator<String> groupIds = allGroups.keys();
        JSONObject packages = getAuthPackages(userId, locale);
        JSONObject groups = new JSONObject();
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
        return groups;
    }

    /**
     * 有权限的业务包
     *
     * @param userId
     * @param locale
     * @return
     * @throws Exception
     */
    private static JSONObject getAuthPackages(long userId, Locale locale) throws Exception {
        JSONObject allPacks = BIModuleUtils.createAnalysisPackJSON(userId, locale);
        if (ComparatorUtils.equals(UserControl.getInstance().getSuperManagerID(), userId)) {
            return allPacks;
        }
        List<BIPackageID> authPacks = BIModuleUtils.getAvailablePackID(userId);
        JSONObject packages = new JSONObject();
        for (BIPackageID pId : authPacks) {
            if (allPacks.has(pId.getIdentityValue())) {
                packages.put(pId.getIdentityValue(), allPacks.getJSONObject(pId.getIdentityValue()));
            }
        }
        return packages;
    }

    /**
     * format group
     *
     * @param userId
     * @param locale
     * @return
     * @throws Exception
     */
    private static JSONObject getFormatGroups(long userId, Locale locale) throws Exception {
        JSONObject allGroups = BICubeConfigureCenter.getPackageManager().createGroupJSON(userId);
        JSONObject allPacks = BIModuleUtils.createAnalysisPackJSON(userId, locale);
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
        return allGroups;
    }

    /**
     * 初始化表、字段
     *
     * @param userId
     * @param tables
     * @param fields
     * @param noAuthFields
     * @throws Exception
     */
    private static void initTableAndFields(long userId, JSONObject tables, JSONObject fields, JSONObject noAuthFields) throws Exception {
        Set<IBusinessPackageGetterService> packs = BIModuleUtils.getAllPacks(userId);
        List<BIPackageID> authPacks = BIModuleUtils.getAvailablePackID(userId);
        for (IBusinessPackageGetterService p : packs) {
            for (BIBusinessTable t : (Set<BIBusinessTable>) p.getBusinessTables()) {
                JSONObject jo = t.createJSONWithFieldsInfo(userId);
                CubeTableSource tableSource = t.getTableSource();
                JSONObject sourceJO = tableSource.createJSON();
                String connectionName = sourceJO.optString("connection_name", StringUtils.EMPTY);
                JSONObject fieldsInfo = jo.getJSONObject("fieldsInfo");
                if (ComparatorUtils.equals(UserControl.getInstance().getSuperManagerID(), userId) ||
                        authPacks.contains(p.getID())) {
                    JSONObject tableFields = jo.getJSONObject("tableFields");
                    tableFields.put("connection_name", connectionName);
                    tables.put(t.getID().getIdentityValue(), tableFields);
                    fields.join(fieldsInfo);
                } else {
                    noAuthFields.join(fieldsInfo);
                }
            }
        }
    }
}