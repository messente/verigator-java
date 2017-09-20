package com.messente.verigator;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.messente.verigator.exceptions.*;
import com.messente.verigator.serializers.GetUsersResponse;
import com.messente.verigator.serializers.RegisterUserRequest;


/**
 * Represents a Service in Verigator API
 */
public class Service {
    /**
     * Returns the service ID
     *
     * Store this ID in your service.
     * @return service UUID in verigator
     */
    public String getServiceId() {
        return serviceId;
    }

    @SerializedName(("id"))
    private String serviceId;

    private Verigator verigatorApi;
    private static final String ENDPOINT = "service/service/%s";
    private static final String USERS_ENDPOINT = "service/service/%s/users";
    private static final String USER_ENDPOINT = "service/service/%s/user/%s";

    @Expose
    private String ctime;
    @Expose
    private String name;

    @Expose(deserialize = false)
    private String fqdn;

    public Service(String serviceId, Verigator verigatorApi) {
        this.serviceId = serviceId;
        this.verigatorApi = verigatorApi;
    }

    protected void setApi(Verigator api) {
        this.verigatorApi = api;
    }

    /**
     * Return the UNIX timestamp indicating when the service was created
     * @return
     */
    public String getCtime() {
        return ctime;
    }

    /**
     * Return the name of the service in Verigator
     * @return
     */
    public String getName() {
        return name;
    }

    protected Http getHttp() {
        return verigatorApi.getHttp();
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceId='" + serviceId + '\'' +
                ", ctime='" + ctime + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Retrieves a Verigator service by its UUID identifier
     *
     * @param verigator instantiated Verigator API object
     * @param serviceId Verigator UUID identifier for service
     * @return Verigator Service object
     * @throws NoSuchResourceException if no service is found by this ID
     * @throws ResourceForbiddenException if you do not have access to the specified service
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException if a connection error occurs
     */
    public static Service get(Verigator verigator, String serviceId) throws VerigatorException {
        VerigatorResponse response = verigator.getHttp().performGet(
            String.format(Service.ENDPOINT, serviceId)
        );
        Helpers.validateCommon(response, 200);
        Service service = new Gson().fromJson(response.getResponseBody(), Service.class);
        service.setApi(verigator);
        return service;
    }

    /**
     * Permanently deletes a service from Verigator by its UUID
     * Requires that all of the associated service accounts are removed before-hand
     *
     * @param serviceId
     * @throws NoSuchResourceException if no service is found by this ID
     * @throws ResourceForbiddenException if you do not have access to the specified service
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException
     */
    public void delete(String serviceId) throws VerigatorException {
        VerigatorResponse response = verigatorApi.getHttp().performDelete(
           String.format(Service.ENDPOINT, serviceId)
        );
        Helpers.validateCommon(response, 202);
    }

    /**
     * Retrieves a single user from Verigator
     *
     * @param userId UUID of user in Verigator
     * @return User object in Verigator
     * @throws NoSuchResourceException if no user or service is found by this ID
     * @throws ResourceForbiddenException if you do not have access to the service
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException
     */
    public User getUser(String userId) throws VerigatorException {
        VerigatorResponse response = verigatorApi.getHttp().performGet(
            String.format(Service.USER_ENDPOINT, serviceId, userId)
        );
        Helpers.validateCommon(response, 200);
        User user = new Gson().fromJson(response.getResponseBody(), User.class);
        user.setService(this);
        return user;
    }

    /**
     * Retrieves a list of users belonging to this Verigator service
     *
     * @return User[] of users belonging to a Verigator service
     * @throws NoSuchResourceException if no service is found by this ID
     * @throws ResourceForbiddenException if you do not have access to the specified service
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException if a connection error occurs
     */
    public User[] getUsers() throws VerigatorException {
        VerigatorResponse response = verigatorApi.getHttp().performGet(
                String.format(Service.USERS_ENDPOINT, serviceId)
        );
        Helpers.validateCommon(response, 200);
        User[] users = new Gson().fromJson(response.getResponseBody(), GetUsersResponse.class).getUsers();
        for (User user: users) {
           user.setService(this);
        }
        return users;
    }

    /**
     * Adds a user to your service to Verigator
     *
     * @param userName e-mail, username or other human-friendly identifier in your service
     * @param phoneNumber the user's phone number
     * @return User object in Verigator
     * @throws ResourceForbiddenException if you are not allowed to add users to the specified service
     * @throws ResourceAlreadyExists if the specified user already exists
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException if a connection error occurs
     */
    public User registerUser(String userName, String phoneNumber) throws VerigatorException {
        VerigatorResponse response = verigatorApi.getHttp().performPost(
            String.format(Service.USERS_ENDPOINT, serviceId),
            new Gson().toJson(new RegisterUserRequest(phoneNumber, userName))
        );
        Helpers.validateCommon(response, 200);
        User user = new Gson().fromJson(response.getResponseBody(), User.class);
        user.setService(this);
        return user;
    }
}

