package com.fr.bi.common.persistent.json.generator;


import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Connery on 2016/1/19.
 */
public abstract class JSONObjectWriter {
    protected BIBeanJSONWriterWrapper writerWrapper;
    protected Map<String, List<BIBeanJSONWriterWrapper>> disposedBeans;

    public JSONObjectWriter(BIBeanJSONWriterWrapper writerWrapper, Map<String, List<BIBeanJSONWriterWrapper>> disposedBeans) {
        this.writerWrapper = writerWrapper;
        this.disposedBeans = disposedBeans;
    }

    public Object generateJSONObject() throws JSONException {
        if (isDisposed(writerWrapper.getBean())) {
            writerWrapper.setSampleModel(true);
        } else {
            setDisposed(writerWrapper);
        }
        if (writerWrapper.isUseJsonKey()) {
            BINonValueUtils.checkNull(writerWrapper.getJsonKey());
        }
        return dealWithContent();
    }

    protected abstract Object dealWithContent() throws JSONException;

    protected Boolean isDisposed(Object obj) {

        if (disposedBeans.containsKey(obj.getClass().getName())) {
            Iterator<BIBeanJSONWriterWrapper> iterator = disposedBeans.get(obj.getClass().getName()).iterator();
            while (iterator.hasNext()) {
                BIBeanJSONWriterWrapper wrapper = iterator.next();
                if (wrapper.getBean() == obj) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setDisposed(BIBeanJSONWriterWrapper wrapper) {

        if (!disposedBeans.containsKey(wrapper.getBean().getClass().getName())) {
            disposedBeans.put(wrapper.getBean().getClass().getName(), new ArrayList<BIBeanJSONWriterWrapper>());
        }
        disposedBeans.get(wrapper.getBean().getClass().getName()).add(wrapper);
    }
}