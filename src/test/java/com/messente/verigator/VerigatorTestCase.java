package com.messente.verigator;

import org.junit.After;
import org.junit.Before;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndProxy;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import static org.mockserver.integration.ClientAndProxy.startClientAndProxy;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

public class VerigatorTestCase {

    protected final String testServiceName = "test service";
    protected final String testFqdn = "www.example.com";
    protected final Header jsonHeader = new Header("Content-Type", "application/json; charset=utf-8");

    protected final String testUser = "test_user";
    protected final String testPassword = "test_password";
    protected final String mockServiceId = "mock-service-id";
    private ClientAndProxy proxy;
    protected ClientAndServer mockServer;
    private int mockServerPort = 1080;

    @Before
    public void startProxy() {
        mockServer = startClientAndServer(mockServerPort);
        proxy = startClientAndProxy(1090);
    }

    protected Header getExpectAuthHeader() {
        return getExpectAuthHeader(testUser, testPassword);
    }

    protected Header getExpectAuthHeader(String useername, String password) {
        return new Header("X-Service-Auth", useername+ ":" + password);
    }

    @After
    public void stopProxy() {
        proxy.stop();
        mockServer.stop();
    }

    public MockServerRule mockServerRule = new MockServerRule(this);

    protected MockServerClient mockServerClient;

    protected Verigator getVerigatorTestClient() {
        return getVerigatorTestClient(testUser, testPassword);
    }

    protected Verigator getVerigatorTestClient(String username, String password) {
        return new Verigator(username, password, "http://localhost:" + mockServerPort + "/v1");
    }


}
