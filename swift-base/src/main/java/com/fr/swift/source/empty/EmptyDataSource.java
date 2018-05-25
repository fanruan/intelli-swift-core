package com.fr.swift.source.empty;

import com.fr.swift.config.conf.bean.SwiftMetaDataBean;
import com.fr.swift.source.AbstractDataSource;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;

/**
 * This class created on 2018/4/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class EmptyDataSource extends AbstractDataSource {

    @Override
    protected void initMetaData() {
        metaData = new SwiftMetaDataBean("empty", new ArrayList<SwiftMetaDataColumn>());
    }
}
