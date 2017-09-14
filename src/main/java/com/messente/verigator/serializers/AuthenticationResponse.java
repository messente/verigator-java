package com.messente.verigator.serializers;

import com.google.gson.annotations.SerializedName;

public class AuthenticationResponse {
    public String getAuthId() {
        return authId;
    }

    public String getMethod() {
        return method;
    }

    @SerializedName("auth_id")
    private String authId;
    private String method;
}
