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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.Vector;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.n52.sos.feeder.baw.hibernate.InitSessionFactory;
import org.n52.sos.feeder.baw.hibernate.SOS;
import org.n52.sos.feeder.baw.task.DescriptionTask;
import org.n52.sos.feeder.baw.task.ObservationsTask;
import org.n52.sos.feeder.baw.utils.Strings;

/**
 * Just to test the Feeder.
 */
@SuppressWarnings("unused")
public class TestFeeder {

    private static Configuration config;
    private static Timer capabilitiesTimer;

    /**
     * @param args
     */
    public static void main(String[] args) {

        SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat(Strings.getString("ISO8601Dateformat"));
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        DateTime datetime = new DateTime(new Date());
        String print = fmt.print(datetime);
        System.out.println(print);
//        initConfiguration();
//        createSOS("http://v-sos.uni-muenster.de:8080/PegelOnlineSOSv2/sos");
        // createSOS("http://giv-sos.uni-muenster.de:8080/52nSOSv3/sos");
        // createSOS("http://v-swe.uni-muenster.de:8080/WeatherSOS/sos");
        // createSOS("http://ak1.uni-muenster.de:8080/52nSOSv3/sos");
//        testCapabilitiesTask();
//         testObservationsTask();
        // createRelation();
        // createRelationNew();

    }

    private static void initConfiguration() {
        try {
            FileInputStream fis =
                    new FileInputStream(new File(
                            "C:/Users/jansch/workspace/SosSesFeeder/WebContent/conf/configuration.xml"));
            config = Configuration.instance(fis);
        } catch (FileNotFoundException e) {
            //
        } catch (IOException e) {
            //
        }

    }

    private static void testCapabilitiesTask() {
        capabilitiesTimer = new Timer();
        capabilitiesTimer.schedule(new DescriptionTask(), 1, Configuration.getInstance().getCapTime());
    }
    
    private static void testObservationsTask() {
        ObservationsTask obsTask = new ObservationsTask(new Vector<String>());
//        obsTask.startObservationFeeds();
        // Timer timer = new Timer();
        // timer.schedule(new ObservationsTask(), 5000, Long.parseLong(config
        // .getValue(Configuration.KEY_OBSERVATIONS_TASK_PERIOD)));
    }


    private static SOS createSOS(String sosURL) {
        SOS sos = new SOS();
        sos.setUrl(sosURL);
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.save(sos);
        tx.commit();
        return sos;
    }

}