package cn.xiaolin.has.client;

import cn.xiaolin.has.dto.ResourceDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreResourceClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
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
    void saveResource_postsJsonAndAcceptsSuccessCode() throws Exception {
        mockWebServer.enqueue(json("{\"code\":200,\"msg\":\"操作成功\",\"data\":null}"));
        ResourceDto dto = sample();

        client().saveResource(dto);

        RecordedRequest request = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertNotNull(request, "core client made no HTTP request");
        assertEquals("POST", request.getMethod());
        assertEquals("/api/v1/internal/resources", request.getPath());
        assertTrue(request.getHeader("Content-Type").contains("application/json"));
        JsonNode body = objectMapper.readTree(request.getBody().readUtf8());
        assertEquals(10001L, body.get("id").asLong());
        assertEquals("0a1b2c3d4e5f60718293a4b5c6d7e8f9", body.get("md5").asText());
        assertEquals("raw/10001.mp4", body.get("mp4").asText());
        assertEquals("transcoded/10001/index.m3u8", body.get("m3u8").asText());
        assertEquals("transcoded/10001/manifest.mpd", body.get("mpd").asText());
    }

    @Test
    void saveResource_whenHttp5xx_throws() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        CoreResourceClient resourceClient = client();
        assertThrows(CoreUnavailableException.class, () -> resourceClient.saveResource(sample()));
    }

    @Test
    void saveResource_whenHttp4xx_throws() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400));

        CoreResourceClient resourceClient = client();
        assertThrows(CoreUnavailableException.class, () -> resourceClient.saveResource(sample()));
    }

    @Test
    void saveResource_whenBusinessCodeIsNotSuccess_throws() {
        mockWebServer.enqueue(json("{\"code\":500,\"msg\":\"服务器错误\",\"data\":null}"));

        CoreResourceClient resourceClient = client();
        assertThrows(CoreUnavailableException.class, () -> resourceClient.saveResource(sample()));
    }

    @Test
    void saveResource_whenBodyMissing_throws() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        CoreResourceClient resourceClient = client();
        assertThrows(CoreUnavailableException.class, () -> resourceClient.saveResource(sample()));
    }

    @Test
    void constructor_whenBaseUrlBlank_throws() {
        assertThrows(IllegalStateException.class, () -> new CoreResourceClient(RestClient.builder(), " "));
    }

    @Test
    void defaultTimeouts_matchDesign() {
        assertEquals(Duration.ofSeconds(3), CoreResourceClient.CONNECT_TIMEOUT);
        assertEquals(Duration.ofSeconds(30), CoreResourceClient.READ_TIMEOUT);
    }

    @Test
    void saveResource_whenReadTimeoutExceeded_throws() {
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));

        CoreResourceClient resourceClient = new CoreResourceClient(
                RestClient.builder(), baseUrl(), Duration.ofSeconds(1), Duration.ofMillis(300));

        long start = System.nanoTime();
        assertThrows(CoreUnavailableException.class, () -> resourceClient.saveResource(sample()));
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        assertTrue(elapsedMs < 2_000, "expected failure near read timeout, took " + elapsedMs + "ms");
    }

    @Test
    void getResourceById_parsesResultData() throws Exception {
        mockWebServer.enqueue(json("""
                {"code":200,"msg":"操作成功","data":{"id":10001,"md5":"0a1b2c3d4e5f60718293a4b5c6d7e8f9","mp4":"raw/10001.mp4","m3u8":"transcoded/10001/index.m3u8","mpd":"transcoded/10001/manifest.mpd"}}
                """));

        ResourceDto loaded = client().getResourceById(10001L);

        assertEquals(10001L, loaded.getId());
        assertEquals("0a1b2c3d4e5f60718293a4b5c6d7e8f9", loaded.getMd5());
        assertEquals("raw/10001.mp4", loaded.getMp4());
        assertEquals("transcoded/10001/index.m3u8", loaded.getM3u8());
        assertEquals("transcoded/10001/manifest.mpd", loaded.getMpd());
        RecordedRequest request = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertNotNull(request, "core client made no HTTP request");
        assertEquals("GET", request.getMethod());
        assertEquals("/api/v1/internal/resources/10001", request.getPath());
    }

    @Test
    void getResourceById_whenHttp5xx_throws() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(502));

        CoreResourceClient resourceClient = client();
        assertThrows(CoreUnavailableException.class, () -> resourceClient.getResourceById(10001L));
    }

    private CoreResourceClient client() {
        return new CoreResourceClient(RestClient.builder(), baseUrl());
    }

    private String baseUrl() {
        return mockWebServer.url("/").url().toString().replaceAll("/$", "");
    }

    private ResourceDto sample() {
        return ResourceDto.builder()
                .id(10001L)
                .md5("0a1b2c3d4e5f60718293a4b5c6d7e8f9")
                .mp4("raw/10001.mp4")
                .m3u8("transcoded/10001/index.m3u8")
                .mpd("transcoded/10001/manifest.mpd")
                .build();
    }

    private MockResponse json(String body) {
        return new MockResponse()
                .setResponseCode(200)
                .setBody(body)
                .addHeader("Content-Type", "application/json");
    }
}
