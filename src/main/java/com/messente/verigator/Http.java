package com.messente.verigator;


import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Http {
    private String username;
    private String password;
    private String baseUrl;

    public Http(String username, String password, String baseUrl) {
        this.username = username;
        this.password = password;
        this.baseUrl = baseUrl;
    }

    public VerigatorResponse performGet(String endpoint) throws IOException {
        return performRequest(endpoint, "GET", null, null);
    }

    public VerigatorResponse performJSONRequest(String endpoint, String requestMethod, String jsonData) throws IOException {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        return performRequest(endpoint, requestMethod, jsonData, headers);
    }

    public VerigatorResponse performPut(String endpoint, String json) throws IOException {
        return performJSONRequest(endpoint, "PUT", json);
    }

    public VerigatorResponse performPost(String endpoint, String json) throws IOException {
        return performJSONRequest(endpoint, "POST", json);
    }

    public VerigatorResponse performDelete(String endpoint) throws IOException {
        return performRequest(endpoint, "DELETE", null, null);
    }

    public VerigatorResponse performRequest(String endpoint, String requestMethod, String data, HashMap<String, String> headers) throws IOException {

        URL url = new URL(baseUrl + "/" + endpoint);
        HttpURLConnection con = null;
        if (baseUrl.equalsIgnoreCase("https://")) {
            con = (HttpsURLConnection) url.openConnection();
        } else {
            con = (HttpURLConnection) url.openConnection();
        }
        System.out.println("The url is " + url);
        System.out.println("The url is " + url.getPath());
        con.setDoOutput(true);
        System.out.println(requestMethod);
        con.setRequestMethod(requestMethod);
        con.setRequestProperty("X-Service-Auth", username + ":" + password);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (data != null) {
            OutputStream output = con.getOutputStream();
            System.out.println("Sending data: " + data.toString());
            output.write(data.getBytes("UTF-8"));
        }
        BufferedReader in;
        int status = con.getResponseCode();
        System.out.println("The status is " + status);
        InputStream is;
        if (status >= 200 && status < 400) {
            is = con.getInputStream();
        } else {
            is = con.getErrorStream();
        }

        StringBuffer content = new StringBuffer();

        if (is != null) {
            in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
        }
        System.out.println("Received response: " + content.toString());
        con.disconnect();

        return new VerigatorResponse(content.toString(), status);
    }
}
