package com.fr.bi.property;

import java.util.List;
import java.util.Map;

/**
 * This class created on 2017/8/17.
 *
 * @author Each.Zhang
 */
public interface PropertyManager<T> {

    List<T> getProperties(String paramType);

}
