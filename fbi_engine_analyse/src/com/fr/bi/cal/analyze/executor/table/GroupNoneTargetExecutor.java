package com.fr.bi.cal.analyze.executor.table;

import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
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

        //最大的行宽
        int columnSpan = rowData.getColumnRowSpan(column, rowLength);
        int noneChildSpan = rowData.getNoneChildSpan(column, rowLength);
        if (expander == null) {
            return;
        }

        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            tempNode = node.getChild(i);
            @SuppressWarnings("unchecked")
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
            //TODO CBBoxElement需要整合减少内存
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
            Node t = tempNode;
            JSONArray ja = new JSONArray();
            int k = column;
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
            Node t = tempNode;
            JSONArray ja = new JSONArray();
            int k = column;
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
            dealWithNode(tempNode, cbcells, tempRow, column + 1, page, rowColumn, sumColumn, total - 1, hasNumber, rowData);
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
        int rowLen = paging.getOprator() < Node.NONE_PAGE_LEVER ? tree.getTotalLength() : tree.getTotalLength(expander.getYExpander());
        if (paging.getOprator() >= Node.NONE_PAGE_LEVER && tree.getChildLength() == 0) {
            rowLen = 0;
        }
        //+1是标题
        CBCell[][] cbcells = new CBCell[columnLen + widget.isOrder()][rowLen + 1];

        for (int i = 0; i < rowLength; i++) {
            CBCell cell = new CBCell(usedDimensions[i].getValue());
            cell.setColumn(i + widget.isOrder());
            cell.setRow(0);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, false));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BIDimension rowCol = usedDimensions[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.DIMENSIONTITLE_Y);
            if (rowCol.getSortTarget() == null) {
                cbox.setSortType(usedDimensions[i].getSortType());
            }
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
        if (paging.getOprator() < Node.NONE_PAGE_LEVER) {
            dealWithNode(tree, cbcells, 1, 0, paging.getOprator(), usedDimensions, usedSumTarget, usedDimensions.length - 1,  widget.isOrder(), new BIComplexExecutData(usedDimensions));
        } else {
            dealWithNode(tree, expander.getYExpander(), cbcells, 1, 0, paging.getCurrentPage(), usedDimensions, usedSumTarget, new ArrayList<String>(), usedDimensions.length - 1, widget.isOrder(), new BIComplexExecutData(usedDimensions));
        }
        if (widget.isOrder() == 1) {
            createNumberCellTitle(cbcells, 0);
            if (ExecutorCommonUtils.isAllPage(paging.getOprator())) {
                createAllNumberCellElement(cbcells, 1);
            } else {
                createAllNumberCellElement(cbcells, paging.getCurrentPage());
            }
        }
        return cbcells;
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
        int calpage = paging.getOprator();
        long start = System.currentTimeMillis();
        Node tree = CubeIndexLoader.getInstance(session.getUserId()).loadPageGroup(false, widget, new BISummaryTarget[0], usedDimensions, allDimensions, new BISummaryTarget[0], calpage, widget.useRealData(), session, expander.getYExpander());
        if (tree == null) {
            tree = new Node(null, null);
        }
        System.out.println(DateUtils.timeCostFrom(start) + ": cal time");
        return tree;
    }
}