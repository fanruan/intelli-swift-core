package com.fr.bi.stable.report.key;


import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.report.result.SummaryContainer;
import com.fr.bi.stable.report.result.TargetCalculator;

public class SummaryCalculator implements java.util.concurrent.Callable<Object> {
    protected ICubeTableService cr;
    protected TargetCalculator calculator;
    protected volatile SummaryContainer node;
    private TargetGettingKey targetGettingKey;

    public SummaryCalculator(ICubeTableService cr, TargetCalculator calculator, SummaryContainer node) {
        this.cr = cr;
        this.calculator = calculator;
        this.node = node;
    }

    public void setTargetGettingKey(TargetGettingKey targetGettingKey) {
        this.targetGettingKey = targetGettingKey;
    }

    @Override
    public Object call() {
        if (targetGettingKey != null) {
            calculator.doCalculator(cr, node, targetGettingKey);
        } else {
            calculator.doCalculator(cr, node);
        }
        Number result = node.getSummaryValue(calculator.createTargetGettingKey());
        release();
        return result;

    }

    public void release() {
        cr = null;
        node = null;
        calculator = null;
    }

}