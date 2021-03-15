package com.fr.swift.cloud.bitmap.traversal;

public abstract class CalculatorTraversalAction implements TraversalAction {
    protected double result;

    public CalculatorTraversalAction() {

    }

    public abstract double getCalculatorValue();

//    public abstract int getCalculatorCount();
}
