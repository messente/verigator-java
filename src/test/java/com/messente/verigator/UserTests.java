package com.messente.verigator;

import com.messente.verigator.exceptions.NoSuchResourceException;
import com.messente.verigator.exceptions.ResourceForbiddenException;
import com.messente.verigator.exceptions.VerigatorException;
import com.messente.verigator.serializers.AuthenticationResponse;
import com.messente.verigator.serializers.VerificationResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockserver.model.HttpRequest.request;

import static org.junit.Assert.assertEquals;
import static org.mockserver.model.HttpResponse.response;

public class UserTests extends VerigatorTestCase {
    private final String testServiceId = "serviceId";
    private final String testUserId = "userId";
    private final String mockPin = "1111";

    @Test
    public void testAuthSMSSuccess() throws VerigatorException {
        Service service = new Service(testServiceId, getVerigatorTestClient());
        User user = new User(service, testUserId);

        mockServer.
                when(
                        request().
                                withMethod("POST").
                                withPath("/v1/service/service/" + testServiceId + "/users/" + testUserId + "/auth").
                                withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response("{\"method\": \"sms\"}").
                                withStatusCode(200)

                );

        AuthenticationResponse authenticationResponse = user.authenticateUsingSMS();
        assertEquals(authenticationResponse.getMethod(), "sms");
    }

    @Test
    public void testAuthTotpSuccess() throws VerigatorException {
        String testServiceId = "serviceId";
        String testUserId = "userId";
        Service service = new Service(testServiceId, getVerigatorTestClient());
        User user = new User(service, testUserId);

        mockServer.
                when(
                        request().
                                withMethod("POST").
                                withPath("/v1/service/service/" + testServiceId + "/users/" + testUserId + "/auth").
                                withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response("{\"method\": \"totp\", \"auth_id\": null}").
                                withStatusCode(200)

                );

        AuthenticationResponse authenticationResponse = user.authenticateUsingSMS();
        assertEquals(authenticationResponse.getMethod(), "totp");
    }

    @Rule
    public ExpectedException testNotFound = ExpectedException.none();
    @Test
    public void testAuthResourceNotFound() throws VerigatorException {
        testNotFound.expect(NoSuchResourceException.class);

        Service service = new Service(testServiceId, getVerigatorTestClient());
        User user = new User(service, testUserId);

        mockServer.
                when(
                        request().
                                withMethod("POST").
                                withPath("/v1/service/service/" + testServiceId + "/users/" + testUserId + "/auth").
                                withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response().
                                withStatusCode(404)
                );
        user.authenticateUsingTotp();
    }

    @Rule
    public ExpectedException testAuthVerifyForbidden = ExpectedException.none();
    @Test
    public void testAuthResourceForbidden() throws VerigatorException {
        testAuthVerifyForbidden.expect(ResourceForbiddenException.class);
        mockServer.
                when(
                    request().
                        withMethod("POST").
                        withPath("/v1/service/service/" + testServiceId + "/users/" + testUserId + "/auth").
                        withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response("").
                                withStatusCode(403)
                );
        Service service = new Service(testServiceId, getVerigatorTestClient());
        User user = new User(service, testUserId);
        user.authenticateUsingTotp();
    }

    @Test
    public void testVerifyTotpSuccessVerified() throws VerigatorException {
        mockServer.
                when(
                        request().
                                withMethod("PUT").
                                withPath("/v1/service/service/" + testServiceId + "/users/" + testUserId + "/auth").
                                withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response("{\"method\": \"totp\", \"verified\": true}").
                                withStatusCode(200)
                );
        Service service = new Service(testServiceId, getVerigatorTestClient());
        User user = new User(service, testUserId);
        VerificationResponse verificationResponse = user.verifyPin(mockPin);
        assertEquals(verificationResponse.isVerified(), true);
    }

    @Test
    public void testVerifySuccessFailed() throws VerigatorException {

        String mockPin = "1111";
        mockServer.
                when(
                        request().
                                withMethod("PUT").
                                withPath("/v1/service/service/" + testServiceId + "/users/" + testUserId + "/auth").
                                withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response("{\"method\": \"totp\", \"verified\": false}").
                                withStatusCode(200)
                );
        Service service = new Service(testServiceId, getVerigatorTestClient());
        User user = new User(service, testUserId);
        VerificationResponse verificationResponse = user.verifyPin(mockPin);
        assertEquals(verificationResponse.isVerified(), false);
    }

    @Rule
    public ExpectedException testVerifyForbidden = ExpectedException.none();
    @Test
    public void setTestVerifyForbidden() throws VerigatorException {
        testVerifyForbidden.expect(ResourceForbiddenException.class);
        mockServer.
                when(
                        request().
                                withMethod("PUT").
                                withPath("/v1/service/service/" + testServiceId + "/users/" + testUserId + "/auth").
                                withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response("").
                                withStatusCode(403)
                );
        Service service = new Service(testServiceId, getVerigatorTestClient());
        User user = new User(service, testUserId);
        user.verifyPin(mockPin);
    }
}
