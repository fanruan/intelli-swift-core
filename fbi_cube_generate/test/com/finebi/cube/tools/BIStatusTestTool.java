package com.finebi.cube.tools;

import com.finebi.cube.tools.BIFragmentTagTestTool;
import com.finebi.cube.impl.router.status.BIStatusID;
import com.finebi.cube.impl.router.status.BIStatusTag;
import com.finebi.cube.router.status.IStatusTag;

/**
 * This class created on 2016/3/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BIStatusTestTool {
    //    public IStatusTag generateStatuTagS
    public static IStatusTag generateFinishA() {
        return new BIStatusTag(new BIStatusID(""), BIFragmentTagTestTool.getFragmentTagA());
    }
}
