package com.fr.swift.result.node.cal;

import com.fr.swift.query.info.element.target.GroupTarget;
import com.fr.swift.result.GroupNode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetCalculatorUtils {

    /**
     * 对node进行指标的配置类计算，并从中间计算结果指中取出最终要显示的指标
     *
     * @param root 根节点
     *  infoList 用于配置类计算的属性
     *  targetsForShowList 最终要返回的一组指标的位置&顺序属性
     * @return 返回处理完计算指标，并去除配置计算产生的中间结果指标的node根节点
     * @throws SQLException
     */
    public static GroupNode calculate(GroupNode root, List<Map<Integer, Object>> dic, List<GroupTarget> groupTargets) throws SQLException {
        if (groupTargets.size() == 0) {
            return root;
        }
        List<TargetCalculator> calculators = new ArrayList<TargetCalculator>();
        for (int i = 0; i < groupTargets.size(); i++) {
            calculators.add(TargetCalculatorFactory.create(groupTargets.get(i), root, dic));
        }
        for (TargetCalculator calculator : calculators) {
            try {
                calculator.call();
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }
        return root;
    }
}
