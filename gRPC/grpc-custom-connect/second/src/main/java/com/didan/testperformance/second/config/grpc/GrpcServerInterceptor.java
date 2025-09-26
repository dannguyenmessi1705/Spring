package com.didan.testperformance.second.config.grpc;

import io.grpc.ForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GrpcServerInterceptor implements ServerInterceptor {

  @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        log.info("Intercepting gRPC call: {}", call.getMethodDescriptor().getFullMethodName());

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
                    @Override
                    public void sendMessage(RespT message) {
                        log.debug("Sending response: {}", message);
                        super.sendMessage(message);
                    }
                }, headers)) {

            @Override
            public void onMessage(ReqT message) {
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
