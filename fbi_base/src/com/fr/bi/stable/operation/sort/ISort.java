package com.fr.bi.stable.operation.sort;

import com.fr.bi.stable.operation.sort.comp.IComparator;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.json.JSONParser;

/**
 * Created by GUY on 2015/2/12.
 */
public interface ISort extends JSONParser {

    int getSortType();

    void setSortType(int sortType);

    IComparator getComparator();

    ICubeColumnIndexReader createGroupedMap(ICubeColumnIndexReader baseMap);
}