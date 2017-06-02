package com.fr.bi.cal.generate.task.calculator;

import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.ITaskCalculator;
import com.finebi.cube.impl.conf.CubeBuildStuffComplete;
import com.fr.bi.base.BIUser;
import com.finebi.cube.conf.ICubeGenerateTask;

/**
 * Created by Lucifer on 2017-5-24.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class AllTaskCalculator implements ITaskCalculator {

    private ICubeGenerateTask cubeGenerateTask;

    public AllTaskCalculator(ICubeGenerateTask cubeGenerateTask) {
        this.cubeGenerateTask = cubeGenerateTask;
    }

    @Override
    public CubeBuildStuff generateCubeBuildStuff() {
        return new CubeBuildStuffComplete(new BIUser(cubeGenerateTask.getUserId()));
    }
}
