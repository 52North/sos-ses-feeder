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
package org.n52.sos.feeder.baw;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.UnavailableException;

import org.apache.log4j.Logger;
import org.n52.sos.feeder.baw.utils.Strings;

/**
 * Configuration class handles all configurations for the feeder (Singleton).
 * 
 * @author Jan Schulte
 */
public class Configuration {

    /** The logger. */
    private static final Logger log = Logger.getLogger(Configuration.class);

    /** The instance. */
    private static Configuration instance;

    /** The props. */
    private Properties props;

    private int updateInterval;

    // Keys of the configuration properties

    /**
     * Key for the period to start to collect the sensorML documents in
     * milliseconds.
     */
    private static final String KEY_CAPABILITIES_TASK_PERIOD =
            Strings.getString("Configuration.capabilitiesTaskPeriod");

    /** Key for the period to collect the new observations in milliseconds. */
    private static final String KEY_OBSERVATIONS_TASK_PERIOD =
            Strings.getString("Configuration.observationsTaskPeriod");

    /** Key for the supported SOS version. */
    private static final String KEY_SOS_VERSION = Strings.getString("Configuration.sosVersion");

    /** Key for the minimum update time of an observation in milliseconds. */
    private static final String KEY_UPDATE_OBSERVATION_PERIOD =
            Strings.getString("Configuration.updateObservationPeriod");

    /** Key for the maximum number of procedures. */
    private static final String KEY_MAXIMUM_NUMBER_PROCEDURES =
            Strings.getString("Configuration.maximumNumberProcedures");

    /** Key for a list of procedure name constraints. */
    private static final String KEY_PROCEDURE_NAME_CONSTRAINTS =
            Strings.getString("Configuration.procedureNameConstraints");

    /** Key for a list of prohibit procedure names. */
    private static final String KEY_PROHIBIT_PROCEDURE_NAMES =
            Strings.getString("Configuration.prohibitProcedureNames");

    /** Key for a list of no data values. */
    private static final String KEY_NODATAS = Strings.getString("Configuration.noDatas");

//    /** Key for the supported SES version. */
//    private static final String KEY_SES_VERSION = Strings.getString("Configuration.sesVersion");

    /** Key for the SES url. */
    private static final String KEY_SES_URL = Strings.getString("Configuration.sesUrl");

    /** Key for the basic port type path of the SES. */
    private static final String KEY_SES_BASIC_PORT_TYPE_PATH = Strings.getString("Configuration.sesBasicPortTypePath");

    /** Key for the default topic dialect in the SES requests. */
    private static final String KEY_SES_DEFAULT_TOPIC_DIALECT =
            Strings.getString("Configuration.sesDefaultTopicDialect");

    /** Key for the default topic in the SES request. */
    private static final String KEY_SES_DEFAULT_TOPIC = Strings.getString("Configuration.sesDefaultTopic");

    /** Key for the lifetime duration in the SES. */
    private static final String KEY_SES_LIFETIME_DURATION =
            Strings.getString("Configuration.registerPublisherLifetime");

    /** Key for the SES endpoint. */
    private static final String KEY_SES_ENDPOINT = Strings.getString("Configuration.registerPublisherEndpoint");

    /** Key for the start timestamp for a feeded sensor */
    private static final String KEY_START_TIMESTAMP = Strings.getString("Configuration.startTimestamp");

    /** Key for the youngest new observation sended to the ses */
    private static final String KEY_ONLY_YOUNGEST_OBSERVATION = Strings.getString("Configuration.onlyYoungestObservation");

    // private static final String KEY_SLEEP_TIME_OBSERVATIONS =
    // Strings.getString("Configuration.sleepTimeObservation");

    /** The procedure name constraints list. */
    private List<String> procedureNameConstraints;

    /** The prohibit procedure names list. */
    private List<String> prohibitProcedureNames;

    /** The no data value list in the SOSs. */
    private List<String> noDatas;

    /** The time in millis for a capabilities task period */
    private long capTime;

    /** The time in millis for a observation task period */
    private long obsTime;

    private boolean onlyYoungestName;

    private String sosVersion;

    private int maxNumProc;

    private String sesUrl;

    private String sesBasicPortType;

    private String sesDefaultTopicDialect;

    private String sesDefaultTopic;

    private String sesLifetimeDuration;

    private String sesEndpoint;

    private int startTimestamp;

