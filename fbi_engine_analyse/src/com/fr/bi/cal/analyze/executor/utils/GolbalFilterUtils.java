package com.fr.bi.cal.analyze.executor.utils;

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
import com.fr.bi.cal.analyze.executor.table.AbstractTableWidgetExecutor;
import com.fr.bi.cal.analyze.report.report.widget.AbstractBIWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.engine.cal.DimensionIterator;
import com.fr.bi.stable.engine.cal.DimensionIteratorCreator;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.util.BIConfUtils;
import com.fr.json.JSONArray;

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
            FieldTableRelation targetFieldRelation = isRelationField(userId, source, sourceField);
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
}
