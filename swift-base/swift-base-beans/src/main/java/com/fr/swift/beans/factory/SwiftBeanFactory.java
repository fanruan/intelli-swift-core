package com.fr.swift.beans.factory;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.handler.AnnotationHandlerContext;
import com.fr.swift.beans.exception.NoSuchBeanException;
import com.fr.swift.beans.exception.SwiftBeanException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2018/11/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftBeanFactory implements BeanFactory {

    private final BeanRegistry beanRegistry = SwiftBeanRegistry.getInstance();

    private final List<String> packageNames = new ArrayList<String>();

    private BeanScanner beanScanner;

    private final List<String> singletonNotLoadNames = new ArrayList<String>();


    public SwiftBeanFactory() {
        beanScanner = new SwiftBeanScanner(beanRegistry);
    }

    private List<String> beanNamesLoaded = new ArrayList<String>();

    public void init() {
        String[] packageArrays = new String[packageNames.size()];
        packageNames.toArray(packageArrays);
        beanScanner.scan(packageArrays);
        Map<String, SwiftBeanDefinition> beanDefinitionMap = beanRegistry.getBeanDefinitionMap();
        Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>();
        for (Map.Entry<String, SwiftBeanDefinition> entry : beanDefinitionMap.entrySet()) {
            if (entry.getValue().singleton()) {
                if (!singletonObjects.containsKey(entry.getKey())) {
                    singletonNotLoadNames.add(entry.getKey());
                }
            }
        }
        while (!singletonNotLoadNames.isEmpty()) {
            //单例对象初始化
            recursionCreateBean(beanDefinitionMap, singletonObjects);
            beanNamesLoaded.clear();
        }
        SwiftBeanRegistry.getInstance().setSingletonObjects(singletonObjects);
        SwiftLoggers.getLogger().info("Swift singleton beans create successfully!");
    }

    private void recursionCreateBean(Map<String, SwiftBeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects) {
        for (String singletonNotLoadName : singletonNotLoadNames) {
            SwiftBeanDefinition swiftBeanDefinition = beanDefinitionMap.get(singletonNotLoadName);
            try {
                Object singletonObject = createBean(swiftBeanDefinition.getClazz());
                singletonObjects.put(swiftBeanDefinition.getBeanName(), singletonObject);
                beanNamesLoaded.add(swiftBeanDefinition.getBeanName());
            } catch (Exception ignore) {
                SwiftLoggers.getLogger().debug(ignore);
            }
        }
        if (beanNamesLoaded.isEmpty() && !singletonNotLoadNames.isEmpty()) {
            Crasher.crash("RecursionCreateBean will trap in a dead circle! Please check beans " + singletonNotLoadNames.toString());
        }
        singletonNotLoadNames.removeAll(beanNamesLoaded);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> clazz, Object... params) {
        List<String> names = beanRegistry.getBeanNamesByType(clazz);
        if (!names.contains(beanName)) {
            throw new NoSuchBeanException(beanName);
        }
        SwiftBeanDefinition swiftBeanDefinition = beanRegistry.getBeanDefinition(beanName);
        if (swiftBeanDefinition.singleton()) {
            if (!SwiftBeanRegistry.getInstance().getSingletonObjects().containsKey(swiftBeanDefinition.getBeanName())) {
                throw new NoSuchBeanException(swiftBeanDefinition.getBeanName());
            }
            return (T) SwiftBeanRegistry.getInstance().getSingletonObjects().get(swiftBeanDefinition.getBeanName());
        } else {
            try {
                SwiftBeanDefinition beanDefinition = beanRegistry.getBeanDefinition(beanName);
                Class<T> beanClazz = (Class<T>) beanDefinition.getClazz();
                return createBean(beanClazz, params);
            } catch (Exception e) {
                throw new SwiftBeanException(e);
            }
        }
    }

    @Override
    public <T> T getBean(Class<T> clazz, Object... params) {
        List<String> names = beanRegistry.getBeanNamesByType(clazz);
        if (names == null || names.isEmpty()) {
            throw new SwiftBeanException(clazz.getName() + " 's beanNames size is null");
        } else if (names.size() >= 2) {
            throw new SwiftBeanException(clazz.getName() + " 's beanNames size >= 2");
        }
        String beanName = names.get(0);
        return getBean(beanName, clazz, params);
    }

    @Override
    public Object getBean(String beanName, Object... params) {
        Class clazz = getType(beanName);
        return getBean(beanName, clazz, params);
    }

    @Override
    public boolean isSingleton(String beanName) {
        return beanRegistry.getBeanDefinition(beanName).singleton();
    }

    @Override
    public Class<?> getType(String beanName) {
        return beanRegistry.getBeanDefinition(beanName).getClazz();
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) {
        List<String> names = beanRegistry.getBeanNamesByType(typeToMatch);
        return names.contains(name);
    }

    @Override
    public void registerPackages(String... packageNames) {
        for (String packageName : packageNames) {
            this.packageNames.add(packageName);
        }
    }

    public Map<String, Object> getBeansByAnnotations(Class<? extends Annotation> annotation) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> singletonObjectEntry : SwiftBeanRegistry.getInstance().getSingletonObjects().entrySet()) {
            Annotation marked = singletonObjectEntry.getValue().getClass().getAnnotation(annotation);
            if (marked != null) {
                resultMap.put(singletonObjectEntry.getKey(), singletonObjectEntry.getValue());
            }
        }
        return resultMap;
    }

    public List<Class<?>> getClassesByAnnotations(Class<? extends Annotation> annotation) {
        List<Class<?>> resultList = new ArrayList<Class<?>>();
        Map<String, SwiftBeanDefinition> swiftBeanDefinitionMap = beanRegistry.getBeanDefinitionMap();
        for (Map.Entry<String, SwiftBeanDefinition> entry : swiftBeanDefinitionMap.entrySet()) {
            Annotation marked = entry.getValue().getClazz().getAnnotation(annotation);
            if (marked != null) {
                resultList.add(entry.getValue().getClazz());
            }
        }
        return resultList;
    }

    @Override
    public void refresh(Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        SwiftBean swiftBean = clazz.getAnnotation(SwiftBean.class);
        if (swiftBean != null) {
            //获取到目标的object
            Map<String, Object> map = SwiftBeanRegistry.getInstance().getSingletonObjects();
            Object target = map.get(swiftBean.name());
            if (target != null) {
                AnnotationHandlerContext.getInstance().endProcess();
                AnnotationHandlerContext.getInstance().methodProcess();
            }
        }
    }

    @Override
    public void refreshAll() throws InvocationTargetException, IllegalAccessException {
        //获取到全部的object
        Map<String, Object> map = SwiftBeanRegistry.getInstance().getSingletonObjects();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            refresh(entry.getValue().getClass());
        }
    }


    private <T> T createBean(Class<T> tClass, Object... params) throws Exception {
        Class<?>[] parameterTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            parameterTypes[i] = params[i].getClass();
        }
        Constructor[] constructors = tClass.getDeclaredConstructors();
        Constructor<T> cons = null;
        for (Constructor constructor : constructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (paramTypes.length == parameterTypes.length) {
                boolean matched = true;
                for (int i = 0; i < paramTypes.length; i++) {
                    if (!ReflectUtils.isAssignable(parameterTypes[i], paramTypes[i])) {
                        matched = false;
                        break;
                    }
                }
                if (matched) {
                    cons = constructor;
                    break;
                }
            }
        }
        if (cons == null) {
            Crasher.crash("No constructor matched!");
        }
        cons.setAccessible(true);
        T bean = cons.newInstance(params);
        return bean;
    }
}
