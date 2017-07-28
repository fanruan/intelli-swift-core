package com.fr.bi.cal.analyze.executor.detail;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.base.Style;
import com.fr.bi.cal.analyze.executor.BIAbstractExecutor;
import com.fr.bi.cal.analyze.executor.iterator.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.utils.ExecutorUtils;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.style.ChartSetting;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.BIAbstractTargetAndDimension;
import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;
import com.fr.bi.field.target.detailtarget.BIAbstractDetailTarget;
import com.fr.bi.field.target.detailtarget.field.BINumberDetailTarget;
import com.fr.bi.field.target.detailtarget.formula.BINumberFormulaDetailTarget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.algorithem.BIComparatorUtils;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.GeneralUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.*;

/**
 * Created by GUY on 2015/4/16.
 */
public abstract class AbstractDetailExecutor extends BIAbstractExecutor<JSONObject> {


    protected transient BusinessTable target;
    protected transient BIDetailTarget[] viewDimension;
    protected transient String[] sortTargets;
    private transient GroupValueIndex currentGvi;
    protected transient long userId;
    protected BIDetailWidget widget;
    protected BITableStyle tableStyle;

    public AbstractDetailExecutor(BIDetailWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
        this.target = widget.getTargetDimension();
        this.widget = widget;
        this.session = session;

        this.viewDimension = widget.getViewDimensions();
        this.sortTargets = widget.getSortTargets();
        this.userId = session.getUserId();
        this.tableStyle = new BITableStyle(widget.getWidgetStyle().getThemeColor());
    }


    protected GroupValueIndex createDetailViewGvi() {
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
                // 基础表相同的时候才有联动的意义
                if (widgetTargetTable.equals(linkTargetTable)) {
                    // 其联动组件的父联动gvi
                    GroupValueIndex pLinkGvi = linkWidget.createLinkedFilterGVI(widgetTargetTable, session);
                    // 其联动组件的点击过滤gvi
                    GroupValueIndex linkGvi = linkWidget.getLinkFilter(linkWidget, widgetTargetTable, clicked, session);
                    gvi = GVIUtils.AND(gvi, GVIUtils.AND(pLinkGvi, linkGvi));
                }
            }
        }
        return gvi;
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


    //创建一个数字格
    private CBCell createNumberCellElement(int rowIndex, int row) {
        Style style = rowIndex % 2 == 1 ? tableStyle.getOddRowStyle(Style.getInstance()) : tableStyle.getEvenRowStyle(Style.getInstance());
        CBCell cell = ExecutorUtils.createCBCell(rowIndex, row, 1, 0, 1, style);
        List tcellList = new ArrayList();
        tcellList.add(cell);
        CBBoxElement cbox = new CBBoxElement(tcellList);
        cell.setBoxElement(cbox);
        return cell;
    }

    protected void fillOneLine(StreamPagedIterator iter, int row, Object[] ob, Set<Integer> usedDimensionIndexes) {
//        if (widget.isOrder() > 0) {
//            iter.addCell(createNumberCellElement(rowNumber, row));
//        }

//        int columnIndex = widget.isOrder();
        int columnIndex = 0;
        for (int i = 0; i < viewDimension.length; i++) {
            if (usedDimensionIndexes.contains(i)) {
                BIDetailTarget t = viewDimension[i];
                Object v = ob[i];
                v = viewDimension[i].createShowValue(v);
                if (t instanceof BIAbstractDetailTarget && v != null) {
                    if (((BIAbstractDetailTarget) t).getGroup().getType() == BIReportConstant.GROUP.YMD && GeneralUtils.string2Number(v.toString()) != null) {
                        v = DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(v.toString()).longValue()));
                    }
                }
                ChartSetting chartSetting = null;
                Style cellStyle = Style.getInstance();
                if (t instanceof BINumberDetailTarget) {
                    chartSetting = ((BINumberDetailTarget) viewDimension[i]).getChartSetting();
                }
                if (t instanceof BINumberFormulaDetailTarget) {
                    chartSetting = ((BINumberFormulaDetailTarget) viewDimension[i]).getChartSetting();
                }
                if (chartSetting != null) {
                    JSONObject settings = chartSetting.getSettings();
                    int numLevel = settings.optInt("numLevel", BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL);
                    boolean separator = settings.optBoolean("numSeparators", true);
                    int formatDecimal = settings.optInt("formatDecimal", BIReportConstant.TARGET_STYLE.FORMAT.NORMAL);
                    v = ExecutorUtils.formatExtremeSumValue(v, numLevel);
                    cellStyle = cellStyle.deriveFormat(ExecutorUtils.formatDecimalAndSeparator(v, numLevel, formatDecimal, separator));
                }
                cellStyle = row % 2 ==  1 ? tableStyle.getOddRowStyle(cellStyle) : tableStyle.getEvenRowStyle(cellStyle);
                CBCell cell = ExecutorUtils.createCBCell(v == null ? NONEVALUE : v, row, 1, columnIndex++, 1, cellStyle);
                List cellList = new ArrayList();
                cellList.add(cell);
                //TODO CBBoxElement需要整合减少内存
                CBBoxElement cbox = new CBBoxElement(cellList);
                if (t.useHyperLink()) {
                    cell.setNameHyperlinkGroup(t.createHyperLinkNameJavaScriptGroup(v));
                }
                cbox.setType(CellConstant.CBCELL.ROWFIELD);
                cell.setBoxElement(cbox);
                iter.addCell(cell);
            }
        }
    }

    protected List<CBCell> createHeader(int cellType, Set<Integer> usedDimensionIndexes) {
        List<CBCell> cells = new LinkedList<CBCell>();
        BIDetailTarget[] viewDimension = widget.getViewDimensions();
        int columnIdx = 0;
//        if (widget.isOrder() > 0) {
//            CBCell cell = ExecutorUtils.createCBCell(Inter.getLocText("BI-Number_Index"), 0, 1, columnIdx++, 1);
//            cells.add(cell);
//        }
        for (int i = 0; i < viewDimension.length; i++) {
            if (usedDimensionIndexes.contains(i)) {
                BIDetailTarget dimension = viewDimension[i];
                String dimensionName = ((BIAbstractTargetAndDimension) viewDimension[i]).getText();
                ChartSetting chartSetting = null;
                if (dimension instanceof BINumberDetailTarget) {
                    chartSetting = ((BINumberDetailTarget) viewDimension[i]).getChartSetting();
                }
                if (dimension instanceof BINumberFormulaDetailTarget) {
                    chartSetting = ((BINumberFormulaDetailTarget) viewDimension[i]).getChartSetting();
                }
                if (chartSetting != null) {
                    JSONObject settings = chartSetting.getSettings();
                    int numLevel = settings.optInt("numLevel", 0);
                    String unit = settings.optString("unit", StringUtils.EMPTY);
                    String levelAndUnit = ExecutorUtils.formatLevelAndUnit(numLevel, unit);
                    if (!ComparatorUtils.equals(levelAndUnit, StringUtils.EMPTY)) {
                        dimensionName = dimensionName + "(" + levelAndUnit + ")";
                    }
                }
                CBCell cell = ExecutorUtils.createCBCell(dimensionName, 0, 1, columnIdx++, 1, tableStyle.getHeaderStyle(Style.getInstance()));
                List cellList = new ArrayList();
                cellList.add(cell);
                CBBoxElement cbox = new CBBoxElement(cellList);
                cbox.setType(cellType);
                cell.setBoxElement(cbox);
                cells.add(cell);
            }
        }
        return cells;
    }
}