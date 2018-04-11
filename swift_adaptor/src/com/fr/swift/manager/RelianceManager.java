package com.fr.swift.manager;

import com.fr.swift.source.DataSource;
import com.fr.swift.struct.SourceReliance;

import java.util.List;

/**
 * This class created on 2018/4/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface RelianceManager {

    SourceReliance getSourceReliance(List<DataSource> dataSourceList);

}
