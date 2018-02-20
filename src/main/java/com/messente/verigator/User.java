package com.messente.verigator;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.messente.verigator.exceptions.Helpers;
import com.messente.verigator.exceptions.*;
import com.messente.verigator.serializers.*;

/**
 * Represents a User in Verigator API
 */
public class User {
    /**
     * Retrieve the Service to which this user belongs.
     * @return Service
     */
    public Service getService() {
        return service;
    }

    private Service service;
    private static final String AUTH_ENDPOINT = "service/service/%s/users/%s/auth";
    private static final String USERS_ENDPOINT = "service/service/%s/users/%s";

    /**
     * Returns the UNIX time indicating when the user was created
     * @return
     */
    public String getCtime() {
        return ctime;
    }

    /**
     * Returns the username of the Verigator
     * @return
     */
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

    /**
     *
     * @param service
     * @param id
     */
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

    /**
     * Starts the authentication flow using TOTP via the Verigator mobile app.
     * If the user does not have a device with the app installed, Verigator will automatically fall back to the SMS flow.
     * After calling this method, the user will receive a push notification or an SMS (in case the app is not installed)
     * with the pin code.
     *
     * @return AuthenticationResponse, getMethod() specifies which method was used: sms or totp (app)
     * @throws ResourceForbiddenException if you do not have access to the specified service
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException if there is a connection error
     */
    public AuthenticationResponse authenticateUsingTotp() throws VerigatorException {
        return authenticate(AUTHENTICATION_METHOD_TOTP);
    }

    /**
     * Starts the authentication flow using SMS.
     * After calling this method, the user will receive an SMS with the pin code.
     *
     * @return AuthenticationResponse, getMethod() specifies which method was used: sms or totp (app)
     * @throws ResourceForbiddenException if you do not have access to the specified service
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException if there is a connection error
     */
    public AuthenticationResponse authenticateUsingSMS() throws VerigatorException {
        return authenticate(AUTHENTICATION_METHOD_SMS);
    }

    /**
     * Verifies the user-provided PIN against Verigator service
     * @param pin the pin code that the user provided in your service
     * @return VerificationResponse which indicates whether the PIN was correct or not (isVerified() method)
     * @throws ResourceForbiddenException if you do not have access to the specified service
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException if there is a connection error
     */
    public VerificationResponse verifyPin(String pin) throws VerigatorException {
        VerigatorResponse resp = service.getHttp().performPut(
                String.format(AUTH_ENDPOINT, service.getServiceId(), id),
           new Gson().toJson(new VerificationRequest(pin))
        );
        Helpers.validateCommon(resp, 200);
        return new Gson().fromJson(resp.getResponseBody(), VerificationResponse.class);
    }
    /**
     * Deletes given user from service
     * @throws ResourceForbiddenException if you do not have access to the specified service
     * @throws WrongCredentialsException if you have specified the incorrect credentials
     * @throws VerigatorInternalError if a server-side error occurs
     * @throws VerigatorException if there is a connection error
     */
    public void delete() throws VerigatorException {
        VerigatorResponse resp = service.getHttp().performDelete(
            String.format(USERS_ENDPOINT, service.getServiceId(), id)
        );
        Helpers.validateCommon(resp, 202);
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getId() {
        return id;
    }
}
