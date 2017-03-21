package com.finebi.integration.cube.custom.it;

import com.fr.bi.conf.base.cube.BISystemCubeConfManager;

import java.io.File;

/**
 * Created by Lucifer on 2017-3-17.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomCubeConfManager extends BISystemCubeConfManager {

    @Override
    public String getCubePath() {
        return new File("").getAbsolutePath();
    }
}
