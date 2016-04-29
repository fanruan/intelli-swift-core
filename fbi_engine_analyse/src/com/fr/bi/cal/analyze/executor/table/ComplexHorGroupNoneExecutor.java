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
public class ComplexHorGroupNoneExecutor extends AbstractComplexNodeExecutor {


    public ComplexHorGroupNoneExecutor(TableWidget widget, Paging page,
                                       ArrayList<ArrayList<String>> columnArray,
                                       BISession session, ComplexExpander expander) {

        super(widget, page, session, expander);
        rowData = new BIComplexExecutData(columnArray, null);
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
            count += nodes[i].getTotalLength(complexExpander.getXExpander(i));
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
     * @throws NoneAccessablePrivilegeException
     */
    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {
        Map<Integer, Node> nodeMap = getCubeNodes();
        if (nodeMap == null || nodeMap.isEmpty()) {
            return new CBCell[][]{new CBCell[0]};
        }
        Iterator<Map.Entry<Integer, Node>> iterator = nodeMap.entrySet().iterator();
        Node[] trees = new Node[nodeMap.size()];
        Integer[] integers = new Integer[nodeMap.size()];
        int count = 0;
        while (iterator.hasNext()) {
            //导出就全部展开吧
            Map.Entry<Integer, Node> entry = iterator.next();
            trees[count] = entry.getValue();
            integers[count] = entry.getKey();
            count++;
        }
        int rowLen = rowData.getMaxArrayLength();
        //导出就全部展开吧
        int columnLen = paging.getOprator() < Node.NONE_PAGE_LEVER ? getNodesTotalLength(trees) : getNodesTotalLength(trees, complexExpander, integers);

        CBCell[][] cbcells = new CBCell[columnLen + 1][rowLen];
        generateTitle(cbcells, integers[0]);

        int startColumn = 1;
        for (int i = 0; i < trees.length; i++) {
            Node tree = trees[i];

            BIDimension[] rowDimension = rowData.getDimensionArray(integers[i]);
            NodeExpander expander = complexExpander.getXExpander(integers[i]);
            if (paging.getOprator() < Node.NONE_PAGE_LEVER) {
                HorGroupNoneTargetExecutor.dealWithNode(tree, cbcells, startColumn, 0, rowDimension, usedSumTarget, rowDimension.length - 1, rowData);
            } else {
                HorGroupNoneTargetExecutor.dealWithNode(tree, complexExpander.getXExpander(integers[i]), cbcells, startColumn, 0, rowDimension, usedSumTarget, new ArrayList<String>(), rowDimension.length - 1, rowData);
            }
            startColumn += paging.getOprator() < Node.NONE_PAGE_LEVER ? tree.getTotalLength() : tree.getTotalLength(expander);
        }
        geneEmptyCells(cbcells);
        return cbcells;
    }

    private void generateTitle(CBCell[][] cbcells, int firstRegionIndex) {
        BIDimension[] rowDimension = rowData.getDimensionArray(firstRegionIndex);
        int rowLength = rowDimension.length;

        int row = 0;
        for (int i = 0; i < rowLength; i++) {
            CBCell cell = new CBCell(rowDimension[i].getValue());
            cell.setColumn(0);
            cell.setRow(row);
            int rowSpan = rowData.getColumnRowSpan(i, rowLength);
            cell.setRowSpan(rowSpan);
            row += rowSpan;
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, false));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BIDimension rowCol = rowDimension[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.DIMENSIONTITLE_X);
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
    public Map<Integer, Node> getCubeNodes() {
        if (getSession() == null) {
            return null;
        }
        if (rowData.getDimensionArrayLength() + usedSumTarget.length == 0) {
            return null;
        }
        long start = System.currentTimeMillis();
        Map<Integer, Node> nodeMap = null;
        try {
            nodeMap = CubeIndexLoader.getInstance(session.getUserId()).loadComplexPageGroup(true, widget, new BISummaryTarget[0], rowData, allDimensions, allSumTarget, new TargetGettingKey[0], paging.getOprator(), widget.useRealData(), session, complexExpander, false);
        } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
        }

        System.out.println(DateUtils.timeCostFrom(start) + ": cal time");
        return nodeMap;
    }

    /**
     * 获取node
     *
     * @return 获取的node
     */
    @Override
    public Node getCubeNode() {
        return null;
    }
}