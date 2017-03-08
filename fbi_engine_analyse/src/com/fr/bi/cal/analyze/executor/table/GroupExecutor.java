package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.executor.detail.DetailCellIterator;
import com.fr.bi.cal.analyze.executor.detail.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.utils.ExecutorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.style.DetailChartSetting;
import com.fr.bi.conf.report.style.TargetStyle;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.BIAbstractTargetAndDimension;
import com.fr.bi.field.BITargetAndDimensionUtils;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.general.GeneralUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.ExportConstants;
import com.fr.stable.StringUtils;

import java.awt.Rectangle;
import java.util.*;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class GroupExecutor extends AbstractNodeExecutor {
    private Rectangle rectangle;
    private int page = 1;
    private final static int EXCEL_ROW_MODE_VALUE = ExportConstants.MAX_ROWS_2007 - 1;

    public GroupExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session, expander);
    }

    static void dealWithNoChildNode(Node node, CBCell[][] cbcells, int row, int column,
                                    BIDimension[] rowColumn,
                                    BITarget[] sumColumn, TargetGettingKey[] keys, DetailChartSetting chartSetting) {
        int rowLength = rowColumn.length;
        int tempRow = row;
        CBCell cell = null;
        int index = column - rowLength;
        if (index == 0) {
            for (int i = 0, len = keys.length; i < len; i++) {
                Object v = node.getSummaryValue(keys[i]);
                v = ExecutorUtils.formatExtremeSumValue(v, chartSetting.getNumberLevelByTargetId(keys[i].getTargetName()));
                cell = new CBCell(v);
                cell.setRow(tempRow);
                cell.setColumn(column == 0 ? i : (cbcells.length - len + i));
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle(BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1, chartSetting.getNumberLevelByTargetId(keys[i].getTargetName()) == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT));
                cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
                List<CBCell> cellList = new ArrayList<CBCell>();
                cellList.add(cell);
                //TODO CBBoxElement需要整合减少内存
                CBBoxElement cbox = new CBBoxElement(cellList);
                Node t = node;
                JSONArray ja = new JSONArray();
                int k = rowLength - 1;
                while (k != -1 && t != null) {
                    try {
                        ja.put(new JSONObject().put(rowColumn[k].getValue(), rowColumn[k].toFilterObject(t.getData())));
                    } catch (Exception e) {
                    }
                    t = t.getParent();
                    k--;
                }
                cbox.setDimensionJSON(ja.toString());
                BITarget sumCol = sumColumn[i];
                cbox.setName(sumCol.getValue());
                TargetStyle style = sumCol.getStyle();
                if (style != null) {
                    style.changeCellStyle(cell);
                }
                cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
                cell.setBoxElement(cbox);
                cbcells[cell.getColumn()][cell.getRow()] = cell;
            }
        }
    }

    static void dealWithNoExpanNode(Node node, NodeExpander expander, CBCell[][] cbcells, int row, int column,
                                    BIDimension[] rowColumn,
                                    BITarget[] sumColumn, TargetGettingKey[] keys,
                                    final ArrayList<String> indexList, int total, int hasNumber, DetailChartSetting chartSetting) {
        int tempRow = row;
        CBCell cell = null;
        for (int i = 0, len = keys.length; i < len; i++) {
            Object v = node.getSummaryValue(keys[i]);
            v = ExecutorUtils.formatExtremeSumValue(v, chartSetting.getNumberLevelByTargetId(keys[i].getTargetName()));
            cell = new CBCell(v);
            cell.setRow(tempRow);
            cell.setColumn(cbcells.length - len + i);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1, chartSetting.getNumberLevelByTargetId(keys[i].getTargetName()) == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITarget sumCol = sumColumn[i];
            TargetStyle style = sumCol.getStyle();
            if (style != null) {
                style.changeCellStyle(cell);
            }
            cbox.setName(sumCol.getValue());
            cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
            Node t = node;
            JSONArray ja = new JSONArray();
            int k = 0;
            while (t.getParent() != null) {
                t = t.getParent();
                k++;
            }
            k--;
            t = node;
            while (k != -1 && t != null) {
                try {
                    ja.put(new JSONObject().put(rowColumn[k].getValue(), rowColumn[k].toFilterObject(t.getData())));
                } catch (JSONException e) {
                }
                t = t.getParent();
                k--;
            }
            cbox.setDimensionJSON(ja.toString());
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
    }

    static int dealWithExpanNodeChildren(Node node, NodeExpander expander, CBCell[][] cbcells, int row, int column, int page,
                                         BIDimension[] rowColumn,
                                         BITarget[] sumColumn, TargetGettingKey[] keys,
                                         final ArrayList<String> indexList, int total, int hasNumber, int tempRow, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        int rowLength = rowColumn.length;
        if (expander == null) {
            return tempRow;
        }
        return getTempRow(node, expander, cbcells, column, page, rowColumn, sumColumn, keys, indexList, total, hasNumber, tempRow, rowData, chartSetting, rowLength);
    }

    private static int getTempRow(Node node, NodeExpander expander, CBCell[][] cbcells, int column, int page, BIDimension[] rowColumn, BITarget[] sumColumn, TargetGettingKey[] keys, ArrayList<String> indexList, int total, int hasNumber, int tempRow, BIComplexExecutData rowData, DetailChartSetting chartSetting, int rowLength) {
        Node tempNode;
        CBCell cell;
        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            tempNode = node.getChild(i);
            ArrayList<String> currentIndex = (ArrayList<String>) indexList.clone();
            BIDimension rd = rowColumn[column];
            String name = rd.toString(tempNode.getData());
            if (rd.getGroup().getType() == BIReportConstant.GROUP.YMD && GeneralUtils.string2Number(name) != null) {
                name = DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(name).longValue()));
            }
            currentIndex.add(name);
            NodeExpander childEx = expander.getChildExpander(name);
            int rowSpan;
            if (sumColumn.length == 0 || !chartSetting.showRowTotal()) {
                rowSpan = tempNode.getTotalLength(childEx);
            } else {
                rowSpan = tempNode.getTotalLengthWithSummary(childEx);
            }
            cell = new CBCell(name);
            cell.setRow(tempRow);
            cell.setColumn(column + hasNumber);
            cell.setRowSpan(rowSpan);
            int columnSpan = rowData.getColumnRowSpan(column, rowLength);
            int noneChildSpan = rowData.getNoneChildSpan(column, rowLength);
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
            dealWithNode(tempNode, expander.getChildExpander(name), cbcells, tempRow, column + 1, page, rowColumn, sumColumn, keys, currentIndex, total - 1, hasNumber, rowData, chartSetting);
            tempRow += rowSpan;
        }
        return tempRow;
    }

    private static String trunToIndexString(ArrayList<String> currentIndex) {
        return new JSONArray(currentIndex).toString();
    }

    static boolean judgeNode(Node node, BITarget[] sumColumn) {
        return (node.needSummary()) && sumColumn.length > 0;
    }

    static int dealWithExpanSumNode(Node node, NodeExpander expander, CBCell[][] cbcells, int row, int column, BIDimension[] rowColumn, BITarget[] sumColumn, TargetGettingKey[] keys,
                                    final ArrayList<String> indexList, int total, int hasNumber, int tempRow, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        CBCell cell = null;
        int rowLength = rowColumn.length;
        if (chartSetting.showRowTotal() && judgeNode(node, sumColumn)) {
            cell = new CBCell(Inter.getLocText("BI-Summary_Summary"));
            cell.setRow(tempRow);
            cell.setColumn(column == 0 ? 0 : (column + hasNumber));
            cell.setRowSpan(1);
            int noneChildSpan = rowData.getNoneChildSpan(column, rowLength);
            cell.setColumnSpan(column == 0 ? (noneChildSpan + hasNumber) : noneChildSpan);
            if (column > 0 && hasNumber == 1) {
                createSummaryCellElement(cbcells, tempRow);
            }
            cell.setStyle(BITableStyle.getInstance().getYSumStringCellStyle(total));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            cbox.setName(rowColumn[column].getValue());
            cbox.setType(CellConstant.CBCELL.SUMARYNAME);
            cell.setBoxElement(cbox);
            cbox.setDimensionJSON(getDimensionJSONString(rowColumn, column - 1, node));
            cbcells[cell.getColumn()][cell.getRow()] = cell;
            dealWithExpandNodeMetrics(node, cbcells, rowColumn, sumColumn, keys, total, tempRow, chartSetting);
        }
        return tempRow;
    }

    private static void dealWithExpandNodeMetrics(Node node, CBCell[][] cbcells, BIDimension[] rowColumn, BITarget[] sumColumn, TargetGettingKey[] keys, int total, int tempRow, DetailChartSetting chartSetting) {
        CBCell cell;
        List<CBCell> cellList;
        CBBoxElement cbox;
        Node t;
        JSONArray ja;
        int k;
        for (int i = 0, len = keys.length; i < len; i++) {
            Object v = node.getSummaryValue(keys[i]);
            int numLevel = chartSetting.getNumberLevelByTargetId(keys[i].getTargetName());
            v = ExecutorUtils.formatExtremeSumValue(v, numLevel);
            cell = new CBCell(v);
            cell.setRow(tempRow);
            cell.setColumn(cbcells.length - len + i);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getYTotalCellStyle(v, total, ComparatorUtils.equals(numLevel, BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT)));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            cbox = new CBBoxElement(cellList);
            BITarget sumCol = sumColumn[i];
            TargetStyle style = sumCol.getStyle();
            if (style != null) {
                style.changeCellStyle(cell);
            }
            cbox.setName(sumCol.getValue());
            cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
            t = node;
            k = 0;
            while (t.getParent() != null) {
                t = t.getParent();
                k++;
            }
            k--;
            cbox.setDimensionJSON(getDimensionJSONString(rowColumn, k, node));
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
    }

    static void dealWithExpanNode(Node node, NodeExpander expander, CBCell[][] cbcells, int row, int column, int page,
                                  BIDimension[] rowColumn,
                                  BITarget[] sumColumn, TargetGettingKey[] keys,
                                  final ArrayList<String> indexList, int total, int hasNumber, BIComplexExecutData rowData, DetailChartSetting chartSetting) {

        int tempRow = row;
        tempRow = dealWithExpanNodeChildren(node, expander, cbcells, row, column, page,
                rowColumn, sumColumn, keys, indexList, total, hasNumber, tempRow, rowData, chartSetting);
        if (chartSetting.showRowTotal() && judgeNode(node, sumColumn)) {
            dealWithExpanSumNode(node, expander, cbcells, row, column,
                    rowColumn, sumColumn, keys, indexList, total, hasNumber, tempRow, rowData, chartSetting);
        } else {
            tempRow++;
        }
    }

    static void dealWithNode(Node node, NodeExpander expander, CBCell[][] cbcells, int row, int column, int page,
                             BIDimension[] rowColumn,
                             BITarget[] sumColumn, TargetGettingKey[] keys,
                             final ArrayList<String> indexList, int total, int hasNumber, BIComplexExecutData rowData, DetailChartSetting chartSetting) {

        if (isLastDimension(column, rowColumn)) {
//            if (column == 0) {
//                dealWithNoChildNode(node, cbcells, row, column, rowColumn, sumColumn, keys, chartSetting);
//                return;
//            }
            dealWithNoChildNode(node, cbcells, row, column, rowColumn, sumColumn, keys, chartSetting);
            return;
        }
        //收缩着:
        if (expander == null) {

            dealWithNoExpanNode(node, expander, cbcells, row, column, rowColumn, sumColumn, keys, indexList, total, hasNumber, chartSetting);
            return;
        }
        //展开情况
        dealWithExpanNode(node, expander, cbcells, row, column, page, rowColumn, sumColumn, keys, indexList, total, hasNumber, rowData, chartSetting);
    }

    /**
     * 处理节点
     *
     * @param node      节点
     * @param cbcells   cell数组
     * @param row       行
     * @param column    列
     * @param rowColumn 行数
     * @param sumColumn 列数
     * @param keys      键值
     * @param total     总数
     * @param hasNumber 序号
     */
    public static void dealWithNodeNoChild(Node node, CBCell[][] cbcells, int row, int column,
                                           BIDimension[] rowColumn,
                                           BITarget[] sumColumn, TargetGettingKey[] keys, int total, int hasNumber, DetailChartSetting chartSetting) {
        int rowLength = rowColumn.length;
        int tempRow = row;
        CBCell cell = null;
        int index = column - rowLength;
        if (index == 0) {
            for (int i = 0, len = keys.length; i < len; i++) {
                int numLevel = chartSetting.getNumberLevelByTargetId(keys[i].getTargetName());
                Object v = node.getSummaryValue(keys[i]);
                v = ExecutorUtils.formatExtremeSumValue(v, numLevel);
                cell = new CBCell(v);
                cell.setRow(tempRow);
                cell.setColumn(cbcells.length - len + i);
                cell.setRowSpan(1);
                cell.setColumnSpan(1);
                cell.setStyle(BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1, ComparatorUtils.equals(numLevel, BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT)));
                cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
                List<CBCell> cellList = new ArrayList<CBCell>();
                cellList.add(cell);
                //TODO CBBoxElement需要整合减少内存
                CBBoxElement cbox = new CBBoxElement(cellList);
                Node t = node;
                JSONArray ja = new JSONArray();
                int k = rowLength - 1;
                while (k != -1 && t != null) {
                    try {
                        ja.put(new JSONObject().put(rowColumn[k].getValue(), rowColumn[k].toFilterObject(t.getData())));
                    } catch (Exception e) {
                    }
                    t = t.getParent();
                    k--;
                }
                cbox.setDimensionJSON(ja.toString());
                BITarget sumCol = sumColumn[i];
                cbox.setName(sumCol.getValue());
                TargetStyle style = sumCol.getStyle();
                if (style != null) {
                    style.changeCellStyle(cell);
                }
                cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
                cell.setBoxElement(cbox);
                cbcells[cell.getColumn()][cell.getRow()] = cell;
            }
        }
    }

    /**
     * 处理节点
     *
     * @param node      节点
     * @param cbcells   cell数组
     * @param row       行
     * @param column    列
     * @param rowColumn 行数
     * @param sumColumn 列数
     * @param keys      键值
     * @param total     总数
     * @param hasNumber 序号
     * @param tempRow   行数（传参）
     * @param rowData   复杂表数据
     * @return 行数
     */
    public static int dealWidthNodeChildren(Node node, CBCell[][] cbcells, int row, int column,
                                            BIDimension[] rowColumn,
                                            BITarget[] sumColumn, TargetGettingKey[] keys, int total,
                                            int hasNumber, int tempRow, BIComplexExecutData rowData, DetailChartSetting chartSetting) {

        int rowLength = rowColumn.length;
        Node tempNode = null;
        CBCell cell = null;
        for (int i = 0, len = node.getChildLength(); i < len; i++) {
            tempNode = node.getChild(i);
            int rowSpan = (sumColumn.length == 0 || !chartSetting.showRowTotal()) ? tempNode.getTotalLength() : tempNode.getTotalLengthWithSummary();
            BIDimension rd = rowColumn[column];
            String text = rd.toString(tempNode.getData());
            if (ComparatorUtils.equals(rd.getGroup().getType(), BIReportConstant.GROUP.YMD) && GeneralUtils.string2Number(text) != null) {
                text = DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(text).longValue()));
            }
            cell = new CBCell(text);
            cell.setRow(tempRow);
            cell.setColumn(column + hasNumber);
            cell.setRowSpan(rowSpan);
            cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, tempRow % 2 == 1));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            int columnSpan = rowData.getColumnRowSpan(column, rowLength);
            cell.setColumnSpan(columnSpan);
            BITargetAndDimension rowCol = rowColumn[column];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.ROWFIELD);
            cell.setBoxElement(cbox);
            cbox.setDimensionJSON(getDimensionJSONString(rowColumn, column, tempNode));
            cbcells[cell.getColumn()][cell.getRow()] = cell;
            dealWithNode(tempNode, cbcells, tempRow, column + 1, rowColumn, sumColumn, keys, total - 1, hasNumber, rowData, chartSetting);
            tempRow += rowSpan;
        }
        return tempRow;
    }

    /**
     * 处理节点
     *
     * @param node      节点
     * @param cbcells   cell数组
     * @param row       行
     * @param column    列
     * @param rowColumn 行数
     * @param sumColumn 列数
     * @param keys      键值
     * @param total     总数
     * @param hasNumber 序号
     * @param tempRow   行数（传参）
     * @param rowData   复杂表数据
     * @return 行数
     */
    public static int dealWidthNodeSummary(Node node, CBCell[][] cbcells, int row, int column, BIDimension[] rowColumn,
                                           BITarget[] sumColumn, TargetGettingKey[] keys, int total,
                                           int hasNumber, int tempRow, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        int rowLength = rowColumn.length;
        CBCell cell = null;
        cell = new CBCell(Inter.getLocText("BI-Summary_Summary"));
        cell.setRow(tempRow);
        cell.setColumn(column == 0 ? 0 : (column + hasNumber));
        cell.setRowSpan(1);
        if (column > 0 && hasNumber == 1) {
            createSummaryCellElement(cbcells, tempRow);
        }
        int noneChildSpan = rowData.getNoneChildSpan(column, rowLength);
        cell.setColumnSpan(column == 0 ? (noneChildSpan + hasNumber) : noneChildSpan);
        cell.setStyle(BITableStyle.getInstance().getYSumStringCellStyle(total));
        cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
        List<CBCell> cellList = new ArrayList<CBCell>();
        cellList.add(cell);
        //TODO CBBoxElement需要整合减少内存
        CBBoxElement cbox = new CBBoxElement(cellList);
        cbox.setName(rowColumn[column].getValue());
        cbox.setType(CellConstant.CBCELL.SUMARYNAME);
        cell.setBoxElement(cbox);
        cbox.setDimensionJSON(getDimensionJSONString(rowColumn, column - 1, node));
        cbcells[cell.getColumn()][cell.getRow()] = cell;
        dealWithMetrics(node, cbcells, rowColumn, sumColumn, keys, total, tempRow, chartSetting);
        return tempRow;
    }

    private static void dealWithMetrics(Node node, CBCell[][] cbcells, BIDimension[] rowColumn, BITarget[] sumColumn, TargetGettingKey[] keys, int total, int tempRow, DetailChartSetting chartSetting) {
        CBCell cell;
        List<CBCell> cellList;
        CBBoxElement cbox;
        Node t;
        JSONArray ja;
        int k;
        for (int i = 0, len = keys.length; i < len; i++) {
            int numLevel = chartSetting.getNumberLevelByTargetId(keys[i].getTargetName());
            Object v = node.getSummaryValue(keys[i]);
            v = ExecutorUtils.formatExtremeSumValue(v, numLevel);
            cell = new CBCell(v);
            cell.setRow(tempRow);
            cell.setColumn(cbcells.length - len + i);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getYTotalCellStyle(v, total, ComparatorUtils.equals(numLevel, BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT)));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            cbox = new CBBoxElement(cellList);
            BITarget sumCol = sumColumn[i];
            TargetStyle style = sumCol.getStyle();
            if (style != null) {
                style.changeCellStyle(cell);
            }
            cbox.setName(sumCol.getValue());
            cbox.setType(CellConstant.CBCELL.SUMARYFIELD);
            t = node;
            k = 0;
            while (t.getParent() != null) {
                t = t.getParent();
                k++;
            }
            k--;
            cbox.setDimensionJSON(getDimensionJSONString(rowColumn, k, node));
            cell.setBoxElement(cbox);
            cbcells[cell.getColumn()][cell.getRow()] = cell;
        }
    }

    /**
     * 处理节点
     *
     * @param node      节点
     * @param cbcells   cell数组
     * @param row       行
     * @param column    列
     * @param rowColumn 行数
     * @param sumColumn 列数
     * @param keys      键值
     * @param total     总数
     * @param hasNumber 序号
     * @param rowData   复杂数据
     */
    public static void dealWithNodeChild(Node node, CBCell[][] cbcells, int row, int column,
                                         BIDimension[] rowColumn,
                                         BITarget[] sumColumn, TargetGettingKey[] keys, int total,
                                         int hasNumber, BIComplexExecutData rowData, DetailChartSetting chartSetting) {
        int tempRow = row;
        tempRow = dealWidthNodeChildren(node, cbcells, row, column,
                rowColumn, sumColumn, keys, total, hasNumber, tempRow, rowData, chartSetting);

        if (chartSetting.showRowTotal() && judgeNode(node, sumColumn)) {
            dealWidthNodeSummary(node, cbcells, row, column,
                    rowColumn, sumColumn, keys, total, hasNumber, tempRow, rowData, chartSetting);
        } else {
            tempRow++;
        }
    }

    /**
     * 处理节点
     *
     * @param node      节点
     * @param cbcells   cell数组
     * @param row       行
     * @param column    列
     * @param rowColumn 行数
     * @param sumColumn 列数
     * @param keys      键值
     * @param total     总数
     * @param hasNumber 序号
     * @param rowData   复杂报表的数据
     */
    public static void dealWithNode(Node node, CBCell[][] cbcells, int row, int column,
                                    BIDimension[] rowColumn,
                                    BITarget[] sumColumn, TargetGettingKey[] keys, int total, int hasNumber,
                                    BIComplexExecutData rowData, DetailChartSetting chartSetting) {

        if (isLastDimension(column, rowColumn)) {
            dealWithNodeNoChild(node, cbcells, row, column,
                    rowColumn, sumColumn, keys, total, hasNumber, chartSetting);
            return;
        }
        dealWithNodeChild(node, cbcells, row, column,
                rowColumn, sumColumn, keys, total, hasNumber, rowData, chartSetting);
    }

    private static boolean isLastDimension(int column, BIDimension[] rowColumn) {
        return column - rowColumn.length == 0;
    }

    public DetailCellIterator createCellIterator4Excel() throws Exception {
        widget.setComplexExpander(new ComplexAllExpander());
        Node tree = getCubeNode();
        if (tree == null) {
            return new DetailCellIterator(0, 0);
        }
        final int rowLength = usedDimensions.length;
        final int summaryLength = usedSumTarget.length;
        int columnLen = rowLength + summaryLength;
        DetailChartSetting chartSetting = widget.getChartSetting();
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int i = 0; i < summaryLength; i++) {
            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(),
                    usedSumTarget[i].getValue());
        }
        int rowLen = chartSetting.showRowTotal() ? tree.getTotalLengthWithSummary() : tree.getTotalLength();
        final boolean useTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(usedDimensions);
        rectangle = new Rectangle(rowLength + widget.isOrder(), 1, columnLen + widget.isOrder() - 1, rowLen);
        final DetailCellIterator iter = new DetailCellIterator(columnLen + widget.isOrder(), rowLen + 1);
        new Thread() {
            public void run() {
                try {
                    final FinalInt start = new FinalInt();
                    int currentRowNum = 0;
                    List<CBCell> cells = generateTitle(useTargetSort, rowLength, summaryLength,
                            widget.isOrder(), widget.getChartSetting());
                    Iterator<CBCell> it = cells.iterator();
                    while (it.hasNext()) {
                        iter.getIteratorByPage(start.value).addCell(it.next());
                    }
                    createCells(iter, start, currentRowNum);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    iter.finish();
                }
            }
        }.start();
        return iter;
    }

    private void createCells(DetailCellIterator iter, FinalInt start, int currentRowNum) throws Exception {
        Node n = getCubeNode();
        while (n.getFirstChild() != null) {
            n = n.getFirstChild();
        }
        BIDimension[] rows = widget.getViewDimensions();
        while (n != null) {
            List rowList = new ArrayList();
            Node temp = n;
            for (TargetGettingKey key : widget.getTargetsKey()) {
                rowList.add(temp.getSummaryValue(key));
            }
            int i = rows.length;
            while (temp.getParent() != null) {
                Object data = temp.getData();
                BIDimension dim = rows[--i];
                Object v = dim.getValueByType(data);
                rowList.add(0, v);
                temp = temp.getParent();
            }
            if (!rowList.isEmpty()) {
                currentRowNum++;
                int newRow = currentRowNum & EXCEL_ROW_MODE_VALUE;
                if (newRow == 0) {
                    iter.getIteratorByPage(start.value).finish();
                    start.value++;
                }
                createCells4Row(iter.getIteratorByPage(start.value), rowList, currentRowNum);
            }
            n = n.getSibling();
        }
    }

    private void createCells4Row(StreamPagedIterator iter, List rowList, int currentRowNum) {
        for (int i = 0; i < rowList.size(); i++) {
            CBCell cell = new CBCell(rowList.get(i));
            cell.setColumn(i);
            cell.setRow(currentRowNum);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            iter.addCell(cell);
        }
    }

    /**
     * 创建cell
     *
     * @return cell数组
     * @throws NoneAccessablePrivilegeException
     */
    @Override
    public CBCell[][] createCellElement() throws Exception {
//        Node tree = getCubeNode();
//        if (tree == null) {
//            return new CBCell[][]{new CBCell[0]};
//        }
//        int rowLength = usedDimensions.length;
//        int summaryLength = usedSumTarget.length;
//        int columnLen = rowLength + summaryLength;
//        DetailChartSetting chartSetting = widget.getChartSetting();
//        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
//        for (int i = 0; i < summaryLength; i++) {
//            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
//        }
//        //导出就全部展开吧
//        int rowLen = chartSetting.showRowTotal() ? tree.getTotalLengthWithSummary() : tree.getTotalLength();
//        //+1是标题
//        CBCell[][] cbcells;
//        boolean useTargetSort = widget.useTargetSort() || BITargetAndDimensionUtils.isTargetSort(usedDimensions);
//
//        if (tree.getChildLength() == 0) {
//            cbcells = new CBCell[columnLen][rowLen + 1];
//            rectangle = new Rectangle(rowLength, 1, columnLen - 1, rowLen);
//            generateTitle(cbcells, useTargetSort, rowLength, summaryLength, 0, widget.getChartSetting());
//        } else {
//            cbcells = new CBCell[columnLen + widget.isOrder()][rowLen + 1];
//            rectangle = new Rectangle(rowLength + widget.isOrder(), 1, columnLen + widget.isOrder() - 1, rowLen);
//            generateTitle(cbcells, useTargetSort, rowLength, summaryLength, widget.isOrder(), widget.getChartSetting());
//        }
//
//        if (ExecutorCommonUtils.isAllPage(paging.getOperator())) {
//            dealWithNode(tree, cbcells, 1, 0, usedDimensions, usedSumTarget, keys,
//                    usedDimensions.length - 1, widget.isOrder(), new BIComplexExecutData(usedDimensions), widget.getChartSetting());
//        } else {
//            dealWithNode(tree, expander.getYExpander(), cbcells, 1, 0, paging.getCurrentPage(), usedDimensions,
//                    usedSumTarget, keys, new ArrayList<String>(), usedDimensions.length - 1, widget.isOrder(),
//                    new BIComplexExecutData(usedDimensions), widget.getChartSetting());
//        }
//
//        if (widget.isOrder() == 1 && tree.getChildLength() != 0) {
//            createNumberCellTitle(cbcells, 0);
//            if (ExecutorCommonUtils.isAllPage(paging.getOperator())) {
//                createAllNumberCellElement(cbcells, 1);
//            } else {
//                createAllNumberCellElement(cbcells, paging.getCurrentPage());
//            }
//        }

//        return cbcells;
        return null;
    }

    /**
     * 生成表头
     *
     * @param useTargetSort
     * @param rowLength
     * @param summaryLength
     */
    private List<CBCell> generateTitle(boolean useTargetSort, int rowLength, int summaryLength, int hasNumber, DetailChartSetting chartSetting) {
        List<CBCell> cells = new LinkedList<CBCell>();
        if (hasNumber == 1) {
            cells.add(createNumberCellTitle(0));
        }
        generateDimensionTitle(cells, useTargetSort, rowLength, hasNumber);
        generateMetricTitle(cells, rowLength, summaryLength, hasNumber, chartSetting);
        return cells;
    }

    private void generateMetricTitle(List<CBCell> cbcells, int rowLength, int summaryLength, int hasNumber,
                                     DetailChartSetting chartSetting) {
        for (int i = 0; i < summaryLength; i++) {
            BIAbstractTargetAndDimension dimension = usedSumTarget[i];
            String dimensionName = dimension.getText();
            String dId = dimension.getValue();
            String levelAndUnit = ExecutorUtils.formatLevelAndUnit(chartSetting.getNumberLevelByTargetId(dId), chartSetting.getUnitByTargetId(dId));
            if (!ComparatorUtils.equals(levelAndUnit, StringUtils.EMPTY)) {
                dimensionName = dimensionName + "(" + levelAndUnit + ")";
            }
            CBCell cell = new CBCell(dimensionName);
            cell.setColumn(rowLength + i + hasNumber);
            cell.setRow(0);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(0));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BITarget rowCol = usedSumTarget[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.TARGETTITLE_Y);
            cbox.setSortTargetName(rowCol.getValue());
            cbox.setSortTargetValue("[]");
            if (widget.getTargetSort() != null && ComparatorUtils.equals(widget.getTargetSort().getName(),
                    usedSumTarget[i].getValue())) {
                cbox.setSortType((Integer) widget.getTargetSort().getObject());
            } else {
                cbox.setSortType(BIReportConstant.SORT.NONE);
            }
            cell.setBoxElement(cbox);
            cbcells.add(cell);
        }
    }

    private void generateDimensionTitle(List<CBCell> cbcells, boolean useTargetSort, int rowLength, int hasNumber) {
        for (int i = 0; i < rowLength; i++) {
            CBCell cell = new CBCell(((BIAbstractTargetAndDimension) usedDimensions[i]).getText());
            cell.setColumn(i + hasNumber);
            cell.setRow(0);
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setStyle(BITableStyle.getInstance().getTitleDimensionCellStyle(0));
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List<CBCell> cellList = new ArrayList<CBCell>();
            cellList.add(cell);
            CBBoxElement cbox = new CBBoxElement(cellList);
            BIDimension rowCol = usedDimensions[i];
            cbox.setName(rowCol.getValue());
            cbox.setType(CellConstant.CBCELL.DIMENSIONTITLE_Y);
            if (!useTargetSort) {
                cbox.setSortType(usedDimensions[i].getSortType());
            } else {
                cbox.setSortType(BIReportConstant.SORT.NONE);
            }
            cell.setBoxElement(cbox);
            cbcells.add(cell);
        }
    }

    private BISummaryTarget[] createTarget4Calculate() {
        ArrayList<BITarget> list = new ArrayList<BITarget>();
        for (int i = 0; i < usedSumTarget.length; i++) {
            list.add(usedSumTarget[i]);
        }
        if (widget.getTargetSort() != null) {
            String name = widget.getTargetSort().getName();
            boolean inUsedSumTarget = false;
            for (int i = 0; i < usedSumTarget.length; i++) {
                if (ComparatorUtils.equals(usedSumTarget[i].getValue(), name)) {
                    inUsedSumTarget = true;
                }
            }
            if (!inUsedSumTarget) {
                for (int i = 0; i < allSumTarget.length; i++) {
                    if (ComparatorUtils.equals(allSumTarget[i].getValue(), name)) {
                        list.add(allSumTarget[i]);
                    }
                }
            }
        }
        Iterator<String> it1 = widget.getTargetFilterMap().keySet().iterator();
        while (it1.hasNext()) {
            String key = it1.next();
            boolean inUsedSumTarget = false;
            for (int i = 0; i < usedSumTarget.length; i++) {
                if (ComparatorUtils.equals(usedSumTarget[i].getValue(), key)) {
                    inUsedSumTarget = true;
                }
            }
            if (!inUsedSumTarget) {
                for (int i = 0; i < allSumTarget.length; i++) {
                    if (ComparatorUtils.equals(allSumTarget[i].getValue(), key)) {
                        list.add(allSumTarget[i]);
                    }
                }
            }

        }
        return list.toArray(new BISummaryTarget[list.size()]);
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.report.summary.BIEngineExecutor#getCubeNode()
     */
    @Override

    public Node getCubeNode() throws Exception {
        if (session == null) {
            return null;
        }
        int rowLength = usedDimensions.length;
        int summaryLength = usedSumTarget.length;
        int columnLen = rowLength + summaryLength;
        if (columnLen == 0) {
            return null;
        }
        long start = System.currentTimeMillis();
        int calpage = paging.getOperator();
        CubeIndexLoader cubeIndexLoader = CubeIndexLoader.getInstance(session.getUserId());
        Node tree = cubeIndexLoader.loadPageGroup(false, widget, createTarget4Calculate(), usedDimensions,
                allDimensions, allSumTarget, calpage, widget.isRealData(), session, expander.getYExpander());
        if (tree == null) {
            tree = new Node();
        }
        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return tree;
    }

    @Override
    public Rectangle getSouthEastRectangle() {
        return rectangle;
    }

}