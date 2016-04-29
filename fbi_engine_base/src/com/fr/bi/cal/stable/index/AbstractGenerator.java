package com.fr.bi.cal.stable.index;

import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.stable.index.CubeGenerator;

public abstract class AbstractGenerator implements CubeGenerator {

    protected TableCubeFile cube;

    protected BIRecord log;

    public AbstractGenerator(TableCubeFile cube, BIRecord log) {
        this.cube = cube;
        this.log = log;
    }


}