package com.fr.swift.provider;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * This class created on 2018/4/2
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface DataProvider {

    List<Segment> getPreviewData(DataSource dataSource) throws Exception;

    List<Segment> getRealData(DataSource dataSource) throws Exception;
}
