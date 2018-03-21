package com.fr.swift.source.etl.datamining.timeseries;

import java.util.ArrayList;
import java.util.List;

public class MultiFieldKey {
    private List<Object> fields = new ArrayList<Object>();

    public MultiFieldKey(){
    }

    public MultiFieldKey(List<Object> fields){
        this.fields = fields;
    }

    public List<Object> getFields(){
        return this.fields;
    }

    public void addFieldValue(Object fieldValue){
        this.fields.add(fieldValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiFieldKey that = (MultiFieldKey) o;

        return fields != null ? fields.equals(that.fields) : that.fields == null;
    }

    @Override
    public int hashCode() {
        return fields != null ? fields.hashCode() : 0;
    }
}
