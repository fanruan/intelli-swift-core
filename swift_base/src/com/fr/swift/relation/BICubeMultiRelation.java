package com.fr.swift.relation;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.core.MD5Utils;

/**
 * @author yee
 * @date 2018/1/17
 */
public class BICubeMultiRelation extends BIBasicRelation<SourceKey, BICubeLogicColumnKey> {
    public BICubeMultiRelation(BICubeLogicColumnKey primaryField, BICubeLogicColumnKey foreignField, SourceKey primaryTable, SourceKey foreignTable) {
        super(primaryField, foreignField, primaryTable, foreignTable);
    }

    public String getKey() {
        return MD5Utils.getMD5String(new String[]{primaryField.getKey(), foreignField.getKey()});
    }
}
