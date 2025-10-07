package com.didan.testperformance.first.config.grpc;

import io.grpc.ForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServerTracingInterceptor implements ServerInterceptor {

  @Override
  public <Q, P> ServerCall.Listener<Q> interceptCall(
      ServerCall<Q, P> call,
      Metadata headers,
      ServerCallHandler<Q, P> next) {

    log.info("Intercepting gRPC call: {}", call.getMethodDescriptor().getFullMethodName());

    return new ForwardingServerCallListener.SimpleForwardingServerCallListener<Q>(
        next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<Q, P>(call) {
          @Override
          public void sendMessage(P message) {
            log.debug("Sending response: {}", message);
            super.sendMessage(message);
          }
        }, headers)) {

      @Override
      public void onMessage(Q message) {
        log.debug("Received request: {}", message);
        super.onMessage(message);
      }

      @Override
      public void onComplete() {
        log.info("gRPC call completed successfully");
        super.onComplete();
      }

      @Override
      public void onCancel() {
        log.warn("gRPC call was cancelled");
        super.onCancel();
      }
    };
  }
}
