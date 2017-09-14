package com.messente.verigator.serializers;

import com.google.gson.annotations.SerializedName;

public class RegisterUserRequest {

    @SerializedName("phone_number")
    public String phoneNumber;
    @SerializedName("id_in_service")
    public String username;

    public RegisterUserRequest(String phoneNumber, String username) {
        this.phoneNumber = phoneNumber;
        this.username = username;
    }
}
