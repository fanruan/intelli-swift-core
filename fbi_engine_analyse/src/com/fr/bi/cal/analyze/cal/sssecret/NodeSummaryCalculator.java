package com.fr.bi.cal.analyze.cal.sssecret;import com.fr.base.FRContext;import com.fr.bi.cal.analyze.cal.result.Node;import com.finebi.cube.api.ICubeDataLoader;import com.finebi.cube.api.ICubeTableService;import com.fr.bi.stable.report.key.SummaryCalculator;import com.fr.bi.stable.report.result.TargetCalculator;import com.finebi.cube.common.log.BILoggerFactory;import java.util.concurrent.ExecutionException;/** * Created by Hiram on 2015/1/5. */class NodeSummaryCalculator {    private final ICubeDataLoader loader;    public NodeSummaryCalculator(ICubeDataLoader loader) {        this.loader = loader;    }    public Number getNodeSummary(Node node, TargetCalculator key) {        try {            if (key == null) {                return null;            }            summaryIfAbsent(node, key);            return node.getSummaryValue(key.createTargetGettingKey());        } catch (Exception e) {            FRContext.getLogger().error(e.getMessage(), e);        }        return null;    }    public void summaryIfAbsent(Node node, TargetCalculator key) {        if (key == null) {            return;        }        if (node.getSummaryValue(key) == null) {            try {                summary(node, key);            } catch (InterruptedException e) {                BILoggerFactory.getLogger().error(e.getMessage(), e);            } catch (ExecutionException e) {                BILoggerFactory.getLogger().error(e.getMessage(), e);            }        }    }    private Number summary(Node node, TargetCalculator key) throws InterruptedException, ExecutionException {        ICubeTableService summaryIndex = loader.getTableIndex(key.createTableKey().getTableSource());        key.calculateFilterIndex(loader);        SummaryCalculator sc = key.createSummaryCalculator(summaryIndex, node);        return (Number) sc.call();    }}