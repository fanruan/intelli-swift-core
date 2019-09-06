package com.fr.swift.converter;

import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftService;
import com.fr.swift.util.ServiceBeanFactory;

import java.util.HashSet;
import java.util.List;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/6/2019
 */
public class ServiceTypeConverter {

    /**
     * @param type 服务的类型
     * @return SwiftService对象
     * 通过一个服务类型可能拿到不止一个SwiftService对象，详见ServiceBeanFactory.getSwiftServiceByNames方法
     */
    public static List<SwiftService> toSwiftService(final ServiceType type) {
        //需要检测的SwiftService值均小于8
        if (type.getType() < ServiceType.EXCEPTION.getType()) {

            return ServiceBeanFactory.getSwiftServiceByNames(new HashSet<String>() {
                {
                    add(type.getName());
                }
            });
        } else {
            throw new IllegalStateException("Unexpected value: " + type.getType());
        }
    }
}