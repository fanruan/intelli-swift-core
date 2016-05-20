package com.finebi.cube.api;

import com.fr.bi.base.BIUser;

/**
 * Created by 小灰灰 on 2016/5/19.
 */
public interface ICubeDataLoaderCreator {
    String XML_TAG = "ICubeDataLoaderCreator";
    ICubeDataLoader fetchCubeLoader(BIUser user);
}
