package com.didan.logquickwit.filter;

import com.didan.logquickwit.constant.TrackingContextEnum;
import com.didan.logquickwit.utils.CommonUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Order(0)
@Configuration
public class ClientDataFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String appVersion = request.getHeader(TrackingContextEnum.APP_VERSION.getHeaderKey());
        String osVersion = request.getHeader(TrackingContextEnum.OS_VERSION.getHeaderKey());
        String typeOS = request.getHeader(TrackingContextEnum.TYPE_OS.getHeaderKey());
        String userAgent = request.getHeader(TrackingContextEnum.USER_AGENT.getHeaderKey());

        ThreadContext.put(TrackingContextEnum.APP_VERSION.getThreadKey(), appVersion);
        ThreadContext.put(TrackingContextEnum.TYPE_OS.getThreadKey(), typeOS);
        ThreadContext.put(TrackingContextEnum.OS_VERSION.getThreadKey(), osVersion);
        ThreadContext.put(TrackingContextEnum.USER_AGENT.getThreadKey(), userAgent);
        ThreadContext.put(TrackingContextEnum.IMEI_APP.getThreadKey(),
                request.getHeader(TrackingContextEnum.IMEI_APP.getHeaderKey()));
        String xOriginalForwardFor = request.getHeader(TrackingContextEnum.X_ORIGINAL_FORWARD_FOR.getHeaderKey());
        String ip = request.getHeader(TrackingContextEnum.X_FORWARD_FOR.getHeaderKey());
        if (StringUtils.isNotBlank(xOriginalForwardFor)) {
            try {
                ip = xOriginalForwardFor.split(",")[0];
            } catch (Exception e) {
                log.error("Exception when parse IP", e);
            }
        } else {
            String xRealIp = request.getHeader(TrackingContextEnum.X_REAL_IP.getHeaderKey());
            ip = StringUtils.isBlank(xRealIp) ? request.getRemoteAddr() : xRealIp;
        }
        ThreadContext.put(TrackingContextEnum.X_REAL_IP.getThreadKey(), ip);
        ThreadContext.put(TrackingContextEnum.PROCESS.getThreadKey(), request.getRequestURI());
        if (StringUtils.isEmpty(request.getHeader(HttpHeaders.ACCEPT_LANGUAGE))) {
            LocaleContextHolder.setLocale(new Locale.Builder().setLanguage("vi").setRegion("VN").build());
        }
        generateXRequestIdIfNotExists(request.getHeader(TrackingContextEnum.X_REQUEST_ID.getHeaderKey()));
        response.setHeader(TrackingContextEnum.X_REQUEST_ID.getHeaderKey(),
                ThreadContext.get(TrackingContextEnum.X_REQUEST_ID.getThreadKey()));
        generateCorrelationIdIfNotExists(
                request.getHeader(TrackingContextEnum.X_CORRELATION_ID.getHeaderKey()));
        response.setHeader(TrackingContextEnum.X_CORRELATION_ID.getHeaderKey(),
                ThreadContext.get(TrackingContextEnum.X_CORRELATION_ID.getThreadKey()));

        filterChain.doFilter(request, response);
        ThreadContext.clearAll();
    }

    private void generateXRequestIdIfNotExists(String xRequestId) {
        String requestId =
                StringUtils.isBlank(xRequestId) ? CommonUtils.createNewUUID() : xRequestId;
        ThreadContext.put(TrackingContextEnum.X_REQUEST_ID.getThreadKey(), requestId);
    }

    private String generateCorrelationIdIfNotExists(String xCorrelationId) {
        String correlationId =
                !org.springframework.util.StringUtils.hasText(xCorrelationId) ? String.format("%s-%s", "LOG", CommonUtils.createNewUUID()).trim() :
                        xCorrelationId;
        ThreadContext.put(TrackingContextEnum.X_CORRELATION_ID.getThreadKey(), correlationId);
        return correlationId;
    }

}
