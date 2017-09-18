package com.messente.verigator.exceptions;

import com.messente.verigator.VerigatorResponse;

public class Helpers {
    public static void validateCommon(VerigatorResponse verigatorResponse, int expectedStatus) throws VerigatorException{
        int statusCode = verigatorResponse.getStatusCode();
        if (statusCode == expectedStatus) {
            return;
        } else if (statusCode == 404) {
            throw new NoSuchResourceException(verigatorResponse.getResponseBody());
        } else if (statusCode == 401) {
            throw new WrongCredentialsException(verigatorResponse.getResponseBody());
        } else if (statusCode == 403) {
            throw new ResourceForbiddenException(verigatorResponse.getResponseBody());
        } else if (statusCode == 422 || statusCode == 400) {
            throw new InvalidDataException(verigatorResponse.getResponseBody());
        } else if (statusCode == 409) {
            throw new ResourceAlreadyExists(verigatorResponse.getResponseBody());
        } else {
           throw new VerigatorInternalError(verigatorResponse.getResponseBody());
        }
    }

    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
