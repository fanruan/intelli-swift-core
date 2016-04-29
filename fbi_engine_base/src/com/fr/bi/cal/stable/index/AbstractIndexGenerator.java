package com.fr.bi.cal.stable.index;

import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.stable.data.source.ITableSource;

import java.util.Set;

/**
 * Created by GUY on 2015/3/10.
 */
public abstract class AbstractIndexGenerator extends AbstractSourceGenerator {


    protected Set<ITableSource> derivedDataSources;


    protected AbstractIndexGenerator(TableCubeFile cube, ITableSource dataSource, Set<ITableSource> derivedDataSources, BIRecord log) {
        super(cube, dataSource, log);
        this.derivedDataSources = derivedDataSources;
    }

}