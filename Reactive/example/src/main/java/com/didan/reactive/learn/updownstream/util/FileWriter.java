package com.didan.reactive.learn.updownstream.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FileWriter {

  private final Path path;
  private BufferedWriter writer;

  private FileWriter(Path path) {
    this.path = path;
  }

  private void createFile() {
    try {
      this.writer = Files.newBufferedWriter(path);
    } catch (IOException ex) {
      throw new RuntimeException("Error creating file writer", ex);
    }
  }

  private void closeFile() {
    try {
      if (writer != null) {
        writer.close();
      }
    } catch (IOException ex) {
      throw new RuntimeException("Error closing file writer", ex);
    }
  }

  // just for demo
  private void write(String content) {
    try {
      this.writer.write(content);
      this.writer.newLine();
      this.writer.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Mono<Void> create(Flux<String> flux, Path path) {
    var writer = new FileWriter(path);
    return flux.doOnNext(writer::write)
        .doFirst(writer::createFile)
        .doFinally(s -> writer.closeFile())
        .then();
  }

}
