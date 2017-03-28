/**
 *
 */
package com.fr.bi.stable.engine;

import com.fr.json.JSONCreator;

import java.io.Serializable;
import java.util.Set;


public interface CubeTask extends JSONCreator, Serializable {

    String getTaskId();

    CubeTaskType getTaskType();

    void start();

    void end();

    void run();

    long getUserId();

    Set<String> getTaskTableSourceIds();

}
