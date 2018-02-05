package com.fr.swift.bitmap.traversal;

public abstract class CalculatorTraversalAction implements TraversalAction{
    protected double sum;

    public CalculatorTraversalAction() {

    }

    public abstract double getCalculatorValue();

//    public abstract int getCalculatorCount();
}
