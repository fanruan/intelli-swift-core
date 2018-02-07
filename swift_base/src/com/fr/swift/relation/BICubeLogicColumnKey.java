package com.fr.swift.relation;

//import com.fr.bi.stable.data.BIFieldID;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public class BICubeLogicColumnKey extends BaseLogicKeyField<SourceKey, ColumnKey> {
    private String fieldName;
    private String key;

    public BICubeLogicColumnKey(List<ColumnKey> keyFields) {
        super(keyFields);
    }

    @Override
    public SourceKey belongTo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFieldName() {
        if (StringUtils.isEmpty(fieldName)) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0, len = keyFields.size(); i < len; i++) {
                buffer.append(keyFields.get(i).getName()).append(",");
            }
            buffer.setLength(buffer.length() - 1);
            fieldName = buffer.toString();
        }
        return fieldName;
    }

    public String getKey() {
        if (StringUtils.isEmpty(key)) {
            key = getFieldName();
        }
        return key;
    }

    //@Override
    //public BIFieldID getFieldID() {
        //throw new UnsupportedOperationException();
    //}

    @Override
    public JSONObject createJSON() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        throw new UnsupportedOperationException();
    }
}
