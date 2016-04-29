package com.finebi.cube.gen.subset;

import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/4/19.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeBuildProbeTool {
    public static BICubeBuildProbeTool INSTANCE = new BICubeBuildProbeTool();
    private Map<String, Integer> flag;

    private BICubeBuildProbeTool() {
        flag = new HashMap<String, Integer>();
    }

    public Map<String, Integer> getFlag() {
        return flag;
    }
}
