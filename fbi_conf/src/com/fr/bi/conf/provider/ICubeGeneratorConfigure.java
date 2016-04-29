package com.fr.bi.conf.provider;

import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.relation.BITableRelationPath;

import java.util.Set;

/**
 * This class created on 2016/3/8.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeGeneratorConfigure {
    String XML_TAG = "ICubeGeneratorConfigure";

    Set<ITableSource> getAllTableData(long userId);

    Set<BITableRelationPath> getAllPathsData(long userId);
}
