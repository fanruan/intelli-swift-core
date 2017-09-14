package com.fr.bi.cal.analyze.executor.detail;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.executor.BIAbstractExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.utils.GlobalFilterUtils;
import com.fr.bi.cal.analyze.report.report.widget.AbstractBIWidget;
import com.fr.bi.cal.analyze.report.report.widget.DetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.algorithem.BIComparatorUtils;
import com.fr.bi.util.BIConfUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by GUY on 2015/4/16.
 */
public abstract class AbstractDetailExecutor extends BIAbstractExecutor<JSONObject> {


    protected transient BusinessTable target;

    protected transient BIDetailTarget[] viewDimension;

    protected transient String[] sortTargets;

    private transient GroupValueIndex currentGvi;

    protected transient long userId;

    protected DetailWidget widget;

    protected BITableStyle tableStyle;

    public AbstractDetailExecutor(DetailWidget widget, Paging paging, BISession session) {

        super(widget, paging, session);
        this.target = widget.getTargetDimension();
        this.widget = widget;
        this.session = session;

        this.viewDimension = widget.getViewDimensions();
        this.sortTargets = widget.getSortTargets();
        this.userId = session.getUserId();
    }


    public GroupValueIndex createDetailViewGvi() {

        if (currentGvi == null) {
            ICubeTableService ti = getLoader().getTableIndex(target.getTableSource());
            GroupValueIndex gvi = ti.getAllShowIndex();
            for (int i = 0; i < this.viewDimension.length; i++) {
                BIDetailTarget target = this.viewDimension[i];
                TargetFilter filterValue = target.getFilter();
                if (filterValue != null) {
                    BusinessField dataColumn = target.createColumnKey();
                    List<BITableRelation> simpleRelations = target.getRelationList(this.target, this.userId);
                    gvi = GVIUtils.AND(gvi, filterValue.createFilterIndex(new NoneDimensionCalculator(dataColumn, BIConfUtils.convert2TableSourceRelation(simpleRelations)), this.target, getLoader(), this.userId));
                }
            }
            Map<String, TargetFilter> filterMap = widget.getTargetFilterMap();
            for (Map.Entry<String, TargetFilter> entry : filterMap.entrySet()) {
                String targetId = entry.getKey();
                BIDetailTarget target = getTargetById(targetId);
                if (target != null) {
                    BusinessField dataColumn = target.createColumnKey();
                    List<BITableRelation> simpleRelations = target.getRelationList(this.target, this.userId);
                    gvi = GVIUtils.AND(gvi, entry.getValue().createFilterIndex(new NoneDimensionCalculator(dataColumn, BIConfUtils.convert2TableSourceRelation(simpleRelations)), this.target, getLoader(), this.userId));
                }
            }
            gvi = GVIUtils.AND(gvi,
                               widget.createFilterGVI(new DimensionCalculator[]{new NoneDimensionCalculator(new BIBusinessField(this.target, StringUtils.EMPTY),
                                                                                                            new ArrayList<BITableSourceRelation>())}, this.target, getLoader(), this.userId));
            currentGvi = gvi;
        }
        try {
            currentGvi = getLinkFilter(currentGvi);
            // 跳转的
            currentGvi = getJumpLinkFilter(currentGvi);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        return currentGvi;
    }

    private GroupValueIndex getLinkFilter(GroupValueIndex gvi) throws Exception {

        if (widget.getLinkWidget() != null && widget.getLinkWidget() instanceof TableWidget) {
            // 判断两个表格的基础表是否相同
            BusinessTable widgetTargetTable = widget.getTargetDimension();
            TableWidget linkWidget = widget.getLinkWidget();
            Map<String, JSONArray> clicked = widget.getClicked();

            BISummaryTarget summaryTarget = null;
            String[] ids = clicked.keySet().toArray(new String[]{});
            for (String linkTarget : ids) {
                try {
                    summaryTarget = linkWidget.getBITargetByID(linkTarget);
                    break;
                } catch (Exception e) {
                    BILoggerFactory.getLogger(TableWidget.class).warn("Target id " + linkTarget + " is absent in linked widget " + linkWidget.getWidgetName());
                }
            }

            if (summaryTarget != null) {
                BusinessTable linkTargetTable = summaryTarget.createTableKey();
                // 基础表相同的时候才有联动的意义 | 是否是主表联动到子表
                if (widgetTargetTable.equals(linkTargetTable) || GlobalFilterUtils.isPrimaryTable(linkTargetTable, widgetTargetTable)) {
                    // 其联动组件的父联动gvi
                    GroupValueIndex pLinkGvi = linkWidget.createLinkedFilterGVI(widgetTargetTable, session);
                    // 其联动组件的点击过滤gvi
                    GroupValueIndex linkGvi = linkWidget.getLinkFilter(linkWidget, widgetTargetTable, clicked, session);
                    linkGvi = GVIUtils.AND(pLinkGvi, linkGvi);
                    gvi = GVIUtils.AND(gvi, linkGvi);
                }
            }
        }
        return gvi;
    }

    protected GroupValueIndex getJumpLinkFilter(GroupValueIndex g) {

        DetailWidget bw = widget;
        // 如果是跳转打开的才需要进行设置
        if (bw.getGlobalFilterWidget() != null) {
            // 如果已经设置了源字段和目标字段
            if (((AbstractBIWidget) bw.getGlobalFilterWidget()).getGlobalSourceAndTargetFieldList().size() > 0) {
                g = GVIUtils.AND(g, GlobalFilterUtils.getSettingSourceAndTargetJumpFilter(widget, userId, session, target, ((AbstractBIWidget) bw.getGlobalFilterWidget()).getBaseTable()));
            } else {
                g = GVIUtils.AND(g, GlobalFilterUtils.getNotSettingSourceAndTargetJumpFilter(session, target, widget, false));
            }
        }
        return g;
    }

    private BIDetailTarget getTargetById(String id) {

        BIDetailTarget target = null;
        for (int i = 0; i < viewDimension.length; i++) {
            if (BIComparatorUtils.isExactlyEquals(viewDimension[i].getValue(), id)) {
                target = viewDimension[i];
            }
        }
        return target;
    }
}