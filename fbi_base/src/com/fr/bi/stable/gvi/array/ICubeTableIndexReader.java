package com.fr.bi.stable.gvi.array;


import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.common.inter.Release;

/**
 * Created by Connery on 2014/12/1.
 */
public interface ICubeTableIndexReader extends Release {

    GroupValueIndex get(final long row);

}