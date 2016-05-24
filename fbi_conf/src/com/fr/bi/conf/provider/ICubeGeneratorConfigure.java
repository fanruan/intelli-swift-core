package com.fr.bi.conf.provider;

import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * This class created on 2016/3/8.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeGeneratorConfigure {
    String XML_TAG = "ICubeGeneratorConfigure";

    Set<CubeTableSource> getAllTableData(long userId);

    Set<BITableRelationPath> getAllPathsData(long userId);
}
