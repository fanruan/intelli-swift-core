package com.fr.swift.segment.operator.collate;

import com.fr.swift.SwiftContext;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.operator.Collater;
import com.fr.swift.segment.operator.Importer;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/7/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractCollater implements Collater {

    protected DataSource dataSource;
    private SwiftSourceAlloter alloter;
    private List<SegmentKey> segmentKeys;

    public AbstractCollater(DataSource dataSource, SwiftSourceAlloter alloter) {
        this.dataSource = dataSource;
        this.alloter = alloter;
        this.segmentKeys = new ArrayList<SegmentKey>();
    }

    @Override
    public void collate(SwiftResultSet swiftResultSet) throws Exception {
        Importer historyImporter = SwiftContext.get().getBean("historyBlockImporter", Importer.class, dataSource, alloter);
        historyImporter.importData(swiftResultSet);
        segmentKeys.addAll(historyImporter.getImportSegments());
    }

    @Override
    public List<SegmentKey> getNewSegments() {
        return segmentKeys;
    }
}
