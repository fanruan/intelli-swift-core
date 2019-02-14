package com.fr.swift.source.etl.datamining;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterUtils {

    public static List<Object> convertSwiftData(List<Object> row){
        List<Object> convert = new ArrayList<Object>();
        for (Object o : row) {
            if (o instanceof Date) {
                convert.add(((Date) o).getTime());
            } else if (o instanceof Integer) {
                convert.add(((Integer) o).longValue());
            } else {
                convert.add(o);
            }
        }
        return convert;
    }

}
