package com.fr.swift.executor.config;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;

import java.util.List;

/**
 * @author Heng.J
 * @date 2020/12/25
 * @description
 * @since swift-1.2.0
 */
@SwiftBean(name = "repeatTaskService")
public class RepeatTaskServiceImpl implements RepeatTaskService {

    private final SwiftDao<SwiftRepeatTaskEntity> dao = new SwiftDaoImpl<>(SwiftRepeatTaskEntity.class);

    @Override
    public void save(SwiftRepeatTaskEntity repeatTaskEntity) {
        dao.insert(repeatTaskEntity);
    }

    @Override
    public void delete(SwiftRepeatTaskEntity repeatTaskEntity) {
        dao.delete(repeatTaskEntity);
    }

    @Override
    public List<SwiftRepeatTaskEntity> getAllTasks() {
        return (List<SwiftRepeatTaskEntity>) dao.selectAll();
    }
}
