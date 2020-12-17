package com.fr.swift.analyse;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.Segment;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/16
 */
public class FilteredCalcSeg {

    Segment segment;
    ImmutableBitMap filteredIndex;


    List<Pair<Segment, ImmutableBitMap>> filteredList = new ArrayList<>();

}
