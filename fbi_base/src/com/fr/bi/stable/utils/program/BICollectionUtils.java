package com.fr.bi.stable.utils.program;

import java.util.*;

/**
 * Created by Connery on 2015/12/23.
 */
public class BICollectionUtils {
    public static <T> T unmodifiedCollection(T collection) {
        if (collection instanceof List) {
            return (T) Collections.unmodifiableList((List) collection);
        } else if (collection instanceof Set) {
            if (collection instanceof SortedSet) {
                return (T) Collections.unmodifiableSortedSet((SortedSet) collection);
            }
            return (T) Collections.unmodifiableSet((Set) collection);
        } else {
            return (T) Collections.unmodifiableCollection((Collection)collection);
        }
    }

    private static Integer INTEGER_ONE = new Integer(1);
    public static final Collection EMPTY_COLLECTION = unmodifiedCollection(new ArrayList());

    public static Collection union(Collection a, Collection b) {
        ArrayList list = new ArrayList();
        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);
        HashSet elts = new HashSet(a);
        elts.addAll(b);
        Iterator it = elts.iterator();

        while (it.hasNext()) {
            Object obj = it.next();
            int i = 0;

            for (int m = Math.max(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
                list.add(obj);
            }
        }

        return list;
    }

    private static final int getFreq(Object obj, Map freqMap) {
        Integer count = (Integer) freqMap.get(obj);
        return count != null ? count.intValue() : 0;
    }

    public static Collection intersection(Collection a, Collection b) {
        ArrayList list = new ArrayList();
        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);
        HashSet elts = new HashSet(a);
        elts.addAll(b);
        Iterator it = elts.iterator();

        while (it.hasNext()) {
            Object obj = it.next();
            int i = 0;

            for (int m = Math.min(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
                list.add(obj);
            }
        }

        return list;
    }

    public static Collection disjunction(Collection a, Collection b) {
        ArrayList list = new ArrayList();
        Map mapa = getCardinalityMap(a);
        Map mapb = getCardinalityMap(b);
        HashSet elts = new HashSet(a);
        elts.addAll(b);
        Iterator it = elts.iterator();

        while (it.hasNext()) {
            Object obj = it.next();
            int i = 0;

            for (int m = Math.max(getFreq(obj, mapa), getFreq(obj, mapb)) - Math.min(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
                list.add(obj);
            }
        }

        return list;
    }

    public static Collection subtract(Collection a, Collection b) {
        ArrayList list = new ArrayList(a);
        Iterator it = b.iterator();

        while (it.hasNext()) {
            list.remove(it.next());
        }

        return list;
    }

    public static boolean containsAny(Collection coll1, Collection coll2) {
        Iterator it;
        if (coll1.size() < coll2.size()) {
            it = coll1.iterator();

            while (it.hasNext()) {
                if (coll2.contains(it.next())) {
                    return true;
                }
            }
        } else {
            it = coll2.iterator();

            while (it.hasNext()) {
                if (coll1.contains(it.next())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Map getCardinalityMap(Collection coll) {
        HashMap count = new HashMap();
        Iterator it = coll.iterator();

        while (it.hasNext()) {
            Object obj = it.next();
            Integer c = (Integer) ((Integer) count.get(obj));
            if (c == null) {
                count.put(obj, INTEGER_ONE);
            } else {
                count.put(obj, new Integer(c.intValue() + 1));
            }
        }

        return count;
    }
}