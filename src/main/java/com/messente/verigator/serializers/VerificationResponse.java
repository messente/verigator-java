package com.messente.verigator.serializers;

public class VerificationResponse {
    private boolean verified;
    private String method;

    public String getMethod() {
        return method;
    }

    public boolean isVerified() {
        return verified;
    }


    @Override
    public String toString() {
        return "VerificationResponse{" +
                "verified=" + verified +
                ", method='" + method + '\'' +
                '}';
    }
}
