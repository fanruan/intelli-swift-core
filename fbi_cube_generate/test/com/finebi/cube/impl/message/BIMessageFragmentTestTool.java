package com.finebi.cube.impl.message;

import com.finebi.cube.impl.router.fragment.BIFragmentTagTestTool;
import com.finebi.cube.message.IMessageFragment;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMessageFragmentTestTool {
    public static IMessageFragment generateFa() {
        return new BIMessageFragment(BIFragmentTagTestTool.getFragmentTagA());
    }

    public static IMessageFragment generateFb() {
        return new BIMessageFragment(BIFragmentTagTestTool.getFragmentTagB());
    }

    public static IMessageFragment generateFc() {
        return new BIMessageFragment(BIFragmentTagTestTool.getFragmentTagC());
    }
}
