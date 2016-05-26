package com.fr.bi.cal.generate.relation.inuserelation;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.stable.utils.BIReportUtils;
import com.fr.bi.fs.BIDAOUtils;
import com.fr.bi.fs.BIReportNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/2/15.
 */
public class UseFieldGetter {
    private BIUser user;

    public UseFieldGetter(BIUser user) {
        this.user = user;
    }

    public Set<BusinessField> getUsedField() {
        Set<BusinessField> fields = new HashSet<BusinessField>();
        try {
            //Todo 权限过滤用到的字段
            List<BIReportNode> sysNodeList = BIDAOUtils.findByUserID(user.getUserId());
            dealWithBIReportNodeList(sysNodeList, fields, user.getUserId());
        } catch (Throwable e) {
        }
        return fields;

    }

    private void dealWithBIReportNodeList(List<BIReportNode> ilist, Set<BusinessField> relationInUse, long userId) {
        Iterator<BIReportNode> iter = ilist.iterator();
        while (iter.hasNext()) {
            BIReportNode node = iter.next();
            try {
                relationInUse.addAll(BIReportUtils.getUsedFieldByReportNode(node, userId));
            } catch (Exception e) {
            }
        }
    }
}