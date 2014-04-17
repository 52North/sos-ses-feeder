/**
 * ﻿Copyright (C) 2014-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.feeder.baw;

import java.util.Iterator;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.n52.sos.feeder.baw.task.DescriptionTask;
import org.n52.sos.feeder.baw.utils.DatabaseAccess;
import org.n52.sos.feeder.baw.utils.Strings;

/**
 * The RequestHandler class handles the incoming request for the used and unused
 * sensors.
 * 
 * @author Jan Schulte
 * 
 */
public class RequestHandler {

    /** The logger. */
    private static final Logger log = Logger.getLogger(RequestHandler.class);

    /**
     * Gets the response.
     *
     * @param msg The incoming SOAPMessage
     * @return The response the to request
     */
    public SOAPMessage getResponse(SOAPMessage msg) {
        SOAPMessage message = null;
        try {
            log.info("Incoming request!");

            // get contents of response
            SOAPBody body = msg.getSOAPBody();
            Iterator<?> childElements = body.getChildElements();

            while (childElements.hasNext()) {
                Object next = childElements.next();
                // check if object is an element
                if (next instanceof SOAPElement) {
                    // check the request
                    SOAPElement elem = (SOAPElement) next;
                    // usedSensors element
                    if (elem.getElementName().getLocalName().equals(
                            Strings.getString("IncomingRequest.usedSensorsRequest"))) { 
                        usedSensors(elem);
                    }
                    // unsedSensors element
                    if (elem.getElementName().getLocalName().equals(
                            Strings.getString("IncomingRequest.unusedSensorsRequest"))) { 
                        unusedSensors(elem);
                    }
                    // addSOS element
                    if (elem.getElementName().getLocalName()
                            .equals(Strings.getString("IncomingRequest.addSOSRequest"))) { 
                        addSOS(elem);
                    }
                }
            }
        } catch (SOAPException e) {
            log.error("Unable to parse SOAPrequest: " + e);
        }
        return message;
    }

    /**
     * Adds the given sos to the database.
     *
     * @param addSOSElem the add sos element
     */
    private void addSOS(SOAPElement addSOSElem) {
        Iterator<?> childElements = addSOSElem.getChildElements();
        while (childElements.hasNext()) {
            Object next = childElements.next();
            if (next instanceof SOAPElement) {
                // check if it is a sensor element
                SOAPElement elem = (SOAPElement) next;
                if (elem.getElementName().getLocalName().equals(Strings.getString("IncomingRequest.sosElement"))) { 
                    log.info("New SOS added: " + elem.getValue()); 
                    // save in database
                    DatabaseAccess.saveNewSOS(elem.getValue());
                    // start an DescriptionTask
                    DescriptionTask task = new DescriptionTask();
                    new Thread(task).start();
                }
            }
        }
    }

    /**
     * Sets the given sensor element to unused.
     *
     * @param unusedSensorsElem the unused sensors element
     */
    private void unusedSensors(SOAPElement unusedSensorsElem) {
        Iterator<?> childElements = unusedSensorsElem.getChildElements();
        while (childElements.hasNext()) {
            Object next = childElements.next();
            if (next instanceof SOAPElement) {
                // check if it is a sensor element
                SOAPElement elem = (SOAPElement) next;
                if (elem.getElementName().getLocalName().equals(Strings.getString("IncomingRequest.sensorElement"))) { 
                    log.info("New Unused Sensor: " + elem.getValue()); 
                    DatabaseAccess.saveSensorUsage(elem.getValue(), false);
                    // FIXME delete lastupdate timestamp
                }
            }
        }
    }

    /**
     * Sets the given sensor element to used.
     *
     * @param usedSensorsElem the used sensors element
     */
    private void usedSensors(SOAPElement usedSensorsElem) {
        Iterator<?> childElements = usedSensorsElem.getChildElements();
        while (childElements.hasNext()) {
            Object next = childElements.next();
            if (next instanceof SOAPElement) {
                // check if it is a sensor element
                SOAPElement elem = (SOAPElement) next;
                if (elem.getElementName().getLocalName().equals(Strings.getString("IncomingRequest.sensorElement"))) { 
                    log.info("New Used Sensor: " + elem.getValue()); 
                    DatabaseAccess.saveSensorUsage(elem.getValue(), true);
                }
            }
        }
    }
}
