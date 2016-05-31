/**
 *
 */
package com.fr.bi.stable.engine;

import com.finebi.cube.conf.build.CubeBuildStuffManager;
import com.fr.json.JSONCreator;

import java.io.Serializable;


public interface CubeTask extends JSONCreator, Serializable {

    String getUUID();

    CubeTaskType getTaskType();

    void start();

    void end();

    void run();

    long getUserId();

    void setCubeBuildStuffManager(CubeBuildStuffManager object);
}
