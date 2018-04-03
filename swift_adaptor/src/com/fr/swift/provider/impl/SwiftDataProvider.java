package com.fr.swift.provider.impl;

import com.fr.swift.generate.preview.MinorSegmentManager;
import com.fr.swift.generate.preview.MinorUpdater;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.provider.DataProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * This class created on 2018/4/2
 *
 * @author Lucifer
 * @description 真实数据和预览数据provider
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftDataProvider implements DataProvider {

    @Override
    public List<Segment> getPreviewData(DataSource dataSource) throws Exception {
        // TODO: 2018/4/2
        minorUpdate(dataSource);
        return MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
    }

    @Override
    public List<Segment> getRealData(DataSource dataSource) throws Exception {
        return LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
    }

    private void minorUpdate(DataSource dataSource) throws Exception {
        new MinorUpdater(dataSource).update();
    }
}
