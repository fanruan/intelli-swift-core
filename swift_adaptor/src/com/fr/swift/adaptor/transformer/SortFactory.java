package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.ConfConstant;
import com.fr.swift.query.sort.AscSort;
import com.fr.swift.query.sort.DescSort;
import com.fr.swift.query.sort.Sort;

/**
 * Created by pony on 2017/12/21.
 */
public class SortFactory {
    public static Sort transformSort(int sortType) {
        switch (sortType) {
            case ConfConstant.SortType.ASC:
                return new AscSort(0);
            default:
                return new DescSort(0);
        }
    }
}
