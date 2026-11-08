package cn.xiaolin.gateway.client;

import cn.xiaolin.gateway.config.AuthClientProperties;
import cn.xiaolin.utils.enums.ResponseCode;
import cn.xiaolin.utils.resp.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Loads user roles and permissions from auth over HTTP.
 * Transport failures and unusable payloads throw {@link AuthUnavailableException}
 * instead of an empty list.
 */
@Component
public class AuthPermClient {

    static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(3);

    private final WebClient webClient;
    private final Duration timeout;

    @Autowired
    public AuthPermClient(WebClient.Builder builder, AuthClientProperties properties) {
        this(builder, properties.getBaseUrl(), DEFAULT_TIMEOUT);
    }

    public AuthPermClient(WebClient.Builder builder, String baseUrl) {
        this(builder, baseUrl, DEFAULT_TIMEOUT);
    }

    public AuthPermClient(WebClient.Builder builder, String baseUrl, Duration timeout) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("auth.base-url is required");
        }
        String normalized = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.webClient = builder.baseUrl(normalized).build();
        this.timeout = timeout;
    }

    public List<String> getPermissions(Long userId) {
        return fetch("/api/v1/internal/users/{id}/permissions", userId);
    }

    public List<String> getRoles(Long userId) {
        return fetch("/api/v1/internal/users/{id}/roles", userId);
    }

    private List<String> fetch(String path, Long userId) {
        Result<List<String>> result;
        try {
            result = await(webClient.get()
                    .uri(path, userId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Result<List<String>>>() {
                    }));
        } catch (AuthUnavailableException ex) {
            throw ex;
        } catch (WebClientResponseException ex) {
            throw new AuthUnavailableException("auth service HTTP " + ex.getStatusCode().value(), ex);
        } catch (RuntimeException ex) {
            throw new AuthUnavailableException("auth service unavailable", ex);
        }
        if (result == null
                || result.getData() == null
                || !ResponseCode.SUCCESS.getCode().equals(result.getCode())) {
            throw new AuthUnavailableException("auth service returned an unusable permission payload");
        }
        return result.getData();
    }

    private Result<List<String>> await(Mono<Result<List<String>>> mono) {
        if (Schedulers.isInNonBlockingThread()) {
            return awaitOffEventLoop(mono);
        }
        try {
            return mono.block(timeout);
        } catch (IllegalStateException ex) {
            if (ex.getMessage() != null && ex.getMessage().startsWith("Timeout on blocking read")) {
                throw new AuthUnavailableException("auth service timeout", ex);
            }
            throw ex;
        }
    }

    private Result<List<String>> awaitOffEventLoop(Mono<Result<List<String>>> mono) {
        CompletableFuture<Result<List<String>>> future = mono.subscribeOn(Schedulers.boundedElastic()).toFuture();
        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw new AuthUnavailableException("auth service timeout", ex);
        } catch (InterruptedException ex) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            throw new AuthUnavailableException("auth service call interrupted", ex);
        } catch (ExecutionException ex) {
            future.cancel(true);
            Throwable cause = ex.getCause() == null ? ex : ex.getCause();
            if (cause instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new AuthUnavailableException("auth service unavailable", cause);
        }
    }
}
