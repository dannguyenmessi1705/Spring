package com.didan.grpc.serverfile.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DiskFileStorage {
  private final ByteArrayOutputStream byteArrayOutputStream;

  public DiskFileStorage() {
    this.byteArrayOutputStream = new ByteArrayOutputStream();
  }

  public ByteArrayOutputStream getStream() {
    return this.byteArrayOutputStream; // Trả về đối tượng ByteArrayOutputStream
  }

  public void write(String fileNameWithType) throws IOException {
    String path = "output//"; // Chỉ định thư mục đầu ra
    try (FileOutputStream fileOutputStream = new FileOutputStream(path.concat(fileNameWithType))) { // Tạo FileOutputStream để ghi dữ liệu vào tệp
      this.byteArrayOutputStream.writeTo(fileOutputStream); // Ghi dữ liệu vào tệp
    }
  }

  public void close() throws IOException {
    this.byteArrayOutputStream.close(); // Đóng ByteArrayOutputStream
  }
}
