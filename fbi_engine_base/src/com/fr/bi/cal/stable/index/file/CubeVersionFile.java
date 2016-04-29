package com.fr.bi.cal.stable.index.file;

import com.fr.bi.stable.constant.CubeConstant;

/**
 * cube版本信息
 * Created by GUY on 2015/3/12.
 */
public class CubeVersionFile extends AbstractOneValueICubeFile {

    public CubeVersionFile(String path) {
        super(path);
    }

    @Override
    public long write(String data) {
        String version = String.valueOf(CubeConstant.CUBEVERSION);
        return super.write(version);
    }

}