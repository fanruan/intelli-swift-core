package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.fr.bi.base.BIUser;

/**
 * Created by 小灰灰 on 2016/5/19.
 */
public class UserETLCubeDataLoaderCreator implements ICubeDataLoaderCreator {

    public static String XML_TAG = "UserETLCubeDataLoaderCreator";

    private static UserETLCubeDataLoaderCreator instance;

    public static UserETLCubeDataLoaderCreator getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (UserETLCubeDataLoaderCreator.class) {
                if (instance == null) {
                    instance = new UserETLCubeDataLoaderCreator();
                }
                return instance;
            }
        }
    }

    public ICubeDataLoader fetchCubeLoader(BIUser user) {
        return UserETLCubeTILoader.getInstance(user.getUserId());

    }

    @Override
    public void clear(long userId) {

    }

    public ICubeDataLoader fetchCubeLoader(long user) {
        return fetchCubeLoader(new BIUser(user));
    }

    private UserETLCubeDataLoaderCreator() {

    }
}
