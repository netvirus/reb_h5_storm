/*
 * Copyright (C) 2017  Nick Chapsas
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 *
 * L2ACP is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver;

import l2r.gameserver.l2acp.Requests;
import l2r.gameserver.l2acp.requests.Request;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

public class L2ACPServer {

    private static final Logger _log = LoggerFactory.getLogger(L2ACPServer.class);

    public L2ACPServer() {
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/api", new RequestHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
            _log.info("L2 API is started on port: " + Config.API_USE_PORT);
        } catch (BindException e) {
            _log.info("HttpServer has started already!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ThreadPoolManager.scheduleAtFixedRate(new DatamineTask(), 120000, 600000);
    }

    class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            if ("GET".equals(httpExchange.getRequestMethod())) {
                try {
                    String requestBody = read(httpExchange.getRequestBody());
                    JsonElement jelement = new JsonParser().parse(requestBody);
                    JsonObject jobject = jelement.getAsJsonObject();
                    int reqId = Integer.parseInt(jobject.get("RequestId").getAsString());

                    if (Config.API_KEY.equals(jobject.get("ApiKey").getAsString())) {
                        Request request = Requests.getById(reqId);

                        if (request != null) {
                            request.setContent(jobject);

                            Gson gson = new Gson();
                            String jsonInString = gson.toJson(request.getResponse());
                            String jsonResponse = jsonInString.toString();
                            httpExchange.sendResponseHeaders(200, jsonResponse.length());
                            try (OutputStream responseBody = httpExchange.getResponseBody()) {
                                responseBody.write(jsonResponse.getBytes());
                            }
                        } else {
                            _log.info("API: wrong request " + reqId);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    public static final L2ACPServer getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final L2ACPServer _instance = new L2ACPServer();
    }
}
