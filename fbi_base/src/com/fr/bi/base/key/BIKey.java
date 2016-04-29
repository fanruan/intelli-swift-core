package com.fr.bi.base.key;

import com.fr.stable.StringUtils;

/**
 * BI Key值接口
 * Created by GUY on 2015/4/13.
 */
public interface BIKey {

    BIKey DEFAULT = new BIKey() {

        @Override
        public String getKey() {
            return StringUtils.EMPTY;
        }
    };

    String getKey();
}