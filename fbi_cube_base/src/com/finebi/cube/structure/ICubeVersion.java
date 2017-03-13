package com.finebi.cube.structure;

import com.fr.bi.common.inter.Release;

import java.io.Serializable;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeVersion extends Release,Serializable {
    long getCubeVersion();

    void addVersion(long version);

    Boolean isVersionAvailable();

}
