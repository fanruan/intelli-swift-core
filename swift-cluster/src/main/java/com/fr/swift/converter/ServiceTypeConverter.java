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
    public static List<SwiftService> toSwiftService(ServiceType type) {
        final String serviceName;
        switch (type) {
            case DELETE:
                serviceName = "delete";
                break;
            case SERVER:
                serviceName = "server";
                break;
            case UPLOAD:
                serviceName = "upload";
                break;
            case ANALYSE:
                serviceName = "analyse";
                break;
            case COLLATE:
                serviceName = "collate";
                break;
            case HISTORY:
                serviceName = "history";
                break;
            case INDEXING:
                serviceName = "indexing";
                break;
            case REAL_TIME:
                serviceName = "realtime";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type.getType());
        }
        return ServiceBeanFactory.getSwiftServiceByNames(new HashSet<String>() {
            {
                add(serviceName);
            }
        });
    }
}