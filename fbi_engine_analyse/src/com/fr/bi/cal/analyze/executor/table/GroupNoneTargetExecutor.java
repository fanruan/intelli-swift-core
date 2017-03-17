package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.detail.DetailCellIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.style.DetailChartSetting;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.BIAbstractTargetAndDimension;
import com.fr.bi.field.BITargetAndDimensionUtils;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 小灰灰 on 14-3-13.
 */
public class GroupNoneTargetExecutor extends AbstractNodeExecutor {

    public GroupNoneTargetExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session, expander);
    }

    /**
     * 处理node节点
     *
     * @param node      node节点
     * @param expander  展开信息
     * @param cbcells   处理的cells
     * @param row       当前行
     * @param column    当前列
     * @param page      页数
     * @param rowColumn 行维度
     * @param sumColumn 统计组件
     * @param total     暂时没有
     * @param indexList 数组
     * @param hasNumber 序号
     * @param rowData   复杂报表的数据
     */
    public static void dealWithNode(Node node, NodeExpander expander, CBCell[][] cbcells, int row, int column, int page,
                                    BIDimension[] rowColumn,
                                    BITarget[] sumColumn,
                                    final ArrayList<String> indexList, int total, int hasNumber, BIComplexExecutData rowData) {
        int rowLength = rowColumn.length;
        Node tempNode = null;
        int tempRow = row;
        CBCell cell = null;
        int columnSpan = rowData.getColumnRowSpan(column, rowLength);
        int noneChildSpan = rowData.getNoneChildSpan(column, rowLength);
        if (expander == null) {
            return;
        }
        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            tempNode = node.getChild(i);
            ArrayList<String> currentIndex = (ArrayList<String>) indexList.clone();
            BIDimension rd = rowColumn[column];
            String name = rd.toString(tempNode.getData());
            currentIndex.add(name);
            NodeExpander childEx = expander.getChildExpander(name);
            int rowSpan = tempNode.getTotalLength(childEx);
            cell = new CBCell(name);
            cell.setRow(tempRow);
            cell.setColumn(column + hasNumber);
            cell.setRowSpan(rowSpan);
            if (childEx == null) {
                cell.setColumnSpan(noneChildSpan);
            } else {
                cell.setColumnSpan(columnSpan);
            }
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, tempRow % 2 == 1));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITargetAndDimension rowCol = rowColumn[column];
            cbox.setName(rowCol.getValue());
            if (column != rowLength - 1) {
                cbox.setIndexString_y(trunToIndexString(currentIndex));
                cbox.setExpand(expander.isChildExpand(name));
                cbox.setDimensionRegionIndex(rowData.getDimensionRegionFromDimension(rowColumn));
            }
            cbox.setType(CellConstant.CBCELL.ROWFIELD);
            cell.setBoxElement(cbox);
            cbox.setDimensionJSON(getDimensionJSONString(rowColumn, column, tempNode));
            cbcells[cell.getColumn()][cell.getRow()] = cell;
            dealWithNode(tempNode, expander.getChildExpander(name), cbcells, tempRow, column + 1, page, rowColumn, sumColumn, currentIndex, total - 1, hasNumber, rowData);
            tempRow += rowSpan;
        }
    }

    /**
     * 处理node节点
     *
     * @param node      node节点
     * @param cbcells   处理的cells
     * @param row       当前行
     * @param column    当前列
     * @param page      页数
     * @param rowColumn 行维度
     * @param sumColumn 统计组件
     * @param total     暂时没有
     * @param hasNumber 序号
     * @param rowData   复杂报表数据
     */
    public static void dealWithNode(Node node, CBCell[][] cbcells, int row, int column, int page,
                                    BIDimension[] rowColumn,
                                    BITarget[] sumColumn, int total, int hasNumber, BIComplexExecutData rowData) {
        Node tempNode = null;
        int tempRow = row;
        CBCell cell = null;
        int rowLength = rowColumn.length;

        //最大的行宽
        int maxRowLen = rowData.getMaxArrayLength();
        int columnSpan = rowData.getColumnRowSpan(column, rowLength);

        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            tempNode = node.getChild(i);
            int rowSpan = tempNode.getTotalLength();
            BIDimension rd = rowColumn[column];
            cell = new CBCell(rd.toString(tempNode.getData()));
            cell.setRow(tempRow);
            cell.setColumn(column + hasNumber);
            cell.setRowSpan(rowSpan);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, tempRow % 2 == 1));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            CBBoxElement cbox = new CBBoxElement(cellList);
            cell.setColumnSpan(columnSpan);
            BITargetAndDimension rowCol = rowColumn[column];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.ROWFIELD);
            cell.setBoxElement(cbox);
            cbox.setDimensionJSON(getDimensionJSONString(rowColumn, column, tempNode));
            cbcells[cell.getColumn()][cell.getRow()] = cell;
            dealWithNode(tempNode, cbcells, tempRow, column + 1, page, rowColumn, sumColumn, total - 1, hasNumber, rowData);
            tempRow += rowSpan;
        }
    }

    private static String trunToIndexString(ArrayList<String> currentIndex) {
        return new JSONArray(currentIndex).toString();
    }

    public DetailCellIterator createCellIterator4Excel() throws Exception {
        return new DetailCellIterator(0, 0);
    }

    /**
     * 构建cells
     *
     * @return 构建的cells
     * @throws NoneAccessablePrivilegeException
     */
    @Override
    public CBCell[][] createCellElement() throws Exception {
        return new CBCell[0][0];
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.report.summary.BIEngineExecutor#getCubeNode()
     */
    @Override
    public Node getCubeNode() throws Exception {
        if (getSession() == null) {
            return null;
        }
        int rowLength = usedDimensions.length;
        int summaryLength = usedSumTarget.length;
        int columnLen = rowLength + summaryLength;
        if (rowLength + summaryLength + columnLen == 0) {
            return null;
        }
        int calpage = paging.getOperator();
        long start = System.currentTimeMillis();
        Node tree = CubeIndexLoader.getInstance(session.getUserId()).loadPageGroup(false, widget, new BISummaryTarget[0], usedDimensions, allDimensions, new BISummaryTarget[0], calpage, widget.useRealData(), session, expander.getYExpander());
        if (tree == null) {
            tree = new Node();
        }
        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return tree;
    }
}