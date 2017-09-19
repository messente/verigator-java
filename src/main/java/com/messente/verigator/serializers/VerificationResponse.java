package com.messente.verigator.serializers;

public class VerificationResponse {
    private boolean verified;

    public boolean isVerified() {
        return verified;
    }


    @Override
    public String toString() {
        return "VerificationResponse{" +
                "verified=" + verified +
                '}';
    }
}
