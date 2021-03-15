package com.fr.swift.cloud.basics.base.selector;

import com.fr.swift.cloud.basics.Selector;
import com.fr.swift.cloud.basics.UrlFactory;
import com.fr.swift.cloud.local.LocalUrlFactory;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class UrlSelector implements Selector<UrlFactory> {

    private UrlFactory urlFactory;

    private UrlSelector() {
        this.urlFactory = new LocalUrlFactory();
    }

    private static final UrlSelector INSTANCE = new UrlSelector();

    public static UrlSelector getInstance() {
        return INSTANCE;
    }

    @Override
    public UrlFactory getFactory() {
        synchronized (UrlSelector.class) {
            return urlFactory;
        }
    }

    @Override
    public void switchFactory(UrlFactory factory) {
        synchronized (UrlSelector.class) {
            this.urlFactory = factory;
        }
    }
}
