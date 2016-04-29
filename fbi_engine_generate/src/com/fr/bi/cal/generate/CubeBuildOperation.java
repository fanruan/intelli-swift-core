package com.fr.bi.cal.generate;

/**
 * Created by FineSoft on 2015/7/2.
 */
public interface CubeBuildOperation {
    Object getData();

    Object process(Object tableKeys);
}