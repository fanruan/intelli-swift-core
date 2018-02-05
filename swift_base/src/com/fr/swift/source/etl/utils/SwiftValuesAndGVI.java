package com.fr.swift.source.etl.utils;

import com.fr.swift.bitmap.ImmutableBitMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Handsome on 2017/12/6 0006 14:20
 */
public class SwiftValuesAndGVI {

    private Object[] values;
    private ImmutableBitMap gvi;
    private List<AggregatorValueCollection> aggreator = new ArrayList<AggregatorValueCollection>();

    public ImmutableBitMap getGvi() {
        return gvi;
    }

    public void setGvi(ImmutableBitMap gvi) {
        this.gvi = gvi;
    }



    public List<AggregatorValueCollection> getAggreator() {
        return aggreator;
    }

    public void setAggreator(List<AggregatorValueCollection> aggreator) {
        this.aggreator = aggreator;
    }



    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }


    public SwiftValuesAndGVI(Object[] values, ImmutableBitMap gvi) {
        this.values = values;
        this.gvi = gvi;
    }

    public int compareTo(SwiftValuesAndGVI o, Comparator[] comparators) {
        if (o == null){
            return -1;
        }
        for (int i = 0; i < values.length; i++){
            int result = comparators[i].compare(values[i], o.values[i]);
            if (result != 0){
                return result;
            }
        }
        return 0;
    }

}
