package com.fr.swift.conf.business.container;

import com.finebi.conf.structure.bean.pack.FineBusinessPackage;

import java.util.ArrayList;

/**
 * This class created on 2018-1-29 11:30:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class PackageContainer extends AbstractResourceContainer<FineBusinessPackage> {

    private PackageContainer() {
        super.resourceList = new ArrayList<FineBusinessPackage>();
    }

    private static class SingletonHolder {
        private static final PackageContainer INSTANCE = new PackageContainer();
    }

    public static final PackageContainer getContainer() {
        return PackageContainer.SingletonHolder.INSTANCE;
    }
}
