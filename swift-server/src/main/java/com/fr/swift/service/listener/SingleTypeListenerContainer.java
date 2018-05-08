package com.fr.swift.service.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by pony on 2017/11/13.
 */
public class SingleTypeListenerContainer implements Iterable<List<SwiftServiceListener>>{
    /**
     * 多线程，有序访问
     */
    private ConcurrentSkipListMap<EventOrder, List<SwiftServiceListener>> orderMap = new ConcurrentSkipListMap<EventOrder, List<SwiftServiceListener>>();

    public SingleTypeListenerContainer() {
        init();
    }

    private void init() {
        for (EventOrder order : EventOrder.values()){
            orderMap.put(order, new ArrayList<SwiftServiceListener>());
        }
    }

    public void addListener(SwiftServiceListener listener) {
        List<SwiftServiceListener> listenerList = orderMap.get(listener.getOrder());
        if (listenerList != null){
            listenerList.add(listener);
        }
    }


    @Override
    public Iterator<List<SwiftServiceListener>> iterator() {
        return orderMap.values().iterator();
    }
}
