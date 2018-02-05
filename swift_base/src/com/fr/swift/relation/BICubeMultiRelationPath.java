package com.fr.swift.relation;

import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.swift.source.SourceKey;

/**
 * @author yee
 * @date 2018/1/17
 */
public class BICubeMultiRelationPath extends BIBasicRelationPath<SourceKey, BICubeLogicColumnKey, BICubeMultiRelation> {
    public BICubeLogicColumnKey getPrimaryField() {
        return getFirstRelation().getPrimaryField();
    }

    public String getSourceID() {
        StringBuffer sb = new StringBuffer();
        for (BICubeMultiRelation relation : getAllRelations()) {
            sb.append(relation.getPrimaryTable().getId())
                    .append(relation.getPrimaryField().getKey())
                    .append(relation.getForeignTable().getId())
                    .append(relation.getForeignField().getKey());
        }
        return BIMD5Utils.getMD5String(new String[]{sb.toString()});
    }
}
