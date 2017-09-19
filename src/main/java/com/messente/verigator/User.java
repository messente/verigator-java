package com.messente.verigator;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.messente.verigator.exceptions.Helpers;
import com.messente.verigator.exceptions.VerigatorException;
import com.messente.verigator.serializers.AuthenticationRequest;
import com.messente.verigator.serializers.AuthenticationResponse;
import com.messente.verigator.serializers.VerificationRequest;
import com.messente.verigator.serializers.VerificationResponse;

public class User {
    public Service getService() {
        return service;
    }

    private Service service;
    private static final String AUTH_ENDPOINT = "service/service/%s/users/%s/auth";

    public String getCtime() {
        return ctime;
    }

    public String getUsername() {
        return username;
    }

    public final String AUTHENTICATION_METHOD_TOTP = "totp";
    public final String AUTHENTICATION_METHOD_SMS = "sms";

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", ctime='" + ctime + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public User(Service service, String id) {
        this.service = service;
        this.id = id;
    }

    @Expose
    private String id;

    @Expose
    private String ctime;

    @Expose
    @SerializedName("id_in_service")
    private String username;

    private AuthenticationResponse authenticate(String method) throws VerigatorException {
        VerigatorResponse resp = service.getHttp().performPost(
           String.format(AUTH_ENDPOINT, service.getServiceId(), id),
           new Gson().toJson(new AuthenticationRequest(method))
        );
        Helpers.validateCommon(resp,200);
        return new Gson().fromJson(resp.getResponseBody(), AuthenticationResponse.class);
    }

    public AuthenticationResponse authenticateUsingTotp() throws VerigatorException {
        return authenticate(AUTHENTICATION_METHOD_TOTP);
    }

    public AuthenticationResponse authenticateUsingSMS() throws VerigatorException {
        return authenticate(AUTHENTICATION_METHOD_SMS);
    }

    public VerificationResponse verifyPin(String token) throws VerigatorException {
        VerigatorResponse resp = service.getHttp().performPut(
                String.format(AUTH_ENDPOINT, service.getServiceId(), id),
           new Gson().toJson(new VerificationRequest(token))
        );
        Helpers.validateCommon(resp, 200);
        return new Gson().fromJson(resp.getResponseBody(), VerificationResponse.class);
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getId() {
        return id;
    }
}
