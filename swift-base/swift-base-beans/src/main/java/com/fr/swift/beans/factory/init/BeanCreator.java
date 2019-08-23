package com.fr.swift.beans.factory.init;

import com.fr.swift.beans.annotation.process.SwiftClassUtil;
import com.fr.swift.beans.exception.InitClassException;
import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.SwiftBeanRegistry;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class created on 2019/8/13
 *
 * @author Krysta
 * @description 构建依赖树并创建bean，与SwiftBeanFactory对接
 * */

public class BeanCreator {
    private DependencyTreeNode tree; //需要构建的树
    private Map<String, Integer> beanCount = new HashMap<>(); //对autowired重复的BeanDefinition进行编号 ，方便构建出依赖树

    private final List<String> beanNamesLoaded;
    private final Map<String, Object> singletonObjects;

    public BeanCreator(List<String> beanNamesLoaded, Map<String, Object> singletonObjects) {
        this.beanNamesLoaded = beanNamesLoaded;
        this.singletonObjects = singletonObjects;
    }

    public void buildTreeByBeanName(String beanName, Map<String, SwiftBeanDefinition> beanDefinitionMap) {
        SwiftBeanDefinition swiftBeanDefinition = beanDefinitionMap.get(beanName);
        buildFromSuper(swiftBeanDefinition);
    }

    public void buildFromSuper(SwiftBeanDefinition definition) {

        if (beanNamesLoaded.contains(definition.getBeanName())) {
            return;
        }
        Class<?> clazz = definition.getClazz();
        List<SwiftBeanDefinition> definitions = getBeanDefinitionsByType(clazz);
        for (SwiftBeanDefinition swiftBeanDefinition : definitions) {
            WrapperDefinition wrapperDefinition = new WrapperDefinition(swiftBeanDefinition, 0);
            tree = new DependencyTreeNode(wrapperDefinition);
            recursion(wrapperDefinition);
            createBeansByTree(tree);
            tree.clear();
        }

    }

    /*
     * 根据java对象的构造顺序，是先构造父类，而父类有多个BeanName即多个SwiftBeanDefinition 它们按照父类到子类排列
     * 也就是说只需要构造顶级父类的definitions 就可以完成继承链的构造。
     * */

    public List<SwiftBeanDefinition> getTopSuperClass(Class<?> clazz) {
        Set<Class<?>> interfaces = SwiftClassUtil.getAllInterfacesAndSelf(clazz);
        List<Class<?>> list = new ArrayList<>(interfaces);
        Class<?> topClass = list.get(list.size() - 1);// interfaces按子类到父类排列 ,所以顶级父类在最后面
        return getBeanDefinitionsByType(topClass);
    }

    public void recursion(WrapperDefinition wrapperDefinition) {
        if (!wrapperDefinition.getDefinition().getAutoWired()) {
            return;
        }
        DependencyTreeNode root = new DependencyTreeNode(wrapperDefinition); //每次递归都会有一个子节点的树根
        getAllSubNode(root);

        TreeUtils.buildTree(tree, root); //将这一课子树加到总的树中
        TreeUtils.isCircle(tree); //如果有循环依赖，抛出异常

        for (int i = 0; i < root.next.size(); i++) {
            recursion(root.next.get(i).getWrapperDefinition());
        }
    }

    /*
     * 获得root的所有子节点，子节点会将自己的继承链都加入进root，用于判断继承链中的循环依赖
     * */
    private void getAllSubNode(DependencyTreeNode root) {
        WrapperDefinition wrapperDefinition = root.getWrapperDefinition();
        List<Field> autowireds = wrapperDefinition.getDefinition().getAllAutowiredFiles();
        for (Field autowired : autowireds) {
            List<SwiftBeanDefinition> definitions = getTopSuperClass(autowired.getType());
            if (definitions.size() > 0) {
                for (SwiftBeanDefinition definition : definitions) {
                    if (beanCount.containsKey(autowired.getName())) {
                        beanCount.put(autowired.getName(), beanCount.get(autowired.getName()) + 1);
                    } else {
                        beanCount.put(autowired.getName(), 1);
                    }
                    DependencyTreeNode node = new DependencyTreeNode(new WrapperDefinition(definition, beanCount.get(autowired.getName())));
                    root.next.add(node);
                }
            }
        }

    }

    private List<SwiftBeanDefinition> getBeanDefinitionsByType(Class<?> clazz) {
        List<SwiftBeanDefinition> swiftBeanDefinitionList = new ArrayList<>();
        List<String> beanNames = SwiftBeanRegistry.getInstance().getBeanNamesByType(clazz);
        for (String beanName : beanNames) {
            swiftBeanDefinitionList.add(SwiftBeanRegistry.getInstance().getBeanDefinition(beanName));
        }
        return swiftBeanDefinitionList;
    }


    /*
     * 后序遍历该树，构造整个依赖树
     * */
    public void createBeansByTree(DependencyTreeNode root) {
        if (root == null) {
            return;
        }

        for (DependencyTreeNode node : root.next) {
            createBeansByTree(node);
        }

        putBeanToContainer(root);

    }

    private void putBeanToContainer(DependencyTreeNode root) {
        try {
            SwiftBeanDefinition definition = root.getWrapperDefinition().getDefinition();
            if (beanNamesLoaded.contains(definition.getBeanName())) {
                return;
            }
            Object singletonObject = createBean(definition.getClazz(), definition.getAutowiredFields());
            singletonObjects.put(definition.getBeanName(), singletonObject);
            beanNamesLoaded.add(definition.getBeanName());
        } catch (Exception ignore) {
            SwiftLoggers.getLogger().debug(ignore);
        }
    }


    public <T> T createBean(Class<T> tClass, Map<Field, String> fields) throws Exception {

        checkModifier(tClass); //如果注解了SwiftBean的类不是public的，抛出异常
        checkConstructor(tClass); //检查是否包含一个空的构造函数

        final Constructor<T> declaredConstructor = tClass.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        T bean = declaredConstructor.newInstance();


        for (Field field : fields.keySet()) {
            field.setAccessible(true);
            String beanName = fields.get(field);
            field.set(bean, singletonObjects.get(beanName));
        }

        return bean;
    }

    private void checkModifier(Class<?> tClass) {
        if (!Modifier.isPublic(tClass.getModifiers())) {
            throw new InitClassException(tClass.getName() + " is not public, can't modifier by reflection");
        }
    }

    private void checkConstructor(Class<?> tClass) {
        Constructor[] constructors = tClass.getDeclaredConstructors();
        boolean flag = false;
        for (Constructor constructor : constructors) {
            Class[] classes = constructor.getParameterTypes();
            if (classes.length == 0) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            throw new InitClassException(tClass.getName() + " does not have a empty constructor，can't create bean for this class");
        }

    }
}
