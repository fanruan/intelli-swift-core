package com.fr.bi.property;

import java.util.*;

/**
 * This class created on 2017/8/16.
 *
 * @author Each.Zhang
 */
public interface PropertyOperate {

    List read();

    void write(List<PropertiesConfig> propertiesConfigList);
}
