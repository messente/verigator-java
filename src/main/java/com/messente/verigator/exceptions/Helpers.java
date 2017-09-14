package com.messente.verigator.exceptions;

import com.messente.verigator.VerigatorResponse;

public class Helpers {
    public static void validateCommon(VerigatorResponse verigatorResponse, int expectedStatus) throws VerigatorException{
        int statusCode = verigatorResponse.getStatusCode();
        if (statusCode == expectedStatus) {
            return;
        } else if (statusCode == 404) {
            throw new NoSuchResourceException();
        } else if (statusCode == 401) {
            throw new WrongCredentialsException();
        } else if (statusCode == 403) {
            throw new ResourceForbiddenException();
        } else if (statusCode == 422) {
            throw new InvalidDataException();
        } else if (statusCode == 409) {
            throw new ResourceAlreadyExists();
        } else {
           throw new VerigatorInternalError();
        }
    }
    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
