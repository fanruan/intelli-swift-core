package com.fr.bi.base;

import com.fr.bi.exception.BIAmountLimitUnmetException;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;

import javax.activation.UnsupportedDataTypeException;
import java.security.MessageDigest;
import java.util.Iterator;

/**
 * 替代原有的MD5，
 * 主要作用是，标识当前对象的源。
 * 当前两个对象可能equals方法不同。但是需要获得同一个数据源或者其他同一个对象。
 * <p/>
 * TODO 起个好理解的名字
 * TODO 通过其他方法实现，去掉当前类
 * Created by Connery on 2015/12/22.
 */
public class BIBasicCore extends BICore {
    public static BIBasicCore EMPTY_CORE = new BIBasicCore();

    public static BICore generateValueCore(String value) {
        BIBasicCore core = new BIBasicCore();
        core.value = new BIBasicIdentity(value);
        return core;
    }

    public BIBasicCore() {
    }

    public BIBasicCore(Object... attributes) throws UnsupportedDataTypeException, BIAmountLimitUnmetException {
        super(attributes);
    }

    @Override

    protected BIBasicIdentity computeValue() {
        MessageDigest digest = BIMD5Utils.getMessageDigest();
        digest.update("".getBytes());
        Iterator<Object> it = coreAttributes.iterator();
        while (it.hasNext()) {
            String str = getObjectStringValue(it.next());
            digest.update(str.getBytes());
        }
        return generateValue(BIMD5Utils.getMD5String(digest.digest()));
    }

    private String getObjectStringValue(Object object) {
        return object.toString();
    }


    private BIBasicIdentity generateValue(String id) {
        return new BIBasicIdentity(id);
    }


    @Override
    protected boolean isTypeSupport(Object value) throws UnsupportedDataTypeException {
        return true;

    }


}