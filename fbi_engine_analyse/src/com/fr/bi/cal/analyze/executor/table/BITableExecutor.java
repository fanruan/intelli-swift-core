package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.executor.BIAbstractExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.general.Inter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BITableExecutor<T> extends BIAbstractExecutor<T> {

    protected BISummaryTarget[] usedSumTarget;
    protected BISummaryTarget[] allSumTarget;
    protected BIDimension[] allDimensions;
    protected BIDimension[] usedDimensions;
    protected TableWidget widget;
    protected CrossExpander expander;

    protected BITableExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session);
        this.widget = widget;
        usedSumTarget = widget.getViewTargets();
        allSumTarget = widget.getTargets();
        allDimensions = widget.getDimensions();
        usedDimensions = widget.getViewDimensions();
        this.expander = expander;
//        this.expander = CrossExpander.ALL_EXPANDER;
    }


    //创建序号格
    protected static void createNumberCellTitle(CBCell[][] cbcells, int row) {
        if (cbcells[0][row] != null) {
            return;
        }
        CBCell cell = new CBCell(Inter.getLocText("BI-Row_Index"));
        cell.setColumn(0);
        cell.setRow(row);
        cell.setRowSpan(1);
        cell.setColumnSpan(1);
        cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
        cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, false));
        java.util.List tcellList = new ArrayList();
        tcellList.add(cell);
        CBBoxElement cbox = new CBBoxElement(tcellList);
        cbox.setName(Inter.getLocText("BI-Row_Index"));
        cell.setBoxElement(cbox);
        cbcells[cell.getColumn()][cell.getRow()] = cell;
    }

    //创建汇总格
    protected static void createSummaryCellElement(CBCell[][] cbcells, int row) {
        String name = Inter.getLocText("BI-Summary");
        CBCell cell = new CBCell(name);
        cell.setRow(row);
        cell.setColumn(0);
        cell.setRowSpan(1);
        cell.setColumnSpan(1);
        cell.setStyle(BITableStyle.getInstance().getYSumStringCellStyle(1));
        cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
        List<CBCell> cellList = new ArrayList<CBCell>();
        cellList.add(cell);
        //TODO CBBoxElement需要整合减少内存
        CBBoxElement cbox = new CBBoxElement(cellList);
        cbox.setName(name);
        cbox.setType(CellConstant.CBCELL.SUMARYNAME);
        cell.setBoxElement(cbox);
        cbcells[cell.getColumn()][cell.getRow()] = cell;
    }

    @Override
    public Rectangle getSouthEastRectangle() {
        return null;
    }
}