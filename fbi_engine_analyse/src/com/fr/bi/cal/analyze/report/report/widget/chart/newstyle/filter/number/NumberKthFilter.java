package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.number;

import com.fr.bi.cal.analyze.report.report.widget.chart.newstyle.filter.IFilter;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;

/**
 * Created by AstronautOO7 on 2016/9/19.
 */
public class NumberKthFilter implements IFilter{

    private int kNumber;

    public NumberKthFilter(int number) {
        this.kNumber = number;
    }

    private double getNumberKth(double[] array, int k) throws JSONException{
        if(k > array.length){
            return Double.NaN;
        }
        int low = 0;
        int high = array.length - 1;
        while (true){
            int pos = partition(array, low, high);
            if(pos == k - 1){
                return array[pos];
            }
            if(pos < k - 1){
                low = pos + 1;
            }
            if(pos > k - 1){
                high = pos - 1;
            }
        }
    }

    private int partition(double[] array, int i, int j) throws JSONException{
        double tmp = array[i];
        while (i < j){
            while (i < j && array[j] <= tmp){
                j--;
            }
            if(i < j){
                array[i++] = array[j];
            }
            while (i < j && array[i] >= tmp){
                i++;
            }
            if(i < j){
                array[j--] = array[i];
            }
            array[i] = tmp;
        }
        return i;
    }

    @Override
    public JSONArray getFilterResult(JSONArray array) throws JSONException {
        if(this.kNumber == 0){
            return new JSONArray();
        }
        double[] tmp = new double[array.length()];
        for(int i = 0; i < array.length(); i++){
            tmp[i] = array.getDouble(i);
        }
        double kthValue = this.getNumberKth(tmp, this.kNumber);
        JSONArray result = new JSONArray();
        for(int i = 0; i < array.length(); i++){
            if(kthValue == array.getDouble(i)){
                result.put(array.getDouble(i));
            }
        }
        return result;
    }
}
