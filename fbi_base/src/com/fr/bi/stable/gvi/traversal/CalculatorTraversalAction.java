package com.fr.bi.stable.gvi.traversal;


public abstract class CalculatorTraversalAction implements SingleRowTraversalAction {
    protected double sum;

    public CalculatorTraversalAction() {
        setSum(0);
    }

    public abstract double getCalculatorValue();

    public void setSum(double sum) {
        this.sum = sum;
    }

}