package com.didan.grpc.protofile;

import com.didan.grpc.file.FileMetadta;
import io.grpc.Context;
import io.grpc.Metadata;

public class Constant {
  // Tạo một key cho metadata với tên là "file-metadata-bib" và kiểu dữ liệu là byte[]
  // Lưu ý rằng kiểu dữ liệu này sẽ được sử dụng để truyền metadata giữa client và server
  // Key luôn luôn kết thúc bằng "-bin"
  public static final Metadata.Key<byte[]> FILE_METADATA_KEY = Metadata.Key.of("file-metadata-bin", Metadata.BINARY_BYTE_MARSHALLER);

   // Tạo một key cho context với tên là "file-meta" để lưu trữ metadata của file
  public static final Context.Key<FileMetadta> FILE_METADTA_CONTEXT_KEY = Context.key("file-meta");

}
