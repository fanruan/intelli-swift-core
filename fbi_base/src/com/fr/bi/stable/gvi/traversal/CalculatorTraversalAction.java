package com.fr.bi.stable.gvi.traversal;


public abstract class CalculatorTraversalAction implements SingleRowTraversalAction {
    protected double sum;

    public CalculatorTraversalAction() {
    }

    public abstract double getCalculatorValue();
}