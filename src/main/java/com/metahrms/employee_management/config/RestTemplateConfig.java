package com.metahrms.employee_management.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final FaceRecognitionProperties properties;

    @Bean(name = "faceRecognitionRestTemplate")
    public RestTemplate faceRecognitionRestTemplate(RestTemplateBuilder builder) {

        log.info("Configuring RestTemplate for Face Recognition Service");
        log.info("Base URL: {}", properties.getService().getBaseUrl());

        // ✅ Request config (timeout)
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(
                        Timeout.ofMilliseconds(properties.getTimeouts().getConnection()))
                .setResponseTimeout(
                        Timeout.ofMilliseconds(properties.getTimeouts().getRead()))
                .build();

        // ✅ Connection Pool (QUAN TRỌNG - thay cho setMaxConnTotal)
        PoolingHttpClientConnectionManager connManager =
                new PoolingHttpClientConnectionManager();

        connManager.setMaxTotal(100);              // Tổng connection
        connManager.setDefaultMaxPerRoute(20);     // Mỗi route

        // ✅ HttpClient 5
        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        // ✅ Request factory
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        // ✅ RestTemplate
        return builder
                .requestFactory(() -> requestFactory)
                .setConnectTimeout(Duration.ofMillis(properties.getTimeouts().getConnection()))
                .setReadTimeout(Duration.ofMillis(properties.getTimeouts().getRead()))
                .build();
    }
}