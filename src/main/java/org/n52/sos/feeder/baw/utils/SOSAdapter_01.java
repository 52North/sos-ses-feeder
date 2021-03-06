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
package org.n52.sos.feeder.baw.utils;

import java.io.IOException;
import java.io.InputStream;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionType;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.OWSException;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.adapter.SOSRequestBuilderFactory;
import org.n52.oxf.util.web.HttpClientException;

public class SOSAdapter_01 extends SOSAdapter{

    /**
     *
     * @param serviceVersion
     *        the schema version for which this adapter instance shall be initialized.
     */
    public SOSAdapter_01(String serviceVersion) {
        super(serviceVersion);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Instance of class \"" + this.getClass().getName() + "\" created.");
        }
    }


    private static Logger LOGGER = Logger.getLogger(SOSAdapter_01.class);

    /**
     *
     * @param operation
     *        the operation which the adapter has to execute on the service. this operation includes also the
     *        parameter values.
     *
     * @param parameters
     *        Map which contains the parameters of the operation and the corresponding parameter values
     *
     * @param serviceVersion
     *        the schema version to which the operation execution shall be conform.
     *
     * @throws ExceptionReport
     *         Report which contains the service sided exceptions
     *
     * @throws OXFException
     *         if the sending of the post message failed.<br>
     *         if the specified Operation is not supported.
     *
     * @return the result of the executed operation
     */
    @Override
    public OperationResult doOperation(Operation operation, ParameterContainer parameters) throws ExceptionReport,
            OXFException {

        // FIXME Correct constructor in Operation class, parameters could be null
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("starting Operation: " + operation + " with parameters: " + parameters);
//        }

        ISOSRequestBuilder requestBuilder = SOSRequestBuilderFactory.generateRequestBuilder(getServiceVersion());

        OperationResult result = null;

        String request = null;

        // GetCapabilities Operation
        if (operation.getName().equals(GET_CAPABILITIES)) {
            request = requestBuilder.buildGetCapabilitiesRequest(parameters);
        }

        // GetObservation Operation
        else if (operation.getName().equals(GET_OBSERVATION)) {
            request = requestBuilder.buildGetObservationRequest(parameters);
        }

        // DescribeSensor Operation
        else if (operation.getName().equals(DESCRIBE_SENSOR)) {
            request = requestBuilder.buildDescribeSensorRequest(parameters);
        }

        // GetFeatureOfInterest Operation
        else if (operation.getName().equals(GET_FEATURE_OF_INTEREST)) {
            request = requestBuilder.buildGetFeatureOfInterestRequest(parameters);
        }

        // InsertObservation Operation
        else if (operation.getName().equals(INSERT_OBSERVATION)) {
            request = requestBuilder.buildInsertObservation(parameters);
        }

        // RegisterSensor Operation
        else if (operation.getName().equals(REGISTER_SENSOR)) {
            request = requestBuilder.buildRegisterSensor(parameters);
        }

        // GetObservationByID Operation
        else if (operation.getName().equals(GET_OBSERVATION_BY_ID)) {
            request = requestBuilder.buildGetObservationByIDRequest(parameters);
        }

        // Operation not supported
        else {
            throw new OXFException("The operation '" + operation.getName() + "' is not supported.");
        }

        // request = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"+request;

        try {
            InputStream is;
            if (operation.getName().equals(GET_CAPABILITIES)) {
                is = IOHelper_01.sendGetMessage(operation.getDcps()[0].getHTTPGetRequestMethods().get(0).getOnlineResource().getHref(),
                                             "REQUEST=GetCapabilities&SERVICE=SOS");
            }
            else {
                is = IOHelper_01.sendPostMessage(operation.getDcps()[0].getHTTPPostRequestMethods().get(0).getOnlineResource().getHref(),
                                              request);
            }
            result = new OperationResult(is, parameters, request);

            try {
                ExceptionReport execRep = parseExceptionReport_000(result);

                throw execRep;
            }
            catch (XmlException e) {
                // parseError --> no ExceptionReport was returned.
                LOGGER.info("Service reported no 0.0.0 exceptions.");
            }

            try {
                ExceptionReport execRep = parseExceptionReport_100(result);

                throw execRep;
            }
            catch (XmlException e) {
                // parseError --> no ExceptionReport was returned.
                LOGGER.info("Service reported no 1.0.0 exceptions.");
            }
        }
        catch (IOException e) {
            throw new OXFException(e);
        }
        catch (HttpClientException e) {
            throw new OXFException(e);
        }

        return result;
    }

    /**
     * checks whether the response of the doOperation is an ExceptionReport. If it is, the report with the
     * contained OWSExceptions are parsed and a new ExceptionReport is created and will be returned.
     *
     * @throws ExceptionReport
     *         the exception report containing the service exceptions
     * @throws OXFException
     *         if an parsing error occurs
     * @throws XmlException
     */
        // TODO could be externalized because the same methods are use in the SESAdapter => identify common ServiceAdapterTasks
    private ExceptionReport parseExceptionReport_000(OperationResult result) throws XmlException {

        String requestResult = new String(result.getIncomingResult());

        ExceptionReportDocument xb_execRepDoc = ExceptionReportDocument.Factory.parse(requestResult);
        ExceptionType[] xb_exceptions = xb_execRepDoc.getExceptionReport().getExceptionArray();

        String language = xb_execRepDoc.getExceptionReport().getLang();
        String version = xb_execRepDoc.getExceptionReport().getVersion();

        ExceptionReport oxf_execReport = new ExceptionReport(version, language);
        for (ExceptionType xb_exec : xb_exceptions) {
            String execCode = xb_exec.getExceptionCode();
            String[] execMsgs = xb_exec.getExceptionTextArray();
            String locator = xb_exec.getLocator();

            OWSException owsExec = new OWSException(execMsgs, execCode, result.getSendedRequest(), locator);

            oxf_execReport.addException(owsExec);
        }

        return oxf_execReport;
    }

    private ExceptionReport parseExceptionReport_100(OperationResult result) throws XmlException {

        String requestResult = new String(result.getIncomingResult());

        net.opengis.ows.x11.ExceptionReportDocument xb_execRepDoc = net.opengis.ows.x11.ExceptionReportDocument.Factory.parse(requestResult);
        net.opengis.ows.x11.ExceptionType[] xb_exceptions = xb_execRepDoc.getExceptionReport().getExceptionArray();

        String language = xb_execRepDoc.getExceptionReport().getLang();
        String version = xb_execRepDoc.getExceptionReport().getVersion();

        ExceptionReport oxf_execReport = new ExceptionReport(version, language);
        for (net.opengis.ows.x11.ExceptionType xb_exec : xb_exceptions) {
            String execCode = xb_exec.getExceptionCode();
            String[] execMsgs = xb_exec.getExceptionTextArray();
            String locator = xb_exec.getLocator();

            OWSException owsExec = new OWSException(execMsgs, execCode, result.getSendedRequest(), locator);

            oxf_execReport.addException(owsExec);
        }

        return oxf_execReport;

    }


}
