package com.finebi.cube.location.meta;

import com.finebi.common.name.Name;
import com.finebi.common.resource.ResourceName;
import com.finebi.common.resource.ResourceNameImpl;
import com.fr.general.ComparatorUtils;

/**
 * Created by wang on 2017/6/20.
 */
public class BILocationInfoImp implements BILocationInfo {
    private ResourceName name;
    //   每次生生成cube对应的随机路径
    private String cubeFolder;
    //    /-999/tableID/~
    private String logicFolder;
    //    文件名
    private String child;

    public BILocationInfoImp(String cubeFolder, String logicPath, String child) {
        this.name = new ResourceNameImpl(logicPath + child);
        this.cubeFolder = cubeFolder;
        this.logicFolder = logicPath;
        this.child = child;
    }

    public String getLogicFolder() {
        return logicFolder;
    }

    public String getCubeFolder() {
        return cubeFolder;
    }

    @Override
    public String getChild() {
        return child;
    }

    @Override
    public String getRealFolder() {
        return cubeFolder + logicFolder;
    }

    @Override
    public String getRealPath() {
        return cubeFolder + logicFolder + child;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public ResourceName getResourceName() {
        return name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public long version() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BILocationInfoImp that = (BILocationInfoImp) o;

        if (name != null ? !ComparatorUtils.equals(name, that.name) : that.name != null) {
            return false;
        }
        if (cubeFolder != null ? !ComparatorUtils.equals(cubeFolder, that.cubeFolder) : that.cubeFolder != null) {
            return false;
        }
        if (logicFolder != null ? !ComparatorUtils.equals(logicFolder, that.logicFolder) : that.logicFolder != null) {
            return false;
        }
        return child != null ? ComparatorUtils.equals(child, that.child) : that.child == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (cubeFolder != null ? cubeFolder.hashCode() : 0);
        result = 31 * result + (logicFolder != null ? logicFolder.hashCode() : 0);
        result = 31 * result + (child != null ? child.hashCode() : 0);
        return result;
    }
}
