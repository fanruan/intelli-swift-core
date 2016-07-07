package com.fr.bi.conf.base.cube;

import com.finebi.cube.conf.BISystemDataManager;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.conf.provider.BICubeConfManagerProvider;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

/**
 * Created by Young's on 2016/5/19.
 */
public class BISystemCubeConfManager extends BISystemDataManager<BICubeConfManager> implements BICubeConfManagerProvider {
    @Override
    public BICubeConfManager constructUserManagerValue(Long userId) {
        return new BICubeConfManager();
    }

    @Override
    public String managerTag() {
        return "BISystemCubeConfManager";
    }

    @Override
    public String persistUserDataName(long key) {
        return managerTag();
    }

    @Override
    public String getCubePath() {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).getCubePath();
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void saveCubePath(String path) {
        try {
            getValue(UserControl.getInstance().getSuperManagerID()).setCubePath(path);
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public String getLoginField() {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).getLoginField();
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void saveLoginField(String loginField) {
        try {
            getValue(UserControl.getInstance().getSuperManagerID()).setLoginField(loginField);
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public Object getLoginFieldValue(BusinessField field, long userId) {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).getFieldValue(field, userId);
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void updatePackageLastModify() {
        try {
            getValue(UserControl.getInstance().getSuperManagerID()).setPackageLastModify(System.currentTimeMillis());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    public long getPackageLastModify() {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).getPackageLastModify();
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return System.currentTimeMillis();
    }

    @Override
    public void updateMultiPathLastCubeStatus(BIReportConstant.MULTI_PATH_STATUS needGenerateCube) {
        try {
            getValue(UserControl.getInstance().getSuperManagerID()).setMultiPathCubeStatus(needGenerateCube);
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage());
        }
    }

    @Override
    public BIReportConstant.MULTI_PATH_STATUS getMultiPathCubeStatus() {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).getMultiPathCubeStatus();
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage());
        }
        return BIReportConstant.MULTI_PATH_STATUS.NOT_NEED_GENERATE_CUBE;
    }

    @Override
    public JSONObject createJSON(long userId) throws Exception {
        try {
            return getValue(UserControl.getInstance().getSuperManagerID()).createJSON();
        } catch (BIKeyAbsentException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void persistData(long userId) {
        super.persistUserData(userId);
    }
}
