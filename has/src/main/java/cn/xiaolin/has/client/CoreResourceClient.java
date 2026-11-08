package cn.xiaolin.has.client;

import cn.xiaolin.has.config.CoreClientProperties;
import cn.xiaolin.has.dto.ResourceDto;
import cn.xiaolin.utils.enums.ResponseCode;
import cn.xiaolin.utils.resp.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.function.Supplier;

/**
 * Persists transcode results by calling core's internal resource API.
 * Non-2xx responses and a business {@code code} other than success throw
 * {@link CoreUnavailableException} so the MQ consumer nacks instead of acking.
 */
@Component
public class CoreResourceClient {

    static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    static final Duration READ_TIMEOUT = Duration.ofSeconds(30);

    private static final ParameterizedTypeReference<Result<Void>> VOID_RESULT =
            new ParameterizedTypeReference<>() {
            };

    private static final ParameterizedTypeReference<Result<ResourceDto>> RESOURCE_RESULT =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    @Autowired
    public CoreResourceClient(RestClient.Builder builder, CoreClientProperties properties) {
        this(builder, properties.getBaseUrl());
    }

    public CoreResourceClient(RestClient.Builder builder, String baseUrl) {
        this(builder, baseUrl, CONNECT_TIMEOUT, READ_TIMEOUT);
    }

    CoreResourceClient(
            RestClient.Builder builder, String baseUrl, Duration connectTimeout, Duration readTimeout) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("core.base-url is required");
        }
        String normalized = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.restClient = builder
                .baseUrl(normalized)
                .requestFactory(jdkRequestFactory(connectTimeout, readTimeout))
                .build();
    }

    private static JdkClientHttpRequestFactory jdkRequestFactory(Duration connectTimeout, Duration readTimeout) {
        HttpClient httpClient =
                HttpClient.newBuilder().connectTimeout(connectTimeout).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

    public void saveResource(ResourceDto dto) {
        Result<Void> result = exchange(() -> restClient.post()
                .uri("/api/v1/internal/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(VOID_RESULT));
        requireSuccess(result);
    }

    public ResourceDto getResourceById(Long id) {
        Result<ResourceDto> result = exchange(() -> restClient.get()
                .uri("/api/v1/internal/resources/{id}", id)
                .retrieve()
                .body(RESOURCE_RESULT));
        requireSuccess(result);
        return result.getData();
    }

    private <T> T exchange(Supplier<T> call) {
        try {
            return call.get();
        } catch (CoreUnavailableException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new CoreUnavailableException("core service unavailable", ex);
        }
    }

    private void requireSuccess(Result<?> result) {
        if (result == null || !ResponseCode.SUCCESS.getCode().equals(result.getCode())) {
            Integer code = result == null ? null : result.getCode();
            throw new CoreUnavailableException("core service returned a non-success code: " + code);
        }
    }
}
