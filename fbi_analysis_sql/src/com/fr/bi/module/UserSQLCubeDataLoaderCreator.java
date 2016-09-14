package com.fr.bi.module;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeDataLoaderCreator;
import com.fr.bi.base.BIUser;

/**
 * Created by 小灰灰 on 2016/5/19.
 */
public class UserSQLCubeDataLoaderCreator implements ICubeDataLoaderCreator {
    private static UserSQLCubeDataLoaderCreator instance;

    public static UserSQLCubeDataLoaderCreator getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (UserSQLCubeDataLoaderCreator.class) {
                if (instance == null) {
                    instance = new UserSQLCubeDataLoaderCreator();
                }
                return instance;
            }
        }
    }

    public ICubeDataLoader fetchCubeLoader(BIUser user) {
       return UserSQLCubeTILoader.getInstance(user.getUserId());

    }

    public ICubeDataLoader fetchCubeLoader(long user) {
        return fetchCubeLoader(new BIUser(user));
    }

    private UserSQLCubeDataLoaderCreator() {

    }
}
