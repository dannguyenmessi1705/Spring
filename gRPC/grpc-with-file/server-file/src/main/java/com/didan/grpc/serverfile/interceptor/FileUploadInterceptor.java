package com.didan.grpc.serverfile.interceptor;

import com.didan.grpc.file.FileMetadta;
import com.didan.grpc.protofile.Constant;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

@GrpcGlobalServerInterceptor
public class FileUploadInterceptor implements ServerInterceptor {

  @Override
  public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
    FileMetadta fileMetadta = null; // Khởi tạo biến fileMetadta
    if (metadata.containsKey(Constant.FILE_METADATA_KEY)) { // Kiểm tra xem metadata có chứa key "file-metadata-bib" hay không
      byte[] bytes = metadata.get(Constant.FILE_METADATA_KEY); // Lấy giá trị của key "file-metadata-bib" trong metadata
      try {
        fileMetadta = FileMetadta.parseFrom(bytes); // Chuyển đổi byte[] thành đối tượng FileMetadta
      } catch (InvalidProtocolBufferException ex) {
        Status status = Status.INTERNAL.withDescription("unable to create file metadata"); // Nếu không chuyển đổi được thì trả về lỗi INTERNAL
        serverCall.close(status, metadata); // Đóng serverCall với status INTERNAL và metadata
      }
      Context context = Context.current().withValue(
          Constant.FILE_METADTA_CONTEXT_KEY,
          fileMetadta
      ); // Tạo một context với key là "file-meta" và giá trị là fileMetadta
      return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler); // Gọi hàm interceptCall của Contexts để tiếp tục xử lý request
    }
    return new ServerCall.Listener<ReqT>() {}; // Nếu không có metadata thì trả về một listener rỗng
  }
}
