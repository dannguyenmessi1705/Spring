package com.didan.testperformance.first.config.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GrpcInterceptor implements ClientInterceptor {

  private static final Metadata.Key<String> TRACE_ID_KEY =
      Metadata.Key.of("trace-id", Metadata.ASCII_STRING_MARSHALLER);

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
    return new DescriptionForwardingClientCall<>(methodDescriptor, channel.newCall(methodDescriptor, callOptions)) {
      @Override
      public void sendMessage(ReqT message) {
        log.info("Send gRPC request - service: {}, operation: {}, message: {}", getService(), getOperation(), message);
        super.sendMessage(message);
      }

      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        headers.put(TRACE_ID_KEY, UUID.randomUUID().toString());
        ResponseListener<RespT> listener = new ResponseListener<>(responseListener, getService(), getOperation());
        super.start(listener, headers);
      }
    };
  }

  private static class ResponseListener<RespT> extends ClientCall.Listener<RespT> {
    private final ClientCall.Listener<RespT> delegate; // Đây là listener gốc
    private final String service; // Tên service
    private final String operation; // Tên operation

    public ResponseListener(ClientCall.Listener<RespT> delegate, String service, String operation) {
      super();
      this.delegate = delegate;
      this.service = service;
      this.operation = operation;
    }

    @Override
    public void onMessage(RespT message) {
      log.info("Received gRPC response - service: {}, operation: {}, message: {}", service, operation, message);
      delegate.onMessage(message);
    }

    @Override
    public void onClose(io.grpc.Status status, Metadata trailers) {
      if (status.isOk()) {
        log.info("gRPC call completed successfully - service: {}, operation: {}", service, operation);
      } else {
        log.error("gRPC call failed - service: {}, operation: {}, status: {}", service, operation, status);
      }
      delegate.onClose(status, trailers);
    }

    @Override
    public void onReady() {
      delegate.onReady();
    }

    @Override
    public void onHeaders(Metadata headers) {
      delegate.onHeaders(headers);
    }
  }

  @Getter
  private static class DescriptionForwardingClientCall<ReqT, RespT> extends SimpleForwardingClientCall<ReqT, RespT> {

    private final String service;
    private final String operation;

    protected DescriptionForwardingClientCall(MethodDescriptor<ReqT, RespT> methodDescriptor, ClientCall<ReqT, RespT> clientCall) {
      super(clientCall);
      this.service = methodDescriptor.getServiceName();
      this.operation = methodDescriptor.getFullMethodName();
    }
  }
}
