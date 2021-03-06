package com.messente.verigator;

import com.messente.verigator.exceptions.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockserver.model.HttpRequest.request;

import static org.mockserver.model.HttpResponse.response;

public class ServiceTests extends VerigatorTestCase {
    private final String testPhoneNumber = "+372555555";
    private final String username = "test@example.com";

    @Test
    public void testListUsersSuccess() throws VerigatorException {
        String successId = "success-id";

        mockServer.
                when(
                        request().
                                withMethod("GET").
                                withPath("/v1/service/service/" + successId + "/users").
                                withHeaders(getCommonHeaders())
                )
                .respond(
                        response(
        "{\"total\": 2, \"users\": [{\"ctime\": \"fake-ctime-1\",  id:\"mock-id-1\"," +
        "\"id_in_service\": \"fake-id-in-service-1\"}, {\"ctime\": \"fake-ctime-2\","+
        "\"id\":\"mock-id-2\", \"id_in_service\": \"fake-id-in-service-2\"}]}"
                        ).
                                withStatusCode(200)

                );

        Service service = new Service(successId, getVerigatorTestClient());
        User[] users = service.getUsers();
        assertEquals(users.length, 2);
        assertEquals(users[0].getId(), "mock-id-1");
        assertEquals(users[0].getCtime(), "fake-ctime-1");
        assertEquals(users[0].getUsername(), "fake-id-in-service-1");
        assertNotNull(users[0].getService());
        assertEquals(users[1].getId(), "mock-id-2");
        assertEquals(users[1].getCtime(), "fake-ctime-2");
        assertEquals(users[1].getUsername(), "fake-id-in-service-2");
        assertNotNull(users[1].getService());
    }

    @Rule
    public final ExpectedException listUsersNotFound = ExpectedException.none();
    @Test
    public void testListUsersServiceNotFound() throws VerigatorException {
        listUsersNotFound.expect(NoSuchResourceException.class);
        String wrongId = "wrong-id";
        mockServer.
                when(
                        request().
                                withMethod("GET").
                                withPath("/v1/service/service/" + wrongId + "/users").
                                withHeaders(getCommonHeaders())
                )
                .respond(
                        response().
                                withStatusCode(404)

                );

        Service service = new Service(wrongId, getVerigatorTestClient());
        service.getUsers();
    }

    @Test
    public void testListUsersServiceEmptyUsers() throws VerigatorException {
        String successId = "success-id";

        mockServer.
                when(
                        request().
                                withMethod("GET").
                                withPath("/v1/service/service/" + successId + "/users").
                                withHeaders(getCommonHeaders())
                )
                .respond(
                        response("{\"total\": 0, \"users\": []}").
                                withStatusCode(200)

                );

        Service service = new Service(successId, getVerigatorTestClient());
        User[] users = service.getUsers();
        assertEquals(users.length, 0);

    }

    @Test
    public void testRegisterUserSuccess() throws VerigatorException {
        String successId = "success-id";

        mockServer.
                when(
                        request().
                                withMethod("POST").
                                withPath("/v1/service/service/" + successId + "/users").
                                withHeaders(getCommonHeaders())
                )
                .respond(
                        response("{\"ctime\": \"fake-ctime-1\", id:\"mock-id-1\", \"id_in_service\": \"fake-id-in-service-1\"}"
).
                                withStatusCode(200)

                );
        Service service = new Service(successId, getVerigatorTestClient());
        User user = service.registerUser(username, testPhoneNumber);
        assertEquals(user.getUsername(), "fake-id-in-service-1");
        assertEquals(user.getId(), "mock-id-1");
        assertEquals(user.getCtime(), "fake-ctime-1");
        assertNotNull(user.getService());
    }

    @Rule
    public final ExpectedException registerUserInvalidData = ExpectedException.none();
    @Test
    public void testRegisterUserInvalidData() throws VerigatorException {
        registerUserInvalidData.expect(InvalidDataException.class);
        String successId = "success-id";

        mockServer.
                when(
                        request().
                                withMethod("POST").
                                withPath("/v1/service/service/" + successId + "/users").
                                withHeaders(getCommonHeaders())
                )
                .respond(
                        response("").
                                withStatusCode(422)

                );
        Service service = new Service(successId, getVerigatorTestClient());
        service.registerUser(username, testPhoneNumber);

    }

