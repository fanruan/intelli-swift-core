package com.fr.bi.common.factory;

import com.fr.bi.common.factory.annotation.BISingletonObject;
import com.fr.bi.exception.BIKeyDuplicateException;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * String->Class的基础工厂类。要求是
 * 通过标签singletonObject来支持单例
 * 默认不是单例，每次get到的对象都是重新构建的。
 * Created by Connery on 2015/12/7.
 */
public class BIBeanFactory implements IFactoryService {
    private BIFactoryContainer<Class> classContainer;
    private BIFactoryContainer<Object> objectContainer;

    public BIBeanFactory() {
        synchronized (BIBeanFactory.class) {
            classContainer = new BIFactoryContainer<Class>();
            objectContainer = new BIFactoryContainer<Object>();
        }
    }

    private boolean isSingleton(String registeredName) {
        if (isNameExist(registeredName)) {
            Class clazz = classContainer.getValue(registeredName);
            return isSingleton(clazz);
        } else {
            return false;
        }
    }

    private boolean isSingleton(Class clazz) {

        Annotation[] annotations = clazz.getAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            if (clazz.isAnnotationPresent(BISingletonObject.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getObject(String registeredName, Object... parameter) throws Exception {
        if (isNameExist(registeredName)) {
            BINonValueUtils.checkNull(parameter);
            if (isSingleton(registeredName)) {
                synchronized (this) {
                    if (!objectContainer.containsKey(registeredName)) {
                        objectContainer.putKeyValue(registeredName, constructObject(registeredName, parameter));
                    }
                }
                return objectContainer.getValue(registeredName);
            } else {
                return constructObject(registeredName, parameter);
            }
        }
        return null;
    }

    @Override
    public Object getObject(String registeredName) throws Exception {
        return getObject(registeredName, new Object[]{});
    }

    private Object constructObject(String registeredName, Object... parameter) throws Exception {
        if (isNameExist(registeredName)) {
            Class clazz = classContainer.getValue(registeredName);
            return BIConstructorUtils.constructObjectWithParameter(clazz, parameter);
        }
        return null;


    }

    public Object constructObject(Class clazz, Object... parameter) throws NoSuchMethodException,
            InstantiationException, InvocationTargetException, IllegalAccessException {
        Class[] types = new Class[parameter.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = parameter[i].getClass();
        }
        Constructor constructor = BIConstructorUtils.chosePublicConstructor(clazz, types);
        return constructor.newInstance(parameter);
    }

    @Override
    public void registerClass(String name, Class clazz) throws BIFactoryKeyDuplicateException {
        BINonValueUtils.checkNull(name);
        synchronized (this) {
            if (classContainer.containsKey(name)) {
                throw new BIFactoryKeyDuplicateException("the name:" + name + " for class:" + clazz.getName() + " is repeated.\n" +
                        "current in container is:" + classContainer.getValue(name));
            }
            try {
                classContainer.putKeyValue(name, clazz);
            } catch (BIKeyDuplicateException e) {
                throw new BIFactoryKeyDuplicateException("the name:" + name + " for class:" + clazz.getName() + " is repeated.\n" +
                        "current in container is:" + classContainer.getValue(name));
            }
        }
    }

    @Override
    public String getFactoryTag() {
        return "Bean";
    }

    /**
     * 对于单例，准许直接注册对象。
     *
     * @param name
     * @param object
     */
    @Override
    public void registerObject(String name, Object object) throws BIClassNonsupportException {
        Class clazz = object.getClass();
        if (isSingleton(clazz)) {
            synchronized (this) {
                try {
                    classContainer.putKeyValue(name, clazz);
                    objectContainer.putKeyValue(name, object);
                } catch (BIKeyDuplicateException e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }

            }
        } else {
            throw new BIClassNonsupportException();
        }
    }

    private boolean isNameExist(String registeredName) {
        BINonValueUtils.checkNull(registeredName);
        return classContainer.containsKey(registeredName);
    }

    public Map<String, Class> getAllRegisteredClass() {
        return classContainer.getContainer();
    }
}