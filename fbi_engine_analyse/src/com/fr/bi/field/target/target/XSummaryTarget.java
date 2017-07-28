package com.fr.bi.field.target.target;

import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.target.calculator.XCalculator;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/7/6.
 */
public class XSummaryTarget extends BISummaryTarget {
    private BISummaryTarget target;
    private GroupValueIndex[] filterIndex;
    private GroupValueIndex rootIndex;

    public XSummaryTarget(BISummaryTarget target, GroupValueIndex rootIndex, GroupValueIndex[] filterIndex) {
        this.target = target;
        this.filterIndex = filterIndex;
        this.rootIndex = rootIndex;
    }

    @Override
    public String getName() {
        return target.getName();
    }

    @Override
    public String getValue() {
        return target.getValue();
    }

    @Override
    public TargetCalculator createSummaryCalculator() {
        return new XCalculator(target.createSummaryCalculator(), filterIndex);
    }

    public GroupValueIndex getRootIndex() {
        return rootIndex;
    }

    @Override
    public int getTargetType() {
        return target.getTargetType();
    }

    public void setTargetMap(Map<String, BITarget> targetMap) {
        target.setTargetMap(targetMap);
    }

    public Collection<String> getCalculateUseTargetIDs(){
        return target.getCalculateUseTargetIDs();
    }


    public TargetType getType(){
        return target.getType();
    }

    public SumType getSumType(){
        return target.getSumType();
    }

    @Override
    public boolean calculateAllNode() {
        return target.calculateAllNode();
    }

    @Override
    public boolean calculateSingleNode(BITarget[] usedTargets) {
        return target.calculateSingleNode(usedTargets);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        XSummaryTarget that = (XSummaryTarget) o;

        if (target != null ? !ComparatorUtils.equals(target, that.target) : that.target != null) {
            return false;
        }
        return Arrays.equals(filterIndex, that.filterIndex);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(filterIndex);
        return result;
    }
}
