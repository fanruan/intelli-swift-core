package com.fr.bi.conf.base.pack;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIGroupTagName;
import com.fr.bi.conf.base.pack.group.BIBusinessGroupGetterService;
import com.fr.bi.conf.data.BIBusinessPackageTestTool;
import com.fr.bi.conf.data.pack.exception.BIGroupAbsentException;
import com.fr.bi.conf.data.pack.exception.BIGroupDuplicateException;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

import java.util.Set;

/**
 * Created by Connery on 2016/1/11.
 */
public class SystemPackageTest extends TestCase {
    private BISystemPackageConfigurationManager manager;
    private BIUser user;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        manager = new BISystemPackageConfigurationManager();
        user = new BIUser(999);
    }

    public void testAddGroup() {
        try {
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("a"), 0);
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("a")));
            assertFalse(manager.containGroup(user.getUserId(), new BIGroupTagName("ab")));
            assertFalse(manager.containGroup(user.getUserId(), new BIGroupTagName("abc")));
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("ab"), 0);
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("a")));
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("ab")));
            assertFalse(manager.containGroup(user.getUserId(), new BIGroupTagName("abc")));
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("abc"), 0);
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("a")));
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("ab")));
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("abc")));
            boolean isDuplicate = false;
            try {
                manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("abc"), 0);

            } catch (BIGroupDuplicateException ignore) {
                isDuplicate = true;
            }
            assertTrue(isDuplicate);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testRemoveGroup() {
        try {
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("TestGroup"), 0);
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("TestGroup")));
            manager.removeGroup(user.getUserId(), new BIGroupTagName("TestGroup"));
            assertFalse(manager.containGroup(user.getUserId(), new BIGroupTagName("TestGroup")));
            boolean isMismatch = false;
            try {
                manager.removeGroup(user.getUserId(), new BIGroupTagName("TestGroup"));
            } catch (BIGroupAbsentException ignore) {
                isMismatch = true;
            }
            assertTrue(isMismatch);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testAddPackage() {
        try {
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a"));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a")));
            assertFalse(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab")));
            assertFalse(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc")));

            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab"));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a")));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab")));
            assertFalse(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc")));

            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc"));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a")));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab")));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc")));
            boolean isDuplicate = false;
            try {
                manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc"));
            } catch (BIPackageDuplicateException ignore) {
                isDuplicate = true;
            }
            assertTrue(isDuplicate);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testRemovePackage() {
        try {
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc"));

            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a")));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab")));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc")));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID()));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab").getID()));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc").getID()));
            manager.removePackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc").getID());

            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a")));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab")));
            assertFalse(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc")));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID()));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab").getID()));
            assertFalse(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc").getID()));
            manager.removePackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab").getID());

            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a")));
            assertFalse(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab")));
            assertFalse(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc")));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID()));
            assertFalse(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab").getID()));
            assertFalse(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc").getID()));

            manager.removePackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID());
            assertFalse(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a")));
            assertFalse(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab")));
            assertFalse(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc")));
            assertFalse(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID()));
            assertFalse(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab").getID()));
            assertFalse(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc").getID()));
            boolean isMismatch = false;
            try {
                manager.removePackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID());
            } catch (BIPackageAbsentException ignore) {
                isMismatch = true;
            }
            assertTrue(isMismatch);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testRemovePackageWithOneGroup() {
        try {
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc"));
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("TestGroup"), 0);
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a")));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab")));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc")));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID()));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab").getID()));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc").getID()));
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("TestGroup")));
            try {
                manager.removePackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc").getID());
            } catch (BIPackageAbsentException ignore) {
                assertTrue(false);
            }

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testRemovePackageWithTwoGroup() {
        try {
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc"));
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("TestGroup"), 0);
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("TestGroup_2"), 0);

            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a")));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab")));
            assertTrue(manager.containPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc")));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID()));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab").getID()));
            assertTrue(manager.containPackageID(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc").getID()));
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("TestGroup")));
            try {
                manager.removePackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc").getID());
            } catch (BIPackageAbsentException ignore) {
                assertTrue(false);
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testRenameGroup() {
        try {
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("a"), 0);
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("ab"), 0);
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("abc"), 0);
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("a")));
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("ab")));
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("abc")));
            manager.renameGroup(user.getUserId(), new BIGroupTagName("a"), new BIGroupTagName("new_a"));
            manager.renameGroup(user.getUserId(), new BIGroupTagName("ab"), new BIGroupTagName("new_ab"));
            manager.renameGroup(user.getUserId(), new BIGroupTagName("abc"), new BIGroupTagName("new_abc"));
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("new_a")));
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("new_ab")));
            assertTrue(manager.containGroup(user.getUserId(), new BIGroupTagName("new_abc")));
            assertFalse(manager.containGroup(user.getUserId(), new BIGroupTagName("a")));
            assertFalse(manager.containGroup(user.getUserId(), new BIGroupTagName("ab")));
            assertFalse(manager.containGroup(user.getUserId(), new BIGroupTagName("abc")));
            boolean isMismatch = false;
            try {
                manager.renameGroup(user.getUserId(), new BIGroupTagName("a"), new BIGroupTagName("new_a"));
            } catch (BIGroupAbsentException ignore) {
                isMismatch = true;
            }
            assertTrue(isMismatch);

            boolean isDup = false;
            try {
                manager.renameGroup(user.getUserId(), new BIGroupTagName("new_a"), new BIGroupTagName("new_ab"));
            } catch (BIGroupDuplicateException ignore) {
                isDup = true;
            }
            assertTrue(isDup);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testGetAllPackage() {
        try {
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc"));
            Set<BIBusinessPackage> allPackages = manager.getAllPackages(user.getUserId());
            assertEquals(allPackages.size(), 3);
            assertTrue(allPackages.contains(BIBusinessPackageTestTool.generatePackage("a")));
            assertTrue(allPackages.contains(BIBusinessPackageTestTool.generatePackage("ab")));
            assertTrue(allPackages.contains(BIBusinessPackageTestTool.generatePackage("abc")));
            assertFalse(allPackages.contains(BIBusinessPackageTestTool.generatePackage("abcd")));
            allPackages.add(BIBusinessPackageTestTool.generatePackage("abcd"));
            Set<BIBusinessPackage> allPackages2 = manager.getAllPackages(user.getUserId());
            assertEquals(allPackages2.size(), 3);
            assertFalse(allPackages2.contains(BIBusinessPackageTestTool.generatePackage("abcd")));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abcd"));
            assertTrue(manager.getAllPackages(user.getUserId()).contains(BIBusinessPackageTestTool.generatePackage("abcd")));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testStickGroupTagOnPackage() {
        try {
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc"));
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("a"), 0);
            manager.stickGroupTagOnPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID(), new BIGroupTagName("a"));
            BIBusinessGroupGetterService group = manager.getGroup(user.getUserId(), new BIGroupTagName("a"));
            assertTrue(group.containPackage(BIBusinessPackageTestTool.generatePackage("a")));
            assertFalse(group.containPackage(BIBusinessPackageTestTool.generatePackage("ab")));
            assertFalse(group.containPackage(BIBusinessPackageTestTool.generatePackage("abc")));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testRemoveGroupTagOnPackage() {
        try {
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc"));
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("a"), 0);
            manager.stickGroupTagOnPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID(), new BIGroupTagName("a"));
            manager.stickGroupTagOnPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab").getID(), new BIGroupTagName("a"));
            manager.stickGroupTagOnPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("abc").getID(), new BIGroupTagName("a"));
            BIBusinessGroupGetterService group = manager.getGroup(user.getUserId(), new BIGroupTagName("a"));
            assertTrue(group.containPackage(BIBusinessPackageTestTool.generatePackage("a")));
            assertTrue(group.containPackage(BIBusinessPackageTestTool.generatePackage("ab")));
            assertTrue(group.containPackage(BIBusinessPackageTestTool.generatePackage("abc")));
            manager.removeGroupTagFromPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID(), new BIGroupTagName("a"));
            assertFalse(group.containPackage(BIBusinessPackageTestTool.generatePackage("a")));
            assertTrue(group.containPackage(BIBusinessPackageTestTool.generatePackage("ab")));
            assertTrue(group.containPackage(BIBusinessPackageTestTool.generatePackage("abc")));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testisPackageTaggedGroup() {
        try {
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a"));
            manager.addPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ab"));
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("a"), 0);

            manager.stickGroupTagOnPackage(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID(), new BIGroupTagName("a"));
            assertTrue(manager.isPackageTaggedSomeGroup(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID()));
            assertTrue(manager.isPackageTaggedSpecificGroup(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID(), new BIGroupTagName("a")));
            assertFalse(manager.isPackageTaggedSomeGroup(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ac").getID()));
            assertFalse(manager.isPackageTaggedSpecificGroup(user.getUserId(), BIBusinessPackageTestTool.generatePackage("ac").getID(), new BIGroupTagName("a")));
            Boolean isAbs = false;
            try {
                assertFalse(manager.isPackageTaggedSpecificGroup(user.getUserId(), BIBusinessPackageTestTool.generatePackage("a").getID(), new BIGroupTagName("ac")));
            } catch (BIGroupAbsentException ignore) {
                isAbs = true;
            }
            assertTrue(isAbs);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    public void testGetPackagePosition() {
        try {
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("a"), 0);
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("b"), 1);
            manager.createEmptyGroup(user.getUserId(), new BIGroupTagName("c"), 2);
            assertTrue(manager.getGroup(user.getUserId(), new BIGroupTagName("a")).getPosition() == 0);
            assertTrue(manager.getGroup(user.getUserId(), new BIGroupTagName("b")).getPosition() == 1);
            assertTrue(manager.getGroup(user.getUserId(), new BIGroupTagName("c")).getPosition() == 3);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}