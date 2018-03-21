package com.fr.swift.source.etl.datamining.timeseries;

import java.util.List;

public class MultiFieldValueItem{
    private long datestamp;

    public long getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(long datestamp) {
        this.datestamp = datestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    private double value;

    public MultiFieldValueItem(long datestamp, double value){
        this.datestamp = datestamp;
        this.value = value;
    }
}
