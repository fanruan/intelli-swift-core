package com.fr.bi.cal.stable.index;

import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.stable.data.source.CubeTableSource;

public abstract class AbstractSourceGenerator extends AbstractGenerator {


    protected CubeTableSource dataSource;


    public AbstractSourceGenerator(TableCubeFile cube, CubeTableSource dataSource, BIRecord log) {
        super(cube, log);
        this.dataSource = dataSource;
    }


}