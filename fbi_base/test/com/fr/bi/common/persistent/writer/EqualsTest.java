package com.fr.bi.common.persistent.writer;

import com.fr.bi.common.world.people.Person;
import com.fr.bi.stable.utils.algorithem.BIComparatorUtils;
import junit.framework.TestCase;

/**
 * Created by Connery on 2016/1/2.
 */
public class EqualsTest extends TestCase {
    public void testEqual() {
        IterablePart part = IterablePart.generateTwoPersonConnect();
        IterablePart part2 = IterablePart.generateTwoPersonConnect();
        assertTrue(BIComparatorUtils.isExactlyEquals(part, part2));
        part.getPersons().add(Person.getAB());
        assertFalse(BIComparatorUtils.isExactlyEquals(part, part2));
    }
}