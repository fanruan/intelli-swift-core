package com.fr.bi.web.conf.services.session;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.data.TableDataSource;
import com.fr.fs.control.UserControl;
import com.fr.general.FRLogManager;
import com.fr.script.Calculator;
import com.fr.script.CalculatorMap;
import com.fr.stable.Constants;
import com.fr.stable.fun.IOFileAttrMark;
import com.fr.stable.script.CalculatorProvider;
import com.fr.web.core.SessionIDInfor;

/**
 * 配置部分用到的session
 * Created by Young's on 2016/12/14.
 */
public class BIConfSession extends SessionIDInfor {

    //当前回话中正在访问的业务包表
    private String editingTable;

    public BIConfSession(String remoteAddress, long userId) {
        this.remoteAddress = remoteAddress;
        this.parameterMap4Execute = CalculatorMap.createEmptyMap();
        try {
            this.parameterMap4Execute.put(Constants.P.PRIVILEGE_USERNAME, UserControl.getInstance().getUser(userId).getUsername());
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        updateTime();
    }

    @Override
    public TableDataSource getTableDataSource() {
        return null;
    }

    @Override
    protected Object resolveVariable(Object o, CalculatorProvider calculatorProvider) {
        return null;
    }

    @Override
    public IOFileAttrMark getIOFileAttrMark(String s) {
        return null;
    }

    @Override
    public String getDurationPrefix() {
        return this.getWebTitle();
    }

    @Override
    public String getWebTitle() {
        return "BI-CONFIG";
    }

    @Override
    public void release() {
        releaseTableLock();
        FRLogManager.setSession(null);
        Calculator.putThreadSavedNameSpace(null);
    }

    private void releaseTableLock() {

    }
}
