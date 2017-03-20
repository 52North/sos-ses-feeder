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
package org.n52.sos.feeder.baw.task;

import java.util.List;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.n52.sos.feeder.baw.hibernate.Sensor;
import org.n52.sos.feeder.baw.utils.DatabaseAccess;

/**
 * This class handles the collection of all necessary observations for every
 * registered sensor. It starts a thread for every sensor in the database.
 * 
 * @author Jan Schulte
 * 
 */
public class ObservationsTask extends TimerTask {

    /** The Constant log. */
    private static final Logger log = Logger.getLogger(ObservationsTask.class);

    private ExecutorService executor = Executors.newFixedThreadPool(1);
    
    private boolean isActive;

    /**
     * Reference to the overall list of currently feeded sensors to prohibit
     * double feeding
     */
    private Vector<String> currentyFeededSensors;

    public ObservationsTask(Vector<String> currentlyFeededSensors) {
        this.currentyFeededSensors = currentlyFeededSensors;
    }

    @Override
    public void run() {
        log.info("Currenty feeded sensors: " + currentyFeededSensors.size());
        isActive = true;
        try {
            log.info("############## Prepare Observations task ################");
            List<Sensor> sensors = DatabaseAccess.getUsedSensors();
            log.info("Number of GetObservations: " + sensors.size());
            long time = System.currentTimeMillis();
            for (Sensor sensor : sensors) {
                if (sensor.getLastUpdate() == null
                        || (time - sensor.getUpdateInterval() > sensor.getLastUpdate().getTimeInMillis())) {
                    // 
                    // start only threads for sensors which are currently not
                    // feeding
                    //
                    if (!this.currentyFeededSensors.contains(sensor.getProcedure())) {
                        FeedObservationThread obsThread =
                                new FeedObservationThread(sensor, this.currentyFeededSensors);
                        if (!executor.isShutdown()) {
                            executor.execute(obsThread);
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            log.fatal("Could not parse 'KEY_OBSERVATIONS_TASK_PERIOD'.");
        } catch (IllegalStateException e) {
            e.printStackTrace();
            log.debug("Configuration is not available (anymore).");
        }
        finally {
        	isActive = false;
        }
    }
    /*
     * Why do we use here our own method and in DescriptionTask override the 
     * TimerTask.cancel() method?
     */
    public void stopObservationFeeds() {
        log.info("############## Stop Observations task ################");
        List<Runnable> threads = executor.shutdownNow();
        log.debug("Threads aktiv: " + threads.size());
        for (Runnable runnable : threads) {
            FeedObservationThread thread = (FeedObservationThread) runnable;
            if (thread != null) {
                thread.setRunning(false);
            }
        }
        while (!executor.isTerminated()) {
            log.debug("Wait while ObservationThreads are finished");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("Error during stop of observations threads: " + e.getLocalizedMessage(),e);
            }
        }
        log.info("############## Observation task stopped ##############.");
    }

    /**
     * @return the isActive
     */
    public boolean isActive() {
        return isActive;
    }

}