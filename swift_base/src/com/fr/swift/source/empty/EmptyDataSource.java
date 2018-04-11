package com.fr.swift.source.empty;

import com.fr.swift.source.AbstractDataSource;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;

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
        metaData = new SwiftMetaDataImpl("empty", new ArrayList<SwiftMetaDataColumn>());
    }
}
