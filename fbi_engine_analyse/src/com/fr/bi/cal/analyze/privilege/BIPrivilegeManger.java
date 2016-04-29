package com.fr.bi.cal.analyze.privilege;


import com.fr.bi.conf.base.pack.data.BIBasicBusinessPackage;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * 权限处理
 * Created by GUY on 2015/4/15.
 */
public class BIPrivilegeManger {

    private static BIPrivilegeManger privilege;


    public static BIPrivilegeManger getInstance() {
        if (privilege == null) {
            privilege = new BIPrivilegeManger();
        }
        return privilege;
    }

    public boolean packagePrivilege(BIBasicBusinessPackage pack, long userId) {
        if (pack == null) {
            return false;
        }
        boolean flag = false;
        try {
     //todo
            //       flag = pack.containsPrivilege(userId);
        } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
        }
        return flag;

    }
}