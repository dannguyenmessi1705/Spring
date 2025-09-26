package com.didan.testperformance.first.config.restemplate;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.HostnameVerificationPolicy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@UtilityClass
@Slf4j
public class RestTemplateUtils {

  public RestTemplate createRestTemplate(RestTemplateInterceptor interceptorDefault, BufferingClientHttpRequestFactory requestFactory) {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setInterceptors(Collections.singletonList(interceptorDefault));
    restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    restTemplate.setRequestFactory(requestFactory);
    return restTemplate;
  }

  public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    return PoolingHttpClientConnectionManagerBuilder.create()
        .setTlsSocketStrategy(
            new DefaultClientTlsStrategy(
                SSLContextBuilder.create()
                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                    .build(),
                HostnameVerificationPolicy.BOTH,
                NoopHostnameVerifier.INSTANCE
            )
        )
        .build();
  }
}
