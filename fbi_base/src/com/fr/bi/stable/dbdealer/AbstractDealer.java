package com.fr.bi.stable.dbdealer;


public abstract class AbstractDealer<T> implements DBDealer<T> {
    protected int rsColumn;

    AbstractDealer(int rsColumn) {
        this.rsColumn = rsColumn;
    }

}