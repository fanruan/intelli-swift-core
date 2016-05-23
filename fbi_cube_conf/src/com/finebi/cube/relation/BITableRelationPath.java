package com.finebi.cube.relation;

import com.finebi.cube.conf.field.IBusinessField;
import com.finebi.cube.conf.table.IBusinessTable;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * Created by Connery on 2016/1/12.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BITableRelationPath.class)
public class BITableRelationPath extends BIBasicRelationPath<IBusinessTable, IBusinessField, BITableRelation> {
    public BITableRelationPath() {
        super();
    }

    public BITableRelationPath(BITableRelation[] relations) throws BITablePathConfusionException {
        super();
        BINonValueUtils.checkNull(relations);
        for (int i = 0; i < relations.length; i++) {
            addRelationAtTail(relations[i]);
        }
    }
}