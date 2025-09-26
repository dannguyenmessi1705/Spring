package com.didan.testperformance.first.config.restemplate;

import com.didan.testperformance.first.util.DateUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
public class RestTemplateTracer {

  public void trace(HttpRequest request, byte[] body, ClientHttpResponse response, Date startTime) {
    StringBuilder sb = new StringBuilder();

    try (InputStream inputStream = response.getBody();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    ) {
      HttpHeaders httpHeaders = request.getHeaders();
      long start = startTime.getTime();
      sb.append("Request  : [").append(request.getMethod()).append("] ").append(request.getURI()).append(" \n")
          .append("Time     : ").append(DateUtil.dateToString(startTime, DateUtil.YY_MM_DD_HH_MM_SS)).append(" \n")
          .append("Headers  : ").append(httpHeaders).append(" \n")
          .append("Body     : ").append(new String(body, StandardCharsets.UTF_8)).append(" \n")
          .append(" \n");
      long end = System.currentTimeMillis();
      httpHeaders = response.getHeaders();
      sb.append("Response : ").append(response.getStatusCode()).append(" \n")
          .append("Time     : ").append(DateUtil.dateToString(new Date(), DateUtil.YY_MM_DD_HH_MM_SS)).append(" \n")
          .append("Duration : ").append(end - start).append(" ms\n")
          .append("Headers  : ").append(httpHeaders).append(" \n");

      StringBuilder inputString = new StringBuilder();
      String line = bufferedReader.readLine();
      while (line != null) {
        inputString.append(line);
        inputString.append("\n");
        line = bufferedReader.readLine();
      }
      sb.append("Body     : ").append(inputString).append(" \n");
    } catch (Exception ex) {
      log.error("Exception when trace restemplate: ", ex);
      sb.insert(0, "Error parsing response body \n");
    } finally {
      if (StringUtils.hasText(sb.toString())) {
        log.info(sb.toString());
      }
    }
  }

  public void addHeader(HttpHeaders headers, String key, String value) {
    if (!headers.containsKey(key)) { // Nếu header chưa tồn tại
      headers.add(key, value); // Thêm header mới
    }
  }
}
