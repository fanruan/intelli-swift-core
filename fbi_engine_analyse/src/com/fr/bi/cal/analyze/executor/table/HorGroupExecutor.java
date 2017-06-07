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
import com.fr.bi.stable.report.key.TargetGettingKey;
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
        int colDimIdx = 0;
        while (colDimIdx < colDimension.length) {
            CBCell cell = ExecutorUtils.createCell(colDimension[colDimIdx].getText(), colDimIdx, 1, widget.isOrder(), 1, style);
            pagedIterator.addCell(cell);
            node = node.getFirstChild();
            Node temp = node;
            int columnIdx = widget.isOrder() + 1;
            BIDimension dim = colDimension[colDimIdx];
            while (temp != null) {
                String v = dim.toString(temp.getData());
                if (dim.getGroup().getType() == BIReportConstant.GROUP.YMD && GeneralUtils.string2Number(v) != null) {
                    v = DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(v).longValue()));
                }
                CBCell dimCell = ExecutorUtils.createCell(v, colDimIdx, 1, columnIdx, temp.getTotalLength(), style);
                pagedIterator.addCell(dimCell);
                columnIdx += temp.getTotalLength();
                temp = temp.getSibling();
            }
            colDimIdx++;
        }
    }

    private void generateCells(Node node, TableWidget widget, BIDimension[] colDimension, BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator) {
        int rowIdx = colDimension.length;
        TargetGettingKey[] keys = widget.getTargetsKey();
        while (node.getFirstChild() != null) {
            node = node.getFirstChild();
        }

        for (int i = 0; i < usedSumTarget.length; i++) {
            int columnIdx = widget.isOrder() + 1;
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
                CBCell cell = ExecutorUtils.createCell(data, rowIdx + i, 1, columnIdx++, 1, style);
                pagedIterator.addCell(cell);
                temp = temp.getSibling();
            }
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