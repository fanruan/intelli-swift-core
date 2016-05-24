package com.finebi.cube.conf.table;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

/**
 * This class created on 2016/5/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface BusinessTable extends JSONTransform {
    BITableID getID();

    Object clone() throws CloneNotSupportedException;

    String getTableName();

    ICubeTableSource getTableSource();

    JSONObject createJSONWithFieldsInfo(ICubeDataLoader loader);
}
