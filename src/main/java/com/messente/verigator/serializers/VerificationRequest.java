package com.messente.verigator.serializers;

public class VerificationRequest {
    public String method;
    public String token;

    public VerificationRequest(String method, String token, String auth_id) {
        this.method = method;
        this.token = token;
        this.auth_id = auth_id;
    }

    public String auth_id;
}
