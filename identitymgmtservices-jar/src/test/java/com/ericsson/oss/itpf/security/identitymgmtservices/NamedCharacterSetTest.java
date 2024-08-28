/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.itpf.security.identitymgmtservices;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author eshihao
 * 
 */
public class NamedCharacterSetTest {

    @Test
    public void NamedCharacterSetConstructorTest() {
        final String chars = "abcdefghijklmnABCD";
        try {
            final NamedCharacterSet ns = new NamedCharacterSet("alpha", chars.toCharArray());
            Assert.assertArrayEquals(ns.getCharacters(), chars.toCharArray());
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void NamedCharacterSetWithNameNullTest() {
        try {
            new NamedCharacterSet(null, "abcdefghijklmnABCD".toCharArray());
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void NamedCharacterSetEmptyNameTest() {
        try {
            new NamedCharacterSet("", "abcdefghijklmnABCD".toCharArray());
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void NamedCharacterSetNotAlphaCharsTest() {
        try {
            new NamedCharacterSet("Wojtek*", "abcdefghijklmnABCD".toCharArray());
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void NamedCharacterSetWithRandomTest() {
        try {
            final String chars = "abcdefghijklmnABCD";
            final String name = "alpha";
            final NamedCharacterSet ns = new NamedCharacterSet(name, chars.toCharArray());
            Assert.assertNotNull(ns.getRandomCharacter());
            Assert.assertArrayEquals(ns.getCharacters(), chars.toCharArray());
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void NamedCharacterSetWithRandomNullTest() {
        try {
            new NamedCharacterSet(null, "abcdefghijklmnABCD".toCharArray());
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void NamedCharacterSetWithRandomEmptyNameTest() {
        try {
            new NamedCharacterSet("", "abcdefghijklmnABCD".toCharArray());
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void NamedCharacterSetWithRandomNotAlphaTest() {
        try {
            new NamedCharacterSet("Wojtek*", "abcdefghijklmnABCD".toCharArray());
            Assert.fail();
        } catch (final IllegalArgumentException e) {
        }
    }

    @Test
    public void getNameTest() {
        final String name = "numeric";
        final NamedCharacterSet ns = new NamedCharacterSet(name, "0123456789".toCharArray());
        Assert.assertTrue(name.equals(ns.getName()));
    }

    @Test
    public void getRandomCharacterCharactersNullTest() {
        final String name = "numeric";
        final NamedCharacterSet ns = new NamedCharacterSet(name, null);
        Assert.assertEquals(0, ns.getRandomCharacter());
    }

    @Test
    public void getRandomCharacterEmptyCharactersTest() {
        final NamedCharacterSet ns = new NamedCharacterSet("numeric", new char[] {});
        Assert.assertEquals(0, ns.getRandomCharacter());
    }

    @Test
    public void getRandomCharactersTest() {
        final String chars = "0123456789";
        final NamedCharacterSet ns = new NamedCharacterSet("numeric", chars.toCharArray());
        final StringBuilder sb = new StringBuilder();
        ns.getRandomCharacters(sb, 1);
        Assert.assertTrue(chars.contains(sb));
    }

    @Test
    public void encodeTest() {
        final String chars = "0123456789";
        final String name = "numeric";
        final NamedCharacterSet ns = new NamedCharacterSet(name, chars.toCharArray());
        Assert.assertEquals(name + ":" + chars, ns.encode());
    }

    @Test
    public void decodeCharacterSetsNullTest() {
        try {
            NamedCharacterSet.decodeCharacterSets(null);
            Assert.fail();
        } catch (final Exception e) {
        }
    }

    @Test
    public void decodeCharacterSetsTest() {
        try {
            final SortedSet<String> set = new TreeSet<String>();
            set.add("te:st");
            set.add("te:xt");
            NamedCharacterSet.decodeCharacterSets(set);
        } catch (final Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void decodeCharacterSetsNotColonTest() {
        try {
            final SortedSet<String> set = new TreeSet<String>();
            set.add("test");
            set.add("text");
            NamedCharacterSet.decodeCharacterSets(set);
            Assert.fail();
        } catch (final Exception e) {
            Assert.assertTrue("err charset no colon".equals(e.getMessage()));
        }
    }

    @Test
    public void decodeCharacterSetsColonAtFirstTest() {
        try {
            final SortedSet<String> set = new TreeSet<String>();
            set.add(":text");
            NamedCharacterSet.decodeCharacterSets(set);
            Assert.fail();
        } catch (final Exception e) {
            Assert.assertTrue("err charset no name".equals(e.getMessage()));
        }
    }

    @Test
    public void decodeCharacterSetsColonAtEndTest() {
        try {
            final SortedSet<String> set = new TreeSet<String>();
            set.add("text:");
            NamedCharacterSet.decodeCharacterSets(set);
            Assert.fail();
        } catch (final Exception e) {
            Assert.assertTrue("err chartset no chars".equals(e.getMessage()));
        }
    }

}
