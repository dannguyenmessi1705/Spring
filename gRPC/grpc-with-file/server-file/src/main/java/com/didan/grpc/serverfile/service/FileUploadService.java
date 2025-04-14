package com.didan.grpc.serverfile.service;

import com.didan.grpc.file.FileMetadta;
import com.didan.grpc.file.FileUploadRequest;
import com.didan.grpc.file.FileUploadResponse;
import com.didan.grpc.file.FileUploadServiceGrpc;
import com.didan.grpc.file.UploadStatus;
import com.didan.grpc.protofile.Constant;
import com.didan.grpc.serverfile.utils.DiskFileStorage;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.logging.Logger;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class FileUploadService extends FileUploadServiceGrpc.FileUploadServiceImplBase {

  Logger logger = Logger.getLogger(FileUploadService.class.getName());

  @Override
  public StreamObserver<FileUploadRequest> uploadFile(StreamObserver<FileUploadResponse> responseObserver) {
    FileMetadta fileMetadta = Constant.FILE_METADTA_CONTEXT_KEY.get(); // Lấy metadata từ context
    DiskFileStorage diskFileStorage = new DiskFileStorage(); // Tạo một đối tượng DiskFileStorage để lưu trữ file

    return new StreamObserver<FileUploadRequest>() {
      @Override
      public void onNext(FileUploadRequest fileUploadRequest) {
        // Được gọi khi client gửi request
        logger.info(String.format("received %d length of data", fileUploadRequest.getFile().getContent().size()));
        try {
          fileUploadRequest.getFile().getContent().writeTo(diskFileStorage.getStream());
        } catch (IOException ex) {
          responseObserver.onError(Status.INTERNAL.withDescription("cannot write data due to " + ex.getMessage()).asRuntimeException());
        }
      }

      @Override
      public void onError(Throwable throwable) {
        // Được gọi khi client gửi request không thành công
        logger.warning(throwable.getMessage());
      }

      @Override
      public void onCompleted() {
        // Được gọi khi client gửi xong tất cả các request
        try {
          int totalBytesReceived = diskFileStorage.getStream().size(); // Lấy kích thước file đã nhận
          if (totalBytesReceived == fileMetadta.getContentLength()) { // Kiểm tra xem kích thước file đã nhận có bằng với kích thước file trong metadata không
            diskFileStorage.write(fileMetadta.getFileNameWithType()); // Ghi file vào đĩa
            diskFileStorage.close();
          } else {
            responseObserver.onError(Status.INTERNAL.withDescription("file size is not equal").asRuntimeException()); // Nếu không bằng thì trả về lỗi INTERNAL
          }
        } catch (IOException ex) {
          responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage()).asRuntimeException()); // Nếu có lỗi xảy ra thì trả về lỗi INTERNAL
        }
        // Gửi phản hồi về cho client
        responseObserver.onNext(
            FileUploadResponse.newBuilder()
                .setFileName(fileMetadta.getFileNameWithType())
                .setUploadStatus(UploadStatus.SUCCESS)
                .build()
        );
        responseObserver.onCompleted(); // Kết thúc cuộc gọi
      }

    };
  }
}
