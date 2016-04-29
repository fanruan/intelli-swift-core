package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.DateUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sheldon on 14-9-2.
 */
public class ComplexGroupNoneExecutor extends AbstractComplexNodeExecutor {

    public ComplexGroupNoneExecutor(TableWidget widget, Paging page,
                                    ArrayList<ArrayList<String>> rowArray,
                                    BISession session, ComplexExpander expander) {

        super(widget, page, session, expander);
        rowData = new BIComplexExecutData(rowArray, null);
    }

    /**
     * 获取nodes的个数
     *
     * @param nodes
     * @return
     */
    @Override
    public int getNodesTotalLength(Node[] nodes) {
        int count = 0;

        for (int i = 0; i < nodes.length; i++) {
            count += nodes[i].getTotalLength();
        }

        return count;
    }


    /**
     * 获取node的个数
     */
    @Override
    public int getNodesTotalLength(Node[] nodes, ComplexExpander complexExpander, Integer[] integers) {

        int count = 0;

        for (int i = 0; i < nodes.length; i++) {
            count += nodes[i].getTotalLength(complexExpander.getYExpander(integers[i]));
        }

        return count;
    }

    @Override
    public Rectangle getSouthEastRectangle() {
        return super.getSouthEastRectangle();
    }

    /**
     * 构建cells
     *
     * @return 构建的cells
     */
    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {
        Map<Integer, Node> nodeMap = getCubeNodes();
        if (nodeMap == null || nodeMap.isEmpty()) {
            return new CBCell[][]{new CBCell[0]};
        }
        int collen = rowData.getMaxArrayLength();
        int columnLen = collen + usedSumTarget.length;

        boolean needAll = paging.getOprator() < Node.NONE_PAGE_LEVER;
        Iterator<Map.Entry<Integer, Node>> iterator = nodeMap.entrySet().iterator();
        Node[] trees = new Node[nodeMap.size()];
        Integer[] integers = new Integer[nodeMap.size()];
        int i = 0;
        while (iterator.hasNext()) {
            //导出就全部展开吧
            Map.Entry<Integer, Node> entry = iterator.next();
            trees[i] = entry.getValue();
            integers[i] = entry.getKey();
            i++;
        }

        int rowLength = needAll ? getNodesTotalLength(trees) : getNodesTotalLength(trees, complexExpander, integers);
        CBCell[][] cbcells = new CBCell[columnLen][rowLength + 1];
        generateTitle(cbcells, integers[0]);
        int startRow = 1;

        for (i = 0; i < trees.length; i++) {
            Node node = trees[i];
            BIDimension[] rowDimension = rowData.getDimensionArray(integers[i]);

            NodeExpander nodeExpander = complexExpander.getYExpander(integers[i]);
            if (paging.getOprator() < Node.NONE_PAGE_LEVER) {
                GroupNoneTargetExecutor.dealWithNode(node, cbcells, startRow, 0, paging.getCurrentPage(), rowDimension, usedSumTarget, rowDimension.length - 1, 0, rowData);

            } else {
                GroupNoneTargetExecutor.dealWithNode(node, nodeExpander, cbcells, startRow, 0, paging.getCurrentPage(), rowDimension, usedSumTarget, new ArrayList<String>(), rowDimension.length - 1, 0, rowData);
            }
            startRow += needAll ? trees[i].getTotalLength() : trees[i].getTotalLength(nodeExpander);
        }
        geneEmptyCells(cbcells);
        return cbcells;
    }

    private void generateTitle(CBCell[][] cbcells, int firstRegionIndex) {
        BIDimension[] rowDimension = rowData.getDimensionArray(firstRegionIndex);
        int rowLength = rowDimension.length;

        for (int i = 0; i < rowLength; i++) {
            CBCell cell = new CBCell(rowDimension[i].getValue());
            cell.setColumn(i);
            cell.setRow(0);
            cell.setRowSpan(1);
            cell.setColumnSpan(rowData.getColumnRowSpan(i, rowLength));
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, false));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BIDimension rowCol = rowDimension[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.DIMENSIONTITLE_Y);
            if (rowCol.getSortTarget() == null) {
                cbox.setSortType(rowDimension[i].getSortType());
            }
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.report.summary.BIEngineExecutor#getCubeNode()
     */
    @Override
    public Node getCubeNode() {
        return null;
    }

    /**
     * 获取某个nodes
     *
     * @return
     */
    @Override
    public Map<Integer, Node> getCubeNodes() {
        if (getSession() == null) {
            return null;
        }

        int summaryLength = usedSumTarget.length;
        if (rowData.getDimensionArrayLength() + summaryLength == 0) {
            return null;
        }
        long start = System.currentTimeMillis();

        Map<Integer, Node> nodeMap = null;
        try {
            nodeMap = CubeIndexLoader.getInstance(session.getUserId()).loadComplexPageGroup(false, widget, new BISummaryTarget[0], rowData, allDimensions, allSumTarget, new TargetGettingKey[0], paging.getOprator(), widget.useRealData(), session, complexExpander, true);
        } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
        }


        System.out.println(DateUtils.timeCostFrom(start) + ": cal time");

        return nodeMap;
    }


}