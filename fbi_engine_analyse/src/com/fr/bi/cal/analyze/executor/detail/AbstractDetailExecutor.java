package com.fr.bi.cal.analyze.executor.detail;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.analyze.executor.BIAbstractExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.style.TargetStyle;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.BIAbstractTargetAndDimension;
import com.fr.bi.field.BIStyleTarget;
import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.algorithem.BIComparatorUtils;
import com.fr.bi.util.BIConfUtils;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by GUY on 2015/4/16.
 */
public abstract class AbstractDetailExecutor extends BIAbstractExecutor<JSONObject> {


    protected transient CubeTableSource target;
    protected transient BIDetailTarget[] viewDimension;
    protected transient String[] sortTargets;
    protected transient long userId;
    protected BIDetailWidget widget;

    public AbstractDetailExecutor(BIDetailWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
        this.target = BusinessTableHelper.getTableDataSource(widget.getTargetDimension());
        this.widget = widget;
        this.session = session;

        this.viewDimension = widget.getViewDimensions();
        this.sortTargets = widget.getSortTargets();
        this.userId = session.getUserId();
    }

    protected GroupValueIndex createDetailViewGvi() {
        ICubeTableService ti = getLoader().getTableIndex(target);
        GroupValueIndex gvi = ti.getAllShowIndex();
        for (int i = 0; i < this.viewDimension.length; i++) {
            BIDetailTarget target = this.viewDimension[i];
            TargetFilter filterValue = target.getFilter();
            if (filterValue != null) {
                CubeFieldSource dataColumn = target.createColumnKey();
                List<BITableRelation> simpleRelations = target.getRelationList(this.target, this.userId);
                gvi = GVIUtils.AND(gvi, filterValue.createFilterIndex(new NoneDimensionCalculator(dataColumn, BIConfUtils.convertToMD5RelationFromSimpleRelation(simpleRelations, new BIUser(this.userId))), this.target, getLoader(), this.userId));
            }
        }
        Map<String, TargetFilter> filterMap = widget.getTargetFilterMap();
        for (Map.Entry<String, TargetFilter> entry : filterMap.entrySet()) {
            String targetId = entry.getKey();
            BIDetailTarget target = getTargetById(targetId);
            if (target != null) {
                CubeFieldSource dataColumn = target.createColumnKey();
                List<BITableRelation> simpleRelations = target.getRelationList(this.target, this.userId);
                gvi = GVIUtils.AND(gvi, entry.getValue().createFilterIndex(new NoneDimensionCalculator(dataColumn, BIConfUtils.convertToMD5RelationFromSimpleRelation(simpleRelations, new BIUser(this.userId))), this.target, getLoader(), this.userId));
            }
        }
        gvi = GVIUtils.AND(gvi,
                widget.createFilterGVI(new DimensionCalculator[]{new NoneDimensionCalculator(new BICubeFieldSource(this.target),
                        new ArrayList<BITableSourceRelation>())}, this.target, getLoader(), this.userId));
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

    protected CBCell[][] createCells(GroupValueIndex gvi) {
        if (gvi == null) {
            return null;
        }
        BIDetailTarget[] viewDimension = widget.getViewDimensions();
        if (widget.getViewDimensions().length == 0) {
            return null;
        }
        int count = gvi.getRowsCountWithData();
        paging.setTotalSize(count);

        if (paging.getCurrentPage() > paging.getPages()) {
            return null;
        }

//        int maxRow = paging.getCurrentSize();
        CBCell[][] cbcells = new CBCell[viewDimension.length + widget.isOrder()][count + 1];
        createCellTitle(cbcells, CellConstant.CBCELL.TARGETTITLE_Y);
        return cbcells;
    }

    protected void fillOneLine(CBCell[][] cells, int row, Object[] ob) {

        for (int i = 0; i < viewDimension.length; i++) {
            BIDetailTarget t = viewDimension[i];
            Object v = ob[i];
            CBCell cell = new CBCell(v == null ? NONEVALUE : v);

            cell.setRow(row);
            cell.setColumn(i + widget.isOrder());
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List cellList = new ArrayList();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            CBBoxElement cbox = new CBBoxElement(cellList);

            if (t.useHyperLink()) {
                cell.setNameHyperlinkGroup(t.createHyperLinkNameJavaScriptGroup(v));
            }
            if (t instanceof BIStyleTarget) {
                cell.setStyle(BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1, t.useHyperLink()));
                BIStyleTarget sumCol = (BIStyleTarget) t;
                cbox.setName(sumCol.getValue());
                TargetStyle style = sumCol.getStyle();
                if (style != null) {
                    style.changeCellStyle(cell);
                }
            } else {
                cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, cell.getRow() % 2 == 1, t.useHyperLink()));
            }
            cbox.setType(CellConstant.CBCELL.ROWFIELD);
            cell.setBoxElement(cbox);
            cells[cell.getColumn()][cell.getRow()] = cell;
        }
    }

    protected void createCellTitle(CBCell[][] cbcells, int cellType) {
        BIDetailTarget[] viewDimension = widget.getViewDimensions();
        for (int i = 0; i < viewDimension.length; i++) {
            CBCell cell = new CBCell(((BIAbstractTargetAndDimension) viewDimension[i]).getText());
            cell.setColumn(i + widget.isOrder());
            cell.setRow(0);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, false));
            List cellList = new ArrayList();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            cbox.setName(viewDimension[i].getValue());
            cbox.setType(cellType);
            cbox.setSortTargetName(viewDimension[i].getValue());
            cbox.setSortTargetValue("[]");
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
    }
}