package com.fr.bi.stable.data.key.date;

import com.fr.bi.common.BICoreService;

/**
 * Created by 小灰灰 on 2016/1/4.
 */
public interface BIDateValue<T extends Number> extends BICoreService{

    T getValue();

}