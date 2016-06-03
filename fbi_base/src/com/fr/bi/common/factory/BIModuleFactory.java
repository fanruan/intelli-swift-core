package com.fr.bi.common.factory;

import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.ComparatorUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2016/3/14.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIModuleFactory implements IModuleFactory {


    private Map<String, IFactoryService> units;
    private IFactoryService currentFactory;

    public BIModuleFactory() {
        units = new ConcurrentHashMap<String, IFactoryService>();
        initialMateFactory();
        setDefaultCurrentFactory();

    }


    public abstract void initialMateFactory();

    protected abstract void setDefaultCurrentFactory();


    public void registerFactory(IFactoryService factory) {
        units.put(factory.getFactoryTag(), factory);
    }

    public boolean useFactory(String tag) {
        synchronized (units) {
            if (units.containsKey(tag)) {
                currentFactory = units.get(tag);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public Object getObject(String registeredName, Object... parameter) throws Exception {
        synchronized (currentFactory) {
            return currentFactory.getObject(registeredName, parameter);
        }
    }

    @Override
    public Object getObject(String registeredName) throws Exception {
        synchronized (currentFactory) {
            return currentFactory.getObject(registeredName);
        }
    }

    @Override
    public void registerClass(String moduleTag, String name, Class clazz) throws BIFactoryKeyDuplicateException {
        synchronized (currentFactory) {
            if (ComparatorUtils.equals(moduleTag, getFactoryTag())) {
                currentFactory.registerClass(name, clazz);
            } else {
                throw new IllegalArgumentException(BIStringUtils.appendWithSpace("the moduleTag " +
                        "don't belong to current module factory", moduleTag));
            }
        }
    }

    @Override
    public abstract String getFactoryTag();

    @Override
    public void registerObject(String moduleTag, String name, Object object) throws BIClassNonsupportException {
        synchronized (currentFactory) {
            if (ComparatorUtils.equals(moduleTag, getFactoryTag())) {
                currentFactory.registerObject(name, object);
            } else {
                throw new IllegalArgumentException(BIStringUtils.appendWithSpace("the moduleTag " +
                        "don't belong to current module factory", moduleTag));
            }
        }
    }


}
