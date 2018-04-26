package com.finebi.conf.pack;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.pack.FineBusinessPackageImp;
import com.finebi.conf.provider.SwiftPackageConfProvider;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.fr.swift.config.TestConfDb;
import junit.framework.TestCase;

/**
 * This class created on 2018-1-23 16:46:11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class BusinessPackageProviderTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestConfDb.setConfDb();
    }

    public void testWritePackage() throws FineEngineException {
        SwiftPackageConfProvider provider = new SwiftPackageConfProvider();
        for (FineBusinessPackage fineBusinessPackage : provider.getAllPackage()) {
            provider.removePackage(fineBusinessPackage.getId());
        }
        assertEquals(provider.getAllPackage().size(), 0);

        FineBusinessPackage fineBusinessPackage = new FineBusinessPackageImp("001", "lucifer", -999, 1000, FineEngineType.Cube, 1000);
        provider.addPackage(fineBusinessPackage, null);
        assertEquals(provider.getAllPackage().size(), 1);
        assertNotNull(provider.getSinglePackage("001"));
        assertNotNull(provider.getPackageByName("lucifer"));
        provider.removePackage("001");
        assertEquals(provider.getAllPackage().size(), 0);
    }
}
