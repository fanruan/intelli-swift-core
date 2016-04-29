package com.fr.bi.stable.utils;

import com.fr.bi.base.provider.AllUserTravel;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.fs.control.UserControl;

/**
 * Created by Connery on 2015/12/4.
 */
public class BIUserUtils {

    public static boolean allUserTravelAction(AllUserTravel travel) {
        if (travel != null) {
            travel.start(UserControl.getInstance().getSuperManagerID());
        }
        return true;
    }

    /**
     * 是否使用管理员的设置
     *
     * @param userId 当前登录Id
     * @return
     */
    public static boolean isAdministrator(long userId) {
        return userId == UserControl.getInstance().getSuperManagerID() || BIBaseConstant.BI_MODEL == BIBaseConstant.CLASSIC_BI;
    }


}