package com.finebi.cube.impl.router.fragment;

import com.fr.bi.base.BIIdentity;
import com.finebi.cube.router.fragment.IFragmentID;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFragmentID extends BIIdentity<String> implements IFragmentID {
    public BIFragmentID(String id) {
        super(id);
    }

}