package com.messente.verigator;

import com.google.gson.Gson;
import com.messente.verigator.exceptions.*;
import com.messente.verigator.serializers.CreateServiceRequest;


/**
 * The main class for using the Verigator API
 */
public class Verigator {

    public Http getHttp() {
        return http;
    }

    private Http http;

    private static final String DEFAULT_ENDPOINT = "https://api.verigator.com";
    private static final String DEFAULT_API_VERSION = "v1";
    private static final String SERVICE_ENDPOINT = "service/service";

    /**
     * The constructor that should be used for creating a Verigator object
     * API credentials can be found from https://dashboard.messente.com
     *
     * @param username your Messente API username
     * @param password your Messente API password
     */
    public Verigator(String username, String password) {
        String apiUrl = DEFAULT_ENDPOINT + "/" + DEFAULT_API_VERSION;
        this.http = new Http(username, password, apiUrl);
    }

    /**
     *  This constructor should not be normally used
     */
    public Verigator(String username, String password, String apiBaseUrl, String apiVersion) {
        String apiUrl = apiBaseUrl + "/" + apiVersion;
        this.http = new Http(username, password, apiUrl);
    }

    /**
     *  This constructor should not be normally used
     */
    public Verigator(String username, String password, String apiUrl) {
        this.http = new Http(username, password, apiUrl);
    }

    /**
     * Retrieves service from Verigator API
     *
     * @param serviceId UUID of the service in Verigator
     * @return retrieved Service object in Verigator
     * @throws NoSuchResourceException if you have specified the wrong service id
     * @throws ResourceForbiddenException if you are not allowed to add users to the specified service
     * @throws ResourceAlreadyExists if the specified user already exists
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException if a connection problem occurs
     */
    public Service getService(String serviceId) throws VerigatorException {
        if (Helpers.isEmpty(serviceId)) {
            throw new InvalidInvocationError("Service id is required, but not specified");
        }
        return Service.get(this, serviceId);
    }

    /**
     * Creates a new service with the specified name and FQDN in Verigator
     * The returned Service object includes the service ID (getServiceId() method) which should be stored by your service
     *
     * @param name The name of your service
     * @param fqdn FQDN or Full qualified domain name, such as www.example.com
     * @return created Service object in Verigator
     * @throws InvalidInvocationError if empty or null empty values are provided for name and fqdn
     * @throws ResourceAlreadyExists if a service with this name already exists
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException if a connection problem occurs
     */
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