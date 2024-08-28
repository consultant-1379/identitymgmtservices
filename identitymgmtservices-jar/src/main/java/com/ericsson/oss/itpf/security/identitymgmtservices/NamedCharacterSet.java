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

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.SortedSet;

/**
 * This class provides a data structure that makes it possible to associate a name with a given set of characters. The name must consist only of ASCII
 * alphabetic characters.
 */
public final class NamedCharacterSet {
    // The characters contained in this character set.
    private final char[] characters;

    // The random number generator to use with this character set.
    private SecureRandom random = new SecureRandom();

    // The name assigned to this character set.
    private String name;

    /**
     * Creates a new named character set with the provided information.
     * 
     * @param name
     *            The name for this character set.
     * @param characters
     *            The characters to include in this character set.
     * 
     * @throws IllegalArgumentException
     *             If the provided name contains one or more illegal characters.
     */
    public NamedCharacterSet(final String name, final char[] characters) {
        this.name = name;
        this.characters = (characters != null) ? characters.clone() : null;

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name argument null or empty.");
        }

        for (int i = 0; i < name.length(); i++) {
            if (!isAlpha(name.charAt(i))) {
                throw new IllegalArgumentException("err charset constructor invalid name");
            }
        }
    }

    /**
     * Retrieves the name for this character set.
     * 
     * @return The name for this character set.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the characters included in this character set.
     * 
     * @return The characters included in this character set.
     */
    public char[] getCharacters() {
        return Arrays.copyOf(this.characters, this.characters.length);
    }

    /**
     * Retrieves a character at random from this named character set.
     * 
     * @return The randomly-selected character from this named character set, return 0 if characters is null or empty;
     */
    public char getRandomCharacter() {
        if (characters == null || (characters.length == 0)) {
            return 0;
        }
        return characters[random.nextInt(characters.length)];
    }

    /**
     * Appends the specified number of characters chosen at random from this character set to the provided buffer.
     * 
     * @param buffer
     *            The buffer to which the characters should be appended.
     * @param count
     *            The number of characters to append to the provided buffer.
     */
    public void getRandomCharacters(final StringBuilder buffer, final int count) {
        if (characters == null || characters.length == 0) {
            return;
        }
        for (int i = 0; i < count; i++) {
            buffer.append(characters[random.nextInt(characters.length)]);
        }
    }

    /**
     * Encodes this character set to a form suitable for use in the value of a configuration attribute.
     * 
     * @return The encoded character set in a form suitable for use in the value of a configuration attribute.
     */
    public String encode() {
        return name + ":" + new String(characters);
    }

    /**
     * Decodes the values of the provided configuration attribute as a set of character set definitions.
     * 
     * @param values
     *            The set of encoded character set values to decode.
     * 
     * @return The decoded character set definitions.
     * 
     * @throws IllegalArgumentException
     *             If a problem occurs while attempting to decode the character set definitions.
     */
    public static NamedCharacterSet[] decodeCharacterSets(final SortedSet<String> values) {
        final NamedCharacterSet[] sets = new NamedCharacterSet[values.size()];
        int i = 0;
        for (final String value : values) {
            final int colonPos = value.indexOf(':');
            if (colonPos < 0) {
                throw new IllegalArgumentException("err charset no colon");
            } else if (colonPos == 0) {
                throw new IllegalArgumentException("err charset no name");
            } else if (colonPos == (value.length() - 1)) {
                throw new IllegalArgumentException("err chartset no chars");
            } else {
                final String name = value.substring(0, colonPos);
                final char[] characters = value.substring(colonPos + 1).toCharArray();
                sets[i] = new NamedCharacterSet(name, characters);
                i++;
            }
        }
        return sets;
    }

    private boolean isAlpha(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
}
