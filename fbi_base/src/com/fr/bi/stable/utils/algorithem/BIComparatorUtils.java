package com.fr.bi.stable.utils.algorithem;


import com.fr.bi.common.persistent.xml.writer.BIBeanXMLWriterWrapper;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIFieldUtils;
import com.fr.general.ComparatorUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Connery on 2016/1/2.
 */
public class BIComparatorUtils {


    /**
     * 注意：性能不高。对象复杂的时候还是通过equals方法比较
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean isExactlyEquals(Object obj1, Object obj2) {
        return isExactlyEquals(obj1, obj2, new HashSet<Object>());
    }

    public static boolean isExactlyEquals(Object obj1, Object obj2, HashSet<Object> disposedObj) {
        if (obj1 == obj2) {
            return true;
        } else if (obj1 == null || obj2 == null) {
            return obj1 == null && obj2 == null;
        } else if (!ComparatorUtils.equals(obj1.getClass(), obj2.getClass())) {
            return false;
        } else {
            if (BIFieldUtils.isBasicType(obj1.getClass()) && BIFieldUtils.isBasicType(obj2.getClass())) {
                return ComparatorUtils.equals(obj1, obj2);
            } else {
                if (isDisposed(obj1, disposedObj) && isDisposed(obj2, disposedObj)) {
                    return true;
                } else if (!isDisposed(obj1, disposedObj) && !isDisposed(obj2, disposedObj)) {
                    add(obj1, obj2, disposedObj);
                    if (obj1 instanceof Collection) {
                        return compareCollectionObject(obj1, obj2, disposedObj);
                    } else if (obj1 instanceof Map) {
                        return compareMapObject(obj1, obj2, disposedObj);
                    } else {
                        return compareNormalObject(obj1, obj2, disposedObj);
                    }
                } else {
                    return false;
                }
            }
        }
    }

    private static boolean isDisposed(Object obj, HashSet<Object> disposedObj) {
        Iterator it = disposedObj.iterator();
        while (it.hasNext()) {
            if (obj == it.next()) {
                return true;
            }
        }
        return false;
    }

    private static boolean compareMapObject(Object obj1, Object obj2, HashSet<Object> disposedObj) {
        if (((Map) obj1).size() != ((Map) obj2).size()) {
            return false;
        } else {
            Iterator<Map.Entry> it = ((Map) obj1).entrySet().iterator();
            Map map2 = ((Map) obj2);
            while (it.hasNext()) {
                Map.Entry entry = it.next();
                if (map2.containsKey(entry.getKey())) {
                    if (!isExactlyEquals(map2.get(entry.getKey()), entry.getValue())) {
                        return false;
                    }
                }
            }
            return true;
        }

    }

    private static boolean compareNormalObject(Object obj1, Object obj2, HashSet<Object> disposedObj) {
        try {
            BIBeanXMLWriterWrapper writerWrapper1 = new BIBeanXMLWriterWrapper(obj1);
            BIBeanXMLWriterWrapper writerWrapper2 = new BIBeanXMLWriterWrapper(obj2);
            Iterator<Field> it = writerWrapper1.getPropertyDescriptorFields().iterator();
            while (it.hasNext()) {
                Field field = it.next();
                Object value1 = writerWrapper1.getOriginalValue(field);
                Object value2 = writerWrapper2.getOriginalValue(writerWrapper2.getField(field.getName()));
                if (!isExactlyEquals(value1, value2, disposedObj)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return false;
        }
    }

    private static boolean compareCollectionObject(Object obj1, Object obj2, HashSet<Object> disposedObj) {
        if (((Collection) obj1).size() != ((Collection) obj2).size()) {
            return false;
        }
        if (obj1 instanceof List) {
            List list1 = ((List) obj1);
            List list2 = ((List) obj2);

            for (int i = 0; i < list1.size(); i++) {
                Object value1 = list1.get(i);
                Object value2 = list2.get(i);
                if (!isExactlyEquals(value1, value2, disposedObj)) {
                    return false;
                }
            }
            return true;

        } else {
            Iterator it1 = ((Collection) obj1).iterator();
            while (it1.hasNext()) {
                Iterator it2 = ((Collection) obj2).iterator();
                while (it2.hasNext()) {
                    Object value1 = it1.next();
                    Object value2 = it1.next();
                    if (!isExactlyEquals(value1, value2, disposedObj)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private static void add(Object obj1, Object obj2, HashSet<Object> disposedObj) {
        disposedObj.add(obj1);
        disposedObj.add(obj2);
    }
}