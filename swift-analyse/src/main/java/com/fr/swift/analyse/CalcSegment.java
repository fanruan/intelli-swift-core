package com.fr.swift.analyse;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/15
 */
public interface CalcSegment extends AutoCloseable {

    int rowCount();

    Row getRow();

    Row getRow(int curs);

    SwiftMetaData getMetaData();
}
