package com.fr.bi.cal.generate;

import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.json.JSONObject;

/**
 * This class created on 16-12-13.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class EmptyCubeTask implements CubeTask {
    @Override
    public CubeTaskType getTaskType() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }

    @Override
    public void run() {

    }

    @Override
    public long getUserId() {
        return 0;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }
}
