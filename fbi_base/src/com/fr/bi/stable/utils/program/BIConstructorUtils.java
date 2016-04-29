package com.fr.bi.stable.utils.program;

import com.fr.bi.stable.utils.BIUserUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * 构建对象
 * 这里不分析构造函数是否可见，一律可见处理
 * 可见性防止对结构不清晰而误用，
 * 既然已经采用反射来构造函数了，认为使用者了解对象内部构造。
 * Created by Connery on 2015/12/4.
 */
public class BIConstructorUtils {

    public static Constructor chosePublicConstructor(Class clazz, Class[] typs) throws NoSuchMethodException {
        if (typs == null) {
            typs = new Class[0];
        }
        return dealWithPrimitiveParam(clazz, typs);
    }

    public static Constructor choseAllConstructor(Class clazz, Class[] types) throws NoSuchMethodException {
        return dealWithPrimitiveParam(clazz.getDeclaredConstructors(), types);
    }

    public static <T, A extends T> T constructObject(Class<A> c, T manager) {
        if (manager != null) {
            return manager;
        }
        synchronized (c) {
            if (manager == null) {
                try {
                    Constructor<A> constructor = c.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    manager = constructor.newInstance();
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
            }
            return manager;
        }
    }

    public static <T, A extends T> T constructObject(long userId, Class<A> c, Map<Long, T> userMap) {
        return constructObject(userId, c, userMap, true);
    }

    public static <T, A extends T> T constructObject(long userId, Class<A> c, Map<Long, T> userMap, boolean allAdmin) {
        synchronized (userMap) {
            Long key = userId;
            boolean useAdministrator = BIUserUtils.isAdministrator(userId) && allAdmin;
            if (useAdministrator) {
                key = UserControl.getInstance().getSuperManagerID();
            }
            T manager = userMap.get(key);
            if (manager == null) {
                try {
                    Constructor<A> constructor = c.getDeclaredConstructor(long.class);
                    constructor.setAccessible(true);
                    manager = constructor.newInstance(key);
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
                userMap.put(key, manager);
            }
            return manager;
        }
    }

    /**
     * 这里不能用多态，因为会把变参定为Object[]
     * 处理非public的构造函数
     *
     * @param clazz
     * @param parameter
     * @return
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object constructObjectWithParameter(Class clazz, Object... parameter) throws NoSuchMethodException,
            InstantiationException, InvocationTargetException, IllegalAccessException {
        Class[] types = new Class[parameter.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = parameter[i].getClass();
        }
        Constructor constructor = BIConstructorUtils.choseAllConstructor(clazz, types);
        constructor.setAccessible(true);
        return constructor.newInstance(parameter);
    }

    private static boolean compareDouble2Double(String a_1, String a_2, String b_1, String b_2) {
        return ((compareString(a_1, b_1)) || compareString(a_1, b_2)) && ((compareString(a_2, b_1)) || (compareString(a_2, b_2)));
    }

    private static boolean compareString(String a, String b) {
        return ComparatorUtils.equals(a, b);
    }

    public static Boolean isAbstract(Class clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * 处理基础类型装包的问题。
     *
     * @param targetClass
     * @param paramClass
     * @return
     * @throws NoSuchMethodException
     */
    protected static Constructor dealWithPrimitiveParam(Constructor[] constructors, Class[] paramClass) throws NoSuchMethodException {
        boolean find;
        for (int i = 0; i < constructors.length; i++) {
            Constructor constructor = constructors[i];
            //函数名要相同
            Class[] declaredParamTypes = constructor.getParameterTypes();
            find = true;
            if (paramClass.length == declaredParamTypes.length) { //函数的参数个数要相同
                for (int j = 0; j < paramClass.length; j++) {
                    Class c_a = paramClass[j];
                    Class c_b = declaredParamTypes[j];
                    if (compareParameter(c_b, c_a)) {//参数类型要相同，外加基本数据类型判断
                        continue;
                    } else if (compareDouble2Double(c_a.getName(), c_b.getName(), Integer.class.getName(), int.class.getName())) {
                        continue;
                    } else if (compareDouble2Double(c_a.getName(), c_b.getName(), Boolean.class.getName(), boolean.class.getName())) {
                        continue;
                    } else if (compareDouble2Double(c_a.getName(), c_b.getName(), Character.class.getName(), char.class.getName())) {
                        continue;
                    } else if (compareDouble2Double(c_a.getName(), c_b.getName(), Long.class.getName(), long.class.getName())) {
                        continue;
                    } else if (compareDouble2Double(c_a.getName(), c_b.getName(), Float.class.getName(), float.class.getName())) {
                        continue;
                    } else if (compareDouble2Double(c_a.getName(), c_b.getName(), Double.class.getName(), double.class.getName())) {
                        continue;
                    } else if (compareDouble2Double(c_a.getName(), c_b.getName(), Byte.class.getName(), byte.class.getName())) {
                        continue;
                    } else if (compareDouble2Double(c_a.getName(), c_b.getName(), Short.class.getName(), short.class.getName())) {
                        continue;
                    } else {
                        find = false;
                        break;
                    }
                }
                if (find) {
                    return constructor;
                }
            }
        }
        throw new NoSuchMethodException();
    }

    private static boolean compareParameter(Class declared, Class parameter) {
        return declared.isAssignableFrom(parameter);
    }

    /**
     * 处理基础类型装包的问题。
     *
     * @param targetClass
     * @param paramClass
     * @return
     * @throws NoSuchMethodException
     */
    protected static Constructor dealWithPrimitiveParam(Class targetClass, Class[] paramClass) throws NoSuchMethodException {
        Constructor[] constructors = targetClass.getConstructors();
        try {
            return dealWithPrimitiveParam(constructors, paramClass);

        } catch (NoSuchMethodException e) {
            String errorMess = "check current class:" + targetClass.getName() + ", is there a constructor with parameters type: ";
            for (int i = 0; i < paramClass.length; i++) {
                errorMess += paramClass[i].getName();
                errorMess += " \n ";
            }
            throw new NoSuchMethodException(errorMess);
        }
    }

    public static <T> T constructObject(Class<T> clazz)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> constructor = choseAllConstructor(clazz, new Class[]{});
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    /**
     * 如果没有默认构造函数，或者只有带参数的构造函数
     * 那么构造值为空的对象。
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static <T> T forceConstructObject(Class<T> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        try {
            Constructor<T> constructor = choseAllConstructor(clazz, new Class[]{});
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            try {
                Constructor[] constructors = clazz.getDeclaredConstructors();
                Constructor<T> constructorWithPara = constructors[0];
                constructorWithPara.setAccessible(true);
                Class[] parameterTypes = constructorWithPara.getParameterTypes();
                Object[] value = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (BITypeUtils.isBasicValue(parameterTypes[i])) {
                        value[i] = BITypeUtils.generateBasicValue(parameterTypes[i]);
                    } else {
                        value[i] = null;
                    }
                }
                return constructorWithPara.newInstance(value);
            } catch (Exception ignore) {
                BILogger.getLogger().error(ignore.getMessage(), ignore);
            }
        }
        throw new NoSuchMethodException();
    }
}