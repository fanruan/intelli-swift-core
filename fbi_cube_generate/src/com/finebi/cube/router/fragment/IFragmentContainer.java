package com.finebi.cube.router.fragment;

import com.finebi.cube.exception.BIFragmentAbsentException;
import com.finebi.cube.exception.BIFragmentDuplicateException;
import com.finebi.cube.router.IMessageDeliver;

import java.util.Collection;

/**
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface IFragmentContainer extends IMessageDeliver {
    Collection<IFragment> getAllFragments();

    IFragment getSpecificFragment(IFragmentTag fragmentTag) throws BIFragmentAbsentException;

    void registerFragment(IFragment fragment) throws BIFragmentDuplicateException;

    boolean contain(IFragmentTag fragmentTag);
}
