package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.iterator.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.general.DateUtils;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sheldon on 14-9-2.
 */
public class ComplexGroupExecutor extends AbstractTableWidgetExecutor {
    protected BIComplexExecutData rowData;
    protected ComplexExpander complexExpander;

    public ComplexGroupExecutor(TableWidget widget, Paging page,
                                ArrayList<ArrayList<String>> rowArray,
                                BISession session, ComplexExpander expander) {

        super(widget, page, session);
        rowData = new BIComplexExecutData(rowArray, widget.getDimensions());
        this.complexExpander = expander;
    }

    @Override
    public TableCellIterator createCellIterator4Excel() throws Exception {
        Map<Integer, Node> nodeMap = getCubeNodes();
        if(nodeMap == null) {
            return new TableCellIterator(0, 0);
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
        final TableCellIterator iter = new TableCellIterator(columnLen, rowLen);

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

    @Override
    public Object getCubeNode() throws Exception {
        return null;
    }

    /**
     * 获取nodes的个数
     *
     * @param nodes
     * @return
     */
    public int getNodesTotalLength(Node[] nodes) {
        int count = 0;

        for (int i = 0; i < nodes.length; i++) {
            count += nodes[i].getTotalLengthWithSummary();
        }

        return count;
    }

    /**
     * 获取某个nodes
     *
     * @return
     */
    public Map<Integer, Node> getCubeNodes() throws Exception{

        long start = System.currentTimeMillis();
        if (getSession() == null) {
            return null;
        }
        int summaryLength = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int i = 0; i < summaryLength; i++) {
            keys[i] =usedSumTarget[i].createTargetGettingKey();
        }
        Map<Integer, Node> nodeMap = CubeIndexLoader.getInstance(session.getUserId()).loadComplexPageGroup(false, widget, createTarget4Calculate(), rowData, allDimensions,
                allSumTarget, keys, paging.getOperator(), widget.useRealData(), session, complexExpander, true);
        if (nodeMap.isEmpty()) {
            return null;
        }

        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return nodeMap;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        Iterator<Map.Entry<Integer, Node>> it = getCubeNodes().entrySet().iterator();
        JSONObject jo = new JSONObject();
        while (it.hasNext()){
            Map.Entry<Integer, Node> entry = it.next();
            jo.put(String.valueOf(entry.getKey()), entry.getValue().toJSONObject(rowData.getDimensionArray(entry.getKey()), widget.getTargetsKey(), -1));
        }
        return jo;
    }
}