package com.messente.verigator.serializers;

import com.messente.verigator.User;

public class GetUsersResponse {
    private User[] users;

    public User[] getUsers() {
        return users;
    }

    private int count;
}
