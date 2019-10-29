package com.fr.swift.source.split;

import java.util.Map;

/**
 * @author lucifer
 * @date 2019/7/26
 * @description
 * @since swift 1.1
 */
public interface SubRow {

    void combineSplitRow(SubRow subRow);

    Map<String, Object> getSubRow();

}
