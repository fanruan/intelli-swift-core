package com.fr.bi.base;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connery on 2016/1/25.
 */
public class BICoreGeneratorTest extends TestCase {
    public void testGenerateEmpty() {
        BICoreObject4GeneratorTest test = new BICoreObject4GeneratorTest(null, null, null);
        BICoreGenerator generator = new BICoreGenerator(test);
        assertEquals(generator.fetchObjectCore().getID().getIdentityValue(), "Empty");
    }

    public void testGenerateOneValue() {
        try {
            BICoreObject4GeneratorTest test = new BICoreObject4GeneratorTest("a", null, null);
            BICoreObject4GeneratorTest test2 = new BICoreObject4GeneratorTest("a", null, null);
            BICoreObject4GeneratorTest test3 = new BICoreObject4GeneratorTest("a", "a", null);

            BICoreGenerator generator = new BICoreGenerator(test);
            BICoreGenerator generator2 = new BICoreGenerator(test2);
            BICoreGenerator generator3 = new BICoreGenerator(test3);
            assertEquals(generator.fetchObjectCore().getID().getIdentityValue(), generator2.fetchObjectCore().getID().getIdentityValue());
            assertTrue(!ComparatorUtils.equals(generator.fetchObjectCore().getID().getIdentityValue(), generator3.fetchObjectCore().getID().getIdentityValue()));
        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
        }
    }

    public void testGenerateRegisterValue() {
        try {
            BICoreObject4GeneratorTest test = new BICoreObject4GeneratorTest("a", null, null);
            BICoreObject4GeneratorTest test3 = new BICoreObject4GeneratorTest("a", "a", null);

            BICoreGenerator generator = new BICoreGenerator(test);
            BICoreGenerator generator3 = new BICoreGenerator(test3);
            assertTrue(!ComparatorUtils.equals(generator.fetchObjectCore().getID().getIdentityValue(), generator3.fetchObjectCore().getID().getIdentityValue()));
            generator.addAdditionalAttribute("a");
            assertTrue(ComparatorUtils.equals(generator.fetchObjectCore().getID().getIdentityValue(), generator3.fetchObjectCore().getID().getIdentityValue()));
        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
        }
    }

    public void testGenerateIteratorValue() {
        try {
            List<String> list = new ArrayList<String>();
            list.add("a");
            list.add("b");
            BICoreObject4GeneratorTest test = new BICoreObject4GeneratorTest("a", null, null);
            BICoreObject4GeneratorTest test3 = new BICoreObject4GeneratorTest("a", null, null);
            test.setList(list);

            BICoreGenerator generator = new BICoreGenerator(test);
            BICoreGenerator generator3 = new BICoreGenerator(test3);
            assertTrue(!ComparatorUtils.equals(generator.fetchObjectCore().getID().getIdentityValue(), generator3.fetchObjectCore().getID().getIdentityValue()));
            test3.setList(list);
            generator3 = new BICoreGenerator(test3);
            assertTrue(ComparatorUtils.equals(generator.fetchObjectCore().getID().getIdentityValue(), generator3.fetchObjectCore().getID().getIdentityValue()));
        } catch (Exception ignore) {
            BILogger.getLogger().error(ignore.getMessage(), ignore);
        }
    }
}