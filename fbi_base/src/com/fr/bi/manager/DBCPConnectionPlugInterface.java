package com.fr.bi.manager;


import java.util.Set;

/**
 * Created by neil on 2017/8/4.
 */
public interface DBCPConnectionPlugInterface {

    Set<String> getDriversNotTestOnBorrow();

}
