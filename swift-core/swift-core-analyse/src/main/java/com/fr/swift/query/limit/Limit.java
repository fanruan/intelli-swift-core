package com.fr.swift.query.limit;

import java.io.Serializable;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/11/26
 */
public interface Limit extends Serializable {

    int start();

    int end();
}
