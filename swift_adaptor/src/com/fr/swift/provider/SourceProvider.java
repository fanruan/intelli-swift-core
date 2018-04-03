package com.fr.swift.provider;

import com.fr.swift.source.DataSource;

/**
 * This class created on 2018/4/2
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface SourceProvider {

    DataSource getCacheDataSource(DataSource dataSource) throws Exception;

}
