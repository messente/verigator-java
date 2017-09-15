package com.messente.verigator;

import com.messente.verigator.exceptions.*;
import com.messente.verigator.exceptions.VerigatorException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.junit.Assert.assertEquals;


public class VerigatorTests extends VerigatorTestCase {
    private final String mockCtime = "mock-time";
    @Test
    public void testCreateServiceSuccess() throws VerigatorException {

        mockServer.
                when(
                        request().
                                withMethod("POST")
                                .withPath("/v1/service/service")
                                .withHeaders(getExpectAuthHeader())
//                    .withBody(
//                        String.format("{\"name\":\"%s\":\"fqdn\":\"%s\"}", serviceName, fqdn)
//                    )
                )
                .respond(
                        response().
                                withStatusCode(200).
                                withHeaders(jsonHeader).
                                withBody(String.format("{'name': '%s', 'id': '%s', 'ctime': '%s'}", testServiceName, mockServiceId, mockCtime))

                );
        Verigator verigator = getVerigatorTestClient();
        Service service = verigator.createService(testServiceName, testFqdn);
        assertEquals(service.getServiceId(), mockServiceId);
        assertEquals(service.getCtime(), mockCtime);
        assertEquals(service.getName(), testServiceName);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Test
    public void testCreateServiceAlreadyExists() throws VerigatorException {
        thrown.expect(ResourceAlreadyExists.class);
        mockServer.
                when(
                        request().
                                withMethod("POST")
                                .withPath("/v1/service/service")
                                .withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response().
                                withStatusCode(409).
                                withHeaders(jsonHeader).
                                withBody("wew")

                );
        Verigator verigator = getVerigatorTestClient();
        verigator.createService(testServiceName, testFqdn);
    }

    @Rule public ExpectedException wrongCredentialsThrown = ExpectedException.none();
    @Test
    public void testCreateServiceWrongCredentials() throws VerigatorException {
        wrongCredentialsThrown.expect(WrongCredentialsException.class);
        mockServer.
                when(
                        request().
                                withMethod("POST")
                                .withPath("/v1/service/service")
                                .withHeaders(getExpectAuthHeader("wrong_user", "wrong_pass"))
                )
                .respond(
                        response().
                                withStatusCode(401)

                );
        Verigator verigator = getVerigatorTestClient("wrong_user", "wrong_pass");
        verigator.createService(testServiceName, testFqdn);
    }

    @Rule public ExpectedException noSuchServiceException = ExpectedException.none();
    @Test
    public void testGetServiceNotFound() throws VerigatorException {
        String wrongServiceId = "wrong-service-id";
        noSuchServiceException.expect(NoSuchResourceException.class);
        mockServer.
                when(
                        request().
                                withMethod("GET").
                                withPath("/v1/service/service/" + wrongServiceId).
                                withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response().
                                withStatusCode(404)

                );
        Verigator verigator = getVerigatorTestClient();
        verigator.getService(wrongServiceId);
    }

    @Rule public ExpectedException forbiddenException = ExpectedException.none();
    @Test
    public void testGetServiceForbidden() throws VerigatorException {
        forbiddenException.expect(ResourceForbiddenException.class);
        String forbiddenServiceId = "forbidden-service-id";
        mockServer.
                when(
                        request().
                                withMethod("GET").
                                withPath("/v1/service/service/" + forbiddenServiceId).
                                withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response().
                                withStatusCode(403)

                );
        Verigator verigator = getVerigatorTestClient();
        verigator.getService(forbiddenServiceId);

    }

    @Test
    public void testGetServiceSuccess() throws VerigatorException {
        String successId = "success-id";
        mockServer.
                when(
                        request().
                                withMethod("GET").
                                withPath("/v1/service/service/" + successId).
                                withHeaders(getExpectAuthHeader())
                )
                .respond(
                        response(
                                String.format(
                                        "{'name': '%s', 'id': '%s', 'ctime': '%s'}", testServiceName, mockServiceId, mockCtime)
                        ).
                                withStatusCode(200)

                );
        Verigator verigator = getVerigatorTestClient();
        Service service = verigator.getService(successId);
        assertEquals(service.getName(), testServiceName);
        assertEquals(service.getServiceId(), mockServiceId);
    }


}
