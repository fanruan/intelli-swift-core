package com.fr.bi.etl.analysis.manager;

import com.finebi.cube.conf.BIAliasManagerProvider;
import com.finebi.cube.conf.trans.UserAliasManager;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

/**
 * Created by Lucifer on 2017-4-6.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class BIAnalysisETLAliasManagerWithoutUser extends BIAnalysisETLAliasManager implements BIAliasManagerProvider {

    private static final long serialVersionUID = -7447205168677215248L;
    private final long usedUserId = UserControl.getInstance().getSuperManagerID();

    @Override
    public UserAliasManager constructUserManagerValue(Long userId) {
        return super.constructUserManagerValue(usedUserId);
    }

    @Override
    public UserAliasManager getTransManager(long userId) {
        return super.getTransManager(usedUserId);
    }

    @Override
    public void setTransManager(long userId, UserAliasManager value) {
        super.setTransManager(usedUserId, value);
    }

    @Override
    public void setAliasName(String id, String name, long userId) {
        super.setAliasName(id, name, usedUserId);
    }

    @Override
    public String getAliasName(String id, long userId) {
        return super.getAliasName(id, usedUserId);
    }
    @Override
    public boolean containsAliasName(String name, long userId) {
        return super.containsAliasName(name,usedUserId);
    }
    @Override
    public void removeAliasName(String id, long userId) {
        super.removeAliasName(id, usedUserId);
    }

    public JSONObject getAliasJSON(long userId) {
        return super.getAliasJSON(usedUserId);
    }

    @Override
    public void persistData(long userId) {
        super.persistData(usedUserId);
    }

    @Override
    public void envChanged() {
        super.envChanged();
    }
}
