package com.fr.bi.base.key;

import com.fr.stable.StringUtils;

import java.io.Serializable;

/**
 * BI Key值接口
 * Created by GUY on 2015/4/13.
 */
public interface BIKey extends Serializable{

    BIKey DEFAULT = new BIKey() {

        @Override
        public String getKey() {
            return StringUtils.EMPTY;
        }
    };

    String getKey();
}