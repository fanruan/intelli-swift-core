package com.fr.swift.segment.operator;

import com.fr.swift.source.Row;

import java.util.List;

/**
 * This class created on 2018/3/23
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface Updater {

    boolean updateData(List<Row> rowList) throws Exception;
}
