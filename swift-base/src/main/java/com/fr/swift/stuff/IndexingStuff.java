package com.fr.swift.stuff;

import java.util.List;

/**
 * This class created on 2017-11-27.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public interface IndexingStuff {
    List<String> getUpdateTableSources();

    List<String> getUpdateTableSourceRelations();

    List<String> getUpdateTableSourceRelationPaths();

    String getUpdateType();
}
