package com.fr.swift.result.node.cal;

import com.fr.swift.query.group.by.paging.MapperIterator;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.iterator.GroupNodeIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetCalculatorUtils {

    public static GroupNode calculate(GroupNode root, List<TargetCalculatorInfo> infoList,
                                      final List<Integer> indexesOfTargetsForShow) throws Exception {
        List<TargetCalculator> calculators = new ArrayList<TargetCalculator>();
        for (TargetCalculatorInfo info : infoList) {
            calculators.add(TargetCalculatorFactory.create(info, root));
        }
        for (TargetCalculator calculator : calculators) {
            calculator.call();
        }
        if (infoList.size() == 0) {
            return root;
        }
        // 从计算结果中提取要展示的结果集
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(new GroupNodeIterator(root), new Function<GroupNode, GroupNode>() {
            @Override
            public GroupNode apply(GroupNode p) {
                Number[] showValues = new Number[indexesOfTargetsForShow.size()];
                Number[] allValues = p.getSummaryValue();
                for (int i = 0; i < showValues.length; i++) {
                    showValues[i] = allValues[indexesOfTargetsForShow.get(i)];
                }
                p.setSummaryValue(showValues);
                return p;
            }
        });
        while (iterator.hasNext()) {
            iterator.next();
        }
        return root;
    }
}
