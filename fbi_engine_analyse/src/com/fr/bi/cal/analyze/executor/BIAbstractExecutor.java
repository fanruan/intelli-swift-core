package com.fr.bi.cal.analyze.executor;

import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.finebi.cube.api.ICubeDataLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GUY on 2015/4/16.
 */
public abstract class BIAbstractExecutor<T> implements BIEngineExecutor<T> {

    protected Paging paging; //分页信息
    protected transient BISession session;
    private BIWidget widget;

    public BIAbstractExecutor(BIWidget widget, Paging paging, BISession session) {
        this.paging = paging;
        this.widget = widget;
        this.session = session;
    }

    //创建当前页的所有数字格
    protected static void createAllNumberCellElement(CBCell[][] cbcells, Integer page) {
        if (cbcells == null) {
            return;
        }
        int index = 1;
        //pony 最后一个空行不用写序号
        for (int i = 0; i < cbcells[0].length; i++) {
            if (cbcells[0][i] == null) {
                createNumberCellElement(cbcells, page, index++, i, 1);
            }
        }
    }

    //创建一个数字格
    protected static void createNumberCellElement(CBCell[][] cbcells, Integer page, Integer index, int row, int rowSpan) {
        Integer curIndex = index + (page >= 1 ? (page - 1) : 0) * 20;
        CBCell cell = new CBCell(curIndex);
        cell.setColumn(0);
        cell.setRow(row);
        cell.setRowSpan(rowSpan);
        cell.setColumnSpan(1);
        cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
        cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(true, cell.getRow() % 2 == 1));
        List tcellList = new ArrayList();
        tcellList.add(cell);
        CBBoxElement cbox = new CBBoxElement(tcellList);
        cbox.setName(index.toString());
        cell.setBoxElement(cbox);
        cbcells[cell.getColumn()][cell.getRow()] = cell;
    }

    public BISessionProvider getSession() {
        return session;
    }

    public ICubeDataLoader getLoader() {
        return session.getLoader();
    }

    /**
     * 创建标题栏
     *
     * @param cbcells
     * @param cellType
     */
    protected void createCellTitle(CBCell[][] cbcells, int cellType) {
        BIDimension[] viewDimension = widget.getViewDimensions();
        for (int i = 0; i < viewDimension.length; i++) {
            CBCell cell = new CBCell(viewDimension[i]);
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
            if (viewDimension[i] != null) {
                cbox.setSortType(viewDimension[i].getSortType());
            }
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
    }
}