package com.finebi.cube.conf.table;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

import java.util.List;

/**
 * This class created on 2016/5/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface BusinessTable extends JSONTransform, Cloneable {
    BITableID getID();

    Object clone() throws CloneNotSupportedException;

    List<BusinessField> getFields();

    String getTableName();

    CubeTableSource getTableSource();

    JSONObject createJSONWithFieldsInfo(ICubeDataLoader loader)throws Exception;

    void magicInitial();
}
