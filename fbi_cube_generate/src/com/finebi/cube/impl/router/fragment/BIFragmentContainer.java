package com.finebi.cube.impl.router.fragment;

import com.finebi.cube.exception.BIFragmentAbsentException;
import com.finebi.cube.exception.BIFragmentDuplicateException;
import com.finebi.cube.exception.BIMessageFailureException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.fragment.IFragment;
import com.finebi.cube.router.fragment.IFragmentContainer;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.fr.bi.common.container.BIMapContainer;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = IFragmentContainer.class)
public class BIFragmentContainer extends BIMapContainer<IFragmentTag, IFragment> implements IFragmentContainer {
    @Override
    protected Map<IFragmentTag, IFragment> initContainer() {
        return new HashMap<IFragmentTag, IFragment>();
    }

    @Override
    protected IFragment generateAbsentValue(IFragmentTag key) {
        return null;
    }

    @Override
    public Collection<IFragment> getAllFragments() {
        synchronized (container) {
            return container.values();
        }
    }

    @Override
    public IFragment getSpecificFragment(IFragmentTag fragmentTag) throws BIFragmentAbsentException {
        try {
            return getValue(fragmentTag);
        } catch (BIKeyAbsentException ignore) {
            throw new BIFragmentAbsentException("the fragment:" + fragmentTag);
        }
    }

    @Override
    public void registerFragment(IFragment fragment) throws BIFragmentDuplicateException {
        try {
            BINonValueUtils.checkNull(fragment);
            BINonValueUtils.checkNull(fragment.getFragmentTag());
            putKeyValue(fragment.getFragmentTag(), fragment);
        } catch (BIKeyDuplicateException ignore) {
            throw new BIFragmentDuplicateException();
        }
    }

    @Override
    public boolean contain(IFragmentTag fragmentTag) {
        return containsKey(fragmentTag);
    }

    @Override
    public void deliverMessage(IMessage message) throws BIMessageFailureException {
        if (message != null && message.getFragment() != null) {
            try {
                IFragment fragment = getSpecificFragment(message.getFragment().getFragmentTag());
                fragment.deliverMessage(message);
            } catch (BIFragmentAbsentException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }
}
