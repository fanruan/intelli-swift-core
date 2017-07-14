package com.fr.bi.cal.analyze.executor.utils;

import apple.laf.JRSUIConstants;
import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.field.BusinessFieldHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.analyze.executor.BIEngineExecutor;
import com.fr.bi.cal.analyze.executor.detail.DetailExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.executor.table.AbstractTableWidgetExecutor;
import com.fr.bi.cal.analyze.report.report.widget.AbstractBIWidget;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.engine.cal.DimensionIterator;
import com.fr.bi.stable.engine.cal.DimensionIteratorCreator;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.IDGroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.util.BIConfUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.core.A.OB;

import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by andrew_asa on 2017/7/12.
 * 跳转相关工具类
 */
public class GolbalFilterUtils {

    /**
     * 设置了源字段和目标字段的跳转
     *
     * @param widget  组件
     * @param userId  用户id
     * @param session session
     * @param target  目标表
     * @param source  源表
     * @return
     */
    public static GroupValueIndex getSettingSourceAndTargetJumpFilter(BIWidget widget, long userId, BISession session, BusinessTable target, BusinessTable source) {

        BIWidget globalWidget = ((AbstractBIWidget) widget).getGlobalFilterWidget();
        if (globalWidget == null) {
            return null;
        }
        GroupValueIndex g = getNotSettingSourceAndTargetJumpFilter(session, target, widget, true);
        GroupValueIndex ret = null;
        if (g != null) {
            List<Map> sf = ((AbstractBIWidget) globalWidget).getGlobalSourceAndTargetFieldList();
            for (Map<String, String> item : sf) {
                String targetFieldId = item.get("targetFieldId");
                String sourceFieldId = item.get("sourceFieldId");
                BusinessField targetField = BusinessFieldHelper.getAnalysisBusinessFieldSource(new BIFieldID(targetFieldId));
                BusinessField sourceField = BusinessFieldHelper.getAnalysisBusinessFieldSource(new BIFieldID(sourceFieldId));
                GroupValueIndex r = GolbalFilterUtils.relationFieldFilter(userId, session, target, source, sourceField, targetField, g);
                ret = GVIUtils.AND(ret, r);
            }
        }
        return ret;
    }

    /**
     * 没有设置源字段和目标字段的跳转
     *
     * @param session
     * @param target
     * @param widget
     * @param isSetSourceAndTargetField
     * @return
     */
    public static GroupValueIndex getNotSettingSourceAndTargetJumpFilter(BISession session, BusinessTable target, BIWidget widget, boolean isSetSourceAndTargetField) {

        try {
            TableWidget globalFilterWidget = (TableWidget) ((AbstractBIWidget) widget).getGlobalFilterWidget();
            BIEngineExecutor linkExecutor = globalFilterWidget.getExecutor(session);
            Map<String, JSONArray> click = ((AbstractBIWidget) widget).getGlobalFilterClick();
            Map<String, JSONArray> r = GolbalFilterUtils.combineClick(click, globalFilterWidget);
            if (linkExecutor instanceof AbstractTableWidgetExecutor) {
                if (isSetSourceAndTargetField) {
                    return ((AbstractTableWidgetExecutor) linkExecutor).getClickGvi(r);
                } else {
                    return ((AbstractTableWidgetExecutor) linkExecutor).getClickGvi(r, target);
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(GolbalFilterUtils.class).info("error in get jump link filter gvi", e);
        }
        return null;
    }

    public static Map<String, JSONArray> combineClick(Map<String, JSONArray> click, TableWidget widget) {

        Map<String, JSONArray> r = new HashMap<String, JSONArray>();
        if (click != null) {
            Iterator<String> iter = click.keySet().iterator();
            while (iter.hasNext()) {
                String k = iter.next();
                try {
                    widget.getBITargetByID(k);
                    r.put(k, click.get(k));
                    break;
                } catch (Exception e) {

                }
                try {
                    widget.getDimensionNameByID(k);
                    String t = widget.getTargets()[0].getId();
                    r.put(t, click.get(k));
                    break;
                } catch (Exception e) {

                }
            }
        }
        return r;
    }

    /**
     * 是否是相关连的字段
     *
     * @param target
     * @param targetField
     * @return
     */
    public static FieldTableRelation isRelationField(long userId, BusinessTable target, BusinessField targetField) {

        //BusinessField targetField = BusinessFieldHelper.getAnalysisBusinessFieldSource(new BIFieldID(targetFieldId));
        BusinessTable targetFieldTable = targetField.getTableBelongTo();
        if (target.equals(targetFieldTable)) {
            return FieldTableRelation.CONTAIN;
        }
        try {
            // target是子表
            Set<BITableRelationPath> paths = BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, target, targetFieldTable);
            if (paths != null && paths.size() > 0) {
                return FieldTableRelation.PRIMARYTABLEFIELD;
            }
        } catch (Exception e) {

        }
        try {
            // target是主表
            Set<BITableRelationPath> paths = BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, targetFieldTable, target);
            if (paths != null && paths.size() > 0) {
                return FieldTableRelation.CHILDTABLEFIELD;
            }
        } catch (Exception e) {

        }
        // 两表之间没有关联
        return FieldTableRelation.NONE;
    }

