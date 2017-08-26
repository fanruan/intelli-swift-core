package com.finebi.cube.finerelation;

import com.finebi.common.resource.ResourceItem;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * Created by roy on 2017/7/9 0009.
 */
public interface FineRelation<T extends CubeTableSource , F extends ICubeFieldSource> extends ResourceItem {
    T getPrimaryTable();

    T getForeignTable();

    F getPrimaryField();

    F getForeignField();
}
