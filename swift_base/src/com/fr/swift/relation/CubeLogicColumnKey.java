package com.fr.swift.relation;

import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.core.MD5Utils;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/17
 */
public class CubeLogicColumnKey extends BaseLogicKeyField<SourceKey, ColumnKey> {
    private String fieldName;
    private SourceKey belongTo;

    public CubeLogicColumnKey(SourceKey belongTo, List<ColumnKey> keyFields) {
        super(keyFields);
        this.belongTo = belongTo;
    }

    @Override
    public SourceKey belongTo() {
        return belongTo;
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
        return MD5Utils.getMD5String(new String[]{getFieldName()});
    }

    @Override
    public JSONObject createJSON() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void parseJSON(JSONObject jsonObject) {
        throw new UnsupportedOperationException();
    }
}
