package com.fr.swift.conf.business.table2source;

import com.fr.swift.conf.business.container.AbstractResourceContainer;

import java.util.ArrayList;

/**
 * @author yee
 * @date 2018/3/23
 */
public class TableToSourceContainer extends AbstractResourceContainer<TableToSource> {
    private TableToSourceContainer() {
        super.resourceList = new ArrayList<TableToSource>();
    }

    private static class SingletonHolder {
        private static final TableToSourceContainer INSTANCE = new TableToSourceContainer();
    }

    public static final TableToSourceContainer getContainer() {
        return TableToSourceContainer.SingletonHolder.INSTANCE;
    }
}
