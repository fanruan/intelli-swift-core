package com.fr.bi.cal.generate.task.calculator;

import com.fr.bi.cal.generate.task.CustomCubeGenerateTask;
import com.finebi.cube.conf.ICubeGenerateTask;

/**
 * Created by Lucifer on 2017-5-24.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class PartTaskCalculator extends CustomTaskCalculator {


    public PartTaskCalculator(ICubeGenerateTask cubeGenerateTask) {
        super((CustomCubeGenerateTask) cubeGenerateTask);
    }
}
