package com.fr.swift.adaptor.struct;

import com.finebi.conf.structure.result.BIDetailCell;

/**
 * This class created on 2018-1-2 13:40:35
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftDetailCell implements BIDetailCell {

    private Object data;

    public SwiftDetailCell(Object data) {
        this.data = data;
    }

    @Override
    public Object getData() {
        return data;
    }
}
