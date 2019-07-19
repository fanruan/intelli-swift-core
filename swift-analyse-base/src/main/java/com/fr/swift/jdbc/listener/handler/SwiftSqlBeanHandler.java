package com.fr.swift.jdbc.listener.handler;

import com.fr.swift.jdbc.adaptor.bean.CreationBean;
import com.fr.swift.jdbc.adaptor.bean.DeletionBean;
import com.fr.swift.jdbc.adaptor.bean.DropBean;
import com.fr.swift.jdbc.adaptor.bean.InsertionBean;
import com.fr.swift.jdbc.adaptor.bean.SelectionBean;
import com.fr.swift.jdbc.adaptor.bean.TruncateBean;

/**
 * @author yee
 * @date 2019-07-20
 */
public interface SwiftSqlBeanHandler {
    void handle(SelectionBean bean);

    void handle(InsertionBean bean);

    void handle(CreationBean bean);

    void handle(DropBean bean);

    void handle(TruncateBean bean);

    void handle(DeletionBean bean);
}
