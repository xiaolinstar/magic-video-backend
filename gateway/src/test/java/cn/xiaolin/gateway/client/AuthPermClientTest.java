package cn.xiaolin.gateway.client;

import cn.xiaolin.gateway.service.StpServiceImpl;
import cn.xiaolin.utils.enums.ResponseCode;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthPermClientTest {

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getRoles_parsesResultData() throws Exception {
        assertEquals(200, ResponseCode.SUCCESS.getCode());
        enqueueSuccess("[\"common\"]");

        assertEquals(List.of("common"), client().getRoles(9L));
        assertEquals("/api/v1/internal/users/9/roles", recordedPath());
    }

    @Test
    void getPermissions_parsesResultData() throws Exception {
        enqueueSuccess("[\"video:upload\",\"video:play\"]");

        assertEquals(List.of("video:upload", "video:play"), client().getPermissions(9L));
        assertEquals("/api/v1/internal/users/9/permissions", recordedPath());
    }

    @Test
    void getRoles_whenHttp5xx_throwsInsteadOfEmptyList() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        AuthPermClient permClient = client();
        assertThrows(AuthUnavailableException.class, () -> permClient.getRoles(9L));
    }

    @Test
    void getPermissions_whenHttp5xx_throwsInsteadOfEmptyList() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));

        AuthPermClient permClient = client();
        assertThrows(AuthUnavailableException.class, () -> permClient.getPermissions(9L));
    }

    @Test
    void getRoles_whenTimeout_throwsInsteadOfEmptyList() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(successBody("[\"common\"]"))
                .addHeader("Content-Type", "application/json")
                .setBodyDelay(2, TimeUnit.SECONDS));

        AuthPermClient permClient = client(java.time.Duration.ofMillis(200));
        assertThrows(AuthUnavailableException.class, () -> permClient.getRoles(9L));
    }

    @Test
    void getRoles_whenBusinessCodeIsNotSuccess_throws() {
        mockWebServer.enqueue(json("{\"code\":500,\"msg\":\"服务器错误\",\"data\":[\"ADMIN\"]}"));

        AuthPermClient permClient = client();
        assertThrows(AuthUnavailableException.class, () -> permClient.getRoles(9L));
    }

    @Test
    void getRoles_whenDataMissing_throws() {
        mockWebServer.enqueue(json("{\"code\":200,\"msg\":\"操作成功\"}"));

        AuthPermClient permClient = client();
        assertThrows(AuthUnavailableException.class, () -> permClient.getRoles(9L));
    }

    @Test
    void getRoles_whenDataEmpty_returnsEmptyList() throws Exception {
        enqueueSuccess("[]");

        assertEquals(List.of(), client().getRoles(9L));
        assertEquals("/api/v1/internal/users/9/roles", recordedPath());
    }

    @Test
    void getRoles_onReactorThread_parsesResultData() throws Exception {
        enqueueSuccess("[\"common\"]");
        AuthPermClient permClient = client();
        AtomicReference<List<String>> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch done = new CountDownLatch(1);

        Schedulers.parallel().schedule(() -> {
            try {
                result.set(permClient.getRoles(9L));
            } catch (Throwable thrown) {
                error.set(thrown);
            } finally {
                done.countDown();
            }
        });

        assertTrue(done.await(5, TimeUnit.SECONDS));
        assertNull(error.get());
        assertEquals(List.of("common"), result.get());
    }

    @Test
    void stpService_whenAuth5xx_throwsInsteadOfEmptyList() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(503));
        mockWebServer.enqueue(new MockResponse().setResponseCode(503));
        StpServiceImpl stpService = new StpServiceImpl(client());

        assertThrows(AuthUnavailableException.class, () -> stpService.getRoleList("9", "login"));
        assertThrows(AuthUnavailableException.class, () -> stpService.getPermissionList("9", "login"));
    }

    private AuthPermClient client() {
        return new AuthPermClient(WebClient.builder(), baseUrl());
    }

    private AuthPermClient client(java.time.Duration timeout) {
        return new AuthPermClient(WebClient.builder(), baseUrl(), timeout);
    }

    private String baseUrl() {
        return mockWebServer.url("/").url().toString().replaceAll("/$", "");
    }

    private void enqueueSuccess(String dataJson) {
        mockWebServer.enqueue(json(successBody(dataJson)));
    }

    private String successBody(String dataJson) {
        return "{\"code\":200,\"msg\":\"success\",\"data\":" + dataJson + "}";
    }

    private String recordedPath() throws InterruptedException {
        RecordedRequest request = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertTrue(request != null, "auth client made no HTTP request");
        return request.getPath();
    }

    private MockResponse json(String body) {
        return new MockResponse()
                .setResponseCode(200)
                .setBody(body)
                .addHeader("Content-Type", "application/json");
    }
}
