package com.didan.schedule.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.HostnameVerificationPolicy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestTemplateConfig {

  private static RestTemplate clientRequestRestTemplate(BufferingClientHttpRequestFactory requestFactory) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().add(0 ,new StringHttpMessageConverter(StandardCharsets.UTF_8));
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
      @Override
      public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
      }
    });
    restTemplate.setRequestFactory(requestFactory);
    return restTemplate;
  }

  @Bean
  public BufferingClientHttpRequestFactory defaultHttpRequestFactory(PoolingHttpClientConnectionManager commonHttpPoolingConnectionManager) {
    CloseableHttpClient closeableHttpClient = clientBuilder(commonHttpPoolingConnectionManager).build();
    return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(closeableHttpClient));
  }

  private HttpClientBuilder clientBuilder(PoolingHttpClientConnectionManager commonHttpPoolingConnectionManager) {
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(5000, TimeUnit.SECONDS)
        .setResponseTimeout(5000, TimeUnit.SECONDS)
        .build();
    return HttpClients.custom()
        .setConnectionManager(commonHttpPoolingConnectionManager)
        .setConnectionManagerShared(true)
        .evictIdleConnections(TimeValue.ofSeconds(30L))
        .evictExpiredConnections()
        .setRetryStrategy(new DefaultHttpRequestRetryStrategy(0, TimeValue.ofSeconds(0L)))
        .setDefaultRequestConfig(requestConfig)
        .disableRedirectHandling();
  }

  @Bean
  public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() throws NoSuchAlgorithmException, KeyManagementException {
    return PoolingHttpClientConnectionManagerBuilder.create()
        .setTlsSocketStrategy(
            new DefaultClientTlsStrategy(
                SSLContextBuilder.create().build(),
                HostnameVerificationPolicy.BOTH,
                NoopHostnameVerifier.INSTANCE
            )
        ).build();
  }

  @Primary
  @Bean("restTemplate")
  public RestTemplate restTemplate(@Qualifier("defaultHttpRequestFactory") BufferingClientHttpRequestFactory requestFactory) {
    return clientRequestRestTemplate(requestFactory);
  }

}