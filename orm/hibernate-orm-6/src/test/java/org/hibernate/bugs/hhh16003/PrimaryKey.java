package org.hibernate.bugs.hhh16003;
/*
 * @COPYRIGHT (C) 2018 Schenker AG
 *
 * All rights reserved
 */

import jakarta.persistence.Embeddable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

/**
 * Technical primary key for entities encapsulating a UUID.
 */
@Embeddable
public class PrimaryKey {

    private final String value;

    private PrimaryKey() {
        value = randomUUID().toString();
    }

    private PrimaryKey(String initialValue) {
        value = requireNonNull(initialValue, "value must not be null").trim();
    }

    public static PrimaryKey newPrimaryKey() {
        return new PrimaryKey();
    }

    public static PrimaryKey newPrimaryKey(String initialValue) {
        return new PrimaryKey(initialValue);
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PrimaryKey)) {
            return false;
        }
        PrimaryKey other = (PrimaryKey)obj;
        return Objects.equals(value, other.value);
    }
}
