package com.fr.bi.conf.base.trans;

import com.fr.bi.conf.base.trans.SingleUserBITransManager;
import com.fr.fs.control.UserControl;

/**
 * Created by GUY on 2015/3/31.
 */
public class AdministratorBITransManager extends SingleUserBITransManager {

    public AdministratorBITransManager() {
        super(UserControl.getInstance().getSuperManagerID());
    }

    @Override
    public String fileName() {
        return "bi_trans.xml";
    }
}