package com.messente.verigator.serializers;

public class AuthenticationResponse {
    /**
     * Indicates which authentication flow was actually used:
     * In case of TOTP this might differ from the requested authentication method, because the SMS
     * flow will be used if no devices are available for sending the push notification.
     *
     * @return "sms" if the SMS flow was used, "totp" if the TOTP flow was used
     */
    public String getMethod() {
        return method;
    }

    private String method;
}
