package com.fr.swift.segment;

import com.fr.swift.source.DataSource;

/**
 * This class created on 2018-2-2 16:33:40
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface IMinorSegmentManager extends SwiftSegmentManager {

    void update(DataSource dataSource) throws Exception;

}
