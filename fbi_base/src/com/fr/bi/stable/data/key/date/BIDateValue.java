package com.fr.bi.stable.data.key.date;

import com.fr.bi.common.BICoreService;

import java.io.Serializable;

/**
 * Created by 小灰灰 on 2016/1/4.
 */
public interface BIDateValue<T> extends BICoreService,Serializable{

    T getValue();

}