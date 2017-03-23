package com.fr.bi.cal.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Lucifer on 2017-3-22.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class Collection2StringUtils {

    public static <T> String collection2String(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return "empty";
        }
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<T> it = collection.iterator();
        while (it.hasNext()) {
            T t = it.next();
            stringBuffer.append(t.toString());
            if (it.hasNext()) {
                stringBuffer.append(",");
            }
        }
        return stringBuffer.toString();
    }
}
