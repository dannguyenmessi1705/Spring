package com.didan.testperformance.first.config.restemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
@Data
public class RestTemplateConfig {

  @Primary
  @Bean
  public RestTemplate restTemplate(
      RestTemplateInterceptor restTemplateInterceptor,
      @Qualifier("bufferingClientHttpRequestFactory") BufferingClientHttpRequestFactory requestFactory) {
    return RestTemplateUtils.createRestTemplate(restTemplateInterceptor, requestFactory);
  }

  @Bean
  public BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory(PoolingHttpClientConnectionManager clientConnectionManager) {
    CloseableHttpClient client = clientBuilder(clientConnectionManager).build();
    return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
  }

  @Bean
  public PoolingHttpClientConnectionManager clientConnectionManager() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = RestTemplateUtils.poolingHttpClientConnectionManager();
    poolingHttpClientConnectionManager.setDefaultConnectionConfig(
        ConnectionConfig.custom().setConnectTimeout(20, TimeUnit.SECONDS).build()
    );
    return poolingHttpClientConnectionManager;
  }

  private HttpClientBuilder clientBuilder(PoolingHttpClientConnectionManager clientConnectionManager) {
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(20, TimeUnit.SECONDS)
        .setResponseTimeout(20, TimeUnit.SECONDS)
        .build();

    return HttpClients.custom()
        .setConnectionManager(clientConnectionManager)
        .setConnectionManagerShared(true)
        .evictIdleConnections(TimeValue.ofSeconds(30L))
        .evictExpiredConnections()
        .setRetryStrategy(new DefaultHttpRequestRetryStrategy(0, TimeValue.ofSeconds(0L)))
        .setDefaultRequestConfig(requestConfig)
        .disableRedirectHandling();
  }
}
