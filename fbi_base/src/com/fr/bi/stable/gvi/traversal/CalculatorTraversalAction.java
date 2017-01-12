package com.fr.bi.stable.gvi.traversal;


public abstract class CalculatorTraversalAction implements SingleRowTraversalAction {
    protected double sum = Double.NaN;

    public CalculatorTraversalAction() {
    }

    public abstract double getCalculatorValue();
}