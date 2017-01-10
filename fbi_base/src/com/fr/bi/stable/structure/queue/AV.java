package com.fr.bi.stable.structure.queue;

import com.fr.bi.base.BICore;

/**
 * Created by daniel on 2017/1/10.
 */
public interface AV {

    boolean isAvailable();

    BICore getKey();

}
