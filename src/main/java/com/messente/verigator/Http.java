package com.messente.verigator;


import com.messente.verigator.exceptions.VerigatorException;
import com.messente.verigator.exceptions.VerigatorInternalError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class Http {
    private static final Logger log = LoggerFactory.getLogger(Http.class);

    private String username;
    private String password;
    private String baseUrl;

    public Http(String username, String password, String baseUrl) {
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
    }

    public VerigatorResponse performGet(String endpoint) throws VerigatorException {
        return performRequest(endpoint, "GET", null, null);
    }

    public VerigatorResponse performJSONRequest(String endpoint, String requestMethod, String jsonData) throws VerigatorException {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        return performRequest(endpoint, requestMethod, jsonData, headers);
    }

    public VerigatorResponse performPut(String endpoint, String json) throws VerigatorException {
        return performJSONRequest(endpoint, "PUT", json);
    }

    public VerigatorResponse performPost(String endpoint, String json) throws VerigatorException {
        return performJSONRequest(endpoint, "POST", json);
    }

    public VerigatorResponse performDelete(String endpoint) throws VerigatorException {
        return performRequest(endpoint, "DELETE", null, null);
    }

    public VerigatorResponse performRequest(String endpoint, String requestMethod, String data, HashMap<String, String> headers) throws VerigatorException {
        HttpURLConnection con = null;
        StringBuilder content = new StringBuilder();
        int status;

        try {
            URL url = new URL(baseUrl + "/" + endpoint);
            if (baseUrl.equalsIgnoreCase("https://")) {
                con = (HttpsURLConnection) url.openConnection();
            } else {
                con = (HttpURLConnection) url.openConnection();
            }
            log.debug("Performing request " + requestMethod + "-> " + url.getPath());
            con.setDoOutput(true);
            log.debug(requestMethod);
            con.setRequestMethod(requestMethod);
            con.setRequestProperty("X-Service-Auth", username + ":" + password);
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("User-Agent", "verigator-java-" + BuildVersion.getBuildVersion());
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    con.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (data != null) {
                OutputStream output = con.getOutputStream();
                log.debug("Sending data: " + data);
                output.write(data.getBytes("UTF-8"));
            }
            BufferedReader in;
            status = con.getResponseCode();
            InputStream is;
            if (status >= 200 && status < 400) {
                is = con.getInputStream();
            } else {
                is = con.getErrorStream();
            }


            if (is != null) {
                in = new BufferedReader(new InputStreamReader(is));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
            }
            log.debug("got HTTP status: " + status);
            log.debug("Received response: " + content.toString());
            con.disconnect();
        } catch (Exception ex) {
            log.error("A unexpected connection error occurred: " + ex.getStackTrace());
            throw new VerigatorInternalError(ex.getMessage());

        } finally {
            if (con != null) {
                con.disconnect();
            }

        }

        return new VerigatorResponse(content.toString(), status);
    }
}
