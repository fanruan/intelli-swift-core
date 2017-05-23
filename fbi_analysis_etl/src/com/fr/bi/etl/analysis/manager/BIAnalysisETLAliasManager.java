package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BIAliasManagerProvider;
import com.finebi.cube.conf.BISystemDataManager;
import com.finebi.cube.conf.trans.UserAliasManager;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

import java.io.File;

/**
 * Created by 小灰灰 on 2016/6/8.
 */
public class BIAnalysisETLAliasManager extends BISystemDataManager<UserAliasManager> implements BIAliasManagerProvider {
    private static final long serialVersionUID = 7737620413603253982L;
    private static BILogger LOGGER = BILoggerFactory.getLogger(BIAnalysisETLAliasManager.class);

    @Override
    public UserAliasManager constructUserManagerValue(Long userId) {
        return new UserAliasManager(userId);
    }

    @Override
    public String managerTag() {
        return "BIAnalysisETLAliasManager";
    }

    @Override
    public String persistUserDataName(long key) {
        return "sue" + File.separator + "alisa" + key;
    }


    @Override
    public UserAliasManager getTransManager(long userId) {
        try {
            return getValue(userId);
        } catch (BIKeyAbsentException e) {
            LOGGER.error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void setTransManager(long userId, UserAliasManager value) {
        try {
            if (containsKey(userId)) {
                remove(userId);
            }
            putKeyValue(userId, value);
        } catch (BIKeyAbsentException e) {
            LOGGER.error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl(e);
        } catch (BIKeyDuplicateException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void setAliasName(String id, String name, long userId) {
        getTransManager(userId).setTransName(id, name);
    }

    @Override
    public String getAliasName(String id, long userId) {
        return getTransManager(userId).getTransName(id);
    }

    @Override
    public void removeAliasName(String id, long userId) {
        getTransManager(userId).removeTransName(id);
    }

    public JSONObject getAliasJSON(long userId) {
        try {
            JSONObject jo = getTransManager(userId).createJSON();
            if (userId != UserControl.getInstance().getSuperManagerID()) {
                jo.join(getTransManager(UserControl.getInstance().getSuperManagerID()).createJSON());
            }
            return jo;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void persistData(long userId) {
        persistUserData(userId);
    }

    /**
     * 更新
     */
    @Override
    public void envChanged() {
        clear();
    }
}
