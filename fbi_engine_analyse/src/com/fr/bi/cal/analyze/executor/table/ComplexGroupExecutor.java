package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.executor.detail.DetailCellIterator;
import com.fr.bi.cal.analyze.executor.detail.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.BITargetAndDimensionUtils;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sheldon on 14-9-2.
 */
public class ComplexGroupExecutor extends AbstractComplexNodeExecutor {


    public ComplexGroupExecutor(TableWidget widget, Paging page,
                                ArrayList<ArrayList<String>> rowArray,
                                BISession session, ComplexExpander expander) {

        super(widget, page, session, expander);
        rowData = new BIComplexExecutData(rowArray, widget.getDimensions());
    }

    @Override
    public DetailCellIterator createCellIterator4Excel() throws Exception {
        Map<Integer, Node> nodeMap = getCubeNodes();
        if(nodeMap == null) {
            return new DetailCellIterator(0, 0);
        }

        int collen = rowData.getMaxArrayLength();
        int columnLen = collen + usedSumTarget.length;

        Iterator<Map.Entry<Integer, Node>> iterator = nodeMap.entrySet().iterator();
        final Node[] nodes = new Node[nodeMap.size()];
        Integer[] integers = new Integer[nodeMap.size()];
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Node> entry = iterator.next();
            nodes[i] = entry.getValue();
            integers[i] = entry.getKey();
            i++;
        }

        int rowLen = getNodesTotalLength(nodes);
        final DetailCellIterator iter = new DetailCellIterator(columnLen, rowLen);

        new Thread () {
            public void run() {
                try {
                    FinalInt start = new FinalInt();
                    StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
                    GroupExecutor.generateTitle(widget, rowData.getDimensionArray(0), usedSumTarget, pagedIterator);
                    FinalInt rowIdx =new FinalInt();
                    for(int i = 0, j = nodes.length; i < j; i++) {
                        GroupExecutor.generateCells(nodes[i], widget, rowData.getDimensionArray(i), iter, start, rowIdx);
                    }
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    iter.finish();
                }
            }
        }.start();

        return iter;
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
            count += nodes[i].getTotalLengthWithSummary();
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
            count += nodes[i].getTotalLengthWithSummary(complexExpander.getYExpander(integers[i]));
        }

        return count;
    }

    /**
     * 获取某个nodes
     *
     * @return
     */
    @Override
    public Map<Integer, Node> getCubeNodes() throws Exception{

        long start = System.currentTimeMillis();
        if (getSession() == null) {
            return null;
        }
        int summaryLength = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int i = 0; i < summaryLength; i++) {
            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
        }
        Map<Integer, Node> nodeMap = CubeIndexLoader.getInstance(session.getUserId()).loadComplexPageGroup(false, widget, createTarget4Calculate(), rowData, allDimensions,
                allSumTarget, keys, paging.getOperator(), widget.useRealData(), session, complexExpander, true);
        if (nodeMap.isEmpty()) {
            return null;
        }

        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return nodeMap;
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

    /**
     * 构建cells
     *
     * @return 构建的cells
     */
    @Override
    public CBCell[][] createCellElement() throws Exception {

        return new CBCell[0][0];
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