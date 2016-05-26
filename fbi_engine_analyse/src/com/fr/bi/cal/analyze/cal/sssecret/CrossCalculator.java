/**
 *
 */
package com.fr.bi.cal.analyze.cal.sssecret;

import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.cal.thread.EvaluateSummaryValuePool;
import com.fr.bi.stable.utils.code.BILogger;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.key.SummaryCalculator;
import com.fr.bi.stable.report.result.TargetCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author Daniel
 *         计算交叉表用的
 */
public class CrossCalculator {

    private ICubeDataLoader loader;

//    private Map<Integer, NewCrossRootKeyMap> pageMap = new ConcurrentHashMap<Integer, NewCrossRootKeyMap>(2);

    public CrossCalculator(ICubeDataLoader loader) {
        this.loader = loader;
    }


    public void execute(NewCrossRoot root, List calculators, CrossExpander expander) {


        ExecutorThread t = new ExecutorThread();
        t.calculators = calculators;
        t.root = root;
        t.expander = expander;
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }


    private class ExecutorThread extends Thread {


        private List<TargetCalculator> calculators;

        private NewCrossRoot root;

        private CrossExpander expander;


        @Override
        public void run() {

            if (calculators != null) {
                TargetCalculator[] array = calculators.toArray(new TargetCalculator[calculators.size()]);
                CrossNode baseNode = new CrossNode4Calculate(this.top(), this.left(), array);
                this.left().setValue(baseNode);
                this.top().setValue(baseNode);
                top().dealWithChildTop4Calculate(left(), baseNode, array);
                left().dealWithChildLeft4Calculate(top(), baseNode, array);
                left().buildLeftRelation(top());
            } else {
                CrossNode baseNode = new CrossNode(this.top(), this.left());
                this.left().setValue(baseNode);
                this.top().setValue(baseNode);
                top().dealWithChildTop(left(), baseNode);
                left().dealWithChildLeft(top(), baseNode);
                left().buildLeftRelation(top());
            }
            List<SummaryCalculator> threadList = new ArrayList<SummaryCalculator>();
            if (calculators == null) {
                return;
            }
            createSumIndex(left(), threadList, expander.getYExpander(), expander.getYExpander(), expander.getXExpander());
        }

        private void createSumIndex(CrossHeader left, List threadList, NodeExpander yp, NodeExpander y, NodeExpander x) {
            if (yp != null) {
                dealWithCrossNode(left.getValue(), threadList, x, x);
                for (int i = 0; i < left.getChildLength(); i++) {
                    createSumIndex((CrossHeader) left.getChild(i), threadList, y, y == null ? null : y.getChildExpander((left.getChild(i)).getShowValue()), x);
                }
            }
        }

        private void dealWithCrossNode(CrossNode node, List threadList, NodeExpander xp, NodeExpander x) {
            if (xp != null) {
                for (int i = 0; i < calculators.size(); i++) {
                    TargetCalculator calculator = calculators.get(i);
                    calculator.calculateFilterIndex(loader);
//	                if (node.getSummaryValue(key) == null) {
                    //TODO 改成多线程
                    SummaryCalculator sc = calculator.createSummaryCalculator(loader.getTableIndex(calculator.createTableKey().getTableSource()), node);
                    sc.setTargetGettingKey(calculator.createTargetGettingKey());
                    Future futureValue = EvaluateSummaryValuePool.evaluateValue(sc);
                    try {
                        node.setSummaryValue(calculator.createTargetGettingKey(), futureValue.get());
                    } catch (Exception ex) {

                    }

//	                }
                }
                for (int i = 0; i < node.getTopChildLength(); i++) {
                    dealWithCrossNode(node.getTopChild(i), threadList, x, x == null ? null : x.getChildExpander(node.getTopChild(i).getHead().getShowValue()));
                }
            }
        }

        private CrossHeader left() {
            return root.getLeft();
        }

        private CrossHeader top() {
            return root.getTop();
        }

    }

    private class NewCrossRootKeyMap {
        private NewCrossRoot root;
        private Map<TargetCalculator, Integer> calculatedKeys = new ConcurrentHashMap<TargetCalculator, Integer>(2);

        private NewCrossRootKeyMap(NewCrossRoot root) {
            this.root = root;
        }
    }

}