    @Rule
    public ExpectedException registerUserNotFound = ExpectedException.none();
    @Test
    public void testRegisterUserServiceNotFound() throws VerigatorException {
        registerUserForbidden.expect(NoSuchResourceException.class);
        String successId = "success-id";

        mockServer.
                when(
                        request().
                                withMethod("GET").
                                withPath("/v1/service/service/" + successId + "/users").
                                withHeaders(getCommonHeaders())
                )
                .respond(
                        response("{\"total\": 0, \"users\": []}").
                                withStatusCode(422)

                );
        Service service = new Service(successId, getVerigatorTestClient());
        service.registerUser(username, testPhoneNumber);

    }

    @Rule
    public final ExpectedException registerUserForbidden = ExpectedException.none();
    @Test
    public void testRegisterUserForbidden() throws VerigatorException {
        registerUserForbidden.expect(ResourceForbiddenException.class);
        String serviceId = "success-id";
        mockServer.
                when(
                        request().
                                withMethod("POST").
                                withPath("/v1/service/service/" + serviceId + "/users").
                                withHeaders(getCommonHeaders())
                )
                .respond(
                        response().
                                withStatusCode(403)

                );

        Service service = new Service(serviceId, getVerigatorTestClient());
        service.registerUser(username, testPhoneNumber);
    }

    @Rule
    public final ExpectedException registerUserAlreadyExists = ExpectedException.none();
    @Test
    public void testtRegisterUserAlreadyExists() throws VerigatorException {
        registerUserAlreadyExists.expect(ResourceAlreadyExists.class);
        String serviceId = "success-id";
        mockServer.
                when(
                        request().
                                withMethod("POST").
                                withPath("/v1/service/service/" + serviceId + "/users").
                                withHeaders(getCommonHeaders())
                )
                .respond(
                        response().
                                withStatusCode(409)

                );

        Service service = new Service(serviceId, getVerigatorTestClient());
        service.registerUser(username, testPhoneNumber);
    }

    @Test
    public void testDeleteServiceSuccess() throws VerigatorException {
        String successId = "success-id";
        mockServer.
                when(
                        request().
                                withMethod("DELETE").
                                withPath("/v1/service/service/" + successId).
                                withHeaders(getCommonHeaders())
                )
                .respond(
                        response().
                                withStatusCode(202)

                );
        Service service = new Service(successId, getVerigatorTestClient());
        service.delete(successId);
    }

    @Rule
    public final ExpectedException deleteMissingResource = ExpectedException.none();
    @Test
    public void testDeleteNotFound() throws VerigatorException {
        deleteMissingResource.expect(NoSuchResourceException.class);
        String serviceId = "not-found-service";
        mockServer.
                when(
                        request().
                                withMethod("DELETE").
                                withPath("/v1/service/service/" + serviceId).
                                withHeaders(getCommonHeaders())
                )
                .respond(
                        response().
                                withStatusCode(404)

                );
        Service service = new Service(serviceId, getVerigatorTestClient());
        service.delete(serviceId);
    }

    @Rule
    public final ExpectedException deleteMissingCredentials = ExpectedException.none();
    @Test
    public void testDeleteWrongCredentials() throws VerigatorException {
        deleteMissingCredentials.expect(WrongCredentialsException.class);
        String serviceId = "some-id";
        mockServer.
                when(
                        request().
                                withMethod("DELETE").
                                withPath("/v1/service/service/" + serviceId).
                                withHeaders(getAuthHeader("wrong-user", "wrong-pass"))
                )
                .respond(
                        response().
                                withStatusCode(401)

                );


        Service service = new Service(serviceId, getVerigatorTestClient("wrong-user", "wrong-pass"));
        service.delete(serviceId);
    }

}
