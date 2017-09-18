package com.messente.verigator;

import com.google.gson.Gson;
import com.messente.verigator.exceptions.*;
import com.messente.verigator.serializers.CreateServiceRequest;


public class Verigator {

    public Http getHttp() {
        return http;
    }

    private Http http;

    private static final String DEFAULT_ENDPOINT = "https://api.verigator.com";
    private static final String DEFAULT_API_VERSION = "v1";
    private static final String SERVICE_ENDPOINT = "service/service";


    public Verigator(String username, String password) {
        String apiUrl = DEFAULT_ENDPOINT + "/" + DEFAULT_API_VERSION;
        this.http = new Http(username, password, apiUrl);
    }

    public Verigator(String username, String password, String apiBaseUrl, String apiVersion) {
        String apiUrl = apiBaseUrl + "/" + apiVersion;
        this.http = new Http(username, password, apiUrl);
    }

    public Verigator(String username, String password, String apiUrl) {
        this.http = new Http(username, password, apiUrl);
    }

    public Service getService(String serviceId) throws VerigatorException {
        if (Helpers.isEmpty(serviceId)) {
            throw new InvalidInvocationError("Service id is required, but not specified");
        }
        return Service.get(this, serviceId);
    }
    public Service createService(String name, String fqdn) throws VerigatorException {
        if (Helpers.isEmpty(name) || Helpers.isEmpty(fqdn)) {
            throw new InvalidInvocationError("Both fqdn and name must be provided");
        }
        VerigatorResponse response = http.performPost(
                SERVICE_ENDPOINT, new Gson().toJson(new CreateServiceRequest(name, fqdn))
        );
        Helpers.validateCommon(response, 200);
        Service service = new Gson().fromJson(response.getResponseBody(), Service.class);
        service.setApi(this);
        return service;
    }

}