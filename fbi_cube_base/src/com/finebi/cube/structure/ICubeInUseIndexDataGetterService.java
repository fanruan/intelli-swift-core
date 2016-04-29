package com.finebi.cube.structure;

import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by naleite on 16/3/17.
 */
public interface ICubeInUseIndexDataGetterService {
    GroupValueIndex getIndex(int position);

    GroupValueIndex getNULLIndex(int position);

    int getVersion();
}
