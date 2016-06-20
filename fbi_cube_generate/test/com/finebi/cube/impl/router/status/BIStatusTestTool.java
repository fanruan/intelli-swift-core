package com.finebi.cube.impl.router.status;

import com.finebi.cube.impl.router.fragment.BIFragmentTagTestTool;
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
