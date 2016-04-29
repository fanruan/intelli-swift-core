package com.fr.bi.cal.analyze.cal.result.operator;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractOperator implements Operator {

    private int counter = 0;

    private int maxRow = 20;
    private ExecutorService executorService;

    AbstractOperator(int maxRow) {
        this.maxRow = maxRow;
    }

    @Override
    public void setStartCount(int c) {
        counter = c;
    }

    @Override
    public int getCount() {
        return counter;
    }

    @Override
    public void addRow() {
        counter++;
    }

    @Override
    public boolean isPageEnd() {
        return counter >= maxRow;
    }

    @Override
    public ExecutorService get() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(4);
        }
        return executorService;
    }

}