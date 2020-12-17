package com.fr.swift.analyse;

import com.fr.swift.source.Row;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/16
 */
public class CalcPage {
    List<Row> rows;

    public CalcPage(int fetchSize) {
        this.rows = new ArrayList<>(fetchSize);
    }

    public List<Row> getRows() {
        return rows;
    }

    public void addRow(Row row) {
        rows.add(row);
    }
}
