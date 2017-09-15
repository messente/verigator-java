package com.messente.verigator;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.messente.verigator.exceptions.*;
import com.messente.verigator.serializers.GetUsersResponse;
import com.messente.verigator.serializers.RegisterUserRequest;


public class Service {
    public String getServiceId() {
        return serviceId;
    }

    @SerializedName(("id"))
    private String serviceId;

    private Verigator verigatorApi;
    private static final String endpoint = "service/service/%s";
    private static final String usersEndpoint = "service/service/%s/users";
    private static final String userEndpoint = "service/service/%s/user/%s";

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

    public void setApi(Verigator api) {
        this.verigatorApi = api;
    }

    public String getCtime() {
        return ctime;
    }

    public String getName() {
        return name;
    }

    public Http getHttp() {
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

    public static Service get(Verigator verigator, String serviceId) throws VerigatorException {
        VerigatorResponse response = verigator.getHttp().performGet(
            String.format(Service.endpoint, serviceId)
        );
        Helpers.validateCommon(response, 200);
        Service service = new Gson().fromJson(response.getResponseBody(), Service.class);
        service.setApi(verigator);
        return service;
    }

    public void delete(String serviceId) throws VerigatorException {
        VerigatorResponse response = verigatorApi.getHttp().performDelete(
           String.format(Service.endpoint, serviceId)
        );
        Helpers.validateCommon(response, 202);
    }

    public User getUser(String userId) throws VerigatorException {
        VerigatorResponse response = verigatorApi.getHttp().performGet(
            String.format(Service.userEndpoint, serviceId, userId)
        );
        Helpers.validateCommon(response, 200);
        User user = new Gson().fromJson(response.getResponseBody(), User.class);
        user.setService(this);
        return user;
    }

    public User[] getUsers() throws VerigatorException {
        VerigatorResponse response = verigatorApi.getHttp().performGet(
                String.format(Service.usersEndpoint, serviceId)
        );
        Helpers.validateCommon(response, 200);
        User[] users = new Gson().fromJson(response.getResponseBody(), GetUsersResponse.class).getUsers();
        for (User user: users) {
           user.setService(this);
        }
        return users;
    }

    public User registerUser(String userName, String phoneNumber) throws VerigatorException {
        VerigatorResponse response = verigatorApi.getHttp().performPost(
            String.format(Service.usersEndpoint, serviceId),
            new Gson().toJson(new RegisterUserRequest(phoneNumber, userName))
        );
        Helpers.validateCommon(response, 200);
        User user = new Gson().fromJson(response.getResponseBody(), User.class);
        user.setService(this);
        return user;
    }
}

