/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.core.watcher.execution;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.UUID;

import static org.elasticsearch.xpack.core.watcher.support.Exceptions.illegalArgument;

/**
 * A representation class of a watch id, its execution time and a random UUID
 * This class exists to be able to store several events from the same possible execution time and the same watch
 * in the triggered store index or the history store
 *
 * One 'specialty' of this class is the handling of the underscore in the value. Nothing except the watchId should contain an
 * underscore, otherwise this class will not be able to extract the proper watch id, when a a single string is handed over in its ctor
 *
 * This is also the reason why UUID.randomUUID() is used instead of UUIDs.base64UUID(), as the latter one contains underscores. Also this
 * is not dependant on having time based uuids here, as the time is already included in the value
 */
public class Wid {

    private static final DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

    private final String watchId;
    private final String value;

    public Wid(String watchId, DateTime executionTime) {
        this.watchId = watchId;
        this.value = watchId + "_" + UUID.randomUUID().toString() + "-" +  formatter.print(executionTime);
    }

    public Wid(String value) {
        this.value = value;
        int index = value.lastIndexOf("_");
        if (index <= 0) {
            throw illegalArgument("invalid watcher execution id [{}]", value);
        }
        this.watchId = value.substring(0, index);
    }

    public String value() {
        return value;
    }

    public String watchId() {
        return watchId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Wid wid = (Wid) o;

        return value.equals(wid.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
