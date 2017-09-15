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
    private static final String userEndpoint = "service/service/%s/users/%s/auth";

    public String getCtime() {
        return ctime;
    }

    public String getUsername() {
        return username;
    }

    private final String authenticationMethodTotp = "totp";
    private final String authenticationMethodSms = "sms";

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
           String.format(userEndpoint, service.getServiceId(), id),
           new Gson().toJson(new AuthenticationRequest(method))
        );
        Helpers.validateCommon(resp,200);
        AuthenticationResponse authenticationResponse = new Gson().fromJson(resp.getResponseBody(), AuthenticationResponse.class);
        return authenticationResponse;
    }

    public AuthenticationResponse authenticateUsingTotp() throws VerigatorException {
        return authenticate(authenticationMethodTotp);
    }

    public AuthenticationResponse authenticateUsingSMS() throws VerigatorException {
        return authenticate(authenticationMethodSms);
    }

    public VerificationResponse verifyPinSms(String authId, String token) throws VerigatorException {
        return verifyPin(authId, authenticationMethodSms, token);
    }

    public VerificationResponse verifyPinTotp(String token) throws VerigatorException {
        return verifyPin(null, authenticationMethodTotp, token);
    }

    private VerificationResponse verifyPin(String auth_id, String method, String token) throws VerigatorException {
        VerigatorResponse resp = service.getHttp().performPut(
                String.format(userEndpoint, service.getServiceId(), id),
           new Gson().toJson(new VerificationRequest(method, token, auth_id))
        );
        Helpers.validateCommon(resp, 200);
        VerificationResponse verificationResponse = new Gson().fromJson(resp.getResponseBody(), VerificationResponse.class);
        return verificationResponse;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getId() {
        return id;
    }
}
