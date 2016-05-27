package com.finebi.cube.conf.table;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

import java.util.List;

/**
 * 业务包表，多用组合，谨慎继承
 * 通过DataSource的支持，
 * <p/>
 * This class created on 2016/5/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface BusinessTable extends JSONTransform, Cloneable {
    BITableID getID();

    List<String> getUsedFieldNames();

    void setUsedFieldNames(List<String> usedFieldNames);

    Object clone() throws CloneNotSupportedException;

    List<BusinessField> getFields();

    void setSource(CubeTableSource source);

    String getTableName();

    CubeTableSource getTableSource();

    void setFields(List<BusinessField> fields);

    JSONObject createJSONWithFieldsInfo(ICubeDataLoader loader) throws Exception;

}
