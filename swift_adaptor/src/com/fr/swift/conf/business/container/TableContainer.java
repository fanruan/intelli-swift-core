package com.fr.swift.conf.business.container;

import com.finebi.conf.structure.bean.table.FineBusinessTable;

import java.util.ArrayList;

/**
 * This class created on 2018-1-29 11:30:21
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TableContainer extends AbstractResourceContainer<FineBusinessTable> {


    private TableContainer() {
        super.resourceList = new ArrayList<FineBusinessTable>();
    }

    private static class SingletonHolder {
        private static final TableContainer INSTANCE = new TableContainer();
    }

    public static final TableContainer getContainer() {
        return TableContainer.SingletonHolder.INSTANCE;
    }


}
