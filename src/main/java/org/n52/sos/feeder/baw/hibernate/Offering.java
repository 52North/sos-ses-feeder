/**
 * ﻿Copyright (C) 2014-2017 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.sos.feeder.baw.hibernate;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

/**
 * The Class Offering.
 *
 * @author Jan Schulte
 */
@SuppressWarnings("serial")
public class Offering implements Serializable {

    /** The offering id. */
    private int offeringID;
    
    /** The name. */
    private String name;

    /** The sensor. */
    private Sensor sensor;
    
    /** The observed properties. */
    private Set<ObservedProperty> observedProperties;
    
    /**
     * @return the offeringID
     */
    public int getOfferingID() {
        return this.offeringID;
    }

    /**
     * @param offeringID the offeringID to set
     */
    public void setOfferingID(int offeringID) {
        this.offeringID = offeringID;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the observedProperties
     */
    public Set<ObservedProperty> getObservedProperties() {
        return this.observedProperties;
    }

    /**
     * @param observedProperties the observedProperties to set
     */
    public void setObservedProperties(Set<ObservedProperty> observedProperties) {
        this.observedProperties = observedProperties;
    }

    /**
     * @param sensor the sensor to set
     */
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    /**
     * @return the sensor
     */
    public Sensor getSensor() {
        return this.sensor;
    }
    
    @Override
    public String toString() {
        return this.name;
    }

    public String[] getObsPropAsStringArray() {
        String[] obsPropsStrings = new String[this.observedProperties.size()];
        int i = 0;
        for (Iterator<ObservedProperty> iterator = this.observedProperties.iterator(); iterator.hasNext();) {
            obsPropsStrings[i++] = iterator.next().getName();
        }
        return obsPropsStrings;
    }
    
}
