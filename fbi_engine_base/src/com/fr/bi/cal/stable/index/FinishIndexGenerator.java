package com.fr.bi.cal.stable.index;

import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.conf.log.BIRecord;

/**
 * Created by GUY on 2015/3/13.
 */
public class FinishIndexGenerator extends AbstractGenerator {

    public FinishIndexGenerator(TableCubeFile cube, BIRecord log) {
        super(cube, log);
    }

    @Override
    public void generateCube() {
        cube.writeVersionCheck();
    }

}