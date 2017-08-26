package com.fr.bi.stable.data.tablesource;


import com.finebi.common.resource.ResourceItem;
import com.fr.bi.stable.data.db.FineCubeField;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.List;


/**
 * Created by Roy on 2017/6/16.
 */
public interface FineCubeTable extends ResourceItem, CubeTableSource {

    void initFields(List<FineCubeField> fieldList);

}
