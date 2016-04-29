package com.finebi.cube.impl.router.topic;

import com.finebi.cube.exception.BIMessageFailureException;
import com.finebi.cube.exception.BITopicAbsentException;
import com.finebi.cube.exception.BITopicDuplicateException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.topic.ITopic;
import com.finebi.cube.router.topic.ITopicContainer;
import com.finebi.cube.router.topic.ITopicTag;
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
        , implement = ITopicContainer.class)
public class BITopicConainer extends BIMapContainer<ITopicTag, ITopic> implements ITopicContainer {
    @Override
    protected Map<ITopicTag, ITopic> initContainer() {
        return new HashMap<ITopicTag, ITopic>();
    }

    @Override
    protected ITopic generateAbsentValue(ITopicTag key) {
        return null;
    }

    @Override
    public Collection<ITopic> getAllTopics() {
        return getContainer().values();
    }

    @Override
    public void registerTopic(ITopic topic) throws BITopicDuplicateException {
        BINonValueUtils.checkNull(topic);
        BINonValueUtils.checkNull(topic.getTopicTag());
        try {
            putKeyValue(topic.getTopicTag(), topic);
        } catch (BIKeyDuplicateException ignore) {
            throw new BITopicDuplicateException();
        }
    }

    @Override
    public ITopic getSpecificTopic(ITopicTag topicTag) throws BITopicAbsentException {
        try {
            return getValue(topicTag);
        } catch (BIKeyAbsentException ignore) {
            throw new BITopicAbsentException();
        }
    }

    @Override
    public void deliverMessage(IMessage message) throws BIMessageFailureException {
        if (message != null && message.getTopic() != null) {
            try {
                ITopic topic = getSpecificTopic(message.getTopic().getTopicTag());
                topic.deliverMessage(message);
            } catch (BITopicAbsentException ex) {
                throw new BIMessageFailureException(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public boolean contain(ITopicTag topicTag) {
        return containsKey(topicTag);
    }

    @Override
    public void clear() {
        super.clear();
    }
}
