//========================================================================
//Copyright 2012-2012 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//All rights reserved. This program and the accompanying materials
//are made available under the terms of the Eclipse Public License v1.0
//and Apache License v2.0 which accompanies this distribution.
//The Eclipse Public License is available at
//http://www.eclipse.org/legal/epl-v10.html
//The Apache License v2.0 is available at
//http://www.opensource.org/licenses/apache2.0.php
//You may elect to redistribute this code under either of these licenses.
//========================================================================

package org.eclipse.jetty.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SelectChannelConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Test;

public class HTTPClientTest
{
    private Server server;
    private HTTPClient client;
    private Connector.NetConnector connector;

    public void start(Handler handler) throws Exception
    {
        server = new Server();
        connector = new SelectChannelConnector();
        server.addConnector(connector);
        server.setHandler(handler);
        server.start();

        client = new HTTPClient();
        client.start();
    }

    @After
    public void destroy() throws Exception
    {
        client.stop();
        client.join(5, TimeUnit.SECONDS);

        server.stop();
        server.join();
    }

    @Test
    public void testGETNoResponseContent() throws Exception
    {
        start(new AbstractHandler()
        {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
            {
                baseRequest.setHandled(true);
            }
        });

        Response response = client.GET("http://localhost:" + connector.getLocalPort()).get(5, TimeUnit.SECONDS);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }
}
