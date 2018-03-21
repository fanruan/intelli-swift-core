package com.fr.swift.source.etl.datamining.timeseries;

public class MultiFieldValueItem{
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    private double value;

    public MultiFieldValueItem(long timestamp, double value){
        this.timestamp = timestamp;
        this.value = value;
    }
}
