package com.fr.bi.conf.data.source.operator.create;

import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;

/**
 * Created by 小灰灰 on 2016/11/23.
 */
class ValuesAndGVI{
    Object[] values;
    GroupValueIndex gvi;

    public ValuesAndGVI(Object[] values, GroupValueIndex gvi) {
        this.values = values;
        this.gvi = gvi;
    }

    public int compareTo(ValuesAndGVI o, Comparator[] comparators) {
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