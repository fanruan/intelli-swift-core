package com.finebi.cube.router.fragment;

import com.finebi.cube.router.IMessageDeliver;

/**
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface IFragment extends IFragmentService, IMessageDeliver {
    IFragmentTag getFragmentTag();

}
