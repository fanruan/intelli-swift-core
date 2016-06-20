package com.fr.bi.cal.generate.index;

import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.io.File;

public class TempIndexGenerator extends IndexGenerator {
    /**
     *
     */
    private static final long serialVersionUID = -4475590475435042166L;

    private TempIndexGenerator(CubeTableSource source, long userId) {
        super(source, userId, 0);
    }

    public TempIndexGenerator(CubeTableSource source, String pathSuffix, long userId) {
        super(source, pathSuffix, userId, 0);
    }

    @Override
    protected void createTableCube() {
        this.cube = new TableCubeFile(BIBaseConstant.CACHE.getCacheDirectory() + BIPathUtils.tablePath(source.fetchObjectCore().getID().getIdentityValue()) + File.separator + pathSuffix);
    }
}