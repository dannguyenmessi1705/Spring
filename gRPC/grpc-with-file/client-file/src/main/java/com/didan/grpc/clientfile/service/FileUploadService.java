package com.didan.grpc.clientfile.service;

import com.didan.grpc.file.File;
import com.didan.grpc.file.FileMetadta;
import com.didan.grpc.file.FileUploadRequest;
import com.didan.grpc.file.FileUploadResponse;
import com.didan.grpc.file.FileUploadServiceGrpc;
import com.didan.grpc.file.UploadStatus;
import com.didan.grpc.protofile.Constant;
import com.google.protobuf.ByteString;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {

  @GrpcClient("file-upload")
  FileUploadServiceGrpc.FileUploadServiceStub client;

  public String uploadFile(MultipartFile multipartFile) {
    String fileName;
    int fileSize;
    InputStream inputStream;
    fileName = multipartFile.getOriginalFilename();

    try {
      fileSize = multipartFile.getBytes().length;
      inputStream = multipartFile.getInputStream();
    } catch (IOException ex) {
      return "unable to extract file info";
    }

    StringBuilder response = new StringBuilder();
    CountDownLatch latch = new CountDownLatch(1); // Tạo latch để đợi cho tất cả các request hoàn thành

    Metadata headers = new Metadata(); // Tạo headers để gửi kèm theo request
    headers.put(Constant.FILE_METADATA_KEY,
        FileMetadta.newBuilder()
            .setContentLength(fileSize)
            .setFileNameWithType(fileName)
            .build()
            .toByteArray() // Chuyển đổi metadata thành byte[] để gửi kèm theo request
    ); // Tạo metadata với tên là "file-metadata-bib" và kiểu dữ liệu là byte[]

    StreamObserver<FileUploadRequest> fileUploadRequestStreamObserver = client
        .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers)) // Gửi headers kèm theo request (interceptor là một lớp trung gian để xử lý request và response)
        .uploadFile(new StreamObserver<FileUploadResponse>() {
      @Override
      public void onNext(FileUploadResponse fileUploadResponse) {
        // Được gọi khi server gửi phản hồi
        response.append(fileUploadResponse.getUploadStatus());
      }

      @Override
      public void onError(Throwable throwable) {
        // Được gọi khi có lỗi xảy ra
        response.append(UploadStatus.FAILED);
        throwable.printStackTrace();
        latch.countDown(); // Giải phóng latch nếu có lỗi xảy ra
      }

      @Override
      public void onCompleted() {
        // Được gọi khi server đã hoàn thành gửi phản hồi
        latch.countDown();
      }
    });

    byte[] buffer = new byte[5120]; // Kích thước buffer để đọc dữ liệu từ InputStream
    int length; // Biến để lưu độ dài của dữ liệu đọc được
    try {
      while ((length = inputStream.read(buffer)) > 0) { // Đọc dữ liệu từ InputStream vào buffer cho đến khi không còn dữ liệu
        log.info(String.format("sending %d length of data", length)); // In ra độ dài của dữ liệu đã gửi
        FileUploadRequest fileUploadRequest = FileUploadRequest.newBuilder()
            .setFile(File.newBuilder()
                .setContent(ByteString.copyFrom(buffer, 0, length)) // Chuyển đổi buffer thành ByteString (ByteString là kiểu dữ liệu trong gRPC để truyền tải dữ liệu nhị phân)
                .build()) // Tạo file từ buffer
            .build(); // Tạo request với tên file, kích thước file và dữ liệu file
        fileUploadRequestStreamObserver.onNext(fileUploadRequest); // Gửi request đến server chứa dữ liệu file
      }
      inputStream.close(); // Đóng InputStream sau khi đã gửi tất cả dữ liệu
      fileUploadRequestStreamObserver.onCompleted(); // Gửi thông báo đến server rằng đã gửi xong dữ liệu
      boolean await = latch.await(1, TimeUnit.MINUTES); // Đợi cho tất cả các request hoàn thành trong 1 phút
    } catch (Exception ex) {
      ex.printStackTrace();
      response.append(UploadStatus.FAILED);
    }
    return response.toString();
  }

}
