package com.messente.verigator.serializers;

public class VerificationResponse {
    private boolean verified;

    /**
     * Indicates whether the user pvoided the correct pin
     *
     * @return true if the user entered the correct pin, false if not.
     */
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
