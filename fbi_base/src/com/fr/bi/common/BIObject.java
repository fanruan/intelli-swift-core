package com.fr.bi.common;

import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.common.inter.BIInitialize;
import com.fr.bi.common.persistent.json.BIJSONObject;

/**
 * Created by Connery on 2015/12/22.
 */
@BIMandatedObject()
public class BIObject extends BIJSONObject implements BIInitialize {
    @Override
    public void initial(Object... value) throws ClassCastException {

    }
}