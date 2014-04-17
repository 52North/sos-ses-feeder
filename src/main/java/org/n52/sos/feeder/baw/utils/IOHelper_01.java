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

import org.apache.http.HttpResponse;
import org.n52.oxf.util.IOHelper;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.util.web.HttpClientException;
import org.n52.oxf.util.web.ProxyAwareHttpClient;
import org.n52.oxf.util.web.SimpleHttpClient;

public class IOHelper_01 extends IOHelper {
    
    private static final int TIMEOUT = 15000;
    
    public static InputStream sendGetMessage(String serviceURL, String queryString) throws IOException, HttpClientException {
        HttpClient httpClient = createHttpClient();
		HttpResponse response = httpClient.executeGet(serviceURL + "?" + queryString);
        return response.getEntity().getContent();
    }

	private static ProxyAwareHttpClient createHttpClient() {
		return new ProxyAwareHttpClient(new SimpleHttpClient(TIMEOUT, TIMEOUT));
	}

    public static InputStream sendPostMessage(String serviceURL, String request) throws IOException, HttpClientException {
        HttpClient httpClient = createHttpClient();
        HttpResponse response = httpClient.executePost(serviceURL, request);
        return response.getEntity().getContent();
    }

}
