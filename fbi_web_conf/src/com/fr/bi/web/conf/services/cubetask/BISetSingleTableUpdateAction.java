package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.cal.generate.SingleTableTask;
import com.fr.bi.conf.manager.singletable.data.BICubeTimeTaskCreator;
import com.fr.bi.conf.manager.singletable.data.SingleTableUpdateAction;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TimerTask;

public class BISetSingleTableUpdateAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "set_single_table_update_action";
    }


    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        String config = WebUtils.getHTTPRequestParameter(req, "config");
        final long userId = ServiceUtils.getCurrentUserID(req);
        if (config != null) {
            final SingleTableUpdateAction action = new SingleTableUpdateAction(config);
            BIConfigureManagerCenter.getPackageManager().getSingleTableUpdateManager(userId).setSingleTableUpdateAction(action, new BICubeTimeTaskCreator() {

                @Override
                public TimerTask createNewObject() {
                    return new TimerTask() {
                        @Override
                        public void run() {
                            BIConfigureManagerCenter.getCubeManager().addTask(new SingleTableTask(action.getTableKey(), userId), userId);
                        }
                    };
                }
            });
        }
    }

}