package com.messente.verigator;

public class VerigatorResponse {
    public VerigatorResponse(String responseBody, int statusCode) {
        this.responseBody = responseBody;
        this.statusCode = statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    private String responseBody;

    public int getStatusCode() {
        return statusCode;
    }

    private int statusCode;

    @Override
    public String toString() {
        return "VerigatorResponse{" +
                "responseBody='" + responseBody + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
