package com.fr.swift.encryption;

import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import junit.framework.TestCase;

/**
 * This class created on 2018/3/23
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftEncryptionTest extends TestCase {

    public void testFormat() {
        assertEquals("0000", SwiftEncryption.formatString(0, 4));
        assertEquals("0009", SwiftEncryption.formatString(9, 4));
        assertEquals("0019", SwiftEncryption.formatString(19, 4));
        assertEquals("0219", SwiftEncryption.formatString(219, 4));
        assertEquals("1299", SwiftEncryption.formatString(1299, 4));
        assertEquals("11111", SwiftEncryption.formatString(11111, 4));
    }

    public void testEncryptField() {
        assertEquals(SwiftEncryption.encryptFieldId("DemoContract", "user"), "0012DemoContractuser");
        assertEquals(SwiftEncryption.encryptFieldId("A", "AAA"), "0001AAAA");
        assertEquals(SwiftEncryption.encryptFieldId("AA", "AA"), "0002AAAA");
        assertEquals(SwiftEncryption.encryptFieldId("AAA", "A"), "0003AAAA");
    }

    public void testDecryptField() {
        assertEquals(SwiftEncryption.decryptFieldId("0012DemoContractuser")[0], "DemoContract");
        assertEquals(SwiftEncryption.decryptFieldId("0012DemoContractuser")[1], "user");

        assertEquals(SwiftEncryption.decryptFieldId("0001AAAA")[0], "A");
        assertEquals(SwiftEncryption.decryptFieldId("0001AAAA")[1], "AAA");

        assertEquals(SwiftEncryption.decryptFieldId("0002AAAA")[0], "AA");
        assertEquals(SwiftEncryption.decryptFieldId("0002AAAA")[1], "AA");

        assertEquals(SwiftEncryption.decryptFieldId("0003AAAA")[0], "AAA");
        assertEquals(SwiftEncryption.decryptFieldId("0003AAAA")[1], "A");

    }
}
