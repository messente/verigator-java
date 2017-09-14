package com.messente.verigator.serializers;

public class CreateServiceRequest {
    private String name;
    private String fqdn;

    public CreateServiceRequest(String name, String fqdn) {
        this.name = name;
        this.fqdn = fqdn;
    }
}
