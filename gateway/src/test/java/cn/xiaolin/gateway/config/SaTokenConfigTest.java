package cn.xiaolin.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.util.SaResult;
import cn.xiaolin.gateway.client.AuthUnavailableException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SaTokenConfigTest {

    @AfterEach
    void clearSaContext() {
        SaReactorSyncHolder.clearContext();
    }

    @Test
    void authUnavailable_mapsToHttp503() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/core/videos").build());
        SaReactorSyncHolder.setContext(exchange);
        SaReactorFilter filter = new SaTokenConfig().getSaReactorFilter();

        Object body = filter.error.run(new AuthUnavailableException("auth down"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
        assertInstanceOf(SaResult.class, body);
        assertEquals(503, ((SaResult) body).getCode());
    }
}
