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
package org.n52.sos.feeder.baw.utils;

import java.io.IOException;
import java.io.InputStream;

import org.n52.oxf.OXFException;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ses.adapter.ISESRequestBuilder;
import org.n52.oxf.ses.adapter.SESAdapter;
import org.n52.oxf.util.web.HttpClientException;

/**
 * The Class SESAdapter_01.
 *
 * @author Jan Schulte
 */
public class SESAdapter_01 extends SESAdapter {

    /**
     * Do operation.
     *
     * @param operation the operation
     * @param parameterContainer the parameter container
     * @return the operation result
     * @throws OXFException the oXF exception
     * @see org.n52.oxf.serviceAdapters.ses.SESAdapter#doOperation(org.n52.oxf.owsCommon.capabilities.Operation, org.n52.oxf.serviceAdapters.ParameterContainer)
     */
    public OperationResult doOperation(Operation operation, ParameterContainer parameterContainer)
            throws OXFException, IllegalStateException {
        String request = null;
        ISESRequestBuilder requestBuilder = new SESRequestBuilder_01();
        OperationResult result = null;
        if(operation!=null){

            // SUBSCRIBE
            if (operation.getName().equals(SESAdapter.SUBSCRIBE)) {
                request = requestBuilder.buildSubscribeRequest(parameterContainer);

                // GET_CAPABILITIES
            } else if(operation.getName().equals(SESAdapter.GET_CAPABILITIES)){
                request = requestBuilder.buildGetCapabilitiesRequest(parameterContainer);

                // NOTIFY
            } else if(operation.getName().equals(SESAdapter.NOTIFY)){
                request = requestBuilder.buildNotifyRequest(parameterContainer);

                // REIGSER_PUBLISHER
            } else if(operation.getName().equals(SESAdapter.REGISTER_PUBLISHER)){
                request = requestBuilder.buildRegisterPublisherRequest(parameterContainer);

                // DESCRIBE_SENSOR
            } else if(operation.getName().equals(SESAdapter.DESCRIBE_SENSOR)){
                request = requestBuilder.buildDescribeSensorRequest(parameterContainer);

                // Operation not supported
            } else {
                throw new OXFException("The operation '" + operation.getName()
                        + "' is not supported.");
            }
            try {
                InputStream is = IOHelper_01.sendPostMessage(operation.getDcps()[0]
                                                                          .getHTTPPostRequestMethods().get(0).getOnlineResource()
                                                                          .getHref(), request);

            /*
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

                        String line;
                System.out.println("Request vom SES: ");
                while ((line = rd.readLine()) != null) {
                        System.out.println(line);
                }
             */
                result = new OperationResult(is, parameterContainer, request);
            } catch (IOException e) {
                throw new OXFException("Could not send POST message.", e);
            } 
            catch (HttpClientException e) {
                throw new OXFException(e);
            }

        }

        return result;
    }
}
