package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.field.BIAbstractTargetAndDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 14-3-17.
 */
public class HorGroupNoneTargetExecutor extends AbstractNodeExecutor {
    public HorGroupNoneTargetExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session, expander);
        usedDimensions = widget.getViewTopDimensions();
    }

    /**
     * 处理节点
     *
     * @param node      节点
     * @param expander  展开信息
     * @param cbcells   数组
     * @param row       处理行
     * @param column    列
     * @param rowColumn 列维度
     * @param sumColumn 汇总指标
     * @param indexList 注释
     * @param total     不明
     * @param rowData   获取列框的复杂报表数据
     */
    public static void dealWithNode(Node node, NodeExpander expander, CBCell[][] cbcells, int row, int column, BIDimension[] rowColumn, BITarget[] sumColumn, final ArrayList<String> indexList, int total, BIComplexExecutData rowData) {
        int rowLength = rowColumn.length;
        Node tempNode = null;
        int tempRow = row;
        CBCell cell = null;
        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            tempNode = node.getChild(i);
            ArrayList<String> currentIndex = (ArrayList<String>) indexList.clone();
            int dimensionIndex = rowData.getDimensionIndexFromRow(column, rowLength);
            String name = tempNode.getShowValue();
            currentIndex.add(name);
            NodeExpander childEx = expander.getChildExpander(name);
            int rowSpan = tempNode.getTotalLength(childEx);
            cell = new CBCell(name);
            cell.setRow(column);
            cell.setColumn(tempRow);
            cell.setColumnSpan(rowSpan);
            int columnSpan = rowData.getNormalRowSpan(column, rowLength);
            if (childEx == null) {
                columnSpan = rowData.getNoneChildRowSpan(column, rowLength);
            }
            cell.setRowSpan(columnSpan);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, false));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITargetAndDimension rowCol = rowColumn[dimensionIndex];
            cbox.setName(rowCol.getValue());
            if (dimensionIndex != rowLength - 1) {
                cbox.setIndexString_x(trunToIndexString(currentIndex));
                cbox.setExpand(expander.isChildExpand(name));
                cbox.setDimensionRegionIndex(rowData.getDimensionRegionFromDimension(rowColumn));
            }
            cbox.setType(CellConstant.CBCELL.ROWFIELD);
            cell.setBoxElement(cbox);
            Node t = tempNode;
            JSONArray ja = new JSONArray();
            int k = dimensionIndex;
            while (k != -1 && t != null) {
                try {
                    ja.put(new JSONObject().put(rowColumn[k].getValue(), rowColumn[k].toFilterObject(t.getData())));
                } catch (Exception e) {
                }
                t = t.getParent();
                k--;
            }
            cbox.setDimensionJSON(ja.toString());
            cbcells[cell.getColumn()][cell.getRow()] = cell;
            dealWithNode(tempNode, expander.getChildExpander(name), cbcells, tempRow, column + columnSpan, rowColumn, sumColumn, currentIndex, total - 1, rowData);
            tempRow += rowSpan;
        }
    }

    /**
     * 处理节点
     *
     * @param node      节点
     * @param cbcells   cbcell数组
     * @param row       行号
     * @param column    列号
     * @param rowColumn 列维度
     * @param sumColumn 汇总
     * @param total     注释
     * @param rowData   获取复杂报表的数据
     */
    public static void dealWithNode(Node node, CBCell[][] cbcells, int row, int column,
                                    BIDimension[] rowColumn,
                                    BITarget[] sumColumn, int total, BIComplexExecutData rowData) {
        int rowLength = rowColumn.length;
        Node tempNode = null;
        int tempRow = row;
        CBCell cell = null;
        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            tempNode = node.getChild(i);
            int rowSpan = tempNode.getTotalLength();
            int dimensionIndex = rowData.getDimensionIndexFromRow(column, rowLength);
            BIDimension rd = rowColumn[dimensionIndex];
            cell = new CBCell(rd.toString(tempNode.getData()));
            cell.setRow(column);
            cell.setColumn(tempRow);
            cell.setColumnSpan(rowSpan);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, false));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            CBBoxElement cbox = new CBBoxElement(cellList);
            int columnSpan = rowData.getNormalRowSpan(column, rowLength);
            cell.setRowSpan(columnSpan);
            BITargetAndDimension rowCol = rowColumn[column];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.ROWFIELD);
            cell.setBoxElement(cbox);
            Node t = tempNode;
            JSONArray ja = new JSONArray();
            int k = dimensionIndex;
            while (k != -1 && t != null) {
                try {
                    ja.put(new JSONObject().put(rowColumn[k].getValue(), rowColumn[k].toFilterObject(t.getData())));
                } catch (Exception e) {
                }
                t = t.getParent();
                k--;
            }
            cbox.setDimensionJSON(ja.toString());
            cbcells[cell.getColumn()][cell.getRow()] = cell;
            dealWithNode(tempNode, cbcells, tempRow, column + columnSpan, rowColumn, sumColumn, total - 1, rowData);
            tempRow += rowSpan;
        }
    }

    private static String trunToIndexString(ArrayList<String> currentIndex) {
        return new JSONArray(currentIndex).toString();
    }

    /**
     * 构建cells
     *
     * @return 构建的cells
     * @throws NoneAccessablePrivilegeException
     */
    @Override
    public CBCell[][] createCellElement() throws NoneAccessablePrivilegeException {
        Node tree = getCubeNode();
        if (tree == null) {
            return new CBCell[][]{new CBCell[0]};
        }
        int rowLength = usedDimensions.length;
        int summaryLength = usedSumTarget.length;
        int columnLen = rowLength + summaryLength;
        //导出就全部展开吧
        int rowLen = paging.getOprator() < Node.NONE_PAGE_LEVER ? tree.getTotalLength() : tree.getTotalLength(expander.getXExpander());
        //+1是标题
        CBCell[][] cbcells = new CBCell[rowLen + 1][columnLen];

        generateTitle(cbcells, columnLen);
        if (paging.getOprator() < Node.NONE_PAGE_LEVER) {
            dealWithNode(tree, cbcells, 1, 0, usedDimensions, usedSumTarget, usedDimensions.length - 1, new BIComplexExecutData(usedDimensions));
        } else {
            dealWithNode(tree, expander.getXExpander(), cbcells, 1, 0, usedDimensions, usedSumTarget, new ArrayList<String>(), usedDimensions.length - 1, new BIComplexExecutData(usedDimensions));
        }
        return cbcells;
    }

    private void generateTitle(CBCell[][] cbcells, int rowLength) {
        for (int i = 0; i < rowLength; i++) {
            CBCell cell = new CBCell(((BIAbstractTargetAndDimension)usedDimensions[i]).getText());
            cell.setColumn(0);
            cell.setRow(i);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, false));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BIDimension rowCol = usedDimensions[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.DIMENSIONTITLE_X);
            if (rowCol.getSortTarget() == null) {
                cbox.setSortType(usedDimensions[i].getSortType());
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
        if (getSession() == null) {
            return null;
        }
        int rowLength = usedDimensions.length;
        int summaryLength = usedSumTarget.length;
        int columnLen = rowLength + summaryLength;
        if (rowLength + summaryLength + columnLen == 0) {
            return null;
        }
        long start = System.currentTimeMillis();
        int calpage = paging.getOprator();
        Node tree = CubeIndexLoader.getInstance(session.getUserId()).loadPageGroup(true, widget, new BISummaryTarget[0], usedDimensions, allDimensions, new BISummaryTarget[0], calpage, widget.useRealData(), session, expander.getXExpander());
        if (tree == null) {
            tree = new Node(null, null);
        }
        System.out.println(DateUtils.timeCostFrom(start) + ": cal time");
        return tree;
    }

}