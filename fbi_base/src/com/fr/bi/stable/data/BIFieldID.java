package com.fr.bi.stable.data;

import com.fr.bi.base.BIBasicIdentity;

import java.io.Serializable;

/**
 * Created by Connery on 2015/12/17.
 */
public class BIFieldID extends BIBasicIdentity implements Serializable {


    private static final long serialVersionUID = 2743537969353270587L;
    public static BIFieldID BI_EMPTY_FIELD_ID = new BIFieldID("__FINE_BI_EMPTY__");

    public BIFieldID(String id) {
        super(id);
    }



    public BIFieldID(BITableID id) {
        super(id.getIdentityValue());
    }

    public BIFieldID(BIBasicIdentity id) {
        super(id.getIdentityValue());
    }



}