    /**
     * Instantiates a new configuration.
     * 
     * @param is
     *            InputStream for the configuration file of the servlet.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private Configuration(InputStream is) throws IOException {
        this.props = new Properties();
        this.props.loadFromXML(is);

        // Capabilities Task Period
        try {
            this.capTime = Long.parseLong(getValue(KEY_CAPABILITIES_TASK_PERIOD)) * 1000;
        } catch (NumberFormatException e) {
            this.capTime = 86400000;
            log.warn("Error while parsing " + KEY_CAPABILITIES_TASK_PERIOD + ". Defaultvalue is now " + this.capTime);
        }

        // Observation Task Period
        try {
            this.obsTime = Long.parseLong(getValue(KEY_OBSERVATIONS_TASK_PERIOD)) * 1000;
        } catch (NumberFormatException e) {
            this.obsTime = 60000;
            log.warn("Error while parsing " + KEY_OBSERVATIONS_TASK_PERIOD + ". Defaultvalue is now " + this.obsTime);
        }

        // SOS Version
        this.sosVersion = getValue(KEY_SOS_VERSION);
        if (this.sosVersion == null) {
            this.sosVersion = "1.0.0";
            log.warn("Missing Parameter: " + KEY_SOS_VERSION + ". Default value is now " + this.sosVersion);
        }

        // update Observation period
        try {
            this.updateInterval = Integer.parseInt(getValue(KEY_UPDATE_OBSERVATION_PERIOD)) * 1000;
        } catch (NumberFormatException e) {
            this.updateInterval = 120000;
            log.warn("Error while parsing " + KEY_UPDATE_OBSERVATION_PERIOD + ". Defaultvalue is now "
                    + this.updateInterval);
        }

        // maximum number of procedures
        try {
            this.maxNumProc = Integer.parseInt(getValue(KEY_MAXIMUM_NUMBER_PROCEDURES));
        } catch (NumberFormatException e) {
            this.maxNumProc = Integer.MAX_VALUE;
            log.warn("Error while parsing " + KEY_MAXIMUM_NUMBER_PROCEDURES + ". Defaultvalue is now "
                    + this.maxNumProc);
        }

        // get list of procedure name constraints
        this.procedureNameConstraints = new ArrayList<String>();
        String tmp = getValue(KEY_PROCEDURE_NAME_CONSTRAINTS);
        if (tmp == null) {
            log.warn("Error while parsing " + KEY_PROCEDURE_NAME_CONSTRAINTS + ". Defaultvalue is now an empty list");
        } else if (tmp.contains(",")) {
            for (String constraint : tmp.split(",")) {
                if (!constraint.isEmpty()) {
                    this.procedureNameConstraints.add(constraint);
                }
            }
        } else if (!tmp.equals("")) {
            this.procedureNameConstraints.add(tmp);
        }

        // get list of no data values
        this.noDatas = new ArrayList<String>();
        tmp = getValue(KEY_NODATAS);
        if (tmp == null) {
            log.warn("Error while parsing " + KEY_NODATAS + ". Defaultvalue is now an empty list");
        } else if (tmp.contains(",")) {
            for (String noData : tmp.split(",")) {
                if (!noData.isEmpty()) {
                    this.noDatas.add(noData);
                }
            }
        } else if (!tmp.equals("")) {
            this.noDatas.add(tmp);
        }

        // get list of prohibit procedure names
        this.prohibitProcedureNames = new ArrayList<String>();
        tmp = getValue(KEY_PROHIBIT_PROCEDURE_NAMES);
        if (tmp == null) {
            log.warn("Error while parsing " + KEY_PROHIBIT_PROCEDURE_NAMES + ". Defaultvalue is now an empty list");
        } else if (tmp.contains(",")) {
            for (String prohibitProcName : tmp.split(",")) {
                if (!prohibitProcName.isEmpty()) {
                    this.prohibitProcedureNames.add(prohibitProcName);
                }
            }
        } else if (!tmp.equals("")) {
            this.prohibitProcedureNames.add(tmp);
        }

        // SES URL
        this.sesUrl = getValue(KEY_SES_URL);
        if (this.sesUrl == null) {
            this.sesUrl = "http://localhost:8080/SES-2010/services";
            log.warn("Missing Parameter: " + KEY_SES_URL + ". Default value is now " + this.sesUrl);
        }

        // SES basic port type path
        this.sesBasicPortType = getValue(KEY_SES_BASIC_PORT_TYPE_PATH);
        if (this.sesBasicPortType == null) {
            this.sesBasicPortType = "/SesPortType";
            log.warn("Missing Parameter: " + KEY_SES_BASIC_PORT_TYPE_PATH + ". Default value is now "
                    + this.sesBasicPortType);
        }

        // SES default topic dialect
        this.sesDefaultTopicDialect = getValue(KEY_SES_DEFAULT_TOPIC_DIALECT);
        if (this.sesDefaultTopicDialect == null) {
            this.sesDefaultTopicDialect = "http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple";
            log.warn("Missing Parameter: " + KEY_SES_DEFAULT_TOPIC_DIALECT + ". Default value is now "
                    + this.sesDefaultTopicDialect);
        }

        // SES default topic
        this.sesDefaultTopic = getValue(KEY_SES_DEFAULT_TOPIC);
        if (this.sesDefaultTopic == null) {
            this.sesDefaultTopic = "ses:Measurements";
            log.warn("Missing Parameter: " + KEY_SES_DEFAULT_TOPIC + ". Default value is now " + this.sesDefaultTopic);
        }

        // SES lifetime duration
        this.sesLifetimeDuration = getValue(KEY_SES_LIFETIME_DURATION);
        if (this.sesLifetimeDuration == null) {
            this.sesLifetimeDuration = "2999-12-31T23:59:59+00:00";
            log.warn("Missing Parameter: " + KEY_SES_LIFETIME_DURATION + ". Default value is now "
                    + this.sesLifetimeDuration);
        }

        // SES endpoint
        this.sesEndpoint = getValue(KEY_SES_ENDPOINT);
        if (this.sesEndpoint == null) {
            this.sesEndpoint = "http://localhost:8080/";
            log.warn("Missing Parameter: " + KEY_SES_ENDPOINT + ". Default value is now " + this.sesEndpoint);
        }

        // start timestamp
        try {
            this.startTimestamp = Integer.parseInt(getValue(KEY_START_TIMESTAMP)) * 1000;
        } catch (NumberFormatException e) {
            this.startTimestamp = 120000;
            log.warn("Error while parsing " + KEY_UPDATE_OBSERVATION_PERIOD + ". Defaultvalue is now "
                    + this.startTimestamp);
        }
        
        // only youngest observation
        this.onlyYoungestName = Boolean.parseBoolean(getValue(KEY_ONLY_YOUNGEST_OBSERVATION));
        
        log.info("######################################################################");
        log.info("######################  Configuration loaded   #######################");
        log.info("######################################################################");
    }

    /**
     * Gets the single instance of Configuration.
     * 
     * @return The instance of the Configuration class
     * @throws UnavailableException
     */
    public static Configuration getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("Configuration is not available (anymore.)");
        }
        return instance;
    }

    /**
     * Instance.
     * 
     * @param is
     *            InputStream for the configuration file of the servlet.
     * @return The instance of the Configuration class.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static Configuration instance(InputStream is) throws IOException {
        log.trace("instance()");
        if (instance == null) {
            instance = new Configuration(is);
        }
        return instance;
    }

    /**
     * Gets the value of the given key.
     * 
     * @param key
     *            Configuration key
     * @return The value to the given configuration key
     */
    public String getValue(String key) {
        return this.props.getProperty(key);
    }

    /**
     * Gets the procedure name constraints.
     * 
     * @return the procedure name constraints.
     */
    public List<String> getProcedureNameConstraints() {
        return this.procedureNameConstraints;
    }

    /**
     * @return the prohibitProcedureNames
     */
    public List<String> getProhibitProcedureNames() {
        return this.prohibitProcedureNames;
    }

    /**
     * @return the capTime
     */
    public long getCapTime() {
        return this.capTime;
    }

    /**
     * @return the minDelay
     */
    public long getObsTime() {
        return this.obsTime;
    }

    /**
     * Gets the no datas.
     * 
     * @return the no data values
     */
    public List<String> getNoDatas() {
        return this.noDatas;
    }

    /**
     * @return the updateInterval
     */
    public long getUpdateInterval() {
        return this.updateInterval;
    }

    /**
     * @return the sosVersion
     */
    public String getSosVersion() {
        return this.sosVersion;
    }

    /**
     * @return the maxNumProc
     */
    public int getMaxNumProc() {
        return this.maxNumProc;
    }

    /**
     * @return the sesUrl
     */
    public String getSesUrl() {
        return this.sesUrl;
    }

    /**
     * @return the sesBasicPortType
     */
    public String getSesBasicPortType() {
        return this.sesBasicPortType;
    }

    /**
     * @return the sesDefaultTopicDialect
     */
    public String getSesDefaultTopicDialect() {
        return this.sesDefaultTopicDialect;
    }

    /**
     * @return the sesDefaultTopic
     */
    public String getSesDefaultTopic() {
        return this.sesDefaultTopic;
    }

    /**
     * @return the sesLifetimeDuration
     */
    public String getSesLifetimeDuration() {
        return this.sesLifetimeDuration;
    }

    /**
     * @return the sesEndpoint
     */
    public String getSesEndpoint() {
        return this.sesEndpoint;
    }

    /**
     * @return the startTimestamp
     */
    public int getStartTimestamp() {
        return this.startTimestamp;
    }

    /**
     * @return the onlyYoungestName
     */
    public boolean isOnlyYoungestName() {
        return onlyYoungestName;
    }

}
