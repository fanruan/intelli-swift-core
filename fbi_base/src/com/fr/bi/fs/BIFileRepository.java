package com.fr.bi.fs;


import com.fr.base.FRContext;
import com.fr.fs.FSConfig;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.FSConstants;
import com.fr.stable.project.ProjectConstants;

import java.io.File;

/**
 * BI路径操作的方法类
 *
 * @author Daniel
 */
public class BIFileRepository {
    private static BIFileRepository BFR;
    private String path;

    public BIFileRepository() {
        this.setPath(FRContext.getCurrentEnv().getPath()
                + File.separator
                + ProjectConstants.RESOURCES_NAME
                + File.separator + "biReport");
    }

    public static BIFileRepository getInstance() {
        if (BFR == null) {
            BFR = new BIFileRepository();
        }
        return BFR;
    }

    private void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取文件夹位置
     *
     * @param id
     * @return
     * @throws Exception
     */
    public File getBIDirFile(long id) throws Exception {
        String name = getBIDirName(id);
        return new File(this.path, name);
    }

    /**
     * 获取用户分隔的文件夹
     * 若是system登录或者hsql登录用id保存，或同步数据集验证用用户名保存
     *
     * @param id
     * @return
     * @throws Exception
     */
    public String getBIDirName(long id) throws Exception {
        String name;
        if (id == UserControl.getInstance().getSuperManagerID()
                || FSConfig.getInstance().getControl().getControlType() == FSConstants.CONTROL.HSQLDAO) {
            name = String.valueOf(id);
        } else {
            name = FSConfig.getInstance().getControl().getUserDAO().findByID(id).getUsername();
        }
        return name;
    }
}