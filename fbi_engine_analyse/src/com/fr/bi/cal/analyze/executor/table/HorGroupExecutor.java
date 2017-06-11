package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.iterator.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.utils.ExecutorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.general.DateUtils;
import com.fr.general.GeneralUtils;
import com.fr.general.Inter;
import com.fr.json.JSONObject;

import java.awt.*;
import java.util.Date;

public class HorGroupExecutor extends AbstractTableWidgetExecutor<Node> {
    private Rectangle rectangle;
    private BIDimension[] colDimension;
    private BIDimension[] usedDimensions;
    private CrossExpander expander;

    public HorGroupExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {
        super(widget, paging, session);
        usedDimensions = widget.getViewTopDimensions();
        colDimension = usedDimensions;
        this.expander = expander;
    }

    public TableCellIterator createCellIterator4Excel() throws Exception {
        final Node node = getCubeNode();
        int rowLength = colDimension.length + usedSumTarget.length;
        int columnLength = node.getTotalLength() + widget.isOrder() + 1;
        //显示不显示汇总行
        int rowLen = widget.getChartSetting().showRowTotal() ? node.getTotalLengthWithSummary() : node.getTotalLength();
        rectangle = new Rectangle(rowLength + widget.isOrder(), 1, columnLength + widget.isOrder() - 1, rowLen);
        final TableCellIterator iter = new TableCellIterator(columnLength, rowLength);
        new Thread() {
            public void run() {
                try {
                    FinalInt start = new FinalInt();
                    StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
                    generateTitle(node, widget, colDimension, pagedIterator);
                    generateCells(node, widget, colDimension, usedSumTarget, pagedIterator);
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    iter.finish();
                }
            }
        }.start();
        return iter;
    }

    private void generateTitle(Node node, TableWidget widget, BIDimension[] colDimension, StreamPagedIterator pagedIterator) {
        Style style = BITableStyle.getInstance().getTitleDimensionCellStyle(0);
        if (widget.isOrder() == 1) {
            CBCell cell = ExecutorUtils.createCell(Inter.getLocText("BI-Number_Index"), 0, colDimension.length, 0, 1, style);
            pagedIterator.addCell(cell);
        }
        int rowIdx = 0;
        while (rowIdx < colDimension.length) {
            CBCell cell = ExecutorUtils.createCell(colDimension[rowIdx].getText(), rowIdx, 1, widget.isOrder(), 1, style);
            pagedIterator.addCell(cell);
            node = node.getFirstChild();
            Node temp = node;
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = widget.isOrder() + 1;
            BIDimension dim = colDimension[rowIdx];
            while (temp != null) {
                String v = dim.toString(temp.getData());
                if (dim.getGroup().getType() == BIReportConstant.GROUP.YMD && GeneralUtils.string2Number(v) != null) {
                    v = DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(v).longValue()));
                }
                CBCell dimCell = ExecutorUtils.createCell(v, rowIdx, 1, columnIdx.value, temp.getTotalLengthWithSummary(), style);
                pagedIterator.addCell(dimCell);
                columnIdx.value += temp.getTotalLengthWithSummary();
                generateTitleSumCells(temp, pagedIterator, rowIdx, columnIdx);
                temp = temp.getSibling();
            }
            rowIdx++;
        }
    }

    private void generateTitleSumCells(Node temp, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx) {
        if (checkIfGenerateSumCell(temp) && temp.getParent().getChildLength() != 1) {
            Style style = BITableStyle.getInstance().getYSumStringCellStyle();
            CBCell cell = ExecutorUtils.createCell(Inter.getLocText("BI-Summary_Values"), rowIdx, temp.getDeep(), columnIdx.value, 1, style);
            pagedIterator.addCell(cell);
        }
        adjustColumnIdx(temp, columnIdx);
    }

    private void adjustColumnIdx(Node temp, FinalInt columnIdx) {
        if (checkIfGenerateSumCell(temp)) {
            if(temp.getParent().getChildLength() != 1){
                columnIdx.value++;
            }
            if (temp.getParent() != null) {
                adjustColumnIdx(temp.getParent(), columnIdx);
            }
        }
    }

    private void generateCells(Node node, TableWidget widget, BIDimension[] colDimension, BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator) {
        int rowIdx = colDimension.length;
        TargetGettingKey[] keys = widget.getTargetsKey();
        while (node.getFirstChild() != null) {
            node = node.getFirstChild();
        }

        for (int i = 0; i < usedSumTarget.length; i++) {
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = widget.isOrder() + 1;
            Style headStyle = BITableStyle.getInstance().getDimensionCellStyle(false, (i + 1) % 2 == 1);
            if (widget.isOrder() == 1) {
                CBCell orderCell = ExecutorUtils.createCell(i + 1, rowIdx + i, 1, 0, 1, headStyle);
                pagedIterator.addCell(orderCell);
            }
            Object targetName = usedSumTarget[i].getText();
            CBCell targetNameCell = ExecutorUtils.createCell(targetName, rowIdx + i, 1, widget.isOrder(), 1, headStyle);
            pagedIterator.addCell(targetNameCell);
            Node temp = node;
            while (temp != null) {
                Object data = temp.getSummaryValue(keys[i]);
                boolean isPercent = widget.getChartSetting().getNumberLevelByTargetId(keys[i].getTargetName()) == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT;
                Style style = BITableStyle.getInstance().getNumberCellStyle(data, (i + 1) % 2 == 1, isPercent);
                CBCell cell = ExecutorUtils.createCell(data, rowIdx + i, 1, columnIdx.value++, 1, style);
                pagedIterator.addCell(cell);
                generateTargetSumCell(temp, keys[i], pagedIterator, rowIdx + i, columnIdx, style);
                temp = temp.getSibling();
            }
        }
    }

    private void generateTargetSumCell(Node temp, TargetGettingKey key, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx, Style style) {
        if ((widget.getViewTargets().length != 0) && checkIfGenerateSumCell(temp)) {
            if (temp.getParent().getChildLength() != 1) {
                Object data = temp.getParent().getSummaryValue(key);
                CBCell cell = ExecutorUtils.createCell(data, rowIdx, 1, columnIdx.value++, 1, style);
                pagedIterator.addCell(cell);
            }
            Node parent = temp.getParent();
            generateTargetSumCell(parent, key, pagedIterator, rowIdx, columnIdx, style);
        }
    }

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
        Node tree = cubeIndexLoader.loadPageGroup(true, widget, createTarget4Calculate(), usedDimensions, allDimensions, allSumTarget, calpage, widget.isRealData(), session, expander.getXExpander());
        if (tree == null) {
            tree = new Node(null, allSumTarget.length);
        }
        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return tree;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        return getCubeNode().toJSONObject(usedDimensions, widget.getTargetsKey(), -1);
    }

    @Override
    public Rectangle getSouthEastRectangle() {
        return rectangle;
    }

}