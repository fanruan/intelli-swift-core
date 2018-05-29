package com.fr.swift.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServiceInfoServiceImpl implements SwiftServiceInfoService {

    private SwiftServiceInfoDao serviceInfoDao;

    public SwiftServiceInfoServiceImpl() {
        this.serviceInfoDao = new SwiftServiceInfoDaoImpl();
    }


    @Override
    public void saveOrUpdateServiceInfo(SwiftServiceInfoBean serviceInfoBean) throws SQLException {
        serviceInfoDao.saveOrUpdate(serviceInfoBean.convert());
    }

    @Override
    public void removeServiceInfo(SwiftServiceInfoBean serviceInfoBean) throws SQLException {
        serviceInfoDao.deleteById(serviceInfoBean.convert().getId());
    }

    @Override
    public SwiftServiceInfoBean getServiceInfo(SwiftServiceInfoBean serviceInfoBean) throws SQLException {
        return serviceInfoDao.select(serviceInfoBean.convert().getId()).convert();
    }

    @Override
    public List<SwiftServiceInfoBean> getAllServiceInfo() {
        List<SwiftServiceInfoBean> beanList = new ArrayList<SwiftServiceInfoBean>();
        for (SwiftServiceInfoEntity entity : serviceInfoDao.find()) {
            beanList.add(entity.convert());
        }
        return beanList;
    }

    @Override
    public List<SwiftServiceInfoBean> getServiceInfoByService(String service) {
        List<SwiftServiceInfoBean> beanList = new ArrayList<SwiftServiceInfoBean>();
        for (SwiftServiceInfoEntity entity : serviceInfoDao.getServiceInfoByService(service)) {
            beanList.add(entity.convert());
        }
        return beanList;
    }
}