    /**
     * 字段和表的关系
     */
    public enum FieldTableRelation {
        // 字段和表没关系
        NONE,
        // 字段为表的主表的字段
        PRIMARYTABLEFIELD,
        // 字段为表的字段
        CONTAIN,
        // 字段为表的字表的字段
        CHILDTABLEFIELD,
    }

    /**
     * 关联字段的过滤
     *
     * @param userId
     * @param target
     * @param source
     * @param sourceField
     * @param targetField
     * @param sourceFilterGvi
     * @return
     */
    public static GroupValueIndex relationFieldFilter(long userId, BISession session, BusinessTable target, BusinessTable source, BusinessField sourceField, BusinessField targetField, GroupValueIndex sourceFilterGvi) {
        // 参数校验
        if (target == null || source == null || sourceField == null || targetField == null || sourceFilterGvi == null) {
            return null;
        }
        GroupValueIndex rgvi = null;
        try {
            FieldTableRelation sorceFieldRelation = isRelationField(userId, source, sourceField);
            FieldTableRelation targetFieldRelation = isRelationField(userId, target, targetField);
            // 如果源字段,源表之间或目标字段,目标表之间没有关系
            if (sorceFieldRelation.equals(FieldTableRelation.NONE) || targetFieldRelation.equals(FieldTableRelation.NONE)) {
                return null;
            }
            ICubeDataLoader loader = session.getLoader();
            ICubeTableService sourceTableService = loader.getTableIndex(source.getTableSource());
            ICubeTableService targetTableService = loader.getTableIndex(target.getTableSource());
            Set<BITableRelationPath> sourcePaths = BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, source, sourceField.getTableBelongTo());
            Set<BITableRelationPath> targetPaths = BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, target, targetField.getTableBelongTo());
            List<BITableSourceRelation> sourceRelation = getRelation(sourcePaths);
            List<BITableSourceRelation> targetRelation = getRelation(targetPaths);
            BIKey sourceFieldKey = new IndexKey(sourceField.getFieldName());
            BIKey targetFieldKey = new IndexKey(targetField.getFieldName());
            ICubeValueEntryGetter sourceFieldEntryGetter = sourceTableService.getValueEntryGetter(sourceFieldKey, sourceRelation);
            ICubeValueEntryGetter targetFieldEntryGetter = targetTableService.getValueEntryGetter(targetFieldKey, targetRelation);
            DimensionIterator sourceItemIter = DimensionIteratorCreator.createValueMapIterator(sourceFieldEntryGetter, sourceFilterGvi, true);
            while (sourceItemIter.hasNext()) {
                Map.Entry<Object, GroupValueIndex> item = sourceItemIter.next();
                if (item != null) {
                    int gr = targetFieldEntryGetter.getPositionOfGroupByValue(item.getKey());
                    if (gr != -1) {
                        rgvi = GVIUtils.OR(rgvi, targetFieldEntryGetter.getIndexByGroupRow(gr));
                    }
                }
            }
            // 如果过滤没有说明全部被过滤完了
            if (rgvi == null) {
                rgvi = GVIFactory.createAllEmptyIndexGVI();
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(GolbalFilterUtils.class).info(e.getMessage(), e);
        }
        return rgvi;
    }

    /**
     * 获取默认路径
     *
     * @param tableRelationPaths
     * @return
     */
    public static List<BITableSourceRelation> getRelation(Set<BITableRelationPath> tableRelationPaths) {

        List<BITableSourceRelation> rl = new ArrayList<BITableSourceRelation>();
        if (tableRelationPaths != null) {
            for (BITableRelationPath relation : tableRelationPaths) {
                List<BITableRelation> r = relation.getAllRelations();
                return BIConfUtils.convert2TableSourceRelation(r);
            }
        }
        return rl;
    }

    /**
     * 获取点击节点映射字段的值
     *
     * @param widget   点击的区域
     * @param userId   用户id
     * @param session
     * @param clicked  点击的值
     * @param fieldIds 需要获取的值
     * @return
     */
    public static JSONObject getClickFieldMappingValue(BIWidget widget, long userId, BISession session, JSONObject clicked, JSONArray fieldIds) {

        JSONObject ret = JSONObject.create();
        ICubeDataLoader loader = session.getLoader();
        if (fieldIds == null || fieldIds.length() == 0 || clicked == null) {
            return ret;
        }
        List<String> fIds = new ArrayList<String>();
        for (int i = 0; i < fieldIds.length(); i++) {
            fIds.add(fieldIds.optString(i, ""));
        }
        try {
            if (widget.getType().equals(WidgetType.DETAIL)) {
                Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.GROUP100);
                int page = clicked.optInt("pageCount", 1);
                final long[] rIndex = {clicked.optLong("rowIndex", 0) + (page - 1) * BIExcutorConstant.PAGINGTYPE.GROUP100};
                final int[] r = {-1};
                paging.setCurrentPage(page);
                DetailExecutor exe = new DetailExecutor((BIDetailWidget) widget, paging, session);
                GroupValueIndex viewGvi = exe.createDetailViewGvi();
                if (viewGvi != null) {
                    viewGvi.BrokenableTraversal(new BrokenTraversalAction() {

                        @Override
                        public boolean actionPerformed(int row) {

                            if (rIndex[0] == 0) {
                                r[0] = row;
                                return true;
                            }
                            rIndex[0]--;
                            return false;
                        }
                    });
                }
                if (r[0] >= 0) {
                    GroupValueIndex tarFiledGvi = GVIFactory.createGroupValueIndexBySimpleIndex(r[0]);
                    return getOneLineValue(tarFiledGvi, ((BIDetailWidget) widget).getBaseTable(), fIds, userId, session, ret);
                }
            } else if (widget.getType().equals(WidgetType.TABLE)) {
                // 分组表
                TableWidget targetWidget = (TableWidget) widget;
                BIEngineExecutor executor = targetWidget.getExecutor(session);
                Iterator<String> iter = clicked.keys();
                Map<String, JSONArray> click = new HashMap<String, JSONArray>();
                List<String> fieldKeys = new ArrayList<String>();
                if (iter.hasNext()) {
                    String k = iter.next();
                    JSONArray a = clicked.getJSONArray(k);
                    JSONArray n = JSONArray.create();
                    for (int i = 0; i < a.length(); i++) {
                        JSONObject o = a.getJSONObject(i);
                        JSONArray v = o.optJSONArray("value");
                        String did = o.optString("dId", "");
                        BIDimension dimension = targetWidget.getDimensionBydId(did);
                        String fk = dimension.createColumnKey().getFieldID().getIdentity();
                        if (dimension != null && fIds.contains(fk)) {
                            ret.put(fk, v.optString(0, ""));
                            fIds.remove(fk);
                        }
                    }
                    click.put(k, clicked.getJSONArray(k));
                }
                if (fIds.size() > 0) {

                    click = combineClick(click, targetWidget);
                    GroupValueIndex clickGvi = ((AbstractTableWidgetExecutor) executor).getClickGvi(click);
                    ret = getOneLineValue(clickGvi, targetWidget.getBaseTable(), fIds, userId, session, ret);
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(GolbalFilterUtils.class).info(e.getMessage(), e);
        }
        return ret;
    }

    public static JSONObject getOneLineValue(GroupValueIndex gvi, BusinessTable baseTable, List<String> fieldIds, long userId, BISession session, JSONObject ret) {

        try {
            final int[] r = {-1};
            ICubeDataLoader loader = session.getLoader();
            if (gvi != null && gvi.getRowsCountWithData() == 1) {
                gvi.BrokenableTraversal(new BrokenTraversalAction() {

                    @Override
                    public boolean actionPerformed(int row) {

                        r[0] = row;
                        return true;
                    }
                });
                if (r[0] >= 0) {
                    GroupValueIndex tarFiledGvi = GVIFactory.createGroupValueIndexBySimpleIndex(r[0]);
                    ICubeTableService baseTableService = loader.getTableIndex(baseTable.getTableSource());
                    for (int i = 0; i < fieldIds.size(); i++) {
                        String fieldId = fieldIds.get(i);
                        BusinessField targetField = BusinessFieldHelper.getAnalysisBusinessFieldSource(new BIFieldID(fieldId));
                        FieldTableRelation targetFieldRelation = isRelationField(userId, baseTable, targetField);
                        BusinessTable targetTable = targetField.getTableBelongTo();
                        if (!targetFieldRelation.equals(FieldTableRelation.NONE)) {
                            // 如果是主表的字段
                            Set<BITableRelationPath> sourcePaths;
                            BIKey targetFieldKey = new IndexKey(targetField.getFieldName());
                            Object g = null;
                            if (targetFieldRelation.equals(FieldTableRelation.PRIMARYTABLEFIELD)) {
                                sourcePaths = BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, baseTable, targetTable);
                                List<BITableSourceRelation> targetRelation = getRelation(sourcePaths);
                                ICubeTableIndexReader indexReader = loader.getTableIndex(targetRelation.get(0).getPrimaryTable()).ensureBasicIndex(targetRelation);
                                int row = indexReader.getReverse(r[0]);
                                ICubeTableService targetBaseTable = loader.getTableIndex(targetTable.getTableSource());
                                ICubeColumnDetailGetter targetColumnDetail = targetBaseTable.getColumnDetailReader(targetFieldKey);
                                g = targetColumnDetail.getValue(row);
                            } else {
                                sourcePaths = BICubeConfigureCenter.getTableRelationManager().getAllPath(userId, targetTable, baseTable);
                                List<BITableSourceRelation> targetRelation = getRelation(sourcePaths);
                                ICubeValueEntryGetter targetFieldEntryGetter = baseTableService.getValueEntryGetter(targetFieldKey, targetRelation);
                                DimensionIterator sourceItemIter = DimensionIteratorCreator.createValueMapIterator(targetFieldEntryGetter, tarFiledGvi, true);
                                if (sourceItemIter.hasNext()) {
                                    Map.Entry<Object, GroupValueIndex> item = sourceItemIter.next();
                                    if (sourceItemIter.hasNext()) {
                                        continue;
                                    }
                                    g = item.getKey();
                                }
                            }
                            g = BICollectionUtils.cubeValueToWebDisplay(g);
                            ret.put(fieldId, g);
                        }
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(GolbalFilterUtils.class).info(e.getMessage(), e);
        }
        return ret;
    }
}
