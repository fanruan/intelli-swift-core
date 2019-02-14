package com.fr.swift.source.core;

import com.fr.swift.exception.AmountLimitUnmetException;
import com.fr.swift.log.SwiftLoggers;

import javax.activation.UnsupportedDataTypeException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * 替代原有的MD5，
 * 主要作用是，标识当前对象的源。
 * 当前两个对象可能equals方法不同。但是需要获得同一个数据源或者其他同一个对象。
 * <p/>
 * TODO 起个好理解的名字
 * TODO 通过其他方法实现，去掉当前类
 * Created by Connery on 2015/12/22.
 */
public class BasicCore extends Core {
    public static BasicCore EMPTY_CORE = new BasicCore();

    public static Core generateValueCore(String value) {
        BasicCore core = new BasicCore();
        core.value = value;
        return core;
    }

    public BasicCore() {
    }

    public BasicCore(Object... attributes) throws UnsupportedDataTypeException, AmountLimitUnmetException {
        super(attributes);
    }

    @Override

    protected String computeValue() {
        MessageDigest digest = MD5Utils.getMessageDigest();
        digest.update("".getBytes());
        for (Object coreAttribute : coreAttributes) {
            String str = getObjectStringValue(coreAttribute);
            //pony MD5要指定编码
            try {
                digest.update(str.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
        return MD5Utils.getMD5String(digest.digest());
    }

    private String getObjectStringValue(Object object) {
        return object.toString();
    }

    @Override
    protected boolean isTypeSupport(Object value) throws UnsupportedDataTypeException {
        return true;

    }


}