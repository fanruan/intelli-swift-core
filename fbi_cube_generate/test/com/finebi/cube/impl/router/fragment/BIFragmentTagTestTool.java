package com.finebi.cube.impl.router.fragment;

import com.finebi.cube.impl.router.topic.BITopicTagTestTool;
import com.finebi.cube.router.fragment.IFragmentTag;

/**
 * This class created on 2016/3/23.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFragmentTagTestTool {
    public static IFragmentTag getFragmentTagA() {
        return new BIFragmentTag(new BIFragmentID("A"), null);
    }

    public static IFragmentTag getFragmentTagB() {
        return new BIFragmentTag(new BIFragmentID("B"), null);
    }

    public static IFragmentTag getFragmentTagC() {
        return new BIFragmentTag(new BIFragmentID("C"), null);
    }

    public static IFragmentTag getFragmentTagTFirst_FPartOne() {
        return new BIFragmentTag(new BIFragmentID("partOne"), BITopicTagTestTool.getTopicTagFirst());
    }

    public static IFragmentTag getFragmentTagTFirst_FPartTwo() {
        return new BIFragmentTag(new BIFragmentID("partTwo"), BITopicTagTestTool.getTopicTagFirst());
    }

    public static IFragmentTag getFragmentTagTSeconde_FPartOne() {
        return new BIFragmentTag(new BIFragmentID("partOne"), BITopicTagTestTool.getTopicTagSecond());
    }
}
