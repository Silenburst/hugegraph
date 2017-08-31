/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph.api;

import java.io.File;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;

import com.baidu.hugegraph.HugeGraph;
import com.baidu.hugegraph.core.GraphManager;
import com.baidu.hugegraph.server.HugeServer;
import com.baidu.hugegraph.util.Log;
import com.google.common.collect.ImmutableMap;

@Path("graphs")
@Singleton
public class GraphsAPI extends API {

    private static final Logger LOG = Log.logger(HugeServer.class);

    private static final String TOKEN = "162f7848-0b6d-4faf-b557-3a0797869c55";

    @GET
    @Produces(APPLICATION_JSON_WITH_CHARSET)
    public Object list(@Context GraphManager manager) {
        return ImmutableMap.of("graphs", manager.graphs().keySet());
    }

    @GET
    @Path("{name}")
    @Produces(APPLICATION_JSON_WITH_CHARSET)
    public Object get(@Context GraphManager manager,
                      @PathParam("name") String name) {
        LOG.debug("Graphs [{}] get graph by name '{}'", name);

        HugeGraph g = (HugeGraph) graph(manager, name);
        return ImmutableMap.of("name", g.name());
    }

    @GET
    @Path("{name}/conf")
    @Produces(APPLICATION_JSON_WITH_CHARSET)
    public File getConf(@Context GraphManager manager,
                        @PathParam("name") String name,
                        @QueryParam("token") String token) {
        LOG.debug("Graphs [{}] get graph by name '{}'", name);

        if (!verifyToken(token)) {
            throw new NotAuthorizedException("Invalid token");
        }

        HugeGraph g = (HugeGraph) graph(manager, name);
        File file = g.configuration().getFile();
        if (file == null) {
            throw new NotSupportedException("Can't access the api in " +
                      "a node which started with non local file config.");
        }
        return file;
    }

    private boolean verifyToken(String token) {
        if (token != null && token.equals(TOKEN)) {
            return true;
        }
        return false;
    }
}
