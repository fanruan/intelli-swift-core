package com.fr.bi.stable.operation.sort.comp;

import com.fr.bi.stable.constant.BIReportConstant;

import java.util.Comparator;

/**
 * Created by GUY on 2015/4/9.
 */
public class ComparatorFacotry {

    public final static IComparator<String> CHINESE_ASC = new ChinesePinyinComparator();

    public final static IComparator<String> CHINESE_DESC = new D_ChinesePinyinComparator();

    public final static IComparator<Double> DOUBLE_ASC = new ASCComparableComparator<Double>();

    public final static IComparator<Double> DOUBLE_DESC = new DESCComparableComparator<Double>();

    public final static IComparator<Long> LONG_ASC = new ASCComparableComparator<Long>();

    public final static IComparator<Long> LONG_DESC = new DESCComparableComparator<Long>();

    public final static IComparator<Integer> INTEGER_ASC = new ASCComparableComparator<Integer>();

    public final static IComparator<Integer> INTEGER_DESC = new DESCComparableComparator<Integer>();


    public static IComparator getComparator(int sortType) {
        switch (sortType) {
            case BIReportConstant.SORT.ASC:
                return CHINESE_ASC;
            case BIReportConstant.SORT.DESC:
                return CHINESE_DESC;
            case BIReportConstant.SORT.CUSTOM:
                return new CustomComparator();
            case BIReportConstant.SORT.NUMBER_ASC :
                return LONG_ASC;
            case BIReportConstant.SORT.NUMBER_DESC :
                return LONG_DESC;
        }
        return new ChinesePinyinComparator();
    }
    
    public static <T> IComparator<T> createReverseComparator(Comparator<T> c){
		return new ReverseComparator<T>(c);
    }

    public static IComparator createASCComparator() {
        return new ASCComparator();
    }

    public static IComparator createDSCComparator() {
        return new DSCComparator();
    }
    
}