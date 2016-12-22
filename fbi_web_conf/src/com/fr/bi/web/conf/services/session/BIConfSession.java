package com.fr.bi.web.conf.services.session;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.conf.tablelock.BIConfTableLock;
import com.fr.bi.conf.tablelock.BIConfTableLockDAO;
import com.fr.data.TableDataSource;
import com.fr.fs.control.UserControl;
import com.fr.general.FRLogManager;
import com.fr.script.Calculator;
import com.fr.script.CalculatorMap;
import com.fr.stable.Constants;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.IOFileAttrMark;
import com.fr.stable.script.CalculatorProvider;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.SessionIDInfor;

import java.util.List;

/**
 * Created by Young's on 2016/12/15.
 */
public class BIConfSession extends SessionIDInfor {

    private static final long TIME_OUT = 45000;

    private long userId;
    private boolean isEditingTable;

    public BIConfSession(String remoteAddress, long userId) {
        this.userId = userId;
        this.remoteAddress = remoteAddress;
        this.parameterMap4Execute = CalculatorMap.createEmptyMap();
        try {
            this.parameterMap4Execute.put(Constants.P.PRIVILEGE_USERNAME, UserControl.getInstance().getUser(userId).getUsername());
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        updateTime();
    }

    public long getUserId() {
        return this.userId;
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
//        this.releaseTableLock();
        FRLogManager.setSession(null);
        Calculator.putThreadSavedNameSpace(null);
    }

    public void forceEdit(String tableId) {
        BIConfTableLockDAO lockDAO = StableFactory.getMarkedObject(BIConfTableLockDAO.class.getName(), BIConfTableLockDAO.class);
        if (lockDAO == null) {
            this.isEditingTable = true;
            return;
        }
        lockDAO.forceLock(sessionID, userId, tableId);
        this.isEditingTable = true;
    }

    /**
     * @param isEditingTable
     * @return
     */
    public boolean setEdit(boolean isEditingTable, String tableId) {
        BIConfTableLockDAO lockDAO = StableFactory.getMarkedObject(BIConfTableLockDAO.class.getName(), BIConfTableLockDAO.class);
        if (lockDAO == null) {
            this.isEditingTable = isEditingTable;
            return isEditingTable;
        }
        if (isEditingTable) {
            isEditingTable = lockDAO.lock(sessionID, userId, tableId);
            if (!isEditingTable) {
                List<BIConfTableLock> locks = lockDAO.getLock(userId, tableId);
                boolean doForce = true;
                for (BIConfTableLock l : locks) {
                    SessionIDInfor ss = SessionDealWith.getSessionIDInfor(l.getSessionId());
                    if (ss instanceof BIConfSession) {
                        long t = ((BIConfSession) ss).lastTime;
                        //45- 30 超过15-45秒还没反應可能是没有心跳
                        if (System.currentTimeMillis() - t < TIME_OUT) {
                            doForce = false;
                            break;
                        }
                    }
                }
                if (doForce) {
                    forceEdit(tableId);
                    return this.isEditingTable;
                }
            }
        } else {
            releaseTableLock(tableId);
        }
        this.isEditingTable = isEditingTable;
        return isEditingTable;
    }

    public void releaseTableLock(String tableId) {
        BIConfTableLockDAO lockDAO = StableFactory.getMarkedObject(BIConfTableLockDAO.class.getName(), BIConfTableLockDAO.class);
        if(lockDAO == null){
            return;
        }
        BIConfTableLock lock = lockDAO.getLock(this.sessionID, this.userId, tableId);
        if (lock != null) {
            lockDAO.release(lock);
        }
    }
}
