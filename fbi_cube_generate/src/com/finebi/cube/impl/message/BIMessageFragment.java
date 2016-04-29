package com.finebi.cube.impl.message;

import com.finebi.cube.message.IMessageFragment;
import com.finebi.cube.router.fragment.IFragmentTag;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMessageFragment implements IMessageFragment {

    private IFragmentTag fragmentTag;

    public BIMessageFragment(IFragmentTag fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    @Override
    public IFragmentTag getFragmentTag() {
        return fragmentTag;
    }

    @Override
    public String
    toString() {
        final StringBuffer sb = new StringBuffer("BIMessageFragment{");
        sb.append("fragmentTag=").append(fragmentTag);
        sb.append('}');
        return sb.toString();
    }
}
